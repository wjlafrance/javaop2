/*
 * Created on Apr 8, 2005 By iago
 */
package com.javaop.callback_interfaces;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.PersistantMap;

import com.javaop.exceptions.PluginException;


/**
 * An instance of this will be passed to every both when it is loaded, and it
 * will be shared by all of them.
 */
public interface StaticExposedFunctions
{
	/****************
	 * Bot Instances
	 */

	/**
	 * Creates a new bot and activates all plugins
	 */
	public void newBot(String name) throws PluginException;

	/**
	 * Start a new bot instance with the specified name.
	 * IllegalArgumentException is thrown if there is no bot with that name, or
	 * if the bot is already running.
	 */
	public void botStart(String name) throws PluginException, IOException;

	/**
	 * Kill the bot instance with the specified name. IllegalArgumentException
	 * is thrown if there is no bot with that name, or if the bot isn't running.
	 */
	public void botStop(String name) throws IOException, IllegalArgumentException;

	/** Get a list of all bots */
	public List<String> botGetAllNames();

	/** Get a list of all running bots */
	public List<String> botGetActiveNames();

	/** Get the public function class for all running bots */
	public PublicExposedFunctions[] botGetAllActive();

	/** Get the public function class for a specific bot */
	public PublicExposedFunctions botGet(String name);

	/** Send a system message to all active bots */
	public void systemMessage(int level, String message);

	/** Remove the files for the bot */
	public void botDelete(String name);

	/** Check if the bot is loaded by default */
	public boolean botIsDefault(String name) throws IOException;

	/** Toggle whether or not a bot is loaded by deafult */
	public void botToggleDefault(String name) throws IOException;

	/** Get the settings for this bot */
	public PersistantMap botGetSettings(String bot);
	/** Get the customized flags file */
	;

	public PersistantMap getCustomFlags(String bot);

	/***************
	 * Settings
	 */

	/** Gets the bot's version */
	public String getVersion();

	/** Get a global setting */
	public String getGlobalSetting(String section, String key);

	/** Get a global section, or write/return the default value */
	public String getGlobalSettingDefault(String section, String key, String defaultValue);

	/** Set a global setting */
	public void setGlobalSetting(String section, String key, String value);

	/** Get an entire section of global variables */
	public Properties getGlobalSection(String section);

	/** Get the keys for the specified section (sorted by name) */
	public List<String> getGlobalKeys(String section);

	/** Set a global variable */
	public void putGlobalVariable(Object key, Object value);

	/** Get a global variable */
	public Object getGlobalVariable(Object key);

	/**************
	 * Plugins
	 */

	public List<String> pluginGetNames();

	public GenericPluginInterface pluginGet(String name);

	public Properties pluginGetDefaultSettings(String plugin);

	public Properties pluginGetGlobalDefaultSettings(String plugin);

	public Properties pluginGetDescriptions(String plugin);

	public Properties pluginGetGlobalDescriptions(String plugin);

	public Hashtable pluginGetComponents(String plugin, Properties values);

	public Hashtable pluginGetGlobalComponents(String plugin, Properties values);

	public GenericPluginInterface[] pluginGetAll(boolean includeDefault);

	public String pluginGetFullName(String plugin);

	public String pluginGetAuthor(String plugin);

	public String pluginGetWebsite(String plugin);

	public String pluginGetEmail(String plugin);

	public String pluginGetLongDescription(String plugin);

}
