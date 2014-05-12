/*
 * Created on Apr 11, 2005 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.util.Hashtable;
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
            = new Hashtable<Object, Object>();
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
        if (!name.matches("[\\w\\_\\-\\.]+"))
            throw new PluginException("Bots' names must contain at least one "
                    + "character, and the only characters a-z, A-Z, 0-9, or "
                    + "'.-_'");
        
        try {
            BotManager.startBot(name);
        } catch (IOException e) {
            throw new PluginException(e);
        }
    }
    
    public void botStop(String name) throws IllegalArgumentException {
        BotManager.stopBot(name);
    }
    
    public String[] botGetAllNames() {
        return BotManager.getAllBots();
    }
    
    public String[] botGetActiveNames() {
        return BotManager.getActiveBots();
    }
    
    public PublicExposedFunctions[] botGetAllActive() {
        String[] bots = BotManager.getActiveBots();
        PublicExposedFunctions[] funcs =
                new PublicExposedFunctions[bots.length];
        for (int i = 0; i < funcs.length; i++)
            funcs[i] = botGet(bots[i]);
        
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
        for (int i = 0; i < bots.length; i++)
            bots[i].systemMessage(level, message);
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
        if (section == null)
            section = " default";
        return globalSettings.getWrite(section, key, defaultValue);
    }
    
    public void setGlobalSetting(String section, String key, String value) {
        globalSettings.set(section, key, value);
    }
    
    public Properties getGlobalSection(String section) {
        return globalSettings.getSection(section);
    }
    
    public String[] getGlobalKeys(String section) {
        return Uniq.uniq(globalSettings.propertyNames(section));
    }
    
    public void putGlobalVariable(Object key, Object value) {
        globalVariables.put(key, value);
    }
    
    public Object getGlobalVariable(Object key) {
        return globalVariables.get(key);
    }
    
    public String[] pluginGetNames() {
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
        String[] names = pluginGetNames();
        GenericPluginInterface[] plugins =
                new GenericPluginInterface[names.length];
        
        for (int i = 0; i < plugins.length; i++)
            plugins[i] = pluginGet(names[i]);
        
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
}
