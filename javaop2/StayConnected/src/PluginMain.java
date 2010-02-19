package com.javaop.StayConnected;

import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.ConnectionCallback;
import com.javaop.constants.PacketConstants;
import com.javaop.constants.ErrorLevelConstants;
import com.javaop.exceptions.PluginException;
import com.javaop.util.BnetPacket;


/*
 * Created on Mar 25, 2009 By wjlafrance
 */
/**
 * @author wjlafrance
 * 
 */
public class PluginMain extends GenericPluginInterface implements
        ConnectionCallback
{
    private PublicExposedFunctions out;
    private boolean                enabled = false;
    
    public String getName() {
        return "StayConnected";
    }

    public String getVersion() {
        return "2.1.3";
    }

    public String getAuthorName() {
        return "wjlafrance";
    }

    public String getAuthorWebsite() {
        return "javaop.googlecode.com";
    }

    public String getAuthorEmail() {
        return "wjlafrance@gmail.com";
    }

    public String getShortDescription() {
        return "Keeps the bot connected.";
    }

    public String getLongDescription()
    {
        return "Stays connected by sending SID_NULL every 30 seconds to "
                + "detect socket errors, and reconnecting if one occurs.";
    }
    public Properties getDefaultSettingValues() {
        return new Properties();
    }

    public Properties getSettingsDescription() {
        return new Properties();
    }

    public Properties getGlobalDefaultSettingValues() {
        return new Properties();
    }

    public Properties getGlobalSettingsDescription() {
        return new Properties();
    }

    public JComponent getComponent(String settingName, String value)
    {
        return null;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        return null;
    }
    
    public void load(StaticExposedFunctions staticFuncs) {
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister
            register)
    {
        this.out = out;
        out.schedule(new SendNullPacketCallback(), 30 * 1000); // 30 seconds
        register.registerConnectionPlugin(this, null);
    }

    public boolean connecting(String host, int port, Object data) throws
            IOException, PluginException
    {
        this.enabled = true;
        return true;
    }

    public void connected(String host, int port, Object data) throws
            IOException, PluginException
    {
    }

    public boolean disconnecting(Object data) {
        // The magic here is that this function is only called on
        // intentional disconnects. That means that if we're
        // disconnected due to a socket error, enabled will still
        // be set to true when disconnected is called.
        this.enabled = false;
        return true;
    }

    public void disconnected(Object data) {
        if (this.enabled) {
            out.systemMessage(ErrorLevelConstants.ERROR,
                    "Disconnection detected. Reconnecting..");
            out.reconnect();
        }
    }

    private class SendNullPacketCallback extends TimerTask {
        public void run() {
            try {
                out.sendPacket(new BnetPacket(PacketConstants.SID_NULL));
                //out.systemMessage(ErrorLevelConstants.DEBUG,
                //        "Sending keep-alive..");
            } catch (IOException e) {
                disconnected(e);
            }
        }
    }
}
