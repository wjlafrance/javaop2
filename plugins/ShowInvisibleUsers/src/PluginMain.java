package com.javaop.ShowInvisibleUsers;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JCheckBox;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.gui.JTextFieldNumeric;


/*
 * Created on Feb 16, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements EventCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs) {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister
            register)
    {
        this.out = out;

        register.registerEventPlugin(this, null);

        if (out.getLocalSettingDefault(getName(), "Periodically check the "
                + "current channel", "false").equalsIgnoreCase("true"))
        {
            int delay = 1000 * Integer.parseInt(out.getLocalSettingDefault(
                    getName(), "Periodical check delay", "120"));
            out.schedule(new TimerCallback(), delay);
        }
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public String getName() {
        return "ShowInvisibleUsers";
    }

    public String getVersion() {
        return "2.1.3";
    }

    public String getAuthorName() {
        return "iago";
    }

    public String getAuthorWebsite() {
        return "javaop.com";
    }

    public String getAuthorEmail() {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription() {
        return "Finds/displays invisible users";
    }

    public String getLongDescription() {
        return "Sends a /unsquelch at the bot itself when joining a channel "
                + "to detect invisible users by forcing status updates.";
    }

    public Properties getDefaultSettingValues() {
        Properties p = new Properties();
        p.setProperty("Check channels", "true");
        p.setProperty("Periodically check the current channel", "false");
        p.setProperty("Periodical check delay", "120");
        return p;
    }

    public Properties getSettingsDescription() {
        Properties p = new Properties();
        p.setProperty("Check channels",
                "Whenever you enter a new channel, it will look for "
                + "invisible users");
        p.setProperty("Periodically check the current channel",
                "Occasionally checks the current channel for invisible users. "
                + "As far as I know, this doesn't work anymore, but it doesn't "
                + "hurt.  Must RESTART bot to change this. (NOTE: This can get "
                + "REALLY annoying)");
        p.setProperty("Periodical check delay",
                "The time between checking the channel for invisible users. "
                + "Must RESTART bot to change this.");
        return p;
    }

    public JComponent getComponent(String settingName, String value) {
        if (settingName.equalsIgnoreCase("Check channels")
                || settingName.equalsIgnoreCase("Periodically check the "
                        + "current channel"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true") ? true
                    : false);
        } else if (settingName.equalsIgnoreCase("Periodical check delay")) {
            return new JTextFieldNumeric(value);
        }

        return null;
    }

    public Properties getGlobalDefaultSettingValues() {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription() {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        return null;
    }

    public void talk(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void emote(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void whisperFrom(String user, String statstring, int ping,
            int flags) throws IOException, PluginException
    {
    }

    public void whisperTo(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void userJoin(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void userLeave(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void error(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void info(String user, String statstring, int ping, int flags) 
            throws IOException, PluginException
    {
    }

    public void broadcast(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags)
            throws IOException, PluginException
    {
        if (out.getLocalSettingDefault(getName(), "check channels", "false")
                .equalsIgnoreCase("true"))
        {
            String game = out.getLocalSetting("Battle.net Login Plugin", "game");
            if (game.equals("D2DV") || game.equals("D2XP")) {
                out.sendTextPriority("/unsquelch *"
                        + out.getLocalVariable("username"), PRIORITY_LOW);
            } else {
                out.sendTextPriority("/unsquelch "
                        + out.getLocalVariable("username"), PRIORITY_LOW);
            }
        }
    }

    private class TimerCallback extends TimerTask {
        public void run() {
            try {
                if (out.getLocalSettingDefault(getName(), "check channels",
                        "false").equalsIgnoreCase("true"))
                {
                    String game = out.getLocalSetting("Battle.net Login Plugin",
                            "game");
                    if (game.equals("D2DV") || game.equals("D2XP")) {
                        out.sendTextPriority("/unsquelch *"
                                + out.getLocalVariable("username"),
                                PRIORITY_LOW);
                    } else {
                        out.sendTextPriority("/unsquelch "
                                + out.getLocalVariable("username"),
                                PRIORITY_LOW);
                    }
                }
            } catch (Exception e) {
                // *shrug*
            }
        }
    }
}
