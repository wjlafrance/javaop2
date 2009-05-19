import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import constants.Flags;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
    private PublicExposedFunctions out;
    public void load(StaticExposedFunctions staticFuncs)
    {

    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        
        register.registerCommandPlugin(this, "designate", 0, true, "D", "[user]", "Designates the requested user.  If no user is speciied, it designates the person that used the command.", null);
        register.registerCommandPlugin(this, "op", 0, true, "D", "[user]", "Designates the requested user, then rejoins the channel.  If no user is specified, it designates the user that used the command.", null);
        
        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Designate";
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
        return "A plugin for designating users";
    }

    public String getLongDescription()
    {
        return "This plugin has commands for designating or op'ing users.  It also has auto-designate for users with the proper flags.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("autodesignate flag", "E");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("autodesignate flag", "The flag(s) which, when users have it/them, will automatically give the user ops upon entering the channel.");
        return p;
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
    
    

    private void designate(String user) throws IOException
    {
        out.sendTextPriority("/designate " + user, PRIORITY_HIGH);
    }        
    private void op(String user) throws IOException
    {
        designate(user);
        out.sendTextPriority("/resign", PRIORITY_HIGH);
    }

    public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if(command.equalsIgnoreCase("designate"))
        {
            if(args.length == 0)
                designate(user);
            else
                designate(args[0]);
        }
        else if(command.equalsIgnoreCase("op"))
        {
            if(args.length == 0)
                op(user);
            else
                op(args[0]);
        }
        else
        {
            out.sendTextUser(user, "There was an error in designate plugin -- command not found.  Please report to iago.", loudness);
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
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if((flags & Flags.USER_CHANNELOP) == 0)
        {
            String desFlags = out.getLocalSettingDefault(getName(), "autodesignate flag", "E");
            if(out.dbHasAny(user, desFlags, false))
                op(user);
        }
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if((flags & Flags.USER_CHANNELOP) == 0)
        {
            String desFlags = out.getLocalSettingDefault(getName(), "autodesignate flag", "E");
            if(out.dbHasAny(user, desFlags, false))
                op(user);
        }
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if((flags & Flags.USER_CHANNELOP) == 0)
        {
            String desFlags = out.getLocalSettingDefault(getName(), "autodesignate flag", "E");
            if(out.dbHasAny(user, desFlags, false))
                op(user);
        }
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
