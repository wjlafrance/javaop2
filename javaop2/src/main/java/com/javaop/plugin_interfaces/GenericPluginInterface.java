package com.javaop.plugin_interfaces;

import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.javaop.util.Uniq;
import com.javaop.util.gui.PreferencesPanel;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;


/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * Every plugin has to have a class called "PluginMain" in the default folder
 * that extends this class. If it doesn't, then the plugin will fail to load.
 *
 * @author iago
 *
 */
abstract public class GenericPluginInterface
{
	/**
	 * Called when the plugin is loaded (whenever it's detected) the first time.
	 * You aren't allowed to make hooks at this point. This can be done when the
	 * plugin is Activated.
	 */
	abstract public void load(StaticExposedFunctions staticFuncs);

	/**
	 * Called when the plugin is enabled, or, if it's on by default, when the
	 * bot starts. This is called once for each running instance of the bot
	 * that's using it.
	 */
	abstract public void activate(PublicExposedFunctions out, PluginCallbackRegister register);

	/**
	 * Called when the plugin is disabled. This includes when the instance is
	 * unloaded.
	 */
	abstract public void deactivate(PluginCallbackRegister register);

	/** Get the name of the plugin */
	abstract public String getName();

	/** Get the version number */
	abstract public String getVersion();

	/** Get the author */
	abstract public String getAuthorName();

	/** Get the author's website */
	abstract public String getAuthorWebsite();

	/** Get the author's email address */
	abstract public String getAuthorEmail();

	/** Get a description of the plugin. */
	abstract public String getLongDescription();

	/** Get the default settings for the plugin. */
	abstract public Properties getDefaultSettingValues();

	/**
	 * This should return a Properties with each setting, as well as what it
	 * means. This is used for local settings.
	 */
	abstract public Properties getSettingsDescription();

	/**
	 * This should return the appropriate component for the setting, along with
	 * the default value set. If a JTextField is needed, null can be returned.
	 */
	abstract public JComponent getComponent(String settingName, String value);

	/** Get the default global settings for the plugin. By default, nothing. */
	abstract public Properties getGlobalDefaultSettingValues();

	/** Get the descriptions for the global plugins */
	abstract public Properties getGlobalSettingsDescription();

	/** This returns the appropriate component for the given global setting */
	abstract public JComponent getGlobalComponent(String settingName, String value);

	/** This is just to simplify some code */
	public Hashtable<String, JComponent> getComponents(Properties values)
	{
		Iterable<String> keys = Uniq.uniq(getDefaultSettingValues().keys());
		Hashtable<String, JComponent> ret = new Hashtable<>();

		for (String key : keys) {
			JComponent component = getComponent(key, values.getProperty(key.toLowerCase()));

			if (component == null) {
				component = new JTextField(values.getProperty(key.toLowerCase()));
			}

			ret.put(key, component);
		}

		return ret;
	}

	/** This is just to simplify some code */
	public Hashtable<String, JComponent> getGlobalComponents(Properties values)
	{
		Iterable<String> keys = Uniq.uniq(getGlobalDefaultSettingValues().keys());
		Hashtable<String, JComponent> ret = new Hashtable<>();

		for (String key : keys) {
			JComponent component = getGlobalComponent(key, values.getProperty(key.toLowerCase()));

			if (component == null) {
				component = new JTextField(values.getProperty(key.toLowerCase()));
			}

			ret.put(key, component);
		}

		return ret;
	}

	/**
	 * The PreferencePanels class can be overridden to change how your
	 * preferences look
	 */
	public JPanel getPreferenceImplementation(Properties currentSettings,
			Properties defaultSettings, Properties descriptions,
			Hashtable<String, JComponent> components)
	{
		return new PreferencesPanel(currentSettings, defaultSettings, descriptions, components);
	}

	/**
	 * The PreferencesPanel class for the global settings can also be
	 * overridden, if desired
	 */
	public JPanel getGlobalPreferenceImplementation(Properties currentSettings,
			Properties defaultSettings, Properties descriptions,
			Hashtable<String, JComponent> components)
	{
		return new PreferencesPanel(currentSettings, defaultSettings, descriptions, components);
	}

	/**
	 * Get the full name that uniquely identified this plugin (generally, the
	 * name + version)
	 */
	public String getFullName()
	{
		return getName() + " v" + getVersion();
	}

	/** Add any default settings */
	public void setDefaultSettings(PublicExposedFunctions out)
	{
		Properties props = getDefaultSettingValues();
		for (String settingsKey : Uniq.uniq(props.keys())) {
			out.getLocalSettingDefault(getName(), settingsKey, props.getProperty(settingsKey));
		}

	}

	/** Add any global settings */
	public void setGlobalDefaultSettings(StaticExposedFunctions funcs)
	{
		Properties props = getGlobalDefaultSettingValues();
		for (String settingsKey : Uniq.uniq(props.keys())) {
			funcs.getGlobalSettingDefault(getName(), settingsKey, props.getProperty(settingsKey));
		}
	}

	public String toString()
	{
		return "Plugin: " + getFullName();
	}
}
