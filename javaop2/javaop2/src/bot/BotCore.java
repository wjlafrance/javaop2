package bot;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

import constants.ErrorLevelConstants;
import constants.LoudnessConstants;
import constants.PriorityConstants;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

import pluginmanagers.PluginRegistration;
import pluginmanagers.PluginManager;

import users.UserList;
import util.BnetPacket;
import util.Buffer;
import util.ColorConstants;
import util.PersistantMap;
import util.Splitter;
import util.Uniq;
import util.User;
import util.UserDB;


/*
 * Created on Dec 4, 2004 By iago
 */

/**
 * A single instance of this is created for each bot we're running. It
 * implements PublicExposedFunctions, which the only important thing that is
 * passed to our workers (plugins). This class looks after: - Storing the
 * UserList - Storing the settings - Storing the variables - Stores the socket,
 * along with OutputStream for it - Creates and stores the PacketThread, handing
 * the InputStream for the socket to the PacketThread - Connects to the remote
 * server
 * 
 * @author iago
 * 
 */
public class BotCore implements PublicExposedFunctions
{
    final protected PersistantMap             localSettings;
    final protected UserDB                    userDB;
    final protected Hashtable<Object, Object> localVariables;
    final protected PluginRegistration        callbacks;
    final protected String                    botname;

    final protected UserList                  users;
    protected String                          channelName  = "<not logged in>";

    final protected Timer                     timer;
    final protected Hashtable<TimerTask, JOTimerTask> timerTasks   = new Hashtable<TimerTask, JOTimerTask>();

    final protected PluginManager             plugins;

    protected PacketThread                    packetThread = null;

    private boolean                           running      = true;

    private boolean                           locked       = true;

    private final Queue                       queue;

    /**
     * If an IOException is thrown from here, it means that the localSettings
     * file has been generated. The program should end and/or prompt the user to
     * fill out his information.
     */
    public BotCore(String botname) throws IOException, PluginException
    {
        this.botname = botname;

        users = new UserList();
        localVariables = new Hashtable<Object, Object>();
        this.callbacks = new PluginRegistration(this);
        timer = new Timer();

        localSettings = JavaOpFileStuff.getSettings(botname);
        userDB = JavaOpFileStuff.getUserDB(botname);
        queue = new Queue(this, callbacks);

        if (localSettings == null)
            throw new IOException("Unable to load the settings file for bot " + botname);
        if (userDB == null)
            throw new IOException("Unable to load the user database for bot " + botname);

        plugins = new PluginManager();
        plugins.activatePlugins(this, callbacks);

        callbacks.botInstanceStarting();

        systemMessage(ErrorLevelConstants.NOTICE, "Bot '" + botname + "' has been started.");

        if (getLocalSettingDefault(null, "connect automatically", "false").equalsIgnoreCase("true"))
            connect();
    }

    public void sendPacket(Buffer packet) throws IOException {
        checkRunning();

        try {
            if (packet instanceof BnetPacket) {
                packet = callbacks.processingOutgoingPacket((BnetPacket) packet);
                if (packet == null)
                    return;
                callbacks.processedOutgoingPacket((BnetPacket) packet);
            }
            packetThread.send(packet.getBytes());
        } catch (IOException e) { // We want IOExceptions to propogate
            throw e;
        } catch (PluginException e) {
            callbacks.pluginException(e);
        } catch (Exception e) {
            callbacks.unknownException(e);
        } catch (Error e) {
            callbacks.error(e);
        }
    }

    public void sendTextPriority(String text, int priority) {
        checkRunning();
        queue.send(text, priority);
    }

    public void sendText(String text) {
        checkRunning();
        queue.send(text, PriorityConstants.PRIORITY_NORMAL);
    }

    public void sendTextUser(String user, String text, int loudness) throws IOException {
        checkRunning();
        sendTextUserPriority(user, text, loudness, PriorityConstants.PRIORITY_NORMAL);
    }

    public void sendTextUserPriority(String user, String text, int loudness, int priority) throws IOException{
        checkRunning();

        if (user == null)
            loudness = LoudnessConstants.SILENT;

        final Vector<String> splitText;

        if (loudness == LoudnessConstants.SILENT) {
            splitText = new Vector<String>();
            splitText.add(text);
        } else {
            splitText = Splitter.split(text, true);
        }

        Enumeration<String> e = splitText.elements();

        while (e.hasMoreElements()) {
            if (loudness == LoudnessConstants.LOUD)
                queue.send(user + ": " + e.nextElement(), priority);
            else if (loudness == LoudnessConstants.LOUD_NO_NAME)
                queue.send("" + e.nextElement(), priority);
            else if (loudness == LoudnessConstants.QUIET)
                queue.send("/w " + user + " " + e.nextElement(), priority);
            else if (loudness == LoudnessConstants.SILENT)
                showMessage(ColorConstants.getColor("Silent message") + e.nextElement());
            else
                systemMessage(ErrorLevelConstants.ERROR, "Unknown 'loudness' setting for message: "
                        + e.nextElement() + " (loudness was " + loudness + ")");
        }

    }

    public void schedule(TimerTask task, long interval) {
        JOTimerTask thisTask = new JOTimerTask(task);
        timerTasks.put(task, thisTask);
        timer.schedule(thisTask, interval, interval);

        systemMessage(ErrorLevelConstants.DEBUG, "Scheduled timer: " + task + " (every " + interval
                + "ms)");
    }

    public void unschedule(TimerTask task) {
        JOTimerTask thatTask = (JOTimerTask) timerTasks.get(task);

        if (thatTask != null)
            thatTask.cancel();

        timerTasks.remove(task);

        systemMessage(ErrorLevelConstants.DEBUG, "Unscheduled timer: " + task);
    }

    public void clearQueue() {
        checkRunning();
        queue.clear();
    }

    public StaticExposedFunctions getStaticExposedFunctionsHandle() {
        return BotCoreStatic.getInstance();
    }

    public void setTCPNoDelay(boolean noDelay) throws IOException {
        checkRunning();
        packetThread.setTcpNoDelay(noDelay);
    }

    /**********************
     * Commands
     */

    /**********************
     * Locking
     */
    public boolean isLocked() {
        checkRunning();
        return locked;
    }

    public void lock() {
        checkRunning();
        locked = true;
    }

    public void unlock() {
        checkRunning();
        locked = false;
    }

    /**********************
     * Aliases
     */
    public void addAlias(String command, String alias) {
        callbacks.addAlias(command, alias);
    }

    public String[] getAliasesOf(String command) {
        return callbacks.getAliasesOf(command);
    }

    /**
     * Get the command associated with an alias. Returns the command itself if
     * there is no alias.
     */
    public String getCommandOf(String alias) {
        return callbacks.getCommandOf(alias);
    }

    /** Remove an alias */
    public void removeAlias(String alias) {
        callbacks.removeAlias(alias);
    }

    /**********************
     * Channel list
     */
    public void channelSetName(String name) {
        checkRunning();
        this.channelName = name;
    }

    public String channelGetName() {
        checkRunning();
        return channelName;
    }

    public int channelGetCount() {
        checkRunning();
        return users.size();
    }

    public void channelClear() {
        checkRunning();
        users.clear();
    }

    public User channelAddUser(String name, int flags, int ping, String message) {
        checkRunning();
        return users.addUser(name, flags, ping, message);
    }

    public User channelRemoveUser(String name) {
        checkRunning();
        return users.removeUser(name);
    }

    public User channelGetUser(String name) {
        checkRunning();
        return users.getUser(name);
    }

    public String[] channelGetList() {
        checkRunning();
        return users.getList();
    }

    public String[] channelGetListWithAny(String flags) {
        checkRunning();

        String[] users = channelGetList();
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAny(users[i], flags, false))
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String[] channelGetListWithAll(String flags) {
        checkRunning();

        String[] users = channelGetList();
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAll(users[i], flags))
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String[] channelGetListWithoutAny(String flags) {
        checkRunning();

        String[] users = channelGetList();
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAny(users[i], flags, false) == false)
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String[] channelMatchGetList(String pattern) {
        checkRunning();

        return users.matchNames(pattern);
    }

    public String[] channelMatchGetListWithAny(String pattern, String flags) {
        checkRunning();

        String[] users = channelMatchGetList(pattern);
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAny(users[i], flags, false))
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String[] channelMatchGetListWithAll(String pattern, String flags) {
        checkRunning();

        String[] users = channelMatchGetList(pattern);
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAll(users[i], flags))
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    public String[] channelMatchGetListWithoutAny(String pattern, String flags) {
        checkRunning();

        String[] users = channelMatchGetList(pattern);
        Vector<String> ret = new Vector<String>();
        for (int i = 0; i < users.length; i++)
            if (dbHasAny(users[i], flags, false) == false)
                ret.add(users[i]);

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    /**********************
     * Manipulating bot instances
     */
    /** Completely disconnect, kill, and clean up the current bot instance */
    public void stop() {
        checkRunning();

        lock();

        try {
            callbacks.botInstanceStopping();
        } catch (Exception e) {
        }

        if (packetThread != null)
            packetThread.stopThread();

        if (plugins != null)
            plugins.deactivatePlugins(callbacks);

        localVariables.clear();
        users.clear();

        running = false;
    }

    /** Kill this instance of the bot */
    public void killInstance() throws Exception {
        checkRunning();
        killInstance(getName());
    }

    /**
     * Kill the bot instance with the specified name. IllegalArgumentException
     * is thrown if there is no bot with that name, or if the bot isn't running.
     */
    public void killInstance(String name) throws Exception {
        checkRunning();
        BotManager.stopBot(name);
    }

    /**
     * Start a new bot instance with the specified name.
     * IllegalArgumentException is thrown if there is no bot with that name, or
     * if the bot is already running.
     */
    public void startInstance(String name) throws Exception {
        checkRunning();
        BotManager.startBot(name);
    }

    /** Get a list of all running bots */
    public String[] getRunningBots() {
        checkRunning();
        return BotManager.getActiveBots();
    }

    /** Get a list of all bots */
    public String[] getAllBots() {
        checkRunning();
        return BotManager.getAllBots();
    }

    /******************
     * Events to send to "display" plugins
     */
    public void talk(String user, String message, int ping, int flags) throws IOException, PluginException{
        checkRunning();
        callbacks.talk(user, message, ping, flags);
    }

    public void emote(String user, String message, int ping, int flags) throws IOException, PluginException{
        checkRunning();
        callbacks.emote(user, message, ping, flags);
    }

    public void whisperFrom(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.whisperFrom(user, message, ping, flags);
    }

    public void whisperTo(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.whisperTo(user, message, ping, flags);
    }

    public void userShow(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.userShow(user, message, ping, flags);
    }

    public void userJoin(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.userJoin(user, message, ping, flags);
    }

    public void userLeave(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.userLeave(user, message, ping, flags);
    }

    public void userFlags(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.userFlags(user, message, ping, flags);
    }

    public void error(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.error(user, message, ping, flags);
    }

    public void info(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.info(user, message, ping, flags);
    }

    public void broadcast(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.broadcast(user, message, ping, flags);
    }

    public void channel(String user, String message, int ping, int flags) throws IOException, PluginException {
        checkRunning();
        callbacks.channel(user, message, ping, flags);
    }

    /******************
     * Local settings
     */
    public String getLocalSetting(String section, String key)
    {
        checkRunning();

        if (section == null)
            section = " default";

        if (localSettings.contains(section, key) == false)
        {
            systemMessage(ErrorLevelConstants.ERROR, "Attempted to access a missing setting: "
                    + section + " // " + key);
            return null;
        }

        return localSettings.getNoWrite(section, key, null);
    }

    public String getLocalSettingDefault(String section, String key, String defaultValue)
    {
        checkRunning();

        if (section == null)
            section = " default";

        return localSettings.getWrite(section, key, defaultValue);
    }

    public void putLocalSetting(String section, String key, String value)
    {
        checkRunning();

        localSettings.set(section, key, value);
    }

    public void removeLocalSetting(String section, String key)
    {
        checkRunning();

        localSettings.remove(section, key);
    }

    public String[] getLocalKeys(String section)
    {
        checkRunning();

        return Uniq.uniq(localSettings.propertyNames(section));
    }

    public Properties getLocalSettingSection(String section)
    {
        return localSettings.getSection(section);
    }

    /*************************
     * Runtime variables
     */
    public void putLocalVariable(Object key, Object value)
    {
        checkRunning();

        localVariables.put(key, value);
    }

    public Object getLocalVariable(Object key)
    {
        checkRunning();

        return localVariables.get(key);
    }

    public void dbAddAndRemove(String user, String add, String remove)
    {
        checkRunning();

        String oldFlags = dbGetRawFlags(user);
        userDB.addFlags(user, add);
        userDB.removeFlags(user, remove);
        String newFlags = dbGetRawFlags(user);

        if (oldFlags == null || oldFlags.length() == 0)
            callbacks.userAdded(user, newFlags);
        else if (newFlags == null || newFlags.length() == 0)
            callbacks.userRemoved(user, oldFlags);
        else
            callbacks.userChanged(user, oldFlags, newFlags);
    }

    public void dbAddFlags(String user, String flags)
    {
        checkRunning();

        String oldFlags = dbGetRawFlags(user);
        userDB.addFlags(user, flags);
        if (oldFlags == null || oldFlags.length() == 0)
            callbacks.userAdded(user, dbGetRawFlags(user));
        else
            callbacks.userChanged(user, oldFlags, dbGetRawFlags(user));
    }

    public void dbRemoveFlag(String user, char flag)
    {
        checkRunning();

        String oldFlags = dbGetRawFlags(user);
        if (oldFlags == null || oldFlags.length() == 0)
            return;

        userDB.removeFlag(user, flag);

        String newFlags = dbGetRawFlags(user);
        if (newFlags == null || newFlags.length() == 0)
            callbacks.userRemoved(user, oldFlags);
        else
            callbacks.userChanged(user, oldFlags, newFlags);
    }

    public void dbRemoveFlags(String user, String flags)
    {
        checkRunning();

        for (int i = 0; i < flags.length(); i++)
            dbRemoveFlag(user, flags.charAt(i));
    }

    public String dbGetRawFlags(String user)
    {
        checkRunning();

        // The user himself will only ever have "S"
        if (user != null && user.equalsIgnoreCase((String) getLocalVariable("username")))
            return "S";

        return userDB.getRawFlags(user);
    }

    public String dbGetFlags(String user)
    {
        checkRunning();

        return userDB.getFlags(user);
    }

    public void dbDeleteUser(String user)
    {
        checkRunning();

        userDB.deleteUser(user);
    }

    public int dbGetCount()
    {
        checkRunning();

        return userDB.getCount();
    }

    public boolean dbHasAny(String user, String flagList, boolean allowMOverride)
    {
        checkRunning();

        if (user != null && user.equalsIgnoreCase((String) getLocalVariable("username")))
            return (flagList.indexOf("S") >= 0);

        return userDB.hasAny(user, flagList, allowMOverride);
    }

    public boolean dbHasAll(String user, String flagList)
    {
        checkRunning();

        if (user != null && user.equalsIgnoreCase((String) getLocalVariable("username")))
            return (flagList.equalsIgnoreCase("S"));

        return userDB.hasAll(user, flagList);
    }

    public boolean dbUserExists(String user)
    {
        checkRunning();

        if (user != null && user.equalsIgnoreCase((String) getLocalVariable("username")))
            return true;

        return userDB.userExists(user);
    }

    public String[] dbFindAttr(char flag)
    {
        checkRunning();

        return userDB.findAttr(flag);
    }

    public String[] dbGetAllUsers()
    {
        return userDB.getUserList();
    }

    public PluginRegistration getCallbacks()
    {
        checkRunning();

        return callbacks;
    }

    public void systemMessage(int level, String message)
    {
        checkRunning();

        callbacks.systemMessage(level, message);
    }

    public void showMessage(String message)
    {
        checkRunning();

        callbacks.showMessage(message);
    }

    /** Connect to the specified server/port */
    public void connect()
    {
        checkRunning();

        disconnect();

        systemMessage(ErrorLevelConstants.DEBUG, "Entering connect()");

        packetThread = new PacketThread(callbacks, this);
        packetThread.start();
    }

    /**
     * Disconnects from the server, and null's out the input/output stream. Note
     * that there'll likely be a plugin to automatically reconnect you when this
     * happens.
     */
    public void disconnect()
    {
        checkRunning();

        systemMessage(ErrorLevelConstants.DEBUG, "Entering disconnect()");

        if (callbacks.disconnecting() == false)
            return;

        lock();

        if (packetThread != null)
            packetThread.stopThread();

        if (callbacks != null)
            callbacks.disconnected();
    }

    /** Simply performs a disconnect, then a connect. */
    public void reconnect()
    {
        connect();
    }

    public boolean pluginIsActive(String name)
    {
        return JavaOpFileStuff.isActivePlugin(getName(), name);
    }

    public void pluginToggleActive(String name)
    {
        JavaOpFileStuff.toggleActivePlugin(getName(), name);
    }

    public void pluginSetActive(String name, boolean active)
    {
        if (active && JavaOpFileStuff.isActivePlugin(getName(), name))
            JavaOpFileStuff.removeActivePlugin(getName(), name);
        else if (!active && !JavaOpFileStuff.isActivePlugin(getName(), name))
            JavaOpFileStuff.addActivePlugin(getName(), name);
    }

    public void pluginSetDefaultSettings(String plugin)
    {
        PluginManager.getPlugin(plugin).setDefaultSettings(this);
        PluginManager.getPlugin(plugin).setGlobalDefaultSettings(BotCoreStatic.getInstance());
    }

    /******************
     * Commands
     */
    public boolean raiseCommand(String user, String command, String args, int loudness,
            boolean errorOnUnknown) throws IOException
    {
        try
        {
            return callbacks.raiseCommand(user, command, args, loudness, errorOnUnknown);
        }
        catch (CommandUsedIllegally exception)
        {
            exceptionIllegalCommandUsed(user, exception.getUserFlags(),
                                        exception.getRequiredFlags(), exception.getCommand());
        }
        catch (CommandUsedImproperly exception)
        {
            exceptionCommandUsedImproperly(exception.getUser(), exception.getCommand(), command
                    + " " + args, exception.getMessage());
        }
        catch (PluginException exception)
        {
            callbacks.pluginException(exception);
        }
        catch (Exception e)
        {
            callbacks.unknownException(e);
        }

        return true;
    }

    public String[] getCommandList()
    {
        checkRunning();

        return callbacks.getCommands();
    }

    public String getCommandHelp(String command)
    {
        checkRunning();

        return callbacks.getHelp(command);
    }

    public String getCommandUsage(String command)
    {
        checkRunning();

        return callbacks.getUsage(command);
    }

    public String getRequiredFlags(String command)
    {
        return callbacks.getRequiredFlags(command);
    }

    public boolean canUse(String user, String command)
    {
        return dbHasAny(user, getRequiredFlags(command), true);
    }

    /************************
     * Exceptions
     */
    public void exceptionIllegalCommandUsed(String user, String userFlags, String requiredFlags,
            String command)
    {
        callbacks.illegalCommandUsed(user, userFlags, requiredFlags, command);
    }

    public void exceptionCommandUsedImproperly(String user, String command, String syntaxUsed,
            String errorMessage)
    {
        callbacks.commandUsedImproperly(user, command, syntaxUsed, errorMessage);
    }

    public void exceptionUnknownCommandUsed(String user, String command)
    {
        callbacks.unknownCommandUsed(user, command);
    }

    /************************
     * Gui stuff
     */

    public void addMenuItem(String name, String whichMenu, char mnemonic, ActionListener callback)
    {
        addMenuItem(name, whichMenu, -1, mnemonic, null, null, callback);
    }

    public void addMenuItem(String name, String whichMenu, int index, char mnemonic,
            KeyStroke hotkey, Icon icon, ActionListener callback)
    {
        callbacks.menuItemAdded(name, whichMenu, index, mnemonic, hotkey, icon, callback);
    }

    public void removeMenuItem(String name, String whichMenu)
    {
        callbacks.menuItemRemoved(name, whichMenu);
    }

    public void addMenuSeparator(String whichMenu)
    {
        callbacks.menuSeparatorAdded(whichMenu);
    }

    public void addMenu(String name, char mnemonic, ActionListener callback)
    {
        addMenu(name, -1, mnemonic, null, callback);
    }

    public void addMenu(String name, int index, char mnemonic, Icon icon, ActionListener callback)
    {
        callbacks.menuAdded(name, index, mnemonic, icon, callback);
    }

    public void removeMenu(String name)
    {
        callbacks.menuRemoved(name);
    }

    public void addUserMenu(String name, ActionListener callback)
    {
        addUserMenu(name, -1, null, callback);
    }

    public void addUserMenu(String name, int index, Icon icon, ActionListener callback)
    {
        callbacks.userMenuAdded(name, index, icon, callback);
    }

    public void removeUserMenu(String name)
    {
        callbacks.userMenuRemoved(name);
    }

    public void addUserMenuSeparator()
    {
        callbacks.userMenuSeparatorAdded();
    }

    /*****************
     * Miscellaneous
     */
    private void checkRunning()
    {
        if (running == false)
            throw new Error("Attempting to use a stopped bot instance!");
    }

    public String getName()
    {
        checkRunning();

        return botname;
    }

    private class JOTimerTask extends TimerTask
    {
        private final TimerTask realTimerTask;

        public JOTimerTask(TimerTask realTimerTask)
        {
            this.realTimerTask = realTimerTask;
        }

        public void run()
        {
            if (!locked)
                realTimerTask.run();
        }
    }

}
