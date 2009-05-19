import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.UserDatabaseCallback;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

/**
 * @author iago
 *
 */
public class PluginMain extends GenericPluginInterface implements EventCallback, UserDatabaseCallback
{
    private PublicExposedFunctions out;
    
    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        register.registerEventPlugin(this, null);
        register.registerUserDatabasePlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }


    public String getName()
    {
        return "Autoban Plugin";
    }

    public String getVersion()
    {
        return "v1.0";
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
        return "Does the autoban";
    }

    public String getLongDescription()
    {
        return "Bans users who enter the channel who B but not S, or that have Z.  It also autobans users when their flags are " + 
        	"changed to include B or Z.";
    }

    public Properties getSettingsDescription()
    {
        return new Properties();
    }

    public Properties getDefaultSettingValues()
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

    public void userShow(String user, String statstring, int ping, int flags) throws IOException
    {
        tryAutoban(user);
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException
    {
        tryAutoban(user);
    }

    public void userLeave(String user, String statstring, int ping, int flags)
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException
    {
        tryAutoban(user);
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
    }
    
    private void tryAutoban(String user) throws IOException
    {
        // Never touch a M user
        if(out.dbHasAny(user, "M", true))
            return;
        
        if(out.dbHasAny(user, "Z", false))
        {
            out.sendTextPriority("/ban " + user + " Autoban (Z)", PRIORITY_VERY_HIGH);
            return;
        }
        
        if(out.dbHasAny(user, "B", false))
        {
            if(out.dbHasAny(user, "S", false) == false)
            {
                out.sendTextPriority("/ban " + user + " Autoban (B)", PRIORITY_HIGH);
                return;
            }
        }
    }
    
    private void autobanChannel() throws IOException
    {
        String []users = out.channelGetListWithAny("BZ");

        for(int i = 0; i < users.length; i++)
            tryAutoban(users[i]);
    }

    public void userAdded(String username, String flags, Object data) 
    {
        try
        {
            autobanChannel();
        }
        catch(IOException e)
        {
        }
    }

    public void userChanged(String username, String oldFlags, String newFlags, Object data)
    {
        try
        {
            autobanChannel();
        }
        catch(IOException e)
        {
        }
    }

    public void userRemoved(String username, String oldFlags, Object data)
    {
        try
        {
            autobanChannel();
        }
        catch(IOException e)
        {
        }
    }

}
