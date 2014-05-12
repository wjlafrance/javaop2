package com.javaop.Filters;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;

import java.io.IOException;
import java.util.Properties;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.RawEventCallback;
import com.javaop.util.BnetEvent;
import com.javaop.util.UsernameMatcherPattern;


/*
 * Created on Feb 24, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements RawEventCallback, CommandCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerRawEventPlugin(this, 0, MAX_EVENT, null);

        register.registerCommandPlugin(this, "filterword", 1, false, "N", "<word or phrase>",
                                       "Filters the specified word or phrase", null);
        register.registerCommandPlugin(
                                       this,
                                       "filter",
                                       1,
                                       false,
                                       "N",
                                       "<pattern>",
                                       "Filters messages containing the specified pattern (you may use * and ?)",
                                       null);

        register.registerCommandPlugin(this, "unfilterword", 1, false, "N", "<word or phrase>",
                                       "Removed the filtered word or phrase", null);
        register.registerCommandPlugin(this, "unfilter", 1, false, "N", "<pattern>",
                                       "Removes the pattern (you may use * and ?)", null);

        register.registerCommandPlugin(this, "filters", 0, false, "N", "", "Lists the filters",
                                       null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Filter";
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
        return "Filters stuff";
    }

    public String getLongDescription()
    {
        return "Filters messages from users who have certain flags, or if a message matches a certain pattern";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("ignore flag", "i");

        p.setProperty("Phrase filters", "true");
        p.setProperty("Phrase filter replacements", "&@$%&#$@%=masturbat\n" + "@$%!@%&=asshole\n"
                + "@$%!@!&=asswipe\n" + "%&$#@#!=lesbian\n" + "!@!@!@=vagina\n" + "!@!@!%=faggot\n"
                + "!@!@&#=nigger\n" + "!@!@%&=nipple\n" + "!#!@$&=orgasm\n" + "!@!#&=whore\n"
                + "&#&$%=erect\n" + "!&$%@=pussy\n" + "#@%$!=bitch\n" + "$!@!$=chink\n"
                + "%@%&!=dildo\n" + "!@!@#=nigga\n" + "!&!@$=penis\n" + "$%@%=clit\n"
                + "$!$%=cock\n" + "$&!%=cunt\n" + "%@$%=dick\n" + "!&$%=fuck\n" + "!@!$=gook\n"
                + "$@$&=kike\n" + "$%&!=klux\n" + "$!@%=shit\n" + "$%&%=slut\n" + "$%$=kkk\n");

        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("ignore flag", "Any users with this flag will be completely ignored");
        p.setProperty("Phrase filters",
                      "Replace the text for the phrase filters with the text from the list");
        p.setProperty(
                      "Phrase filter replacements",
                      "These are one/line, in the form ToBeReplaced=ToReplaceWith.  Remember, these can be anything you want, not just uncensor.  You can also use wildcards (* and ?).");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("Phrase filter replacements"))
            return new JTextArea(value);
        else if (settingName.equalsIgnoreCase("Phrase filters"))
            return new JCheckBox("", value.equalsIgnoreCase("true"));

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

    public BnetEvent eventOccurring(BnetEvent event, Object data) throws PluginException
    {
        // Filter out any "ignored" user
        if (out.dbHasAny(event.getUsername(), out.getLocalSettingDefault(getName(), "ignore flag",
                                                                         "i"), false))
        {
            out.systemMessage(DEBUG, "Cancelled message: user has ignore flag");
            return null;
        }

        String message = event.getMessage();

        // Don't bother if there is no message (like in an EID_LEAVE)
        if (message.length() == 0)
            return event;

        // The event didn't get killed by a filter, let's see if we can do any
        // replacements here:
        if (out.getLocalSettingDefault(getName(), "Phrase filters", "true").equalsIgnoreCase("true"))
        {
            String[] filters = out.getLocalSetting(getName(), "Phrase filter replacements").split(
                                                                                                  "\n");
            for (int i = 0; i < filters.length; i++)
            {
                String[] nameValue = filters[i].split("=", 2);

                if (nameValue.length != 2)
                    out.systemMessage(ERROR, "Invalid line in filters: '" + filters[i] + "'");
                else
                    message = message.replaceAll(UsernameMatcherPattern.fixPattern(nameValue[0]), nameValue[1]);

                // If the message was cleared, return
                if (message.length() == 0)
                    return null;
            }
        }

        return new BnetEvent(event.getCode(), event.getUsername(), message, event.getPing(),
                event.getFlags());
    }

    public void eventOccurred(BnetEvent event, Object data) throws PluginException
    {
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
    {
        if (command.equalsIgnoreCase("filterword") || command.equalsIgnoreCase("filter"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperlyException("filter and filterword requires an argument", user,
                        command);

            if (command.equalsIgnoreCase("filterword"))
                args[0] = "*" + args[0] + "*";

            out.putLocalSetting(getName() + "-filters", args[0], "");
            out.sendTextUser(user, "Added filter for phrase: " + args[0], loudness);
        }
        else if (command.equalsIgnoreCase("unfilterword") || command.equalsIgnoreCase("unfilter"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperlyException("unfilter and unfilterword require an argument",
                        user, command);

            if (command.equalsIgnoreCase("unfilterword"))
                args[0] = "*" + args[0] + "*";

            if (out.getLocalSetting(getName() + "-filters", args[0]) == null)
            {
                out.sendTextUser(user, "Filter not found: " + args[0], loudness);
            }
            else
            {
                out.removeLocalSetting(getName() + "-filters", args[0]);
                out.sendTextUser(user, "Removed filter for phrase: " + args[0], loudness);
            }
        }
        else if (command.equalsIgnoreCase("filters"))
        {
            String[] filters = out.getLocalKeys(getName() + "-filters");
            for (int i = 0; i < filters.length; i++)
                out.sendTextUser(user, "Filter " + i + ": " + filters[i], loudness);
        }
        else
        {
            out.sendTextUser(user,
                             "Error: unknown command in Filter Plugin -- please report to iago",
                             QUIET);
        }
    }

}
