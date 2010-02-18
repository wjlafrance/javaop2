package com.javaop.Forwarding;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;


/*
 * Created on Jun 19, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements EventCallback, CommandCallback
{
    private boolean                forward;
    private boolean                forwardAll;
    private String                 forwardTo;

    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(this, "forwardoff", 0, false, "N", "", "Removes message forwarding.", null);
        register.registerCommandPlugin(this, "forwardwhispers", 1, false, "N", "[user]", "Forwards whispers in the channel to the specified user.  If none is specified, forwards to the user who used the command.", null);
        register.registerCommandPlugin(this, "forwardall", 1, false, "N", "[user]", "Forwards all information from the channel to the specifed user.  If none is specified, forwards to the user who used the command.", null);

        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Forwarding";
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

    public String getLongDescription()
    {
        return "Can forward all messages or whispered messages to the specified user.  This is done at run-time, if the plugin is reloaded the forwarding is disabled.";
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

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, user + ": " + statstring, QUIET, PRIORITY_LOW);
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "<" + user + " " + statstring + ">", QUIET, PRIORITY_LOW);
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward)
            out.sendTextUserPriority(forwardTo, "From " + user + ": " + statstring, QUIET, PRIORITY_LOW);
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Joined: " + user, QUIET, PRIORITY_LOW);
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Left: " + user, QUIET, PRIORITY_LOW);
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Error: " + statstring, QUIET, PRIORITY_LOW);
    }

    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Info: " + statstring, QUIET, PRIORITY_LOW);
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Broadcast: " + statstring, QUIET, PRIORITY_LOW);
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        if (forward && forwardAll)
            out.sendTextUserPriority(forwardTo, "Channel: " + statstring, QUIET, PRIORITY_LOW);
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("forwardoff"))
        {
            forward = false;
        }
        else if (command.equalsIgnoreCase("forwardwhispers"))
        {
            forward = true;
            forwardAll = false;
            forwardTo = args.length > 0 ? args[0] : user;

            out.sendTextUser(user, "Forwarding whispers to " + forwardTo, loudness);
        }
        else if (command.equalsIgnoreCase("forwardall"))
        {
            forward = true;
            forwardAll = true;
            forwardTo = args.length > 0 ? args[0] : user;

            out.sendTextUser(user, "Forwarding all messages to " + forwardTo, loudness);
        }
        else
        {
            out.sendTextUser(user, "Error: unknown command in Fowarding.  Please report on forums.", QUIET);
        }
    }
}
