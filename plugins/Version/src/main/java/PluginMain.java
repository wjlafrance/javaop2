package com.javaop.Version;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;


/*
 * Created on Jan 16, 2005 By Warrior
 */

/**
 * This returns the Bot's current Version when someone with L Flags says
 * "version"
 * 
 * @author Warrior
 * 
 */

public class PluginMain extends GenericPluginInterface implements
	CommandCallback
{
    private PublicExposedFunctions        publicFuncs;
    private static StaticExposedFunctions staticFuncs;

    public void load(StaticExposedFunctions staticFuncs) {
        PluginMain.staticFuncs = staticFuncs;
    }

    public void activate(PublicExposedFunctions publicFuncs,
    		PluginCallbackRegister register)
    {
        this.publicFuncs = publicFuncs;

        register.registerCommandPlugin(this, "version", 0, false, "ANL",
        		"<null>", "Displays the current bot version.", null);

        publicFuncs.addAlias("version", "vers");
        publicFuncs.addAlias("version", "ver");
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public String getName() {
        return "Version";
    }

    public String getVersion() {
        return "2.1.3";
    }

    public String getAuthorName() {
        return "Warrior";
    }

    public String getAuthorWebsite() {
        return "javaop.com";
    }

    public String getAuthorEmail() {
        return "Warrior@scbackstab.com";
    }

    public String getShortDescription() {
        return "Returns the bot version.";
    }

    public String getLongDescription() {
        return "Returns the bot version.";
    }

    public Properties getDefaultSettingValues() {
        return new Properties();
    }

    public Properties getSettingsDescription() {
        return new Properties();
    }

    public JComponent getComponent(String settingName, String value) {
        return null;
    }

    public Properties getGlobalDefaultSettingValues() {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription() {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args,
    		int loudness, Object data) throws PluginException, IOException,
			CommandUsedIllegallyException, CommandUsedImproperlyException
    {
        if (args.length != 0)
            throw new CommandUsedImproperlyException("The version command requires "
            		+ "no parameters.", user, command);

        if (command.equalsIgnoreCase("version")) {

            String osName = System.getProperty("os.name");
            String osArch = System.getProperty("os.arch");
            String osVersion = System.getProperty("os.version");

            publicFuncs.sendTextUserPriority(user, "JavaOp2 " + staticFuncs
            		.getVersion() + " ["
            		+ osName + " " + osVersion + " (" + osArch + ")]",
                    loudness, PRIORITY_LOW);
        }
    }

}