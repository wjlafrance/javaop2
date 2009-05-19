import java.io.IOException;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JComponent;

import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;
import util.BNetPacket;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import constants.ErrorLevelConstants;
import constants.PacketConstants;
import exceptions.PluginException;


/*
 * Created on Mar 25, 2009
 * By joe
 */
/**
 * @author joe
 *
 */
public class PluginMain extends GenericPluginInterface implements PacketCallback
{
    private PublicExposedFunctions out;
    
    //
    // Basic plugin information
    //
    public String getName()
    {  return "StayConnected"; }
    public String getVersion()
    { return "1.0"; }
    public String getAuthorName()
    { return "joe"; }
    public String getAuthorWebsite()
    { return "forum.x86labs.org"; }
    public String getAuthorEmail()
    { return "joetheodd@gmail.com"; }
    public String getShortDescription()
    { return "Automatically detects disconnects and reconnects the bot."; }
    public String getLongDescription()
    { return "Detects disconnections by sending SID_NULL every 30 seconds and detecting" +
            "Socket errors, and reconnecting if one occurs."; }

    //
    // Non existant plugin settings
    //
    public Properties getDefaultSettingValues()
    { return new Properties(); }
    public Properties getSettingsDescription()
    { return new Properties(); }
    public JComponent getComponent(String settingName, String value)
    { return null; }
    public Properties getGlobalDefaultSettingValues()
    { Properties p = new Properties(); return p; }
    public Properties getGlobalSettingsDescription()
    { Properties p = new Properties(); return p; }
    public JComponent getGlobalComponent(String settingName, String value)
    { return null; }

    //
    // GenericPluginInterface callbacks
    //
    public void load(StaticExposedFunctions staticFuncs)
    { }
    public void deactivate(PluginCallbackRegister register)
    { }
    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
    	this.out = out;
        out.schedule(new SendNullPacketCallback(), 30 * 1000);
    }
    
    //
    // PacketCallback
    //
    public BNetPacket processingPacket(BNetPacket buf, Object data) throws  PluginException
    { return buf; }
    public void processedPacket(BNetPacket buf, Object data) throws IOException, PluginException
    { }
        

    private class SendNullPacketCallback extends TimerTask
    {
        public void run()
        {
            try
            {
            	out.sendPacket(new BNetPacket(PacketConstants.SID_NULL));
            	out.systemMessage(ErrorLevelConstants.DEBUG, "StayConnected: Sending SID_NULL.");
            }
            catch(IOException e)
            {
            	out.systemMessage(ErrorLevelConstants.ERROR, "StayConnected: Looks like we disconnected..");
            	out.reconnect();
            }
        }
    }
}
