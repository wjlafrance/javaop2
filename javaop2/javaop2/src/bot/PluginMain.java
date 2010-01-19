/*
 * Created on Jan 3, 2005 By iago
 */
package bot;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import javax.swing.JComboBox;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import plugin_interfaces.GenericPluginInterface;
import util.gui.JTextFieldNumeric;


/**
 * This is the same as PluginMain in the plugins, except this is for the main
 * class.
 * 
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface
{

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "_Default";
    }

    public String getVersion()
    {
        return "2.1.2";
    }

    public String getFullName()
    {
        return "JavaOp2";
    }

    public String getAuthorName()
    {
        return "wjlafrance";
    }

    public String getAuthorWebsite()
    {
        return "javaop.googlecode.com";
    }

    public String getAuthorEmail()
    {
        return "wjlafrance@gmail.com";
    }

    public String getShortDescription()
    {
        return "The main core of the bot.";
    }

    public String getLongDescription()
    {
        return "The main core of the bot";
    }

    public String getCommands()
    {
        return "I am the source.";
    }

    public String getEvents()
    {
        return "I am the power.";
    }

    public String getPackets()
    {
        return "It's not safe to toy with this.";
    }

    public String getComment()
    {
        return "Don't mess with me.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("server", "uswest.battle.net");
        p.setProperty("port", "6112");
        p.setProperty("connect automatically", "false");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("server", "The server that the bot connects to.  If you type one in manually, you have to make sure something else receives focus before exiting.  That's Java's fault.");
        p.setProperty("port", "The port that the bot connects on.  Should never be anything except 6112.");
        p.setProperty("connect automatically", "If this is set, the bot will automatically connect to Battle.net when it is loaded.");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        if (settingName.equalsIgnoreCase("server"))
        {
            JComboBox combo = new JComboBox(new String[] {
            		"uswest.battle.net",
            		"useast.battle.net",
            		"europe.battle.net",
            		"asia.battle.net"
            });
            combo.setEditable(true);
            combo.setSelectedItem(value);
            return combo;
        }
        else if (settingName.equalsIgnoreCase("connect automatically"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        }
        else if (settingName.equalsIgnoreCase("port"))
        {
            return new JTextFieldNumeric(value);
        }
        return null;
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("timeout", "30000");
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("timeout", "The amount of time to wait until a connection times out, in milliseconds.");
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

}
