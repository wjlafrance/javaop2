
import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{

    private static boolean locked = false;

    private static boolean kick = false;

    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
        // TODO Auto-generated method stub

    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        // TODO Auto-generated method stub
        this.out = out;
        register.registerCommandPlugin(this, "lock", 1, true, "O", "[kick/ban]", "Locks the channel so that any user that joins will be kicked.", null);
        register.registerCommandPlugin(this, "unlock", 0, true, "O", "", "Removes the effect of lock", null);
        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
        // TODO Auto-generated method stub

    }

    public String getName()
    {
        // TODO Auto-generated method stub
        return "Channel Lock";
    }

    public String getVersion()
    {
        // TODO Auto-generated method stub
        return "1.1";
    }

    public String getAuthorName()
    {
        // TODO Auto-generated method stub
        return "Ryan Marcus";
    }

    public String getAuthorWebsite()
    {
        // TODO Auto-generated method stub
        return "";
    }

    public String getAuthorEmail()
    {
        // TODO Auto-generated method stub
        return "ryan@marcusfamily.info";
    }

    public String getLongDescription()
    {
        // TODO Auto-generated method stub
        return "This plugin allows you to lock a channel to make it private or to stop people from joining.";
    }

    public Properties getDefaultSettingValues()
    {
        // TODO Auto-generated method stub
        Properties p = new Properties();
        return p;
    }

    public Properties getSettingsDescription()
    {
        // TODO Auto-generated method stub
        Properties p = new Properties();
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Properties getGlobalDefaultSettingValues()
    {
        // TODO Auto-generated method stub
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        // TODO Auto-generated method stub
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {

        if (command.equalsIgnoreCase("lock"))
        {
            if (args.length == 0)
            {
                out.sendTextUser(user, "Lock requires one parameter... Please choose kick or ban.", loudness);
            }
            else
            {

                if (args[0].equalsIgnoreCase("kick"))
                {

                    PluginMain.locked = true;
                    PluginMain.kick = true;
                    out.sendTextUser(user, "Channel Locked", loudness);

                }
                else if (args[0].equalsIgnoreCase("ban"))
                {

                    PluginMain.locked = true;
                    PluginMain.kick = false;
                    out.sendTextUser(user, "Channel Locked", loudness);

                }
                else
                {
                    out.sendTextUser(user, "You have sent an invalid parameter. Valid parameters are kick and ban.", loudness);
                }

            }
        }

        if (command.equalsIgnoreCase("unlock"))
        {
            out.sendTextUser(user, "Channel Unlocked", loudness);
            PluginMain.locked = false;
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
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {

        if (PluginMain.locked)
        {
            if (PluginMain.kick)
            {
                out.sendText("/kick " + user);
            }
            else
            {
                out.sendText("/ban " + user);
            }
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

}
