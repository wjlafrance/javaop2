package com.javaop.UserInformation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.PersistantMap;
import com.javaop.util.RelativeFile;


/*
 * Created on Jun 19, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback,
        ActionListener
{
    private PersistantMap          allSeen;
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(this, "seen", 1, false, "ALN", "<username>",
                                       "Shows whether the bot has seen the specified user", null);
        register.registerCommandPlugin(this, "lastseen", 1, false, "ALN", "<username>",
                                       "Shows the last time the specified user was seen", null);
        register.registerCommandPlugin(this, "firstseen", 1, false, "ALN", "<username>",
                                       "Shows the first time the specified user was seen", null);
        allSeen = new PersistantMap(new RelativeFile(out.getName() + ".seen"),
                "This is the last-seen database.  It keeps track of the last time a person was seen.");

        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "UserInformation";
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

    public String getLongDescription()
    {
        return "This plugin stores and displays long-term type information about users such as when they were last seen and how often they are seen.";
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

    private void seen(String user)
    {
        String lastSeen = allSeen.getNoWrite(user, "Last seen", "");

        if (lastSeen.length() == 0)
        {
            // They've never been seen before, set their firstSeen value
            allSeen.set(user, "First seen", new Date().toString());
        }
        allSeen.set(user, "Last seen", new Date().toString());
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
    {
        if (args.length == 0)
            throw new CommandUsedImproperlyException("The 'seen' commands require two parameters", user,
                    command);

        String check = args[0];

        if (command.equalsIgnoreCase("seen"))
        {
            if (allSeen.contains(check, "First seen"))
                out.sendTextUser(user, "User '" + check + "' has been seen", loudness);
            else
                out.sendTextUser(user, "User '" + check + "' has not been seen", loudness);

        }
        else if (command.equalsIgnoreCase("lastseen"))
        {
            out.sendTextUser(user, "User '" + check + "' was last seen: "
                    + allSeen.getNoWrite(check, "Last seen", "Never"), loudness);
        }
        else if (command.equalsIgnoreCase("firstseen"))
        {
            out.sendTextUser(user, "User '" + check + "' was first seen: "
                    + allSeen.getNoWrite(check, "First seen", "Never"), loudness);
        }
        else
        {
            out.sendTextUser(
                             user,
                             "Error: unknown command used in UserInformation.  Please contact iago.",
                             QUIET);
        }
    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        seen(user);
    }

    public void actionPerformed(ActionEvent arg0)
    {
        JOptionPane.showMessageDialog(null, "Item = " + arg0.getSource() + "; user = "
                + arg0.getActionCommand());
    }

}
