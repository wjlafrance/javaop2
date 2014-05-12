package com.javaop.ChannelProtection;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;


public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{

    private static boolean         locked = false;

    private static boolean         kick   = false;

    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {

    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        register.registerCommandPlugin(
                                       this,
                                       "lock",
                                       1,
                                       true,
                                       "O",
                                       "[kick/ban]",
                                       "Locks the channel so that any user that joins will be kicked.",
                                       null);
        register.registerCommandPlugin(this, "unlock", 0, true, "O", "",
                                       "Removes the effect of lock", null);
        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {

    }

    public String getName()
    {
        return "ChannelProtection";
    }

    public String getVersion()
    {
        return "2.1.3";
    }

    public String getAuthorName()
    {
        return "Ryan Marcus";
    }

    public String getAuthorWebsite()
    {
        return "";
    }

    public String getAuthorEmail()
    {
        return "ryan@marcusfamily.info";
    }

    public String getLongDescription()
    {
        return "This plugin allows you to lock a channel to make it private or to stop people from joining.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
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

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
    {

        if (command.equalsIgnoreCase("lock"))
        {
            if (args.length == 0)
            {
                out.sendTextUser(user, "Lock requires one parameter... Please choose kick or ban.",
                                 loudness);
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
                    out.sendTextUser(
                                     user,
                                     "You have sent an invalid parameter. Valid parameters are kick and ban.",
                                     loudness);
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
