package com.javaop.Ping;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JCheckBox;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.PacketCallback;
import com.javaop.util.BnetPacket;


/*
 * Created on Dec 10, 2004 By iago
 */

/**
 * @author iago, Joe[x86]
 * 
 *         Handles SID_PING, SID_NULL, and the work packets.
 */
public class PluginMain extends GenericPluginInterface implements PacketCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        register.registerIncomingPacketPlugin(this, SID_NULL, null);
        register.registerIncomingPacketPlugin(this, SID_PING, null);
        register.registerIncomingPacketPlugin(this, SID_AUTH_CHECK, null);
        register.registerIncomingPacketPlugin(this, SID_OPTIONALWORK, null);
        register.registerIncomingPacketPlugin(this, SID_REQUIREDWORK, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public BnetPacket processingPacket(BnetPacket buf, Object data)
    {
        return buf;
    }

    public void processedPacket(BnetPacket buf, Object data) throws IOException
    {
        switch (buf.getCode())
        {
            case SID_PING:
                out.setTCPNoDelay(true);
                out.sendPacket(buf);
                out.setTCPNoDelay(false);
                break;
            case SID_AUTH_CHECK:
                if (buf.removeDWord() != 0)
                    return; // Don't send UDPPINGRESPONSE on failed AUTH_CHECK
                BnetPacket udpPing = new BnetPacket();
                udpPing.setCode(SID_UDPPINGRESPONSE);
                udpPing.addBytes(new byte[]
                { 'b', 'n', 'e', 't' });
                out.sendPacket(udpPing);
                break;
            case SID_NULL: // nothing
                break;
            case SID_OPTIONALWORK:
                out.systemMessage(DEBUG, "Optional Work: " + buf.removeNTString()
                        + ". (SID_OPTIONALWORK)");
                break;
            case SID_REQUIREDWORK:
                out.systemMessage(DEBUG, "Required Work: " + buf.removeNTString()
                        + ". (SID_REQUIREDWORK)");
                break;
        }
    }

    public String getName()
    {
        return "Ping Plugin";
    }

    public String getVersion()
    {
        return "2.1.3";
    }

    public String getAuthorName()
    {
        return "iago, joe";
    }

    public String getAuthorWebsite()
    {
        return "http://www.javaop.com";
    }

    public String getAuthorEmail()
    {
        return "joetheodd@gmail.com";
    }

    public String getShortDescription()
    {
        return "Looks after ping and other keepalive messages, as well as extra and required work packets.";
    }

    public String getLongDescription()
    {
        return "Returns a ping message (if set to) when SID_PING is received.  Also sends back the UDP Ping probe at the "
                + "appropriate time.  Either of these can be disabled to get either \"unplugged\" icon, or -1ms ping. This "
                + "also handles displaying of the SID_OPTIONALWORK and SID_REQUIREWORK packets.";

    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Return Ping", "true");
        p.setProperty("Return UDP Ping", "true");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Return Ping", "Turn this off to get \"unplugged\" icon.");
        p.setProperty("Return UDP Ping", "Turn this off to get -1ms ping.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return new JCheckBox("", value.equalsIgnoreCase("true"));
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
        return false;
    }
}
