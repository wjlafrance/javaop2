package com.javaop.Greeting;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.gui.JTextFieldNumeric;


/*
 * Created on Feb 4, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
    private PublicExposedFunctions        out;
    private static StaticExposedFunctions staticFuncs;

    private int                           count = 0;

    private TimerTask                     task  = null;

    public void load(StaticExposedFunctions staticFuncs)
    {
        PluginMain.staticFuncs = staticFuncs;
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerEventPlugin(this, null);

        register.registerCommandPlugin(this, "idle", 1, false, "AN", "[count|time|off]",
                                       "Sets the idle to specified type, or shows current type",
                                       null);
        register.registerCommandPlugin(this, "setidle", 1, false, "AN", "[message]",
                                       "Sets the idle message.  %c = channel, %v = bot version, ",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "idlecount",
                                       1,
                                       false,
                                       "AN",
                                       "[count]",
                                       "Sets the count or time between idles, depending on the idle type",
                                       null);

        register.registerCommandPlugin(this, "greet", 1, false, "AN", "[on|off]",
                                       "Turns greeting on or off, or shows the current status",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "setgreet",
                                       1,
                                       false,
                                       "AN",
                                       "[message]",
                                       "Sets the greet message.  %c = channel, %v = bot version, %n = username, %p = ping",
                                       null);

        if (out.getLocalSettingDefault(getName(), "idle", "off").equalsIgnoreCase("time"))
        {
            enableTimer();
        }
    }

    public void enableTimer()
    {
        disableTimer();
        long time = Long.parseLong(out.getLocalSettingDefault(getName(), "idle time/count", "60")) * 1000;
        out.schedule(task = new IdleCallback(), time);
    }

    public void disableTimer()
    {
        if (task != null)
            out.unschedule(task);
        task = null;
    }

    public void deactivate(PluginCallbackRegister register)
    {

    }

    public String getName()
    {
        return "Greeting";
    }

    public String getVersion()
    {
        return "2.1.3";
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
        return "A plugin for greets and idles";
    }

    public String getLongDescription()
    {
        return "A plugin for annoying things like greet and idles";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("greet", "false");
        p.setProperty("greet message", "Welcome to %c, %n! Your ping is %p.  -- %v");
        p.setProperty("idle", "off");
        p.setProperty("idle message", "I am annoying because I have an idle!");
        p.setProperty("idle time/count", "60");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("greet", "Whether or not to greet users who enter the channel");
        p.setProperty(
                      "greet message",
                      "The message that it whispered to users when they enter the channel.  %c = channel, %n = name, %p = ping, %v = bot version");
        p.setProperty(
                      "idle",
                      "Whether or not to have an idle message.  Count = a message is shown ever 'x' messages in the channel, time = a message is shown every 'x' seconds");
        p.setProperty(
                      "idle message",
                      "The message that is said out loud when the idle time/count elapses.  %c = channel, %n = name, %p = ping, %v = bot version");
        p.setProperty(
                      "idle time/count",
                      "If idle is 'count', the number of messages to talk after, or if idle is 'time', the amount of time (in seconds) to wait.  Bot needs to be restarted to change time idle.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("greet"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        }
        else if (settingName.equalsIgnoreCase("idle"))
        {
            JComboBox combo = new JComboBox(new String[]
            { "off", "count", "time" });
            combo.setSelectedItem(value);
            return combo;
        }
        else if (settingName.equalsIgnoreCase("idle time/count"))
        {
            return new JTextFieldNumeric(value);
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

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
    {
        if (command.equalsIgnoreCase("idle"))
        {
            if (args.length == 0)
            {
                out.sendTextUser(user, "Idle is currently "
                        + out.getLocalSettingDefault(getName(), "idle", "off"), loudness);
            }
            else
            {
                if (args[0].equalsIgnoreCase("off") == false
                        && args[0].equalsIgnoreCase("count") == false
                        && args[0].equalsIgnoreCase("time") == false)
                {
                    throw new CommandUsedImproperlyException("Valid values: off, count, or time", user,
                            command);
                }

                out.putLocalSetting(getName(), "idle", args[0]);
                out.sendText("Idle set to " + args[0]);
            }
        }
        else if (command.equalsIgnoreCase("setidle"))
        {
            if (args.length == 0)
                out.sendTextUser(user, "Message is currently "
                        + out.getLocalSettingDefault(getName(), "idle message",
                                                     "Idle message missing"), loudness);
            else
            {
                out.putLocalSetting(getName(), "idle message", args[0]);
                out.sendTextUser(user, "Idle message set to " + args[0], loudness);
            }
        }
        else if (command.equalsIgnoreCase("idlecount"))
        {
            if (args.length == 0)
                out.sendTextUser(user, "Count is currently "
                        + out.getLocalSetting(getName(), "idle time/count")
                        + " seconds or messages", loudness);
            else
            {
                try
                {
                    out.putLocalSetting(getName(), "idle time/count", ""
                            + Integer.parseInt(args[0]));
                    out.sendTextUser(user, "Idle count set to " + args[0], loudness);
                }
                catch (NumberFormatException e)
                {
                    throw new CommandUsedImproperlyException("Argument to .idlecount must be numeric", user,
                            command);
                }
            }
        }
        else if (command.equalsIgnoreCase("greet"))
        {
            if (args.length == 0)
            {
                out.sendTextUser(
                                 user,
                                 "Greet is "
                                         + (out.getLocalSettingDefault(getName(), "greet", "false").equalsIgnoreCase(
                                                                                                                     "true") ? "on"
                                                 : "off"), loudness);
            }
            else
            {
                if (args[0].equalsIgnoreCase("on") == false
                        && args[0].equalsIgnoreCase("off") == false)
                {
                    throw new CommandUsedImproperlyException("Valid values for greet are \"on\" or \"off\"",
                            user, command);
                }

                out.putLocalSetting(getName(), "greet", args[0].equalsIgnoreCase("on") ? "true"
                        : "false");
                out.sendTextUser(user, "Greet set to " + args[0], loudness);
            }
        }
        else if (command.equalsIgnoreCase("setgreet"))
        {
            if (args.length == 0)
            {
                out.sendTextUser(user, "Greet message is "
                        + out.getLocalSettingDefault(getName(), "greet message", "not set"),
                                 loudness);
            }
            else
            {
                out.putLocalSetting(getName(), "greet message", args[0]);
                out.sendTextUser(user, "Greet message set to " + args[0], loudness);
            }
        }

        String finalIdle = out.getLocalSettingDefault(getName(), "idle", "off");

        disableTimer();

        if (finalIdle.equalsIgnoreCase("time"))
            enableTimer();
    }

    private void doCountIdle(String user, int ping) throws PluginException, IOException
    {
        if (out.getLocalSettingDefault(getName(), "idle", "off").equalsIgnoreCase("count") == false)
            return;

        int countLimit;
        try
        {
            countLimit = Integer.parseInt(out.getLocalSettingDefault(getName(), "idle time/count",
                                                                     "60"));
        }
        catch (NumberFormatException e)
        {
            out.systemMessage(ERROR, getName()
                    + "/\"idle time/count\" is set to an invalid valud -- it must be numeric");
            return;
        }

        count++;

        if (count >= countLimit)
        {
            String message = out.getLocalSettingDefault(getName(), "idle message",
                                                        "Missing idle message");
            out.sendTextPriority(convertString(message, user, ping), PRIORITY_LOW);
            count = 0;
        }
    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        doCountIdle(user, ping);
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        doCountIdle(user, ping);
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (out.getLocalSettingDefault(getName(), "greet", "false").equalsIgnoreCase("true"))
        {
            String greet = out.getLocalSetting(getName(), "greet message");
            out.sendTextUser(user, convertString(greet, user, ping), QUIET);

        }
        else
        {
            doCountIdle(user, ping);
        }
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

    private String convertString(String base, String username, int ping)
    {
        base = base.replaceAll("%c", out.channelGetName());
        base = base.replaceAll("%v", "JavaOp2 " + staticFuncs.getVersion());

        // Username and Ping are only replaced in greet messages.
        if (username != null)
        {
            if (out.getLocalVariable("game").equals("D2DV")
                    || out.getLocalVariable("game").equals("D2XP"))
                base = base.replaceAll("%n", "*" + username);
            else
                base = base.replaceAll("%n", username);

            base = base.replaceAll("%p", ping + "");
        }

        return base;
    }

    private String convertString(String base)
    {
        return convertString(base, null, 0);
    }

    private class IdleCallback extends TimerTask
    {
        public IdleCallback()
        {
        }

        public void run()
        {
            try
            {
                String message = out.getLocalSetting(getName(), "idle message");
                out.sendTextPriority(convertString(message), PRIORITY_VERY_LOW);
            }
            catch (Exception e)
            {
                out.systemMessage(WARNING, "Error in idle callback: " + e);
            }
        }
    }
}