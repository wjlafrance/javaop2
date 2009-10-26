import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import javax.swing.JCheckBox;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;
import plugin_interfaces.CommandCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.UserErrorCallback;
import util.Uniq;


/*
 * Created on Jan 9, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback,
        UserErrorCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(
                                       this,
                                       "helpall",
                                       0,
                                       false,
                                       "ANL",
                                       "[command(s)]",
                                       "Gives help on the specified command(s). If no commands are specified, it lists all commands.",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "help",
                                       0,
                                       false,
                                       "ANL",
                                       "[command(s)]",
                                       "Gives help on the specified command(s). If no commands are specified, it lists all commands that you can use.",
                                       null);
        register.registerCommandPlugin(this, "usage", 0, false, "ANL", "[command(s)]",
                                       "Gives the usage for the specified command(s).", null);

        register.registerUserErrorPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Help";
    }

    public String getVersion()
    {
        return "2.1.2";
    }

    public String getAuthorName()
    {
        return "iago";
    }

    public String getAuthorWebsite()
    {
        return "www.javaop.com";
    }

    public String getAuthorEmail()
    {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription()
    {
        return "Provides help on commands.";
    }

    public String getLongDescription()
    {
        return "It lets users use the .help command.  If no parameters are specified, it lists the commands that the bot currently "
                + "understands.  If it is given one or more parameters, it'll give the user the usage and description of the command.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Command not found", "false");
        p.setProperty("Proper usage", "true");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Command not found",
                      "Whispers a user if they use a command that doesn't exist.  Very annoying.");
        p.setProperty("Proper usage",
                      "Whispers a user if they use a command incorrectly.  Very handy.");
        return p;
    }

    public boolean isRequiredPlugin()
    {
        return true;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return new JCheckBox("", value.equalsIgnoreCase("true") ? true : false);
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("helpall") || command.equalsIgnoreCase("help")
                || command.equalsIgnoreCase("usage"))
        {
            if (args.length == 0)
            {
                String[] commands = Uniq.uniq(out.getCommandList());

                StringBuffer list = new StringBuffer("Commands: ");
                for (int i = 0; i < commands.length; i++)
                {
                    if (command.equalsIgnoreCase("helpall") || out.canUse(user, command))
                        list.append(commands[i]).append(" ");
                }

                out.sendTextUserPriority(user, list.toString(), loudness, PRIORITY_LOW);
            }
            else
            {
                for (int i = 0; i < args.length; i++)
                {
                    String alias = out.getCommandOf(args[i]);

                    if (alias != null && alias.equalsIgnoreCase(args[i]) == false)
                        out.sendTextUser(user, args[i] + " is an alias for " + alias, loudness);

                    if (command.equalsIgnoreCase("help"))
                        sendHelp(user, args[i], loudness);
                    if (command.equalsIgnoreCase("usage"))
                        sendUsage(user, args[i], loudness);

                }
            }
        }
        else
        {
            out.sendTextUser(
                             user,
                             "There is an error in the \"Help\" plugin.  Please report this to iago.",
                             loudness);
        }
    }

    public void sendHelp(String user, String command, int loudness) throws IOException
    {
        if (out.getCommandHelp(command) == null)
            out.sendTextUser(user, "Command " + command + " not found.", loudness);
        else
            out.sendTextUserPriority(user, command + " " + out.getCommandUsage(command) + " - "
                    + out.getRequiredFlags(command) + " - " + out.getCommandHelp(command),
                                     loudness, PRIORITY_LOW);
    }

    public void sendUsage(String user, String command, int loudness) throws IOException
    {
        if (out.getCommandUsage(command) == null)
            out.sendTextUser(user, "Command " + command + " not found.", loudness);
        else
            out.sendTextUser(user, "Usage: " + command + " " + out.getCommandUsage(command),
                             loudness);
    }

    public void illegalCommandUsed(String user, String userFlags, String requiredFlags,
            String command, Object data)
    {
    }

    public void nonExistantCommandUsed(String user, String command, Object data)
    {
        try
        {
            if (out.getLocalSettingDefault(getName(), "Command not found", "false").equalsIgnoreCase(
                                                                                                     "true"))
                out.sendTextUserPriority(user, "Command \"" + command + "\" not found.", QUIET,
                                         PRIORITY_VERY_LOW);
        }
        catch (Exception e)
        {
            // *shrug*
        }
    }

    public void commandUsedImproperly(String user, String command, String syntaxUsed,
            String errorMessage, Object data)
    {
        try
        {
            if (out.getCommandUsage(command) == null)
                out.sendTextUser(user, "Command " + command + " not found.", QUIET);
            else
                out.sendTextUser(user, errorMessage + " [Proper usage: " + command + " "
                        + out.getCommandUsage(command) + "]", QUIET);
        }
        catch (Exception e)
        {
            // *shrug*
        }
    }

}
