package pluginmanagers;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bot.BotCoreStatic;
import bot.JavaOpFileStuff;

import constants.ErrorLevelConstants;
import constants.EventConstants;
import constants.PacketConstants;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.LoginException;
import exceptions.PluginException;

import plugin_containers.BotPlugin;
import plugin_containers.CommandPlugin;
import plugin_containers.ConnectionPlugin;
import plugin_containers.ErrorPlugin;
import plugin_containers.EventPlugin;
import plugin_containers.GuiPlugin;
import plugin_containers.RawEventPlugin;
import plugin_containers.OutgoingTextPlugin;
import plugin_containers.PacketPlugin;
import plugin_containers.SystemMessagePlugin;
import plugin_containers.UserDatabasePlugin;
import plugin_containers.UserErrorPlugin;
import plugin_interfaces.BotCallback;
import plugin_interfaces.CommandCallback;
import plugin_interfaces.ConnectionCallback;
import plugin_interfaces.ErrorCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GuiCallback;
import plugin_interfaces.RawEventCallback;
import plugin_interfaces.OutgoingTextCallback;
import plugin_interfaces.PacketCallback;
import plugin_interfaces.SystemMessageCallback;
import plugin_interfaces.UserDatabaseCallback;
import plugin_interfaces.UserErrorCallback;
import util.BNetEvent;
import util.BNetPacket;
import util.PersistantMap;


/*
 * Created on Dec 2, 2004 By iago
 */

/**
 * This is the class that stores the plugins and
 * 
 * I guess iago never finished his documentation. -joe
 * 
 * @author iago
 * 
 */
public class PluginRegistration implements PluginCallbackRegister
{
    private Vector<BotPlugin>      				botPlugins            = new Vector<BotPlugin>();
    private Vector<ConnectionPlugin> 			connectionPlugins     = new Vector<ConnectionPlugin>();
    private Vector<ErrorPlugin>    				errorPlugins          = new Vector<ErrorPlugin>();
    private Vector<OutgoingTextPlugin>  		outgoingTextPlugins   = new Vector<OutgoingTextPlugin>();
    private Vector<SystemMessagePlugin>			systemMessagePlugins  = new Vector<SystemMessagePlugin>();
    private Vector<UserDatabasePlugin> 			userDatabasePlugins   = new Vector<UserDatabasePlugin>();
    private Vector<UserErrorPlugin>				userErrorPlugins      = new Vector<UserErrorPlugin>();
    private Vector<EventPlugin>					eventPlugins          = new Vector<EventPlugin>();
    private Vector<GuiPlugin>					guiPlugins            = new Vector<GuiPlugin>();

    private Vector<RawEventPlugin>[]			rawEventPlugins       = new Vector[EventConstants.MAX_EVENT + 1];
    private Vector<PacketPlugin>[]				incomingPacketPlugins = new Vector[255];
    private Vector<PacketPlugin>[]				outgoingPacketPlugins = new Vector[255];

    private Hashtable<String, CommandPlugin>	commandPlugins        = new Hashtable<String, CommandPlugin>();
    private final PersistantMap    				commandAliases;
    private final PersistantMap    				customCommandFlags;

    private PublicExposedFunctions pubFuncs;

    public PluginRegistration(PublicExposedFunctions pubFuncs) {
        this.pubFuncs = pubFuncs;

        customCommandFlags = JavaOpFileStuff.getCustomFlags(pubFuncs.getName());
        commandAliases = JavaOpFileStuff.getAliases(pubFuncs.getName());
    }

    public String[] getCommands() {
        Enumeration<String> commands = commandPlugins.keys();
        Vector<String> ret = new Vector<String>();

        while (commands.hasMoreElements())
            ret.add(commands.nextElement());

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String getHelp(String command) {
        CommandPlugin plugin = (CommandPlugin) commandPlugins.get(command);
        if (plugin == null)
            return null;
        return plugin.getHelp();
    }

    public String getUsage(String command) {
        CommandPlugin plugin = (CommandPlugin) commandPlugins.get(command);
        if (plugin == null)
            return null;
        return plugin.getUsage();
    }

    public String getRequiredFlags(String command) {
        CommandPlugin plugin = (CommandPlugin) commandPlugins.get(command);
        if (plugin == null)
            return null;

        String requiredFlags = plugin.getRequiredFlags();
        String customFlags = customCommandFlags.getNoWrite(null, plugin.getName(), "");
        if (customFlags.length() > 0)
            requiredFlags = customFlags;

        return requiredFlags;
    }

    public void addAlias(String command, String alias) {
        commandAliases.set(null, alias, command);
    }

    public String[] getAliasesOf(String command) {
        Enumeration<String> e = commandAliases.propertyNames(null);
        Vector<String> v = new Vector<String>();
        while (e.hasMoreElements()) {
            String s = (String) e.nextElement();
            if (commandAliases.getNoWrite(null, s, "").equals(command))
                v.add(0, s);
        }

        return (String[]) (v.toArray(new String[v.size()]));
    }

    public String getCommandOf(String alias) {
        return commandAliases.getNoWrite(null, alias, alias);
    }

    public void removeAlias(String alias) {
        commandAliases.remove(null, alias);
    }

    public void registerBotPlugin(BotCallback callback, Object data) {
        botPlugins.add(new BotPlugin(callback, data));
    }

    public void registerCommandPlugin(CommandCallback callback, String name, int args,
            boolean requiresOps, String requiredFlags, String usage, String help, Object data) {
        commandPlugins.put(name.toLowerCase(), new CommandPlugin(callback, name, args, requiresOps,
                requiredFlags, usage, help, data));
    }

    public void registerConnectionPlugin(ConnectionCallback callback, Object data) {
        connectionPlugins.add(new ConnectionPlugin(callback, data));
    }

    public void registerErrorPlugin(ErrorCallback callback, Object data) {
        errorPlugins.add(new ErrorPlugin(callback, data));
    }

    public void registerRawEventPlugin(RawEventCallback callback, int event, Object data) {
        if (rawEventPlugins[event] == null)
            rawEventPlugins[event] = new Vector<RawEventPlugin>();

        rawEventPlugins[event].add(new RawEventPlugin(callback, event, data));
    }

    public void registerRawEventPlugin(RawEventCallback callback, int minEvent, int maxEvent,
            Object data) {
        for (int i = minEvent; i <= maxEvent; i++)
            registerRawEventPlugin(callback, i, data);
    }

    public void registerEventPlugin(EventCallback callback, Object data) {
        eventPlugins.add(new EventPlugin(callback, data));
    }

    public void registerOutgoingTextPlugin(OutgoingTextCallback callback, Object data) {
        outgoingTextPlugins.add(new OutgoingTextPlugin(callback, data));
    }

    public void registerIncomingPacketPlugin(PacketCallback callback, int packet, Object data) {
        if (incomingPacketPlugins[packet] == null)
            incomingPacketPlugins[packet] = new Vector<PacketPlugin>();

        incomingPacketPlugins[packet].add(new PacketPlugin(callback, packet, data));
    }

    public void registerIncomingPacketPlugin(PacketCallback callback, int minPacket, int maxPacket,
            Object data) {
        for (int i = minPacket; i <= maxPacket; i++)
            registerIncomingPacketPlugin(callback, i, data);
    }

    public void registerOutgoingPacketPlugin(PacketCallback callback, int packet, Object data) {
        if (outgoingPacketPlugins[packet] == null)
            outgoingPacketPlugins[packet] = new Vector<PacketPlugin>();
        outgoingPacketPlugins[packet].add(new PacketPlugin(callback, packet, data));
    }

    public void registerOutgoingPacketPlugin(PacketCallback callback, int minPacket, int maxPacket,
            Object data) {
        for (int i = minPacket; i <= maxPacket; i++)
            registerOutgoingPacketPlugin(callback, i, data);
    }

    public void registerSystemMessagePlugin(SystemMessageCallback callback, int minLevel,
            int maxLevel, Object data) {
        systemMessagePlugins.add(new SystemMessagePlugin(callback, minLevel, maxLevel, data));
    }

    public void registerSystemMessagePlugin(SystemMessageCallback callback, int level, Object data) {
        registerSystemMessagePlugin(callback, level, level, data);
    }

    public void registerUserDatabasePlugin(UserDatabaseCallback callback, Object data) {
        userDatabasePlugins.add(new UserDatabasePlugin(callback, data));
    }

    public void registerUserErrorPlugin(UserErrorCallback callback, Object data) {
        userErrorPlugins.add(new UserErrorPlugin(callback, data));
    }

    public void registerGuiPlugin(GuiCallback callback, Object data) {
        guiPlugins.add(new GuiPlugin(callback, data));
    }

    // Bot callbacks
    /** This is called as soon as the instance of the bot is started. */
    public void botInstanceStarting() throws IOException, PluginException {
        Enumeration<BotPlugin> e = botPlugins.elements();
        while (e.hasMoreElements()) {
            BotPlugin plugin = (BotPlugin) e.nextElement();
            ((BotCallback) plugin.getCallback()).botInstanceStarting(plugin.getData());
        }
    }

    /** This is called when the instance of the bot is ending */
    public void botInstanceStopping() throws IOException, PluginException {
        Enumeration<BotPlugin> e = botPlugins.elements();
        while (e.hasMoreElements()) {
            BotPlugin plugin = (BotPlugin) e.nextElement();
            ((BotCallback) plugin.getCallback()).botInstanceStopping(plugin.getData());
        }
    }

    // Command Callbacks:
    public boolean raiseCommand(String user, String command, String args, int loudness,
            boolean errorOnUnknown) throws PluginException, CommandUsedImproperly, IOException,
            CommandUsedIllegally {
        String newCommand;

        int infiniteLoopChecker = 0;
        while ((++infiniteLoopChecker < 1000)
                && (newCommand = getCommandOf(command)).replaceAll(" .*", "")
                .equalsIgnoreCase(command) == false) {
            String[] commandParams = newCommand.split(" ", 2);
            if (commandParams.length == 2)
            {
                // Do the name replacement
                commandParams[1] = commandParams[1].replaceAll("\\%u", user);
                commandParams[1] = commandParams[1].replaceAll("\\%c", command);
                commandParams[1] = commandParams[1].replaceAll("\\%v", "JavaOp2 "
                        + (String) BotCoreStatic.getInstance().getGlobalVariable("version"));
                commandParams[1] = commandParams[1].replaceAll("\\%n", pubFuncs.getName());

                args = commandParams[1] + " " + args;
            }

            System.out.println(command + " ==> " + commandParams[0]);
            command = commandParams[0];
        }

        if (infiniteLoopChecker == 1000) {
            pubFuncs.systemMessage(ErrorLevelConstants.ERROR,
            		"There was a suspected infinite alias loop.  It's been skipped.");
            return true;
        }

        CommandPlugin plugin = (CommandPlugin) commandPlugins.get(command);

        if (plugin == null) {
            if (errorOnUnknown)
                this.unknownCommandUsed(user, command);
            return false;
        }

        String requiredFlags = this.getRequiredFlags(command);

        if (pubFuncs.dbHasAny(user, requiredFlags, true) == false)
            throw new CommandUsedIllegally("User attempted to use an illegal command", user,
                    command, pubFuncs.dbGetFlags(user), requiredFlags);

        String[] splitArgs = args.length() > 0 ? args.split("\\s+", plugin.getArgs())
                : new String[0];
        // System.out.println("splitArgs.length = " + splitArgs.length);

        ((CommandCallback) plugin.getCallback()).commandExecuted(user, command, splitArgs,
                                                                 loudness, plugin.getData());

        return true;
    }

    // Connection Callbacks:
    /**
     * The bot is about to connect to a server, but hasn't yet. At this point,
     * the connection can be stopped.
     */
    public boolean connecting(String server, int port) throws IOException, PluginException {
        Enumeration<ConnectionPlugin> e = connectionPlugins.elements();
        while (e.hasMoreElements()) {
            ConnectionPlugin plugin = (ConnectionPlugin) e.nextElement();
            if (((ConnectionCallback) plugin.getCallback()).connecting(server, port,
                                                                       plugin.getData()) == false)
                return false;
        }
        return true;
    }

    /** The bot has just connected to the server. */
    public void connected(String server, int port) throws IOException, PluginException {
        Enumeration<ConnectionPlugin> e = connectionPlugins.elements();
        while (e.hasMoreElements()) {
            ConnectionPlugin plugin = (ConnectionPlugin) e.nextElement();
            ((ConnectionCallback) plugin.getCallback()).connected(server, port, plugin.getData());
        }
    }

    /**
     * The bot is about to disconnect from the server. This is only called for
     * planned disconnects.
     */
    public boolean disconnecting() {
        Enumeration<ConnectionPlugin> e = connectionPlugins.elements();
        while (e.hasMoreElements()) {
            ConnectionPlugin plugin = (ConnectionPlugin) e.nextElement();
            if (((ConnectionCallback) plugin.getCallback()).disconnecting(plugin.getData()) == false)
                return false;
        }

        return true;
    }

    /** The bot has disconnected from the server. */
    public void disconnected() {
        Enumeration<ConnectionPlugin> e = connectionPlugins.elements();
        while (e.hasMoreElements()) {
            ConnectionPlugin plugin = (ConnectionPlugin) e.nextElement();
            ((ConnectionCallback) plugin.getCallback()).disconnected(plugin.getData());
        }
    }

    // Error Callbacks:
    /** This is called if there is a connection problem. */
    public void ioException(IOException e) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).ioException(e, plugin.getData());
        }
    }

    /** This is called if an exception makes it to the top level. */
    public void unknownException(Exception e) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).unknownException(e, plugin.getData());
        }
    }

    /**
     * This is called if there is an "error". These should never be handled,
     * they're always something horrible.
     */
    public void error(Error e) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).error(e, plugin.getData());
        }
    }

    /**
     * If an exception is throw from a plugin, besides an IOException (which
     * forces a reconnect), this gets called.
     */
    public void pluginException(PluginException e) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).pluginException(e, plugin.getData());
        }
    }

    /** If a login exception occurs, this is called */
    public void loginException(LoginException e) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).loginException(e, plugin.getData());
        }
    }

    /** This is called if an unhandled packet is received. */
    public void unknownPacketReceived(BNetPacket packet) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).unknownPacketReceived(packet, plugin.getData());
        }
    }

    /** This is called if an unhandled event is received. */
    public void unknownEventReceived(BNetEvent event) {
        Enumeration<ErrorPlugin> enumeration = errorPlugins.elements();
        while (enumeration.hasMoreElements()) {
            ErrorPlugin plugin = (ErrorPlugin) enumeration.nextElement();
            ((ErrorCallback) plugin.getCallback()).unknownEventReceived(new BNetEvent(event),
                                                                        plugin.getData());
        }
    }

    // Event Callbacks:
    /** This is called when an event the implementor is registered for occurs. */
    public BNetEvent eventOccurring(BNetEvent event) throws IOException, PluginException {
        if (rawEventPlugins[event.getCode()] == null) {
            this.unknownEventReceived(event);
            return null;
        }

        Enumeration<RawEventPlugin> e = rawEventPlugins[event.getCode()].elements();
        while (e.hasMoreElements()) {
            RawEventPlugin plugin = (RawEventPlugin) e.nextElement();
            if ((event = ((RawEventCallback) plugin.getCallback()).eventOccurring(event, plugin.getData())) == null)
                return null;
        }

        return event;
    }

    /** This is called when an event the implementor is registered for occurs. */
    public void eventOccurred(BNetEvent event) throws IOException, PluginException {
        if (rawEventPlugins[event.getCode()] == null)
            return;

        Enumeration<RawEventPlugin> e = rawEventPlugins[event.getCode()].elements();
        while (e.hasMoreElements()) {
            RawEventPlugin plugin = (RawEventPlugin) e.nextElement();
            ((RawEventCallback) (plugin).getCallback()).eventOccurred(new BNetEvent(event),
                                                                      plugin.getData());
        }
    }

    // Not-so-raw event callbacks
    public void talk(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).talk(user, statstring, ping, flags);
        }
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).emote(user, statstring, ping, flags);
        }
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).whisperFrom(user, statstring, ping, flags);
        }
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).whisperTo(user, statstring, ping, flags);
        }
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).userShow(user, statstring, ping, flags);
        }
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).userJoin(user, statstring, ping, flags);
        }
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).userLeave(user, statstring, ping, flags);
        }
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).userFlags(user, statstring, ping, flags);
        }
    }

    public void error(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).error(user, statstring, ping, flags);
        }
    }

    public void info(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).info(user, statstring, ping, flags);
        }
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).broadcast(user, statstring, ping, flags);
        }
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException,
    		PluginException {
        Enumeration<EventPlugin> e = eventPlugins.elements();
        while (e.hasMoreElements()) {
            EventPlugin plugin = (EventPlugin) e.nextElement();
            ((EventCallback) (plugin).getCallback()).channel(user, statstring, ping, flags);
        }
    }

    // Outgoing text Callbacks
    /**
     * Text has just been queued to be sent. It can be changed by returning
     * different text, or cancelled by returning null.
     */
    public String queuingText(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            if ((text = ((OutgoingTextCallback) plugin.getCallback()).queuingText(text,
            		plugin.getData())) == null)
                return null;
        }

        return text;
    }

    /** Text has been queued and will wait for its turn. */
    public void queuedText(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            ((OutgoingTextCallback) plugin.getCallback()).queuedText(text, plugin.getData());
        }
    }

    /**
     * Indicates that the string is next in line to be called. It's about to be
     * waited on.
     */
    public String nextInLine(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            if ((text = ((OutgoingTextCallback) plugin.getCallback()).nextInLine(text,
            			plugin.getData())) == null)
                return null;
        }

        return text;
    }

    /** Gets the amount of time to wait before sending the text. */
    public long getDelay(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        long delay = 0;
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            delay += ((OutgoingTextCallback) plugin.getCallback()).getDelay(text, plugin.getData());
        }

        return delay > 0 ? delay : 0;
    }

    /**
     * Delay is up, text is about to be sent. Last chance to cancel it -- it'll
     * still count towards flooding if it's canceled here
     */
    public boolean sendingText(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            if (((OutgoingTextCallback) plugin.getCallback()).sendingText(text, plugin.getData()) == false)
                return false;
        }

        return true;
    }

    /** The text is being sent out. */
    public void sentText(String text) {
        Enumeration<OutgoingTextPlugin> e = outgoingTextPlugins.elements();
        while (e.hasMoreElements()) {
            OutgoingTextPlugin plugin = (OutgoingTextPlugin) e.nextElement();
            ((OutgoingTextCallback) plugin.getCallback()).sentText(text, plugin.getData());
        }
    }

    // Packet Callbacks:
    /**
     * This is called when a packet is about to be sent or received. It can be
     * changed/dropped here.
     */
    public BNetPacket processingIncomingPacket(BNetPacket buf) throws IOException, PluginException {
        int code = buf.getCode() & 0x000000FF;

        if (incomingPacketPlugins[code] == null) {
            // We ignore SID_CHATEVENT's because they are processed as commands
            // away higher.
            if (code != PacketConstants.SID_CHATEVENT) {
                this.unknownPacketReceived(buf);
                return null;
            }

            return buf;
        }

        Enumeration<PacketPlugin> e = incomingPacketPlugins[code].elements();
        while (e.hasMoreElements()) {
            PacketPlugin plugin = (PacketPlugin) e.nextElement();

            if ((buf = ((PacketCallback) plugin.getCallback()).processingPacket(
            		new BNetPacket(buf), plugin.getData())) == null)
                return null;
        }
        return buf;
    }

    /**
     * This is called when a packet has completed being send , and it can no
     * longer be modified/dropped.
     */
    public void processedIncomingPacket(BNetPacket buf) throws IOException, PluginException {
        int code = buf.getCode() & 0x000000FF;
        if (incomingPacketPlugins[code] == null)
            return;

        Enumeration<PacketPlugin> e = incomingPacketPlugins[code].elements();
        while (e.hasMoreElements()) {
            PacketPlugin plugin = (PacketPlugin) e.nextElement();
            ((PacketCallback) plugin.getCallback()).processedPacket(new BNetPacket(buf),
            		plugin.getData());
        }
    }

    /**
     * This is called when a packet is about to be sent or received. It can be
     * changed/dropped here.
     */
    public BNetPacket processingOutgoingPacket(BNetPacket buf) throws IOException, PluginException {
        int code = buf.getCode() & 0x000000FF;
        if (outgoingPacketPlugins[code] == null) {
            return buf;
        }

        Enumeration<PacketPlugin> e = outgoingPacketPlugins[code].elements();
        while (e.hasMoreElements()) {
            PacketPlugin plugin = (PacketPlugin) e.nextElement();

            if ((buf = ((PacketCallback) plugin.getCallback()).processingPacket(buf,
            		plugin.getData())) == null)
                return null;
        }
        return buf;
    }

    /**
     * This is called when a packet has completed being send , and it can no
     * longer be modified/dropped.
     */
    public void processedOutgoingPacket(BNetPacket buf) throws IOException, PluginException {
        int code = buf.getCode() & 0x000000FF;
        if (outgoingPacketPlugins[code] == null)
            return;

        Enumeration<PacketPlugin> e = outgoingPacketPlugins[code].elements();
        while (e.hasMoreElements()) {
            PacketPlugin plugin = (PacketPlugin) e.nextElement();
            ((PacketCallback) plugin.getCallback()).processedPacket(new BNetPacket(buf),
                                                                    plugin.getData());
        }
    }

    // System Message Callbacks:
    /**
     * These are called for system messages. There are several levels, defined
     * by the various constants in ErrorLevelConstants: DEBU G - A lot of crap
     * that you'll never need to see. For debugging; INFO - Standard useful
     * messages to the user; NOTICE - Something is happening that should be
     * looked at; WARNING - A warning about something; ERROR - An error has
     * occurred; CRITICAL - Conditions are critical; ALERT - Action must be
     * taken immediately; EMERGENCY - All hell's going on, we're unusable; If
     * you want to know where I got the ideas for these levels, "man syslog" :)
     */
    public void systemMessage(int level, String message) {
        Enumeration<SystemMessagePlugin> e = systemMessagePlugins.elements();
        while (e.hasMoreElements()) {
            SystemMessagePlugin plugin = (SystemMessagePlugin) e.nextElement();
            if (level >= plugin.getMinLevel() && level <= plugin.getMaxLevel())
                ((SystemMessageCallback) plugin.getCallback()).systemMessage(level, message,
                                                                             plugin.getData());
        }
    }

    public void showMessage(String message) {
        Enumeration<SystemMessagePlugin> e = systemMessagePlugins.elements();
        while (e.hasMoreElements()) {
            SystemMessagePlugin plugin = (SystemMessagePlugin) e.nextElement();
            ((SystemMessageCallback) plugin.getCallback()).showMessage(message, plugin.getData());
        }
    }

    public void menuItemAdded(String name, String whichMenu, int index, char mnemonic,
            KeyStroke hotkey, Icon icon, ActionListener callback) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).menuItemAdded(name, whichMenu,
            		index, mnemonic, hotkey, icon, callback, plugin.getData());
        }
        // System.exit(0);
    }

    public void menuItemRemoved(String name, String whichMenu) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).menuItemRemoved(name, whichMenu, plugin.getData());
        }
    }

    public void menuSeparatorAdded(String whichMenu) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).menuSeparatorAdded(whichMenu, plugin.getData());
        }
    }

    public void menuAdded(String name, int index, char mnemonic, Icon icon, ActionListener callback) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).menuAdded(name, index, mnemonic, icon, callback,
                                                           plugin.getData());
        }
    }

    public void menuRemoved(String name) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).menuRemoved(name, plugin.getData());
        }
    }

    public void userMenuAdded(String name, int index, Icon icon, ActionListener callback) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).userMenuAdded(name, index,
            		icon, callback, plugin.getData());
        }
    }

    public void userMenuRemoved(String name) {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).userMenuRemoved(name, plugin.getData());
        }
    }

    public void userMenuSeparatorAdded() {
        Enumeration<GuiPlugin> e = guiPlugins.elements();
        while (e.hasMoreElements()) {
            GuiPlugin plugin = (GuiPlugin) e.nextElement();
            ((GuiCallback) plugin.getCallback()).userMenuSeparatorAdded(plugin.getData());
        }
    }

    // User database callbacks:
    /** A user who wasn't in the database before was added */
    public void userAdded(String username, String flags) {
        Enumeration<UserDatabasePlugin> e = userDatabasePlugins.elements();
        while (e.hasMoreElements()) {
            UserDatabasePlugin plugin = (UserDatabasePlugin) e.nextElement();
            ((UserDatabaseCallback) plugin.getCallback()).userAdded(username,
            		flags, plugin.getData());
        }
    }

    /** A user who was already in the database was given new flags */
    public void userChanged(String username, String oldFlags, String newFlags) {
        Enumeration<UserDatabasePlugin> e = userDatabasePlugins.elements();
        while (e.hasMoreElements()) {
            UserDatabasePlugin plugin = (UserDatabasePlugin) e.nextElement();
            ((UserDatabaseCallback) plugin.getCallback()).userChanged(username,
            		oldFlags, newFlags, plugin.getData());
        }
    }

    /** A user who was in the database before was removed */
    public void userRemoved(String username, String oldFlags) {
        Enumeration<UserDatabasePlugin> e = userDatabasePlugins.elements();
        while (e.hasMoreElements()) {
            UserDatabasePlugin plugin = (UserDatabasePlugin) e.nextElement();
            ((UserDatabaseCallback) plugin.getCallback()).userAdded(username,
            		oldFlags, plugin.getData());
        }
    }

    // User error callbacks:
    /**
     * This occurs when any access exception is thrown. Either the user doesn't
     * have the flags to use the command, or they are doing something else which
     * is making the command throw an AccessException.
     */
    public void illegalCommandUsed(String user, String userFlags, String requiredFlags, String command) {
        Enumeration<UserErrorPlugin> e = userErrorPlugins.elements();
        while (e.hasMoreElements()) {
            UserErrorPlugin plugin = (UserErrorPlugin) e.nextElement();
            ((UserErrorCallback) plugin.getCallback()).illegalCommandUsed(user,
            		userFlags, requiredFlags, command, plugin.getData());
        }
    }

    /**
     * This occurs when a user uses a command that doesn't exist. This could be
     * helpful in tracking down non-intuitive names.
     */
    public void unknownCommandUsed(String user, String command) {
        Enumeration<UserErrorPlugin> e = userErrorPlugins.elements();
        while (e.hasMoreElements()) {
            UserErrorPlugin plugin = (UserErrorPlugin) e.nextElement();
            ((UserErrorCallback) plugin.getCallback()).nonExistantCommandUsed(user,
            		command, plugin.getData());
        }
    }

    /**
     * This is used when somebody tries to use a command improperly. Again, not
     * useful for much else besides tracking down non-intuitive commands.
     */
    public void commandUsedImproperly(String user, String command, String syntaxUsed, String errorMessage) {
        Enumeration<UserErrorPlugin> e = userErrorPlugins.elements();
        while (e.hasMoreElements()) {
            UserErrorPlugin plugin = (UserErrorPlugin) e.nextElement();
            ((UserErrorCallback) plugin.getCallback()).commandUsedImproperly(user,
            		command, syntaxUsed, errorMessage, plugin.getData());
        }
    }

}
