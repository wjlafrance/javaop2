/*
 * Created on Dec 9, 2004 By iago
 */

package com.javaop.callback_interfaces;

import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import com.javaop.exceptions.PluginException;

import com.javaop.util.Buffer;
import com.javaop.util.User;


/**
 * This is a public interface to the bot. This interface will be passed around a
 * lot, and all the plugins will have direct access to it.
 * 
 * @author iago
 * 
 */
public interface PublicExposedFunctions
{
    /** Connect to the server/port specified in the preferences file */
    public void connect();

    /**
     * Disconnects from the server, and null's out the input/output stream. Note
     * that there'll likely be a plugin to automatically reconnect you when this
     * happens.
     */
    public void disconnect();

    /** Simply performs a disconnect, then a connect. */
    public void reconnect();

    /** Get the name assigned to the bot. */
    public String getName();

    /**
     * Sends a single packet or BNetPacket to battle.net. No flood protection
     * happens on this.
     */
    public void sendPacket(Buffer packet) throws IOException;

    /**
     * Sends a single "chat" string to the channel. This will be sent raw,
     * assuming no filtering plugins change it.
     */
    public void sendText(String text) throws IOException;

    /**
     * Sends a single "chat" string to the channel with a priority. This is
     * better to use.
     */
    public void sendTextPriority(String text, int priority) throws IOException;

    /**
     * Sends a "chat" string directed at a user. Generally as a whisper. If this
     * message is too long, it will be split up into smaller messages which are
     * sent individually.
     */
    public void sendTextUser(String user, String text, int loudness) throws IOException;

    /**
     * Sends a "chat" string at a user, splitting if necessary, with a specific
     * priority.
     */
    public void sendTextUserPriority(String user, String text, int loudness, int priority) throws IOException;

    /**
     * Turn off the Nagle Algorithm on the socket. This is recommended for
     * pings.
     */
    public void setTCPNoDelay(boolean noDelay) throws IOException;

    /**
     * Sends a system message to any handlers, the levels are defined in
     * ErrorLevelConstants.
     */
    public void systemMessage(int level, String message);

    /**
     * Sends a message to any System Message handlers. May include codes from
     * util.ColorCodes.
     */
    public void showMessage(String message);

    /** Schedule a timer. These will be postponed while locked */
    public void schedule(TimerTask task, long interval);

    /** Kill a timer. It will no longer run. */
    public void unschedule(TimerTask task);

    /** Clears the queue of pending outgoing messages. */
    public void clearQueue();

    /**
     * Gets a handle to StaticExposedFunctions. This is merely here for
     * convenience
     */
    public StaticExposedFunctions getStaticExposedFunctionsHandle();

    /**********************
     * Locking
     */
    public boolean isLocked();

    public void lock();

    public void unlock();

    /**********************
     * Aliases
     */
    /** Add a command alias to the instance */
    public void addAlias(String command, String alias);

    /** Get the list of command aliases */
    public String[] getAliasesOf(String command);

    /**
     * Get the command associated with an alias. Returns the command itself if
     * there is no alias.
     */
    public String getCommandOf(String alias);

    /** Remove an alias */
    public void removeAlias(String alias);

    /**********************
     * Commands
     */
    public boolean raiseCommand(String user, String command, String args, int loudness,
            boolean errorOnUnknown) throws IOException;

    /** Get a list of all plugins that have been registered. */
    public String[] getCommandList();

    /** Get help on a specific command, or null if it wasn't found. */
    public String getCommandHelp(String command);

    /** Get the usage for a specific command, or null if it wasn't found. */
    public String getCommandUsage(String command);

    /** Get the required flags for a command */
    public String getRequiredFlags(String command);

    /** Returns true if the user is allowed to use the command */
    public boolean canUse(String user, String command);

    /**********************
     * Channel list
     */
    /** Sets the name of the channel */
    public void channelSetName(String name);

    /** Gets the name of the channel */
    public String channelGetName();

    /** Gets the number of users that are currently in the channel */
    public int channelGetCount();

    /** Resets the channel to having no users */
    public void channelClear();

    /**
     * Adds a user to the channel with the specified stats. Returns a "User"
     * object for them.
     */
    public User channelAddUser(String name, int flags, int ping, String message);

    /**
     * Removes a user from the channel, based on their name, and returns a user
     * object representing them. Returns null if the user isn't found.
     */
    public User channelRemoveUser(String name);

    /**
     * Gets a User object for a user in the channel, based on their name.
     * Returns null if the user isn't found.
     */
    public User channelGetUser(String name);

    /** Gets a string list of all users in the channel. */
    public String[] channelGetList();

    /**
     * Gets a string list of all users in the channel that have any of the
     * specified flags.
     */
    public String[] channelGetListWithAny(String flags);

    /**
     * Gets a string list of all users in the channel that have all of the
     * specified flags.
     */
    public String[] channelGetListWithAll(String flags);

    /**
     * Gets a string list of all users in the channel that have none of the
     * specified flags.
     */
    public String[] channelGetListWithoutAny(String flags);

    /** Gets a string list of all users in the channel. */
    public String[] channelMatchGetList(String pattern);

    /**
     * Gets a string list of all users in the channel that have any of the
     * specified flags.
     */
    public String[] channelMatchGetListWithAny(String pattern, String flags);

    /**
     * Gets a string list of all users in the channel that have all of the
     * specified flags.
     */
    public String[] channelMatchGetListWithAll(String pattern, String flags);

    /**
     * Gets a string list of all users in the channel that have none of the
     * specified flags.
     */
    public String[] channelMatchGetListWithoutAny(String pattern, String flags);

    /******************
     * Events sent to the "display" plugins
     */
    public void talk(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void emote(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void whisperFrom(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void whisperTo(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void userShow(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void userJoin(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void userLeave(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void userFlags(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void error(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void info(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void broadcast(String user, String message, int ping, int flags) throws IOException, PluginException;

    public void channel(String user, String message, int ping, int flags) throws IOException, PluginException;

    /******************
     * Local settings
     */
    public String getLocalSetting(String section, String key);

    public String getLocalSettingDefault(String section, String key, String defaultValue);

    public void putLocalSetting(String section, String key, String value);

    public void removeLocalSetting(String section, String key);

    public String[] getLocalKeys(String section);

    public Properties getLocalSettingSection(String section);

    /*************************
     * Runtime variables
     */
    public void putLocalVariable(Object key, Object value);

    public Object getLocalVariable(Object key);

    /*************************
     * User Database
     */
    public void dbAddAndRemove(String user, String add, String remove);

    public void dbAddFlags(String user, String flags);

    public void dbRemoveFlag(String user, char flag);

    public void dbRemoveFlags(String user, String flags);

    public String dbGetRawFlags(String user);

    public String dbGetFlags(String user);

    public void dbDeleteUser(String user);

    public int dbGetCount();

    public boolean dbHasAny(String user, String flagList, boolean allowMOverride);

    public boolean dbHasAll(String user, String flagList);

    public boolean dbUserExists(String user);

    public String[] dbFindAttr(char flag);

    public String[] dbGetAllUsers();

    /************************
     * Exceptions
     */
    public void exceptionIllegalCommandUsed(String user, String userFlags, String requiredFlags,
            String command);

    public void exceptionCommandUsedImproperly(String user, String command, String syntaxUsed,
            String errorMessage);

    public void exceptionUnknownCommandUsed(String user, String command);

    /************************
     * Plugins
     */
    public boolean pluginIsActive(String name);

    public void pluginToggleActive(String name);

    public void pluginSetActive(String name, boolean active);

    public void pluginSetDefaultSettings(String plugin);

    /************************
     * Gui stuff
     */
    public void addMenuItem(String name, String whichMenu, char mnemonic, ActionListener callback);

    public void addMenuItem(String name, String whichMenu, int index, char mnemonic,
            KeyStroke hotkey, Icon icon, ActionListener callback);

    public void removeMenuItem(String name, String whichMenu);

    public void addMenuSeparator(String whichMenu);

    public void addMenu(String name, char mnemonic, ActionListener callback);

    public void addMenu(String name, int index, char mnemonic, Icon icon, ActionListener callback);

    public void removeMenu(String name);

    public void addUserMenu(String name, ActionListener callback);

    public void addUserMenu(String name, int index, Icon icon, ActionListener callback);

    public void removeUserMenu(String name);

    public void addUserMenuSeparator();

}