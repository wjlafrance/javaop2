package com.javaop.BotMail;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JCheckBox;
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
 * Created on Apr 14, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
    private PublicExposedFunctions out;
    private Mail                   mail;
    private boolean                shareMail;

    public void load(StaticExposedFunctions staticFuncs)
    {
        shareMail = staticFuncs.getGlobalSettingDefault(getName(), "Share mail", "false").equalsIgnoreCase(
                                                                                                           "true");
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        if (shareMail)
            mail = new Mail(out.getName());
        else
            mail = new Mail("_GlobalMail");

        register.registerCommandPlugin(this, "getmail", 0, false, "L", "[number list]",
                                       "Retrieves the specified message or all messages", null);
        register.registerCommandPlugin(this, "mail", 2, false, "ALN", "<user> <message>",
                                       "Sends a message to the specified user", null);
        register.registerCommandPlugin(this, "removemail", 0, false, "L", "[number list]",
                                       "Removes the specified message", null);

        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "BotMail";
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
        return "Bot mail";
    }

    public String getLongDescription()
    {
        return "This plugin is for sending/receiving botmail";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Delete on read", "true");
        p.setProperty("Alert on message", "true");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Delete on read", "Automatically remove messages when they are read");
        p.setProperty("Alert on message",
                      "Alert users if they have mail when they enter the channel");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return new JCheckBox("", value.equalsIgnoreCase("true"));
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Share mail", "false");
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Share mail",
                      "If this is set, all bots share the same database for mail messages");
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return new JCheckBox("", value.equalsIgnoreCase("true"));
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws IOException, PluginException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("getmail"))
        {
            if (args.length == 0)
            {
                String[] messages = mail.getMessages(user);

                if (messages.length == 0)
                {
                    out.sendTextUser(user, "You have no mail.", QUIET);
                }
                else
                {
                    for (int i = 0; i < messages.length; i++)
                    {
                        out.sendTextUser(user, mail.getFullMessage(user, messages[i]), QUIET);

                        if (out.getLocalSetting(getName(), "Delete on read").equalsIgnoreCase(
                                                                                              "true"))
                            mail.remove(user, messages[i]);
                    }
                }
            }
            else
            {
                for (int i = 0; i < args.length; i++)
                {
                    if (mail.exists(user, args[i]))
                    {
                        out.sendTextUser(user, mail.getFullMessage(user, args[i]), QUIET);
                        if (out.getLocalSetting(getName(), "Delete on read").equalsIgnoreCase(
                                                                                              "true"))
                            mail.remove(user, args[i]);
                    }
                    else
                    {
                        out.sendTextUser(user, "No such message: " + args[i], QUIET);
                    }
                }
            }
        }
        else if (command.equalsIgnoreCase("mail"))
        {
            if (args.length != 2)
                throw new CommandUsedImproperly("mail requires exactly 2 parameters", user, command);

            mail.add(args[0], user, args[1]);

            out.sendTextUser(user, "Message queued to be sent", QUIET);
        }
        else if (command.equalsIgnoreCase("removemail"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly(
                        "removemail requires the list of messages to remove", user, command);

            for (int i = 0; i < args.length; i++)
            {
                if (mail.exists(user, args[i]))
                {
                    mail.remove(user, args[i]);
                    out.sendTextUser(user, "Removed: " + args[i], QUIET);
                }
            }
        }
        else
        {
            out.sendTextUser(user, "Error: unknown command used in BotMail -- please inform iago",
                             QUIET);
        }
    }

    private void checkMail(String user) throws IOException
    {
        if (out.getLocalSetting(getName(), "Alert on message").equalsIgnoreCase("true"))
        {
            int count = mail.getCount(user);

            if (count > 0)
                out.sendTextUser(user, "You have " + count
                        + " mail messages waiting; use getmail command to retrieve", QUIET);
        }
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        checkMail(user);
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
        checkMail(user);
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
