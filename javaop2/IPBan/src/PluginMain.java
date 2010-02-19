package com.javaop.IPBan;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;

import com.javaop.constants.Flags;
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
 * Created on Mar 1, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerEventPlugin(this, null);
        register.registerCommandPlugin(
                                       this,
                                       "ipban",
                                       0,
                                       true,
                                       "ON",
                                       "<username>",
                                       "Squelches the specified user, which causes an ipban on them",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "unipban",
                                       0,
                                       true,
                                       "ON",
                                       "<username>",
                                       "Unsquelches the specified user, which causes an ipban to be removed",
                                       null);

    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "IPBan";
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
        return "Lets you ipban users";
    }

    public String getLongDescription()
    {
        return "When a user is .ipban'd, he is banned and squelched.  If a user who is squelched enters the channel, he will automatically "
                + "be banned.  I wouldn't recommend using this plugin for a chat bot, because the attempts to ban people might get annoying.";
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
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("ipban"))
        {
            if (args.length != 1)
                throw new CommandUsedImproperly(".ipban takes one parameter", user, command);

            String[] users = out.channelMatchGetListWithoutAny(args[0], "SFM");

            for (int i = 0; i < users.length; i++)
                out.sendTextPriority("/squelch " + users[i], PRIORITY_HIGH);
        }
        else if (command.equalsIgnoreCase("unipban"))
        {
            if (args.length != 1)
                throw new CommandUsedImproperly(".unipban takes one parameter", user, command);

            out.sendTextPriority("/unsquelch " + args[0], PRIORITY_HIGH);
        }
        else
        {
            out.sendTextUserPriority(
                                     user,
                                     "Error in IPBan plugin: unknown command. Please report to iago.",
                                     QUIET, PRIORITY_HIGH);
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
        ipban(user, flags);
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        ipban(user, flags);
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        ipban(user, flags);
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

    private void ipban(String user, int flags) throws IOException
    {
        if ((flags & Flags.USER_SQUELCHED) > 0)
        {
            if (out.dbHasAny(user, "SF", true) == false)
            {
                out.sendTextPriority("/ban " + user + " IPBan", PRIORITY_HIGH);
            }
            else if (out.dbHasAny(user, "S", true))
            {
                out.sendText("/unsquelch " + user);
            }
        }
    }

}
