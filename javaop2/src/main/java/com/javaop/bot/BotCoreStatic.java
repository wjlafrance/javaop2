/*
 * Created on Apr 11, 2005 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import javax.swing.JComponent;

import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.pluginmanagers.PluginManager;
import com.javaop.util.PersistantMap;
import com.javaop.util.Uniq;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.PluginException;

public class BotCoreStatic implements StaticExposedFunctions {

	private final static Hashtable<Object, Object>  globalVariables
			= new Hashtable<>();
	private final static PersistantMap              globalSettings
			= JavaOpFileStuff.getGlobalSettings();
	private static BotCoreStatic                    instance
			= new BotCoreStatic();

	private BotCoreStatic() {
	}

	public static BotCoreStatic getInstance() {
		return instance;
	}

	public void newBot(String name) throws PluginException {
		botStart(name);
		JavaOpFileStuff.setActivePlugins(name, PluginManager.getAllNames());
	}

	public void botStart(String name) throws PluginException {
		if (!name.matches("[\\w\\_\\-\\.]+")) {
			throw new PluginException("Bots' names must contain at least one "
					+ "character, and the only characters a-z, A-Z, 0-9, or "
					+ "'.-_'");
		}

		try {
			BotManager.startBot(name);
		} catch (IOException e) {
			throw new PluginException(e);
		}
	}

	public void botStop(String name) throws IllegalArgumentException {
		BotManager.stopBot(name);
	}

	public List<String> botGetAllNames() {
		return BotManager.getAllBots();
	}

	public List<String> botGetActiveNames() {
		return BotManager.getActiveBots();
	}

	public PublicExposedFunctions[] botGetAllActive() {
		List<String> bots = BotManager.getActiveBots();
		PublicExposedFunctions[] funcs = new PublicExposedFunctions[bots.size()];
		for (int i = 0; i < funcs.length; i++) {
			funcs[i] = botGet(bots.get(i));
		}

		return funcs;
	}

	public PublicExposedFunctions botGet(String name) {
		return BotManager.getBot(name);
	}

	public PersistantMap botGetSettings(String bot) {
		return JavaOpFileStuff.getSettings(bot);
	}

	public void systemMessage(int level, String message) {
		PublicExposedFunctions[] bots = botGetAllActive();
		for (PublicExposedFunctions bot : bots) {
			bot.systemMessage(level, message);
		}
	}

	public void botDelete(String name) {
		JavaOpFileStuff.deleteBot(name);
	}

	public boolean botIsDefault(String name) {
		return JavaOpFileStuff.isDefaultBot(name);
	}

	public void botToggleDefault(String name) {
		JavaOpFileStuff.toggleDefault(name);
	}

	public PersistantMap getCustomFlags(String bot) {
		return JavaOpFileStuff.getCustomFlags(bot);
	}

	public String getVersion() {
		return "2.1.3";
	}

	public String getGlobalSetting(String section, String key) {
		return globalSettings.getNoWrite(section, key, null);
	}

	public String getGlobalSettingDefault(String section, String key,
			String defaultValue)
	{
		if (section == null) {
			section = " default";
		}
		return globalSettings.getWrite(section, key, defaultValue);
	}

	public void setGlobalSetting(String section, String key, String value) {
		globalSettings.set(section, key, value);
	}

	public Properties getGlobalSection(String section) {
		return globalSettings.getSection(section);
	}

	public List<String> getGlobalKeys(String section) {
		return Uniq.uniq(globalSettings.propertyNames(section));
	}

	public void putGlobalVariable(Object key, Object value) {
		globalVariables.put(key, value);
	}

	public Object getGlobalVariable(Object key) {
		return globalVariables.get(key);
	}

	public List<String> pluginGetNames() {
		return PluginManager.getAllNames();
	}

	public GenericPluginInterface pluginGet(String name)    {
		return PluginManager.getPlugin(name);
	}

	public Properties pluginGetDefaultSettings(String plugin) {
		return pluginGet(plugin).getDefaultSettingValues();
	}

	public Properties pluginGetGlobalDefaultSettings(String plugin) {
		return pluginGet(plugin).getGlobalDefaultSettingValues();
	}

	public Properties pluginGetDescriptions(String plugin) {
		return pluginGet(plugin).getSettingsDescription();
	}

	public Properties pluginGetGlobalDescriptions(String plugin) {
		return pluginGet(plugin).getGlobalSettingsDescription();
	}

	public Hashtable<String, JComponent> pluginGetComponents(String plugin,
			Properties values)
	{
		return pluginGet(plugin).getComponents(values);
	}

	public Hashtable<String, JComponent> pluginGetGlobalComponents(String
			plugin, Properties values)
	{
		return pluginGet(plugin).getGlobalComponents(values);
	}

	public GenericPluginInterface[] pluginGetAll(boolean includeDefault) {
		List<String> names = pluginGetNames();
		GenericPluginInterface[] plugins = new GenericPluginInterface[names.size()];

		for (int i = 0; i < plugins.length; i++) {
			plugins[i] = pluginGet(names.get(i));
		}

		return plugins;
	}

	public String pluginGetFullName(String plugin) {
		return pluginGet(plugin).getFullName();
	}

	public String pluginGetAuthor(String plugin) {
		return pluginGet(plugin).getAuthorName();
	}

	public String pluginGetWebsite(String plugin) {
		return pluginGet(plugin).getAuthorWebsite();
	}

	public String pluginGetEmail(String plugin) {
		return pluginGet(plugin).getAuthorEmail();
	}

	public String pluginGetLongDescription(String plugin) {
		return pluginGet(plugin).getLongDescription();
	}
	
	/**
	 * Takes a user-inputted game name and shortens it to the 4-letter code.
	 * @throws IllegalArgumentException Long name not recognized
	 */
	public String normalizeGameName(String game) throws IllegalArgumentException {
		if (game == null || game.isEmpty()) {
			throw new IllegalArgumentException("Game name is blank.");
		}

		String normalizedGame = game.toLowerCase();
		normalizedGame = normalizedGame.replace("iiii", "4"); // who knows?
		normalizedGame = normalizedGame.replace("iii", "3");
		normalizedGame = normalizedGame.replace("ii", "2");
		normalizedGame = normalizedGame.replace(" ", "");
		normalizedGame = normalizedGame.replace(":", "");

		switch (normalizedGame) {
			case "diablo":
			case "drtl":
			case "ltrd":
				return "DRTL";
			case "star":
			case "rats":
			case "starcraft":
			case "sc":
				return "STAR";
			case "sexp":
			case "pxes":
			case "broodwar":
			case "bw":
				return "SEXP";
			case "w2bn":
			case "nb2w":
			case "war2":
			case "warcraft2":
			case "warcraft2bne":
			case "wc2":
				return "W2BN";
			case "d2dv":
			case "vd2d":
			case "d2":
			case "diablo2":
				return "D2DV";
			case "d2xp":
			case "px2d":
			case "lod":
			case "diablo2lod":
				return "D2XP";
			case "war3":
			case "3raw":
			case "warcraft3":
			case "warcraft3roc":
				return "WAR3";
			case "w3xp":
			case "px3w":
			case "tft":
			case "warcraft3tft":
				return "W3XP";
			default:
				throw new IllegalArgumentException(String.format("Game name is unrecognized: %s", game));
		}
	}
}
