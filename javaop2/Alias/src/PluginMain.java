package com.javaop.Alias;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;


/*
 * Created on Jan 29, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface
		implements CommandCallback
{
    private PublicExposedFunctions pubFuncs;

    public void load(StaticExposedFunctions staticFuncs) {
    }

    public void activate(PublicExposedFunctions out,
    		PluginCallbackRegister register)
    {
        this.pubFuncs = pubFuncs;

        register.registerCommandPlugin(this, "addalias", 2, false, "N",
        		"<alias> <command> [parameters]", "Adds an alias to the "
        		+ "specified command. Parameters may contain: %u for user, "
        		+ "%v for bot version, and %n for bot name.", null);
        register.registerCommandPlugin(this, "removealias", 0, false, "N",
        		"<alias(es)>", "Removes an alias", null);
        register.registerCommandPlugin(this, "removealiases", 1, false, "N",
                "<command>", "Removes all aliases pointing to the command. "
                + "Remember that default aliases will re-load when the bot "
                + "starts.", null);
        register.registerCommandPlugin(this, "listaliases", 1, false, "LAN",
        		"<command>", "Lists all aliases to the specified command",
        		null);
        register.registerCommandPlugin(this, "getalias", 1, false, "LAN",
        		"<alias>", "Shows the command that this alias points to", null);
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public String getName() {
        return "Alias";
    }

    public String getVersion() {
        return "2.1.2";
    }

    public String getAuthorName() {
        return "iago";
    }

    public String getAuthorWebsite() {
        return "javaop.googlecode.com";
    }

    public String getAuthorEmail() {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription() {
        return "Provides aliasing for commands";
    }

    public String getLongDescription() {
        return "Lets users specify custom aliases for commands.";
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
    		CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("addalias")) {
            if (args.length < 2)
                throw new CommandUsedImproperly("addalias requires at least 2 "
                		+ "parameters", user, command);

            pubFuncs.addAlias(args[1], args[0]);
            pubFuncs.sendTextUser(user, args[0] + " => " + args[1], loudness);

        } else if (command.equalsIgnoreCase("removealias")) {
            if (args.length < 1)
                throw new CommandUsedImproperly("removealias requires at least "
                		+ "1 parameter", user, command);

            StringBuffer s = new StringBuffer("Removed: ");

            for (int i = 0; i < args.length; i++) {
                s.append(args[i] + ", ");
                pubFuncs.removeAlias(args[i]);
            }

            pubFuncs.sendTextUser(user, s.substring(0, s.length() - 2),
            		loudness);
        } else if (command.equalsIgnoreCase("removealiases")) {
            if (args.length != 1)
                throw new CommandUsedImproperly("removealiases requires 1 "
                		+ "parameter", user, command);

            String[] aliases = pubFuncs.getAliasesOf(args[0]);

            if (aliases.length == 0) {
            	pubFuncs.sendTextUser(user, "No aliases found for command "
            			+ args[0], PRIORITY_LOW);
                return;
            }

            StringBuffer s = new StringBuffer("Removed: ");
            for (int i = 0; i < aliases.length; i++) {
            	pubFuncs.removeAlias(aliases[i]);
                s.append(aliases[i] + ", ");
            }
            pubFuncs.sendTextUser(user, s.substring(0, s.length() - 2)
            		.toString(), loudness);
        } else if (command.equalsIgnoreCase("listaliases")) {
            if (args.length != 1)
                throw new CommandUsedImproperly("listaliases requires 1 " + 
                		"parameter", user, command);

            String[] aliases = pubFuncs.getAliasesOf(args[0]);

            if (aliases.length == 0) {
            	pubFuncs.sendTextUser(user, "No aliases found for command "
            			+ args[0], PRIORITY_LOW);
                return;
            }

            StringBuffer s = new StringBuffer(args[0] + ": ");
            for (int i = 0; i < aliases.length; i++)
                s.append(aliases[i] + ", ");
            pubFuncs.sendTextUser(user, s.substring(0, s.length() - 2)
            		.toString(), loudness);
        } else if (command.equalsIgnoreCase("getalias")) {
            if (args.length != 1)
                throw new CommandUsedImproperly("getalias requires 1 "
                		+ "parameter", user, command);

            String alias = pubFuncs.getCommandOf(args[0]);

            if (alias.equalsIgnoreCase(args[0]) == false)
            	pubFuncs.sendTextUser(user, args[0] + " => " + alias, loudness);
            else
            	pubFuncs.sendTextUser(user, "Alias not found.", loudness);
        } else {
        	pubFuncs.sendTextUser(user, "Error in Alias plugin -- please "
        			+ "report on website.", loudness);
        }
    }

}
