import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JCheckBox;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.InvalidCDKey;
import exceptions.InvalidPassword;
import exceptions.PluginException;

import plugin_interfaces.ErrorCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.OutgoingTextCallback;
import plugin_interfaces.SystemMessageCallback;
import util.BNetEvent;
import util.BNetPacket;
import util.ColorConstants;


/*
 * Created on Dec 7, 2004 By iago
 */

/**
 * This plugin will display system messages and error messages on the screen.
 * 
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements SystemMessageCallback,
        EventCallback, OutgoingTextCallback, ErrorCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerSystemMessagePlugin(this, DEBUG, EMERGENCY, null);
        register.registerErrorPlugin(this, null);
        register.registerOutgoingTextPlugin(this, null);
        register.registerEventPlugin(this, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public void systemMessage(int level, String message, Object data)
    {
        switch (level)
        {
            case DEBUG:
                Output.output(
                              out.getName(),
                              Output.DARK_GREEN + errorLevelConstants[level] + ": " + message,
                              out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
                break;
            case INFO:
                Output.output(
                              out.getName(),
                              Output.BRIGHT_YELLOW + errorLevelConstants[level] + ": " + message,
                              out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
                break;
            default:
                Output.output(
                              out.getName(),
                              Output.RED + errorLevelConstants[level] + ": " + message,
                              out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
                break;
        }
    }

    public void showMessage(String message, Object data)
    {
        message = ColorConstants.removeColors(message);
        Output.output(out.getName(), Output.CYAN + message,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void userShow(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_GREEN + user + " is in the channel ("
                + statstring + ").",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void userJoin(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_GREEN + user + " has joined the channel ("
                + statstring + ").",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void userLeave(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_GREEN + user + " has left the channel.",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void whisperFrom(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_BLACK + "<From: " + user + "> " + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void talk(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_YELLOW + "<" + user + "> " + Output.WHITE
                + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void broadcast(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_YELLOW + "*** BROADCAST: " + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void channel(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_GREEN + "Joining " + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void userFlags(String user, String statstring, int ping, int flags)
    {
        // Output.output(out.getName(), Output.BRIGHT_YELLOW +
        // "Status update for " + user + " (" + statstring + ").",
        // out.getLocalSetting(getName(), "colors", "true").equals("true"));
    }

    public void whisperTo(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_BLACK + "<To: " + user + "> " + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void error(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_RED + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void info(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_YELLOW + statstring,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void emote(String user, String statstring, int ping, int flags)
    {
        Output.output(out.getName(), Output.BRIGHT_YELLOW + "<" + user + " " + statstring + ">",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public String queuingText(String text, Object data)
    {
        return text;
    }

    public void queuedText(String text, Object data)
    {
    }

    public String nextInLine(String text, Object data)
    {
        return text;
    }

    public long getDelay(String text, Object data)
    {
        return 0;
    }

    public boolean sendingText(String text, Object data)
    {
        return true;
    }

    public void sentText(String text, Object data)
    {
        Output.output(out.getName(), Output.BRIGHT_CYAN + "<" + out.getLocalVariable("username")
                + "> " + Output.WHITE + text, out.getLocalSettingDefault(getName(), "colors",
                                                                         "true").equals("true"));
    }

    public void ioexception(IOException e, Object data)
    {
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void unknownException(Exception e, Object data)
    {
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void error(Error e, Object data)
    {
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void pluginException(PluginException e, Object data)
    {
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void badCDKey(InvalidCDKey e, Object data)
    {
        Output.output(out.getName(), Output.BRIGHT_RED + "Bot tried to use an invalid cdkey.",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void badPassword(InvalidPassword e, Object data)
    {
        Output.output(out.getName(), Output.BRIGHT_RED + "Bot tried to use an invalid password.",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
        displayError(out.getName(), e,
                     out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void unknownPacketReceived(BNetPacket packet, Object data)
    {
        Output.output(out.getName(), Output.DARK_BLUE + "Unknown packet received:",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
        Output.output(out.getName(), Output.DARK_BLUE + packet,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void unknownEventReceived(BNetEvent event, Object data)
    {
        Output.output(out.getName(), Output.DARK_BLUE + "Unknown event received:",
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
        Output.output(out.getName(), Output.DARK_BLUE + event,
                      out.getLocalSettingDefault(getName(), "colors", "true").equals("true"));
    }

    public void displayError(String botName, Throwable t, boolean useColors)
    {
        StackTraceElement[] stack = t.getStackTrace();

        Output.output(botName, Output.BRIGHT_RED + t.toString(), useColors);
        for (int i = 0; i < stack.length; i++)
            Output.output(botName, Output.RED + stack[i], useColors);
    }

    public String getName()
    {
        return "Console Display Plugin";
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
        return "http://www.javaop.com";
    }

    public String getAuthorEmail()
    {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription()
    {
        return "Displays messages to the console, optionally in color.";
    }

    public String getLongDescription()
    {
        return "This displays messages to the console for events and errors.  If color is enabled, then it displays them "
                + "in ANSI color codes, which work on Linux, but not on Windows XP, it seems.  I read somewhere about enabling "
                + "ANSI.sys on Windows to get it working, and if anybody manages that, please let me know.";

    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();

        String os = System.getProperty("os.name");
        if (os.toLowerCase().matches(".*Windows.*"))
            p.setProperty("colors", "false");
        else
            p.setProperty("colors", "true");

        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("colors", "Display ANSI colors on the console (NOT recommended for Windows).");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("colors"))
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

    public boolean isRequiredPlugin()
    {
        return true;
    }
}
