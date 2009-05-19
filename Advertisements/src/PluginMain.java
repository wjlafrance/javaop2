import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.TimerTask;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;
import util.BNetPacket;
import util.ColorConstants;
import util.FileTime;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.PluginException;

public class PluginMain extends GenericPluginInterface implements PacketCallback
{
    private PublicExposedFunctions out;
    
    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        
        register.registerIncomingPacketPlugin(this, SID_CHECKAD, null);
        out.schedule(new GetAd(), 15000);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Advertisements";
    }

    public String getVersion()
    {
        return "1.0";
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

    public String getLongDescription()
    {
        return "I don't see any use for this, but it displays the ads that you would see if you were on a real client.  It requests a new ad every 15 seconds, and I've found that a new one is usually loaded every half hour or so.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Announce ads", "false");
        p.setProperty("Display ads", "true");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Announce ads", "If this is set, the current ad will be displayed in the channel");
        p.setProperty("Display ads", "If this is set, the current ad will be displayed to the user");
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

    
    
    private int lastId = 0;
    //private int extension;
    private String filetime;
    private String filename;
    private String url;
    
    public BNetPacket processingPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        return buf;
    }

    public void processedPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        lastId = buf.removeDWord();
        buf.removeDWord();
        filetime = new Date(FileTime.fileTimeToMillis(buf.removeLong())).toString();
        filename = buf.removeNTString();
        url = buf.removeNTString();

        
        if(out.getLocalSettingDefault(getName(), "Announce ads", "false").equalsIgnoreCase("true"))
            out.sendTextPriority("Current ad: '" + filename + "' (" + url + ")", PRIORITY_LOW);
        
        if(out.getLocalSettingDefault(getName(), "Display ads", "true").equalsIgnoreCase("false"))
            out.showMessage(ColorConstants.getColor("info") + "[" + filetime + "] Current ad: '" + filename + "' (" + url + ")");
    }

    private class GetAd extends TimerTask
    {
        public void run()
        {
            try
            {
                //out.systemMessage(DEBUG, "Sending ad request");
                
                BNetPacket getAd = new BNetPacket(SID_CHECKAD);
    //          (DWORD)      Platform ID.
                getAd.addString("68XI");
    //          (DWORD)      Product ID.
                getAd.addString("RATS");
    //          (DWORD)      ID of last displayed banner.
                getAd.addDWord(lastId);
    //          (DWORD)      Current time.
                getAd.addDWord((int)(System.currentTimeMillis() / 1000));
                
                //System.out.println(getAd);
                
                // ff 15 14 00 36 38 58 49 50 58 45 53 00 00 00 00 ce 2f 86 42
                // -> 0x15 // ff 15 14 00 36 38 58 49 50 58 45 53 00 00 00 00 dd 2f 86 42
                //                         6  8  X  I  P  X  E  S

                
                out.sendPacket(getAd);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
