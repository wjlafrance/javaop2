package com.javaop.ConnectionCommands;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;


/*
 * Created on Jan 27, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback
{
    private PublicExposedFunctions        out;
    private static StaticExposedFunctions staticFuncs;

    public void load(StaticExposedFunctions staticFuncs)
    {
        PluginMain.staticFuncs = staticFuncs;
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(this, "disconnect", 0, false, "C", "",
                "Disconnects the bot from Battle.net.  Not usually a good "
                + "idea.", null);
        register.registerCommandPlugin(this, "quit", 0, false, "U", "",
                "Closes the bot, along with all instances.", null);
        register.registerCommandPlugin(this, "reconnect", 0, false, "C", "",
                "Reconnects to Battle.net", null);
        register.registerCommandPlugin(this, "close", 0, false, "C", "",
                "Disconnects and cleans up the current instance", null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "ConnectionCommands";
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
        return "Commands for connection-related things";
    }

    public String getLongDescription()
    {
        return "Commands that allow the user to disconnect, reconnect, and quit the bot.";
    }

    public String getComment()
    {
        return "";
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

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("disconnect"))
        {
            out.systemMessage(WARNING, "Disconnecting");
            out.disconnect();
        }
        else if (command.equalsIgnoreCase("reconnect"))
        {
            out.systemMessage(WARNING, "Reconnecting");
            out.reconnect();
        }
        else if (command.equalsIgnoreCase("quit"))
        {
            out.systemMessage(WARNING, "Quitting");
            System.exit(0);
        }
        else if (command.equalsIgnoreCase("close"))
        {
            out.systemMessage(WARNING, "Closing");
            try
            {
                staticFuncs.botStop(out.getName());
            }
            catch (Exception e)
            {
                out.sendTextUser(user, "Unable to kill the instance: " + e, loudness);
            }
        }
    }

}
