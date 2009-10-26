import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.PluginException;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.OutgoingTextCallback;
import util.TimeReader;
import util.gui.JTextFieldNumeric;


/*
 * Created on Apr 23, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements OutgoingTextCallback,
        EventCallback
{
    private PublicExposedFunctions out;
    private long                   lastMessage = System.currentTimeMillis();
    private boolean                away        = false;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerOutgoingTextPlugin(this, null);
        register.registerEventPlugin(this, null);

        out.schedule(new Callback(),
                     1000 * Integer.parseInt(out.getLocalSettingDefault(getName(), "Update idle",
                                                                        "60")));
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Away Message";
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

    public String getLongDescription()
    {
        return "Sets an away message that tells people how long you've been away from your bot.  This is reset by, by default, any outgoing text that doesn't begin with /, so if somebody uses .say or a similar command the idle turns off.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Start showing idle", "180");
        p.setProperty("Update idle", "60");
        p.setProperty("Show only minutes", "true");
        p.setProperty("/commands end idle", "false");
        p.setProperty("Away message", "Bot has been idle for %t");

        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Start showing idle",
                      "This is the time (in seconds) before the first idle message is set.");
        p.setProperty(
                      "Update idle",
                      "This is the amount of time that the idle time is updated.  If you're displaying seconds, an odd number like 17 or 29 is useful.  WARNING: don't put this so low that it backs up the flood protection! [Must reload bot to take effect]");
        p.setProperty(
                      "Show only minutes",
                      "If this is turned off, it'll show seconds.  This is usually pretty redundant, but some people like it.");
        p.setProperty("/commands end idle",
                      "If this is enabled, then a /command (like /whereis iago) won't reset the idle.");
        p.setProperty(
                      "Away message",
                      "The away message.  %t is replaced with the elapsed time.  You can have more than one %t if you really want.  %t also isn't required if you just want a standard away message.");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("/commands end idle")
                || settingName.equalsIgnoreCase("Show only minutes"))
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        else if (settingName.equalsIgnoreCase("Start showing idle")
                || settingName.equalsIgnoreCase("update idle"))
            return new JTextFieldNumeric(value);

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

    private class Callback extends TimerTask
    {
        public void run()
        {
            try
            {
                long elapsed = System.currentTimeMillis() - lastMessage;

                if ((elapsed / 1000) > Integer.parseInt(out.getLocalSettingDefault(
                                                                                   getName(),
                                                                                   "Start showing idle",
                                                                                   "180")))
                {
                    String message = out.getLocalSettingDefault(getName(), "Away message",
                                                                "Bot has been idle for %t");

                    if (out.getLocalSettingDefault(getName(), "Show only minutes", "true").equalsIgnoreCase(
                                                                                                            "true"))
                    {
                        elapsed = elapsed / 1000;
                        elapsed = elapsed / 60;
                        elapsed++;

                        if (elapsed > 60)
                        {
                            long hours = elapsed / 60;
                            long minutes = elapsed % 60;
                            message = message.replaceAll("%t", hours + " hour"
                                    + (hours == 1 ? "" : "s") + ", " + minutes + " minute"
                                    + (minutes == 1 ? "" : "s"));
                        }
                        else
                        {
                            message = message.replaceAll("%t", elapsed + " minute"
                                    + (elapsed == 1 ? "" : "s"));
                        }
                    }
                    else
                    {
                        message = message.replaceAll("%t", TimeReader.timeToString(elapsed));
                    }

                    out.sendText("/away " + message);
                }
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }
    }

    public String queuingText(String text, Object data)
    {
        return text;
    }

    public void queuedText(String text, Object data)
    {
    }

    public String nextInLine(String text, Object data)
    {
        return text;
    }

    public long getDelay(String text, Object data)
    {
        return 0;
    }

    public boolean sendingText(String text, Object data)
    {
        return true;
    }

    public void sentText(String text, Object data)
    {
        try
        {
            if (out.getLocalSettingDefault(getName(), "/commands end idle", "false").equalsIgnoreCase(
                                                                                                      "false")
                    && text.matches("/.*"))
                return;

            if (text.matches("/away.*") == false)
            {
                lastMessage = System.currentTimeMillis();
                if (away)
                    out.sendText("/away");
            }
        }
        catch (IOException e)
        {
        }
    }

    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        // [21:29:46.534] You are now marked as being away.
        if (statstring.equalsIgnoreCase("You are now marked as being away."))
            away = true;

        // [21:29:49.091] You are still marked as being away.
        // [21:29:50.299] You are no longer marked as away.
        if (statstring.equalsIgnoreCase("You are no longer marked as away."))
            away = false;

    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
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

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }
}
