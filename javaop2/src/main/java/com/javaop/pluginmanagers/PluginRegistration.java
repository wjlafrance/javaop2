package com.javaop.pluginmanagers;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import com.javaop.bot.BotCoreStatic;
import com.javaop.bot.JavaOpFileStuff;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.EventConstants;
import com.javaop.constants.PacketConstants;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.LoginException;
import com.javaop.exceptions.PluginException;

import com.javaop.plugin_containers.BotPlugin;
import com.javaop.plugin_containers.CommandPlugin;
import com.javaop.plugin_containers.ConnectionPlugin;
import com.javaop.plugin_containers.ErrorPlugin;
import com.javaop.plugin_containers.EventPlugin;
import com.javaop.plugin_containers.GuiPlugin;
import com.javaop.plugin_containers.RawEventPlugin;
import com.javaop.plugin_containers.OutgoingTextPlugin;
import com.javaop.plugin_containers.PacketPlugin;
import com.javaop.plugin_containers.SystemMessagePlugin;
import com.javaop.plugin_containers.UserDatabasePlugin;
import com.javaop.plugin_containers.UserErrorPlugin;
import com.javaop.plugin_interfaces.BotCallback;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.ConnectionCallback;
import com.javaop.plugin_interfaces.ErrorCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GuiCallback;
import com.javaop.plugin_interfaces.RawEventCallback;
import com.javaop.plugin_interfaces.OutgoingTextCallback;
import com.javaop.plugin_interfaces.PacketCallback;
import com.javaop.plugin_interfaces.SystemMessageCallback;
import com.javaop.plugin_interfaces.UserDatabaseCallback;
import com.javaop.plugin_interfaces.UserErrorCallback;
import com.javaop.util.BnetEvent;
import com.javaop.util.BnetPacket;
import com.javaop.util.PersistantMap;


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
	private Set<BotPlugin>             botPlugins            = new HashSet<>();
	private Set<ConnectionPlugin>      connectionPlugins     = new HashSet<>();
	private Set<ErrorPlugin>           errorPlugins          = new HashSet<>();
	private Set<OutgoingTextPlugin>    outgoingTextPlugins   = new HashSet<>();
	private Set<SystemMessagePlugin>   systemMessagePlugins  = new HashSet<>();
	private Set<UserDatabasePlugin>    userDatabasePlugins   = new HashSet<>();
	private Set<UserErrorPlugin>       userErrorPlugins      = new HashSet<>();
	private Set<EventPlugin>           eventPlugins          = new HashSet<>();
	private Set<GuiPlugin>             guiPlugins            = new HashSet<>();

	private Set<RawEventPlugin>[]      rawEventPlugins       = new Set[EventConstants.MAX_EVENT + 1];
	private Set<PacketPlugin>[]        incomingPacketPlugins = new Set[255];
	private Set<PacketPlugin>[]        outgoingPacketPlugins = new Set[255];

	private Map<String, CommandPlugin> commandPlugins        = new HashMap<>();
	private final PersistantMap        commandAliases;
	private final PersistantMap        customCommandFlags;

	private PublicExposedFunctions pubFuncs;

	public PluginRegistration(PublicExposedFunctions pubFuncs) {
		this.pubFuncs = pubFuncs;

		customCommandFlags = JavaOpFileStuff.getCustomFlags(pubFuncs.getName());
		commandAliases = JavaOpFileStuff.getAliases(pubFuncs.getName());
	}

	public String[] getCommands() {
		Set<String> ret = commandPlugins.keySet();
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	public String getHelp(String command) {
		CommandPlugin plugin = commandPlugins.get(command);
		if (plugin == null) {
			return null;
		}
		return plugin.getHelp();
	}

	public String getUsage(String command) {
		CommandPlugin plugin = commandPlugins.get(command);
		if (plugin == null) {
			return null;
		}
		return plugin.getUsage();
	}

	public String getRequiredFlags(String command) {
		CommandPlugin plugin = commandPlugins.get(command);
		if (plugin == null) {
			return null;
		}

		String requiredFlags = plugin.getRequiredFlags();
		String customFlags = customCommandFlags.getNoWrite(null, plugin.getName(), "");
		if (customFlags.length() > 0) {
			requiredFlags = customFlags;
		}

		return requiredFlags;
	}

	public void addAlias(String command, String alias) {
		commandAliases.set(null, alias, command);
	}

	public String[] getAliasesOf(String command) {
		Enumeration<String> e = commandAliases.propertyNames(null);
		Set<String> v = new HashSet<>();
		while (e.hasMoreElements()) {
			String s = (String) e.nextElement();
			if (commandAliases.getNoWrite(null, s, "").equals(command)) {
				v.add(s);
			}
		}

		return (String[]) (v.toArray(new String[v.size()]));
	}

	public String getCommandOf(String alias) {
		return commandAliases.getNoWrite(null, alias, alias);
	}

	public void removeAlias(String alias) {
		commandAliases.remove(null, alias);
	}

	@Override public void registerBotPlugin(BotCallback callback, Object data) {
		botPlugins.add(new BotPlugin(callback, data));
	}

	@Override public void registerCommandPlugin(CommandCallback callback, String name, int args,
			boolean requiresOps, String requiredFlags, String usage, String help, Object data)
	{
		commandPlugins.put(name.toLowerCase(), new CommandPlugin(callback, name, args, requiresOps,
				requiredFlags, usage, help, data));
	}

	@Override public void registerConnectionPlugin(ConnectionCallback callback, Object data) {
		connectionPlugins.add(new ConnectionPlugin(callback, data));
	}

	@Override public void registerErrorPlugin(ErrorCallback callback, Object data) {
		errorPlugins.add(new ErrorPlugin(callback, data));
	}

	@Override public void registerRawEventPlugin(RawEventCallback callback, int event, Object data) {
		if (rawEventPlugins[event] == null) {
			rawEventPlugins[event] = new HashSet<>();
		}

		rawEventPlugins[event].add(new RawEventPlugin(callback, event, data));
	}

	@Override public void registerRawEventPlugin(RawEventCallback callback, int minEvent, int maxEvent,
			Object data) {
		for (int i = minEvent; i <= maxEvent; i++) {
			registerRawEventPlugin(callback, i, data);
		}
	}

	@Override public void registerEventPlugin(EventCallback callback, Object data) {
		eventPlugins.add(new EventPlugin(callback, data));
	}

	@Override public void registerOutgoingTextPlugin(OutgoingTextCallback callback, Object data) {
		outgoingTextPlugins.add(new OutgoingTextPlugin(callback, data));
	}

	@Override public void registerIncomingPacketPlugin(PacketCallback callback, int packet, Object data) {
		if (incomingPacketPlugins[packet] == null) {
			incomingPacketPlugins[packet] = new HashSet<>();
		}

		incomingPacketPlugins[packet].add(new PacketPlugin(callback, packet, data));
	}

	@Override public void registerIncomingPacketPlugin(PacketCallback callback, int minPacket, int maxPacket,
			Object data) {
		for (int i = minPacket; i <= maxPacket; i++) {
			registerIncomingPacketPlugin(callback, i, data);
		}
	}

	@Override public void registerOutgoingPacketPlugin(PacketCallback callback, int packet, Object data) {
		if (outgoingPacketPlugins[packet] == null) {
			outgoingPacketPlugins[packet] = new HashSet<>();
		}
		outgoingPacketPlugins[packet].add(new PacketPlugin(callback, packet, data));
	}

	@Override public void registerOutgoingPacketPlugin(PacketCallback callback, int minPacket, int maxPacket,
			Object data) {
		for (int i = minPacket; i <= maxPacket; i++) {
			registerOutgoingPacketPlugin(callback, i, data);
		}
	}

	@Override public void registerSystemMessagePlugin(SystemMessageCallback callback, int minLevel, int maxLevel, Object data) {
		systemMessagePlugins.add(new SystemMessagePlugin(callback, minLevel, maxLevel, data));
	}

	@Override public void registerSystemMessagePlugin(SystemMessageCallback callback, int level, Object data) {
		registerSystemMessagePlugin(callback, level, level, data);
	}

	@Override public void registerUserDatabasePlugin(UserDatabaseCallback callback, Object data) {
		userDatabasePlugins.add(new UserDatabasePlugin(callback, data));
	}

	@Override public void registerUserErrorPlugin(UserErrorCallback callback, Object data) {
		userErrorPlugins.add(new UserErrorPlugin(callback, data));
	}

	@Override public void registerGuiPlugin(GuiCallback callback, Object data) {
		guiPlugins.add(new GuiPlugin(callback, data));
	}

	// Bot callbacks
	/** This is called as soon as the instance of the bot is started. */
	public void botInstanceStarting() throws IOException, PluginException {
		for (BotPlugin plugin : botPlugins) {
			BotCallback callback = (BotCallback) plugin.getCallback();
			callback.botInstanceStarting(plugin.getData());
		}
	}

	/** This is called when the instance of the bot is ending */
	public void botInstanceStopping() throws IOException, PluginException {
		for (BotPlugin plugin : botPlugins) {
			BotCallback callback = (BotCallback) plugin.getCallback();
			callback.botInstanceStopping(plugin.getData());
		}
	}

	// Command Callbacks:
	public boolean raiseCommand(String user, String command, String args, int loudness, boolean errorOnUnknown)
			throws PluginException, CommandUsedImproperlyException, IOException, CommandUsedIllegallyException
	{
		String newCommand;

		int infiniteLoopChecker = 0;
		while ((++infiniteLoopChecker < 1000) && !(newCommand = getCommandOf(command)).replaceAll(" .*", "").equalsIgnoreCase(command)) {
			String[] commandParams = newCommand.split(" ", 2);
			if (commandParams.length == 2) {
				// Do the name replacement
				commandParams[1] = commandParams[1].replaceAll("\\%u", user);
				commandParams[1] = commandParams[1].replaceAll("\\%c", command);
				commandParams[1] = commandParams[1].replaceAll("\\%v", "JavaOp2 " + (String) BotCoreStatic.getInstance().getGlobalVariable("version"));
				commandParams[1] = commandParams[1].replaceAll("\\%n", pubFuncs.getName());

				args = commandParams[1] + " " + args;
			}

			System.out.println(command + " ==> " + commandParams[0]);
			command = commandParams[0];
		}

		if (infiniteLoopChecker == 1000) {
			pubFuncs.systemMessage(ErrorLevelConstants.ERROR, "There was a suspected infinite alias loop.  It's been skipped.");
			return true;
		}

		CommandPlugin plugin = commandPlugins.get(command);

		if (plugin == null) {
			if (errorOnUnknown) {
				this.unknownCommandUsed(user, command);
			}
			return false;
		}

		String requiredFlags = this.getRequiredFlags(command);

		if (!pubFuncs.dbHasAny(user, requiredFlags, true)) {
			throw new CommandUsedIllegallyException("User attempted to use an illegal command", user, command, pubFuncs.dbGetFlags(user), requiredFlags);
		}

		String[] splitArgs = args.length() > 0 ? args.split("\\s+", plugin.getArgs()) : new String[0];

		((CommandCallback) plugin.getCallback()).commandExecuted(user, command, splitArgs, loudness, plugin.getData());

		return true;
	}

	// Connection Callbacks:
	/**
	 * The bot is about to connect to a server, but hasn't yet. At this point,
	 * the connection can be stopped.
	 */
	public boolean connecting(String server, int port) throws IOException, PluginException {
		for (ConnectionPlugin plugin : connectionPlugins) {
			ConnectionCallback callback = (ConnectionCallback) plugin.getCallback();
			if (!callback.connecting(server, port, plugin.getData())) {
				return false;
			}
		}
		return true;
	}

	/** The bot has just connected to the server. */
	public void connected(String server, int port) throws IOException, PluginException {
		for (ConnectionPlugin plugin : connectionPlugins) {
			ConnectionCallback callback = (ConnectionCallback) plugin.getCallback();
			callback.connected(server, port, plugin.getData());
		}
	}

	/**
	 * The bot is about to disconnect from the server. This is only called for
	 * planned disconnects.
	 */
	public boolean disconnecting() {
		for (ConnectionPlugin plugin : connectionPlugins) {
			ConnectionCallback callback = (ConnectionCallback) plugin.getCallback();
			if (!callback.disconnecting(plugin.getData())) {
				return false;
			}
		}

		return true;
	}

	/** The bot has disconnected from the server. */
	public void disconnected() {
		for (ConnectionPlugin plugin : connectionPlugins) {
			ConnectionCallback callback = (ConnectionCallback) plugin.getCallback();
			callback.disconnected(plugin.getData());
		}
	}

	// Error Callbacks:
	/** This is called if there is a connection problem. */
	public void ioException(IOException e) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.ioException(e, plugin.getData());
		}
	}

	/** This is called if an exception makes it to the top level. */
	public void unknownException(Exception e) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.unknownException(e, plugin.getData());
		}
	}

	/**
	 * This is called if there is an "error". These should never be handled,
	 * they're always something horrible.
	 */
	public void error(Error e) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.error(e, plugin.getData());
		}
	}

	/**
	 * If an exception is throw from a plugin, besides an IOException (which
	 * forces a reconnect), this gets called.
	 */
	public void pluginException(PluginException e) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.pluginException(e, plugin.getData());
		}
	}

	/** If a login exception occurs, this is called */
	public void loginException(LoginException e) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.loginException(e, plugin.getData());
		}
	}

	/** This is called if an unhandled packet is received. */
	public void unknownPacketReceived(BnetPacket packet) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.unknownPacketReceived(packet, plugin.getData());
		}
	}

	/** This is called if an unhandled event is received. */
	public void unknownEventReceived(BnetEvent event) {
		for (ErrorPlugin plugin : errorPlugins) {
			ErrorCallback callback = (ErrorCallback) plugin.getCallback();
			callback.unknownEventReceived(new BnetEvent(event), plugin.getData());
		}
	}

	// Event Callbacks:
	/** This is called when an event the implementor is registered for occurs. */
	public BnetEvent eventOccurring(BnetEvent event) throws IOException, PluginException {
		if (rawEventPlugins[event.getCode()] == null) {
			this.unknownEventReceived(event);
			return null;
		}

		for (RawEventPlugin plugin : rawEventPlugins[event.getCode()]) {
			if ((event = ((RawEventCallback) plugin.getCallback()).eventOccurring(event, plugin.getData())) == null) {
				return null;
			}
		}

		return event;
	}

	/** This is called when an event the implementor is registered for occurs. */
	public void eventOccurred(BnetEvent event) throws IOException, PluginException {
		if (rawEventPlugins[event.getCode()] == null) {
			return;
		}

		for (RawEventPlugin plugin : rawEventPlugins[event.getCode()]) {
			((RawEventCallback) plugin.getCallback()).eventOccurred(new BnetEvent(event), plugin.getData());
		}
	}

	// Not-so-raw event callbacks
	public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).talk(user, statstring, ping, flags);
		}
	}

	public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).emote(user, statstring, ping, flags);
		}
	}

	public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).whisperFrom(user, statstring, ping, flags);
		}
	}

	public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).whisperTo(user, statstring, ping, flags);
		}
	}

	public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).userShow(user, statstring, ping, flags);
		}
	}

	public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).userJoin(user, statstring, ping, flags);
		}
	}

	public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).userLeave(user, statstring, ping, flags);
		}
	}

	public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).userFlags(user, statstring, ping, flags);
		}
	}

	public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).error(user, statstring, ping, flags);
		}
	}

	public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).info(user, statstring, ping, flags);
		}
	}

	public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).broadcast(user, statstring, ping, flags);
		}
	}

	public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		for (EventPlugin plugin : eventPlugins) {
			((EventCallback) plugin.getCallback()).channel(user, statstring, ping, flags);
		}
	}

	// Outgoing text Callbacks
	/**
	 * Text has just been queued to be sent. It can be changed by returning
	 * different text, or cancelled by returning null.
	 */
	public String queuingText(String text) {
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			if ((text = ((OutgoingTextCallback) plugin.getCallback()).queuingText(text,
					plugin.getData())) == null) {
				return null;
			}
		}

		return text;
	}

	/** Text has been queued and will wait for its turn. */
	public void queuedText(String text) {
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			((OutgoingTextCallback) plugin.getCallback()).queuedText(text, plugin.getData());
		}
	}

	/**
	 * Indicates that the string is next in line to be called. It's about to be
	 * waited on.
	 */
	public String nextInLine(String text) {
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			if ((text = ((OutgoingTextCallback) plugin.getCallback()).nextInLine(text, plugin.getData())) == null) {
				return null;
			}
		}

		return text;
	}

	/** Gets the amount of time to wait before sending the text. */
	public long getDelay(String text) {
		long delay = 0;
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			delay += ((OutgoingTextCallback) plugin.getCallback()).getDelay(text, plugin.getData());
		}

		return delay > 0 ? delay : 0;
	}

	/**
	 * Delay is up, text is about to be sent. Last chance to cancel it -- it'll
	 * still count towards flooding if it's canceled here
	 */
	public boolean sendingText(String text) {
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			if (!((OutgoingTextCallback) plugin.getCallback()).sendingText(text, plugin.getData())) {
				return false;
			}
		}

		return true;
	}

	/** The text is being sent out. */
	public void sentText(String text) {
		for (OutgoingTextPlugin plugin : outgoingTextPlugins) {
			((OutgoingTextCallback) plugin.getCallback()).sentText(text, plugin.getData());
		}
	}

	// Packet Callbacks:
	/**
	 * This is called when a packet is about to be sent or received. It can be
	 * changed/dropped here.
	 */
	public BnetPacket processingIncomingPacket(BnetPacket buf) throws IOException, PluginException {
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

		for (PacketPlugin plugin : incomingPacketPlugins[code]) {
			if ((buf = ((PacketCallback) plugin.getCallback()).processingPacket( new BnetPacket(buf), plugin.getData())) == null) {
				return null;
			}
		}
		return buf;
	}

	/**
	 * This is called when a packet has completed being send , and it can no
	 * longer be modified/dropped.
	 */
	public void processedIncomingPacket(BnetPacket buf) throws IOException, PluginException {
		int code = buf.getCode() & 0x000000FF;
		if (incomingPacketPlugins[code] == null) {
			return;
		}

		for (PacketPlugin plugin : incomingPacketPlugins[code]) {
			((PacketCallback) plugin.getCallback()).processedPacket(new BnetPacket(buf), plugin.getData());
		}
	}

	/**
	 * This is called when a packet is about to be sent or received. It can be
	 * changed/dropped here.
	 */
	public BnetPacket processingOutgoingPacket(BnetPacket buf) throws IOException, PluginException {
		int code = buf.getCode() & 0x000000FF;
		if (outgoingPacketPlugins[code] == null) {
			return buf;
		}

		for (PacketPlugin plugin : outgoingPacketPlugins[code]) {
			if ((buf = ((PacketCallback) plugin.getCallback()).processingPacket(buf, plugin.getData())) == null) {
				return null;
			}
		}
		return buf;
	}

	/**
	 * This is called when a packet has completed being send , and it can no
	 * longer be modified/dropped.
	 */
	public void processedOutgoingPacket(BnetPacket buf) throws IOException, PluginException {
		int code = buf.getCode() & 0x000000FF;
		if (outgoingPacketPlugins[code] == null) {
			return;
		}

		for (PacketPlugin plugin : outgoingPacketPlugins[code]) {
			((PacketCallback) plugin.getCallback()).processedPacket(new BnetPacket(buf), plugin.getData());
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
		for (SystemMessagePlugin plugin : systemMessagePlugins) {
			if (level >= plugin.getMinLevel() && level <= plugin.getMaxLevel()) {
				((SystemMessageCallback) plugin.getCallback()).systemMessage(level, message,
						plugin.getData());
			}
		}
	}

	public void showMessage(String message) {
		for (SystemMessagePlugin plugin : systemMessagePlugins) {
			((SystemMessageCallback) plugin.getCallback()).showMessage(message, plugin.getData());
		}
	}

	public void menuItemAdded(String name, String whichMenu, int index, char mnemonic, KeyStroke hotkey, Icon icon, ActionListener callback) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).menuItemAdded(name, whichMenu, index, mnemonic, hotkey, icon, callback, plugin.getData());
		}
		// System.exit(0);
	}

	public void menuItemRemoved(String name, String whichMenu) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).menuItemRemoved(name, whichMenu, plugin.getData());
		}
	}

	public void menuSeparatorAdded(String whichMenu) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).menuSeparatorAdded(whichMenu, plugin.getData());
		}
	}

	public void menuAdded(String name, int index, char mnemonic, Icon icon, ActionListener callback) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).menuAdded(name, index, mnemonic, icon, callback, plugin.getData());
		}
	}

	public void menuRemoved(String name) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).menuRemoved(name, plugin.getData());
		}
	}

	public void userMenuAdded(String name, int index, Icon icon, ActionListener callback) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).userMenuAdded(name, index,
					icon, callback, plugin.getData());
		}
	}

	public void userMenuRemoved(String name) {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).userMenuRemoved(name, plugin.getData());
		}
	}

	public void userMenuSeparatorAdded() {
		for (GuiPlugin plugin : guiPlugins) {
			((GuiCallback) plugin.getCallback()).userMenuSeparatorAdded(plugin.getData());
		}
	}

	// User database callbacks:
	/** A user who wasn't in the database before was added */
	public void userAdded(String username, String flags) {
		for (UserDatabasePlugin plugin : userDatabasePlugins) {
			((UserDatabaseCallback) plugin.getCallback()).userAdded(username, flags, plugin.getData());
		}
	}

	/** A user who was already in the database was given new flags */
	public void userChanged(String username, String oldFlags, String newFlags) {
		for (UserDatabasePlugin plugin : userDatabasePlugins) {
			((UserDatabaseCallback) plugin.getCallback()).userChanged(username, oldFlags, newFlags, plugin.getData());
		}
	}

	/** A user who was in the database before was removed */
	public void userRemoved(String username, String oldFlags) {
		for (UserDatabasePlugin plugin : userDatabasePlugins) {
			((UserDatabaseCallback) plugin.getCallback()).userAdded(username, oldFlags, plugin.getData());
		}
	}

	// User error callbacks:
	/**
	 * This occurs when any access exception is thrown. Either the user doesn't
	 * have the flags to use the command, or they are doing something else which
	 * is making the command throw an AccessException.
	 */
	public void illegalCommandUsed(String user, String userFlags, String requiredFlags, String command) {
		for (UserErrorPlugin plugin : userErrorPlugins) {
			((UserErrorCallback) plugin.getCallback()).illegalCommandUsed(user, userFlags, requiredFlags, command, plugin.getData());
		}
	}

	/**
	 * This occurs when a user uses a command that doesn't exist. This could be
	 * helpful in tracking down non-intuitive names.
	 */
	public void unknownCommandUsed(String user, String command) {
		for (UserErrorPlugin plugin : userErrorPlugins) {
			((UserErrorCallback) plugin.getCallback()).nonExistantCommandUsed(user, command, plugin.getData());
		}
	}

	/**
	 * This is used when somebody tries to use a command improperly. Again, not
	 * useful for much else besides tracking down non-intuitive commands.
	 */
	public void commandUsedImproperly(String user, String command, String syntaxUsed, String errorMessage) {
		for (UserErrorPlugin plugin : userErrorPlugins) {
			((UserErrorCallback) plugin.getCallback()).commandUsedImproperly(user, command, syntaxUsed, errorMessage, plugin.getData());
		}
	}

}
