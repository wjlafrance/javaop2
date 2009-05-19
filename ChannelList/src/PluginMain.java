import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

/** This plugin simply maintains the list of users in the channel.  It looks after EID_SHOWUSER, EID_JOIN,
 * EID_LEAVE, and EID_CHANNEL.  It also looks after EID_FLAG changes.
 * 
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
        return "Channel list plugin";
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
        return "http://www.javaop.com";
    }

    public String getAuthorEmail()
    {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription()
    {
        return "Maintains the list of users in the channel.";
    }

    public String getLongDescription()
    {
        return "I debated with myself whether to put this functionality in a plugin, or to make it part of the core.  " + 
        	"I decided to make it a plugin, but I HIGHLY recommend not disabling this, because if you do the bot will " + 
        	"have no idea if somebody joins or leaves the channel, the list of users in the channel will always be blank.  " + 
        	"Don't disable this one.";
        	
    }

    public Properties getDefaultSettingValues()
    {
        return new Properties();
    }
    public Properties getSettingsDescription()
    {
        return new Properties();
    }
	public JComponent getComponent(String settingName, String value)
	{
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

    public void talk(String user, String statstring, int ping, int flags)
    {
    }

    public void emote(String user, String statstring, int ping, int flags)
    {
    }

    public void whisperFrom(String user, String statstring, int ping, int flags)
    {
    }

    public void whisperTo(String user, String statstring, int ping, int flags)
    {
    }

    public void userShow(String user, String statstring, int ping, int flags)
    {
        out.channelAddUser(user, flags, ping, statstring);
    }

    public void userJoin(String user, String statstring, int ping, int flags)
    {
        out.channelAddUser(user, flags, ping, statstring);
    }

    public void userLeave(String user, String statstring, int ping, int flags)
    {
        out.channelRemoveUser(user);
    }

    public void userFlags(String user, String statstring, int ping, int flags)
    {
        out.channelAddUser(user, flags, ping, statstring);
    }

    public void error(String user, String statstring, int ping, int flags)
    {
    }

    public void info(String user, String statstring, int ping, int flags)
    {
    }

    public void broadcast(String user, String statstring, int ping, int flags)
    {
    }

    public void channel(String user, String statstring, int ping, int flags)
    {
        out.channelSetName(statstring);
        out.channelClear();
    }
}
