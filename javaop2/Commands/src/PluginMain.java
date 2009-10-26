import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import constants.ErrorLevelConstants;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;
import plugin_interfaces.CommandCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import util.PersistantMap;


/*
 * Created on Feb 24, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements EventCallback, CommandCallback
{
    private PublicExposedFunctions        out;
    private static StaticExposedFunctions funcs;

    private PersistantMap                 customFlags;

    public void load(StaticExposedFunctions staticFuncs)
    {
        funcs = staticFuncs;
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        this.customFlags = funcs.getCustomFlags(out.getName());
        register.registerEventPlugin(this, null);

        register.registerCommandPlugin(
                                       this,
                                       "loud",
                                       2,
                                       false,
                                       "ANL",
                                       "<command/params>",
                                       "Runs a command, sending its output to the channel, regardless of what the 'loud' setting is",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "quiet",
                                       2,
                                       false,
                                       "ANL",
                                       "<command/params>",
                                       "Runs a command, whispering back the result, regardless of what the 'loud' setting is",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "silent",
                                       2,
                                       false,
                                       "ANL",
                                       "<command/params>",
                                       "Runs a command, sending output only to the console, regardless of what the 'loud' setting is",
                                       null);

        register.registerCommandPlugin(
                                       this,
                                       "setflagsfor",
                                       2,
                                       false,
                                       "N",
                                       "<command> [flags]",
                                       "Sets the flags required to use a command on this bot to the specified flags.  If [flags] isn't included, it is reset to defaults.",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "getflagsfor",
                                       1,
                                       false,
                                       "AN",
                                       "<command>",
                                       "Retrieves the customized flags required to user a command on this bot.",
                                       null);

        register.registerCommandPlugin(
                                       this,
                                       "settrigger",
                                       1,
                                       false,
                                       "N",
                                       "[new trigger]",
                                       "Sets the trigger to the specified value.  The trigger can be 0 or more characters, although a 0-character trigger gets very annoying",
                                       null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Commands";
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
        return "This plugin parses commands sent by users in the channel";
    }

    public String getLongDescription()
    {
        return "This plugin looks after checking for the trigger and raising commands.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("trigger", ".");
        p.setProperty("loud", "quiet");
        p.setProperty("stacked messages", "true");
        p.setProperty("whispers always command", "false");
        p.setProperty("?trigger requires flags", "true");

        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty(
                      "trigger",
                      "The character or characters that, when put at the beginning of a user's message, makes it a command.  This can be blank.");
        p.setProperty(
                      "loud",
                      "Whether or not to give responses to commands out loud.  Values are \"loud\", \"quiet\", \"loud (no name)\", or \"silent (not recommended)\", ");
        p.setProperty("stacked messages",
                      "Whether or not to allow stacking messages with semicolons (like ?say a;say b;say c;say d)");
        p.setProperty(
                      "whispers always command",
                      "If this is set to true, then anything whispered to the bot will be interpreted as a command.  This is handy for ops, but annoying for chatting (and can cause infinite 'command not found' loops)");
        p.setProperty("?trigger requires flags",
                      "If this is enabled, a user must have A, N, or L flag to use ?trigger");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("stacked messages")
                || settingName.equalsIgnoreCase("whispers always command")
                || settingName.equalsIgnoreCase("?trigger requires flags"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        }

        if (settingName.equalsIgnoreCase("loud"))
        {
            JComboBox loud = new JComboBox(new String[]
            { "quiet", "loud", "loud (no name)", "silent (not recommended)" });
            loud.setSelectedItem(value);
            return loud;
        }

        return null;
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

    public boolean isRequiredPlugin()
    {
        return true;
    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (doTrigger(user, statstring))
            return;

        statstring = shouldRun(statstring);

        if (statstring != null)
            doCommand(user, statstring);
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (doTrigger(user, statstring))
            return;

        statstring = shouldRun(statstring);

        if (statstring != null)
            doCommand(user, statstring);
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (doTrigger(user, statstring) || doTrigger(user, "?" + statstring))
            return;

        String newStatstring = shouldRun(statstring);

        if (newStatstring != null)
            doCommand(user, newStatstring, QUIET);
        else if (statstring.length() > 2)
        {
            if (out.getLocalSettingDefault(getName(), "whispers always command", "false").equalsIgnoreCase(
                                                                                                           "true"))
                doCommand(user, statstring, QUIET);
        }
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    private boolean doTrigger(String user, String message) throws IOException, PluginException
    {
        if (out.getLocalSettingDefault(getName(), "?trigger requires flags", "true").equalsIgnoreCase(
                                                                                                      "true")
                && (out.dbHasAny(user, "ANL", true) == false))
            return false;

        if (message.equalsIgnoreCase("?trigger") == false)
            return false;

        out.sendTextUserPriority(user, "The trigger is "
                + out.getLocalSetting(getName(), "trigger"), QUIET, PRIORITY_VERY_LOW - 10);
        return true;
    }

    private String shouldRun(String command)
    {
        String trigger = out.getLocalSettingDefault(getName(), "trigger", ".");

        if (command.startsWith(trigger))
            return command.substring(trigger.length());

        return null;
    }

    private void doCommand(String user, String message) throws IOException
    {
        doCommand(user, message, getLoudness());
    }

    private void doCommand(String user, String message, int loudness) throws IOException
    {
        if (out.getLocalSettingDefault(getName(), "stacked messages", "true").equalsIgnoreCase(
                                                                                               "true"))
        {
            message = message.replaceAll("\\\\;", "::SEMICOLON::").trim();
            String[] messageParts = message.split(";");

            for (int i = 0; i < messageParts.length; i++)
            {
                String[] commandParam = messageParts[i].trim().split(" ", 2);
                String command = commandParam[0].trim().replaceAll("::SEMICOLON::", ";");
                String params = commandParam.length == 2 ? commandParam[1].trim() : "";

                runCommand(user, command, params, loudness);
            }
        }
        else
        {
            String[] commandParam = message.trim().split(" ", 2);
            String command = commandParam[0].trim();
            String params = commandParam.length == 2 ? commandParam[1].trim() : "";

            runCommand(user, command, params, loudness);
        }

    }

    private void runCommand(String user, String command, String args, int loudness) throws IOException
    {
        out.raiseCommand(user, command, args, loudness, true);
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("settrigger"))
        {
            String newTrigger = (args.length == 1 ? args[0] : "");
            out.putLocalSetting(getName(), "trigger", newTrigger);
            out.sendTextUser(user, "Trigger => " + newTrigger, loudness);
        }
        else if (command.equalsIgnoreCase("setflagsfor"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly("setflagsfor requires at least one parameter",
                        user, command);

            if (out.getCommandOf(args[0]).equalsIgnoreCase(args[0]) == false)
            {
                out.sendTextUser(user, "Error: that is an alias, not a command.  Try using '"
                        + out.getCommandOf(args[0]) + "' instead.", QUIET);
                return;
            }

            String oldFlags = customFlags.getNoWrite(null, args[0], "<n/a>");

            if (args.length == 1 || args[1].length() == 0)
            {
                customFlags.remove(null, args[0]);
            }
            else
            {
                args[1] = args[1].toUpperCase().replaceAll("\\W", "");
                customFlags.set(null, args[0], args[1]);
            }

            out.sendTextUser(user, "Flags for '" + args[0] + "': " + oldFlags + " => "
                    + customFlags.getNoWrite(null, args[0], "<n/a>"), loudness);
        }
        else if (command.equalsIgnoreCase("getflagsfor"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly("getflagsfor requires at least one parameter",
                        user, command);

            if (out.getCommandOf(args[0]).equalsIgnoreCase(args[0]) == false)
            {
                out.sendTextUser(user, "Error: that is an alias, not a command.  Try using '"
                        + out.getCommandOf(args[0]) + "' instead.", QUIET);
                return;
            }

            out.sendTextUser(user, "Flags for '" + args[0] + "' => "
                    + customFlags.getNoWrite(null, args[0], "<n/a>"), loudness);
        }
    }

    private int getLoudness()
    {
        // JComboBox loud = new JComboBox(new String[] { "quiet", "loud",
        // "loud (no name)", "silent (not recommended)" });
        String loudness = out.getLocalSettingDefault(getName(), "loud", "info");

        if (loudness.equalsIgnoreCase("packet"))
            return ErrorLevelConstants.PACKET;
        if (loudness.equalsIgnoreCase("debug"))
        	return ErrorLevelConstants.DEBUG;
        if (loudness.equalsIgnoreCase("info"))
        	return ErrorLevelConstants.INFO;
        if (loudness.equalsIgnoreCase("notice"))
        	return ErrorLevelConstants.NOTICE;
        if (loudness.equalsIgnoreCase("warning"))
        	return ErrorLevelConstants.WARNING;
        if (loudness.equalsIgnoreCase("error"))
        	return ErrorLevelConstants.ERROR;
        if (loudness.equalsIgnoreCase("critical"))
        	return ErrorLevelConstants.CRITICAL;
        if (loudness.equalsIgnoreCase("alert"))
        	return ErrorLevelConstants.ALERT;
        if (loudness.equalsIgnoreCase("emergency"))
        	return ErrorLevelConstants.EMERGENCY;
        return ErrorLevelConstants.INFO;
    }

}
