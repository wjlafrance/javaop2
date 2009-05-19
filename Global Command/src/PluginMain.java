import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.GenericPluginInterface;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

public class PluginMain extends GenericPluginInterface implements CommandCallback
{

    private PublicExposedFunctions out;

    private static StaticExposedFunctions sef;

    String arrayToString(String[] ary, String sep)
    {
        StringBuffer result = new StringBuffer("");
        if (ary.length > 0)
        {
            result.append(ary[0]);
            for (int i = 1; i < ary.length; i++)
            {
                result.append(sep);
                result.append(ary[i]);
            }
        }
        return result.toString();
    }

    public void load(StaticExposedFunctions staticFuncs)
    {

        sef = staticFuncs;
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        // TODO Auto-generated method stub
        this.out = out;

        register.registerCommandPlugin(this, "gconnect", 1, false, "U", "[bot name]", "Connects a bot based on its name.", null);
        register.registerCommandPlugin(this, "botlist", 0, false, "U", "", "Returns a list of all running bots.", null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
        // TODO Auto-generated method stub

    }

    public String getName()
    {
        // TODO Auto-generated method stub
        return "Global Connect";
    }

    public String getVersion()
    {
        // TODO Auto-generated method stub
        return "1.0";
    }

    public String getAuthorName()
    {
        // TODO Auto-generated method stub
        return "Ryan Marcus";
    }

    public String getAuthorWebsite()
    {
        // TODO Auto-generated method stub
        return "N/A";
    }

    public String getAuthorEmail()
    {
        // TODO Auto-generated method stub
        return "ryan@marcusfamily.info";
    }

    public String getLongDescription()
    {
        // TODO Auto-generated method stub
        return "This plugin allows you to disconnect and connect bots other then the one currently connected.";
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
        if (command.equalsIgnoreCase("gconnect"))
        {

            sef.botGet(args[0]).connect();
            out.sendText("Connecting bot.");

        }

        if (command.equalsIgnoreCase("botlist"))
        {
            String msg[] = sef.botGetActiveNames();
            String fmsg = arrayToString(msg, ",");
            out.sendText(fmsg);
        }

    }

}
