import java.io.IOException;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import util.BNetPacket;
import util.User;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import constants.LoudnessConstants;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

/**
 * @author iago
 *
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
    private PublicExposedFunctions out;
    
    private static long loaded = 0;
    
    private String lastWhisper = "<n/a>";
    
    public void load(StaticExposedFunctions staticFuncs)
    {
        loaded = System.currentTimeMillis();
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        //this.activated = System.currentTimeMillis();
        
        register.registerCommandPlugin(this, "join", 1, false, "J", "<channel>", "Joins the specified channel", null);
        register.registerCommandPlugin(this, "rejoin", 0, false, "J", "", "Rejoins the current channel", null);
        register.registerCommandPlugin(this, "uptime", 0, false, "ALN", "", "Gives the time the bot's been online for", null);
        register.registerCommandPlugin(this, "ping", 1, false, "AN", "<user>", "Pings the specified user", null);
        register.registerCommandPlugin(this, "where", 0, false, "ALN", "", "Tells the user where the bot is", null);
        register.registerCommandPlugin(this, "say", 1, false, "T", "<text>", "Says the specified text out loud", null);
        register.registerCommandPlugin(this, "lastwhisper", 0, false, "N", "", "Shows the last user to give a command", null);
        
        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Basic Commands";
    }

    public String getVersion()
    {
        return "1.0";
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
        return "Basic commands";
    }

    public String getLongDescription()
    {
        return "A collection of basic commands that most bots have.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Instant joins", "true");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Instant joins", "If this is set, joins will happen instantly without waiting for other events to occur.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return new JCheckBox("", value.equalsIgnoreCase("true"));
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

    public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if(command.equalsIgnoreCase("join"))
        {
            if(args.length == 0)
                throw new CommandUsedImproperly("join requires a parameter", user, command);
            
            if(out.getLocalSettingDefault(getName(), "Instant joins", "true").equalsIgnoreCase("true"))
            {
                BNetPacket join = new BNetPacket(SID_JOINCHANNEL);
                join.addDWord(0x02);
                join.addNTString(args[0]);
                out.sendPacket(join);
            }
            else
            {
                out.sendText("/join " + args[0]);
            }
        }
        else if(command.equalsIgnoreCase("rejoin"))
        {
            if(out.getLocalSettingDefault(getName(), "Instant joins", "true").equalsIgnoreCase("true"))
            {
                BNetPacket leave = new BNetPacket(SID_LEAVECHAT);
                out.sendPacket(leave);
                BNetPacket join = new BNetPacket(SID_JOINCHANNEL);
                join.addDWord(0x02);
                join.addNTString(out.channelGetName());
                out.sendPacket(join);
            }
            else
            {
                String channel = out.channelGetName();
                out.sendText("/join some random JavaOp-channel!");
                out.sendText("/join " + channel);
            }
            
        }
        else if(command.equalsIgnoreCase("uptime"))
        {
            out.sendTextUserPriority(user, "This bot has been up for " + TimeReader.timeToString(System.currentTimeMillis() - loaded), loudness, PRIORITY_LOW);
        }
        else if(command.equalsIgnoreCase("ping"))
        {
            if(args.length == 0)
                args = new String [] { user };

            User u = out.channelGetUser(args[0]);
            
            if(u == null)
            {
                if(user != null)
                    out.sendTextPriority("/w " + args[0] + " [ping]" + user, PRIORITY_LOW);
                else
                    out.sendText("/w " + args[0] + " [ping]" + "<local>");
            }
            else
            {
                out.sendTextUserPriority(user, args[0] + "'s ping is " + u.getPing() + "ms", loudness, PRIORITY_LOW);
            }
        }
        else if(command.equalsIgnoreCase("where"))
        {
            out.sendTextUser(user, "I am in channel: " + out.channelGetName(), PRIORITY_LOW);
        }
        else if(command.equalsIgnoreCase("say"))
        {
            if(args.length == 0 || args[0].length() < 1)
                throw new CommandUsedImproperly("Say requires a parameter", user, command);
            
            if(args[0].charAt(0) == '/' && out.dbHasAny(user, "N", true) == false)
            {
                args[0] = " " + args[0];
            }
            
            if(out.dbHasAny(user, "M", true))
                out.sendTextPriority(args[0], PRIORITY_VERY_HIGH + 1);
            else
                out.sendTextPriority(args[0], PRIORITY_LOW);
        }
        else if(command.equalsIgnoreCase("lastwhisper"))
        {
            out.sendTextUserPriority(user, "Last whisper was: " + lastWhisper, QUIET, PRIORITY_LOW);
        }
        else
        {
            out.sendTextUser(user, "There is an error in BasicCommands -- missing a command -- please inform iago", loudness);
        }
    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        this.lastWhisper = "<" + user + "> " + statstring;
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if(statstring.matches("\\[ping\\].*"))
        {
            String username = statstring.replaceAll("\\[ping\\]", "");

            if(user.equalsIgnoreCase("<local>"))
                out.sendTextUserPriority(username, user + "'s ping is " + ping + "ms", LoudnessConstants.SILENT, PRIORITY_LOW);
            else
                out.sendTextUserPriority(username, user + "'s ping is " + ping + "ms", LoudnessConstants.QUIET, PRIORITY_LOW);
        }
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

}
