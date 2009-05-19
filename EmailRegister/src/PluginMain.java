import java.io.IOException;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;
import util.BNetPacket;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.PluginException;

/**
 * @author iago
 *
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
        
        // For non-nls clients to register their email
        register.registerIncomingPacketPlugin(this, SID_SETEMAIL, null);
        // For nls clients to register their email
        register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTLOGONPROOF, null);
        // For recovering accounts
        register.registerIncomingPacketPlugin(this, SID_AUTH_CHECK, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Email Registration Plugin";
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

    public String getShortDescription()
    {
        return "Email registration";
    }

    public String getLongDescription()
    {
        return "Allows users to set an email address for their account, and to request their password be reset";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Email", "");
        p.setProperty("Register email", "true");
        p.setProperty("Try to recover", "false"); 
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("Email", "The email address to register accounts to.");
        p.setProperty("Register email", "Check this if you want to attempt to register your email whenever you log on.");
        p.setProperty("Try to recover", "Check this ONLY if you need the password reset.");
        return p;
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

    public JComponent getComponent(String settingName, String value)
    {
        if(value == null)
            System.err.println(settingName + " = null??");
        if(settingName.equalsIgnoreCase("Register email") ||
                settingName.equalsIgnoreCase("Try to recover"))
        {
            return new JCheckBox("", value != null && value.equalsIgnoreCase("true") ? true : false);
        }
        
        return null;
    }
    
    
    public boolean isRequiredPlugin()
    {
        return false;
    }

    public BNetPacket processingPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        return buf;
    }
    
    private void setEmail() throws IOException
    {
        String email = out.getLocalSettingDefault(getName(), "Email", "");
        
        if(email.length() == 0 || email.indexOf('@') < 0)
        {
            out.systemMessage(ERROR, "Attempted to register invalid email address; cancelled");
        }
        else
        {
            out.systemMessage(NOTICE, "Attempting to register your email address");
            
            BNetPacket register = new BNetPacket(SID_SETEMAIL);
            register.addNTString(email);
            out.sendPacket(register);
        }
    }
    
    public void processedPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        if(out.getLocalSettingDefault(getName(), "Register email", "false").equalsIgnoreCase("true"))
        {
            if(buf.getCode() == SID_SETEMAIL)
            {
                setEmail();
            }
            else if(buf.getCode() == SID_AUTH_ACCOUNTLOGONPROOF)
            {
                if(buf.removeDWord() == 0x0E)
                {
                    setEmail();
                }
            }
        }
        
        if(out.getLocalSettingDefault(getName(), "Try to recover", "false").equalsIgnoreCase("true"))
        {
            if(buf.getCode() == SID_AUTH_CHECK)
            {
                BNetPacket packet = new BNetPacket(SID_RESETPASSWORD);
                
                // Crossing the plugin border like this is VERY bad and I shouldn't do it.  But here we are.
                packet.addNTString(out.getLocalSetting("battle.net login plugin", "username"));
                packet.addNTString(out.getLocalSettingDefault(getName(), "Email", ""));
                
                out.sendPacket(packet);
                
                out.systemMessage(WARNING, "Tried to reset password via email; check your email.");
            }
        }
    }

}
