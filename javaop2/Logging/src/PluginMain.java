/*
 * Created on Jan 31, 2005 By iago
 */

import javax.swing.JComponent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.LoginException;
import exceptions.PluginException;
import plugin_interfaces.ErrorCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.OutgoingTextCallback;
import plugin_interfaces.PacketCallback;
import plugin_interfaces.SystemMessageCallback;
import util.BnetEvent;
import util.BnetPacket;
import util.RelativeFile;
import util.Timestamp;


/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements EventCallback,
        OutgoingTextCallback, SystemMessageCallback, ErrorCallback, PacketCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerEventPlugin(this, null);
        register.registerOutgoingTextPlugin(this, null);
        register.registerSystemMessagePlugin(this, DEBUG, EMERGENCY, null);
        register.registerErrorPlugin(this, null);

        if (out.getLocalSettingDefault(getName(), "Log packets", "false").equalsIgnoreCase("true"))
        {
            register.registerIncomingPacketPlugin(this, 0, packetConstants.length, "Incoming");
            register.registerOutgoingPacketPlugin(this, 0, packetConstants.length, "Outgoing");
        }
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Logging Plugin";
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

    public String getShortDescription()
    {
        return "Logs to a file";
    }

    public String getLongDescription()
    {
        return "Logs all chat, events, and errors to a file. ";
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty(
                      "Log packets",
                      "Logs incoming and outgoing packets.  Use this to submit a connection problem, but be warned: it'll generate HUGE logs.  Bot must be restarted for this to take effect (simply because of the overhead, it slows things down a little)");
        p.setProperty("Log join/leave", "Logs notifications when users join or leave the channel");
        p.setProperty("Log format",
                      "The format of the logs.  Either \"text\", \"html\" or \"bb\" (bb is for posting on forums)");
        p.setProperty(
                      "Log file",
                      "The full path to where you want the logs to go.  %d will be expanded to the date "
                              + "and %n will be expanded to the bot's name.  If %d is used, you will get a different file for "
                              + "each day.");

        return p;
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Log packets", "false");
        p.setProperty("Log join/leave", "false");
        p.setProperty("Log file", new RelativeFile("logs/%n-%d.html").getAbsolutePath());
        p.setProperty("Log format", "html");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("Log join/leave")
                || settingName.equalsIgnoreCase("Log packets"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true") ? true : false);
        }
        else if (settingName.equalsIgnoreCase("Log format"))
        {
            JComboBox list = new JComboBox(new String[]
            { "html", "text", "bb" });
            list.setSelectedItem(value);
            return list;
        }

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

    /**
     * This function will expect to receive strings in html format,
     * <color="blue"> [text] </color> format.
     */
    private void addToLog(String text)
    {
        try
        {
            String format = out.getLocalSettingDefault(getName(), "Log format", "html");
            String filename = out.getLocalSettingDefault(getName(), "Log file", new RelativeFile(
                    "logs/%n-%d.html").getAbsolutePath());
            filename = filename.replaceAll("%n", out.getName());
            filename = filename.replaceAll("%d", Timestamp.getDate());

            new File(filename).getParentFile().mkdirs();
            PrintWriter output = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));

            if (format.equalsIgnoreCase("html"))
                output.println("<b><font color=\"white\">" + Timestamp.getTimestamp()
                        + "</font></b> " + text + "<br>");
            else if (format.equalsIgnoreCase("bb"))
                output.println("[b][color=white]" + Timestamp.getTimestamp() + "[/color][/b]"
                        + text);
            else
                output.println(Timestamp.getTimestamp() + text);

            output.close();
        }
        catch (IOException e)
        {
            System.err.println("Unable to open log file for writing!");
            e.printStackTrace();
        }
    }

    private String sanitizeHtml(String s)
    {
        s = s.replaceAll("&", "&amp;");
        s = s.replaceAll("<", "&lt;");
        s = s.replaceAll(">", "&gt;");
        s = s.replaceAll("\n", "<br>");
        return s;
    }

    private String sanitizeBB(String s)
    {
        return s;
    }

    private String sanitize(String s)
    {
        String format = out.getLocalSettingDefault(getName(), "Log format", "html");

        if (format.equalsIgnoreCase("html"))
            return sanitizeHtml(s);
        else if (format.equalsIgnoreCase("bb"))
            return sanitizeBB(s);
        return s;
    }

    private String format(Object textObj, String color)
    {
        String text = textObj + "";
        String format = out.getLocalSettingDefault(getName(), "Log format", "html");
        text = sanitize(text);

        if (format.equalsIgnoreCase("html"))
            return "<font color=\"" + color + "\">" + text + "</font>";
        else if (format.equalsIgnoreCase("bb"))
            return "[color=" + color + "]" + text + "[/color]";
        return text;
    }

    public void talk(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format("<" + user + "> ", "yellow") + format(statstring, "white"));
    }

    public void emote(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format("<" + user + " " + statstring, "yellow"));
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format("<From: " + user + "> " + statstring, "gray"));
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format("<To: " + user + "> " + statstring, "gray"));
    }

    public void userShow(String user, String statstring, int ping, int flags) throws PluginException
    {
        if (out.getLocalSettingDefault(getName(), "Log join/leave", "true").equalsIgnoreCase("true"))
            addToLog(format(user + " is in the channel", "green"));
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws PluginException
    {
        if (out.getLocalSettingDefault(getName(), "Log join/leave", "true").equalsIgnoreCase("true"))
            addToLog(format(user + " has joined the channel", "green"));
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws PluginException
    {
        if (out.getLocalSettingDefault(getName(), "Log join/leave", "true").equalsIgnoreCase("true"))
            addToLog(format(user + " has left the channel", "green"));
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format(user + " has had a flag update", "green"));
    }

    public void error(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format(statstring, "red"));
    }

    public void info(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format(statstring, "yellow"));
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format(statstring, "orange"));
    }

    public void channel(String user, String statstring, int ping, int flags) throws PluginException
    {
        addToLog(format("Joining: ", "green") + format(statstring, "white"));
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
        addToLog(format("<" + out.getLocalVariable("username") + "> ", "cyan")
                + format(text, "white"));
    }

    public void systemMessage(int level, String message, Object data)
    {
        addToLog(format(errorLevelConstants[level] + ": " + message, "gray"));
    }

    public void showMessage(String message, Object data)
    {
        addToLog(format(message, "gray"));
    }

    public void loginException(LoginException e, Object data)
    {
        displayError(e);
    }

    public void unknownPacketReceived(BnetPacket packet, Object data)
    {
        addToLog(format("Unknown packet received:\n" + packet, "blue"));
    }

    public void unknownEventReceived(BnetEvent event, Object data)
    {
        addToLog(format("Unknown event received:\n" + event, "blue"));
    }

    public void displayError(Throwable t)
    {
        StackTraceElement[] stack = t.getStackTrace();

        addToLog(format(t, "red"));
        for (int i = 0; i < stack.length; i++)
            addToLog(format(stack[i].toString(), "red"));
    }

    public void ioException(IOException e, Object data)
    {
        displayError(e);
    }

    public void unknownException(Exception e, Object data)
    {
        displayError(e);
    }

    public void error(Error e, Object data)
    {
        displayError(e);
    }

    public void pluginException(PluginException e, Object data)
    {
        displayError(e);
    }

    public BnetPacket processingPacket(BnetPacket buf, Object data) throws PluginException
    {
        return buf;
    }

    public void processedPacket(BnetPacket buf, Object data) throws PluginException
    {
        // Don't log the cdkey or password packets, and just skip pings
        if (buf.getCode() == SID_PING || buf.getCode() == SID_NULL)
            return;
        if (buf.getCode() == SID_AUTH_CHECK || buf.getCode() == SID_LOGONRESPONSE2)
            addToLog(format(data + ": " + packetConstants[buf.getCode()] + " not logged.", "orange"));
        else
            addToLog(format(data + ": [" + packetConstants[buf.getCode()] + "]\n" + buf.toString(),
                            "orange"));
    }

}
