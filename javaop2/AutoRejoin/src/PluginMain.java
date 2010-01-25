import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.PluginException;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import util.BnetPacket;
import util.gui.JTextFieldNumeric;


/*
 * Created on Jan 29, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements EventCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Auto-Rejoin";
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
        return "Auto-rejoin plugin";
    }

    public String getLongDescription()
    {
        return "This plugin will automatically rejoin the channel when kicked, or join a custom channel on ban";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Rejoin delay", "3000");
        p.setProperty("Channel to join if banned", "op x86");

        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty(
                      "Rejoin delay",
                      "The amount of time, in milliseconds, to wait before attempting to rejoin a channel.  This prevents Fast Rejoin bans.");
        p.setProperty("Channel to join if banned", "The channel the bot will go to when banned.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("Rejoin delay"))
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

    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        // [16:47:55.030] iago-test-2 was banned by iago-test-1.
        // [16:48:26.457] iago-test-2 was kicked out of the channel by
        // iago-test-1.

        // [1:06:53 PM] stfu.ngr.plz was kicked out of the channel by x86
        // (<3333).
        if (statstring.matches("[^ ]* was kicked out of the channel by [^ ]+.*\\."))
        {
            String kicker = statstring.replaceAll("[^ ]* was kicked out of the channel by ", "");
            kicker = kicker.replaceAll(" \\(.*\\)", "");
            kicker = kicker.replaceAll("\\.$", "");

            String kicked = statstring.replaceAll(" was kicked out of the channel by .*\\.", "");

            String me = (String) out.getLocalVariable("username");

            if (me.indexOf('@') > 0)
                me = me.replaceAll("@.*?", "");

            if (me.equalsIgnoreCase(kicked))
            {
                out.systemMessage(DEBUG, "I was kicked by " + kicker + "!");

                long delay = Long.parseLong(out.getLocalSetting(getName(), "Rejoin delay"));
                Timer t = new Timer();

                final String channel = out.channelGetName();
                out.systemMessage(DEBUG, "Rejoining channel " + channel + " in " + delay + "ms");

                t.schedule(new TimerTask()
                {
                    public void run()
                    {
                        try
                        {
                            out.systemMessage(DEBUG, "Rejoining " + channel);
                            BnetPacket kick = new BnetPacket(SID_JOINCHANNEL);
                            kick.addDWord(0x02);
                            kick.addNTString(channel);
                            out.sendPacket(kick);
                        }
                        catch (Exception e)
                        {
                            out.systemMessage(ERROR, "Unable to rejoin channel: " + e);
                        }
                    }
                }, delay);
            }
            else
            {
                out.systemMessage(DEBUG, kicked + " was kicked by " + kicker);
            }

        }
        else if (statstring.matches("[^ ]* was banned by [^ ]+.*\\."))
        {
            // String kicker =
            // statstring.replaceAll("[^ ]* was kicked out of the channel by ",
            // "");
            // kicker = kicker.replaceAll(" \\(.*\\)", "");
            // kicker = kicker.replaceAll("\\.$", "");
            //            
            // String kicked =
            // statstring.replaceAll(" was kicked out of the channel by .*\\.",
            // "");

            String banner = statstring.replaceAll("[^ ]* was banned by ", "");
            banner = banner.replaceAll(" \\(.*\\)", "");
            banner = banner.replaceAll("\\.$", "");
            String banned = statstring.replaceAll(" was banned by [^ ]+.*\\.", "");

            String me = (String) out.getLocalVariable("username");

            if (me.indexOf('@') > 0)
                me = me.replaceAll("@.*?", "");

            if (me.equalsIgnoreCase(banned))
            {
                out.systemMessage(DEBUG, "I was banned by " + banner + "!");

                BnetPacket ban = new BnetPacket(SID_JOINCHANNEL);
                ban.addDWord(0x02);
                ban.addNTString(out.getLocalSetting(getName(), "Channel to join if banned"));
                out.sendPacket(ban);
            }
            else
            {
                out.systemMessage(DEBUG, banned + " was banned by " + banner);
            }
        }

    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

}
