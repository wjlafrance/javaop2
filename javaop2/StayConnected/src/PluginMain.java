import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JComponent;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.ConnectionCallback;

import constants.PacketConstants;
import constants.ErrorLevelConstants;
import exceptions.PluginException;

import util.BNetPacket;


/*
 * Created on Mar 25, 2009 By joe
 */
/**
 * @author joe
 * 
 */
public class PluginMain extends GenericPluginInterface implements ConnectionCallback
{
    private PublicExposedFunctions out;
    // if disabled, won't autoreconnect
    private boolean                enabled = false;

    //
    // Basic plugin information
    //
    public String getName()
    {
        return "StayConnected";
    }

    public String getVersion()
    {
        return "2.1.2";
    }

    public String getAuthorName()
    {
        return "joe";
    }

    public String getAuthorWebsite()
    {
        return "forum.x86labs.org";
    }

    public String getAuthorEmail()
    {
        return "joetheodd@gmail.com";
    }

    public String getShortDescription()
    {
        return "Keeps the bot connected.";
    }

    public String getLongDescription()
    {
        return "Stays connected by sending SID_NULL every 30 seconds and detecting"
                + "Socket errors, and reconnecting if one occurs.";
    }

    //
    // Non existant plugin settings
    //
    public Properties getDefaultSettingValues()
    {
        return new Properties();
    }

    public Properties getSettingsDescription()
    {
        return new Properties();
    }

    public Properties getGlobalDefaultSettingValues()
    {
        return new Properties();
    }

    public Properties getGlobalSettingsDescription()
    {
        return new Properties();
    }

    public JComponent getComponent(String settingName, String value)
    {
        return null;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

    //
    // GenericPluginInterface callbacks
    //
    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        out.schedule(new SendNullPacketCallback(), 30 * 1000);
        register.registerConnectionPlugin(this, null);
    }

    public boolean connecting(String host, int port, Object data) throws IOException, PluginException
    {
        this.enabled = true;
        return true;
    }

    public void connected(String host, int port, Object data) throws IOException, PluginException
    {
    }

    public boolean disconnecting(Object data)
    {
        // The magic here is that this function is only called on
        // intentional disconnects. That means that if we're
        // disconnected due to a socket error, enabled will still
        // be set to true when disconnected is called.
        this.enabled = false;
        return true;
    }

    public void disconnected(Object data)
    {
        if (this.enabled)
        {
            out.systemMessage(ErrorLevelConstants.ERROR,
                              "StayConnected: Looks like we disconnected..");
            out.reconnect();
        }
    }

    private class SendNullPacketCallback extends TimerTask
    {
        public void run()
        {
            try
            {
                out.sendPacket(new BNetPacket(PacketConstants.SID_NULL));
                out.systemMessage(ErrorLevelConstants.DEBUG, "StayConnected: Sending SID_NULL.");
            }
            catch (IOException e)
            {
                disconnected(e);
            }
        }
    }
}
