import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import util.gui.JTextFieldNumeric;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.PluginException;

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
        
        if(out.getLocalSettingDefault(getName(), "Periodically check the current channel", "false").equalsIgnoreCase("true"))
        {
            int delay = 1000 * Integer.parseInt(out.getLocalSettingDefault(getName(), "Periodical check delay", "120"));
            out.schedule(new TimerCallback(), delay);
        }
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Show invisible users";
    }

    public String getVersion()
    {
        return "1.1";
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
        return "Finds/displays invisible users";
    }

    public String getLongDescription()
    {
        //return "Sends a /unsquelch at itself when it detects it is entering an invisible channel; also, it's possible to set " + 
        //	"the plugin to periodically do it in normal channels, to search for invisible users; however, this doesn't really " + 
        //	"work anymore (although you never know)!";
    	return "Sends a /unsquelch at the bot itself when joining a channel to detect invisible users by forcing status updates.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Check channels", "true");
        p.setProperty("Periodically check the current channel", "false");
        p.setProperty("Periodical check delay", "120");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Check channels", "Whenever you enter a new channel, it will look for invisible users");
        p.setProperty("Periodically check the current channel", "Occasionally checks the current channel for invisible users.  As far as I know, this doesn't work anymore, but it doesn't hurt.  Must RESTART bot to change this. (NOTE: This can get REALLY annoying)");
        p.setProperty("Periodical check delay", "The time between checking the channel for invisible users.  Must RESTART bot to change this.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if(settingName.equalsIgnoreCase("Check channels") || settingName.equalsIgnoreCase("Periodically check the current channel"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true") ? true : false);
        }
        else if(settingName.equalsIgnoreCase("Periodical check delay"))
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
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if(out.getLocalSetting(getName(), "check channels").equalsIgnoreCase("true"))
            if(out.getLocalVariable("game").equals("D2DV") || out.getLocalVariable("game").equals("D2XP"))
            	out.sendTextPriority("/unsquelch *" + out.getLocalVariable("username"), PRIORITY_LOW);
            else
            	out.sendTextPriority("/unsquelch " + out.getLocalVariable("username"), PRIORITY_LOW);
    }

    private class TimerCallback extends TimerTask
    {
        public void run()
        {
            try
            {
                out.systemMessage(DEBUG, "Checking for invisible users");
                if(out.getLocalVariable("game").equals("D2DV") || out.getLocalVariable("game").equals("D2XP"))
                	out.sendTextPriority("/unsquelch *" + out.getLocalVariable("username"), PRIORITY_LOW);
                else
                	out.sendTextPriority("/unsquelch " + out.getLocalVariable("username"), PRIORITY_LOW);
            }
            catch(Exception e)
            {
                // *shrug*
            }
        }
    }
}
