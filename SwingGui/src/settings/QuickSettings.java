/*
 * Created on Apr 8, 2005
 * By iago
 */
package settings;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import util.gui.Gui;
import util.gui.PreferencesPanel;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

public class QuickSettings extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private JPanel form;
    
    private PublicExposedFunctions out;
    
    private final JPanel right = new JPanel();
    private final PreferencesPanel rightPrefs;
    
    public QuickSettings(PublicExposedFunctions out)
    {
        super("Configuring " + out.getName());

        this.out = out;
        StaticExposedFunctions funcs = out.getStaticExposedFunctionsHandle();

        form = new JPanel();
        this.getContentPane().add(form);
        this.setSize(700, 500);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);       
        Gui.center(this);
        form.setLayout(new BorderLayout(3, 3));
        
        this.getContentPane().add(right, BorderLayout.CENTER);
        right.setLayout(new BorderLayout(10, 0));

        // Create properties tables for each of the important values
        Properties currentSettings = new Properties();
        Properties defaultSettings = new Properties();
        Properties descriptions = new Properties();
        Hashtable components = new Hashtable();
        
        currentSettings.setProperty("server", out.getLocalSetting(" Default", "server"));
        defaultSettings.setProperty("server", funcs.pluginGetDefaultSettings(" Default").getProperty("server"));
        descriptions.setProperty("server", funcs.pluginGetDescriptions(" Default").getProperty("server"));
        components.put("server", funcs.pluginGetComponents(" Default", out.getLocalSettingSection(" Default")).get("server"));
        
        currentSettings.setProperty("connect automatically", out.getLocalSetting(" Default", "connect automatically"));
        defaultSettings.setProperty("connect automatically", funcs.pluginGetDefaultSettings(" Default").getProperty("connect automatically"));
        descriptions.setProperty("connect automatically", funcs.pluginGetDescriptions(" Default").getProperty("connect automatically"));
        components.put("connect automatically", funcs.pluginGetComponents(" Default", out.getLocalSettingSection(" Default")).get("connect automatically"));
        
        currentSettings.setProperty("username", out.getLocalSetting("Battle.net Login Plugin", "username"));
        defaultSettings.setProperty("username", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("username"));
        descriptions.setProperty("username", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("username"));
        components.put("username", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("username"));
        
        currentSettings.setProperty("password", out.getLocalSetting("Battle.net Login Plugin", "password"));
        defaultSettings.setProperty("password", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("password"));
        descriptions.setProperty("password", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("password"));
        components.put("password", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("password"));
        
        currentSettings.setProperty("cdkey", out.getLocalSetting("Battle.net Login Plugin", "cdkey"));
        defaultSettings.setProperty("cdkey", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("cdkey"));
        descriptions.setProperty("cdkey", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("cdkey"));
        components.put("cdkey", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("cdkey"));
        
        currentSettings.setProperty("cdkey2", out.getLocalSetting("Battle.net Login Plugin", "cdkey2"));
        defaultSettings.setProperty("cdkey2", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("cdkey2"));
        descriptions.setProperty("cdkey2", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("cdkey2"));
        components.put("cdkey2", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("cdkey2"));
        
        currentSettings.setProperty("game", out.getLocalSetting("Battle.net Login Plugin", "game"));
        defaultSettings.setProperty("game", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("game"));
        descriptions.setProperty("game", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("game"));
        components.put("game", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("game"));
        
        currentSettings.setProperty("home channel", out.getLocalSetting("Battle.net Login Plugin", "home channel"));
        defaultSettings.setProperty("home channel", funcs.pluginGetDefaultSettings("Battle.net Login Plugin").getProperty("home channel"));
        descriptions.setProperty("home channel", funcs.pluginGetDescriptions("Battle.net Login Plugin").getProperty("home channel"));
        components.put("home channel", funcs.pluginGetComponents("Battle.net Login Plugin", out.getLocalSettingSection("Battle.net Login Plugin")).get("home channel"));
        
        if(out.pluginIsActive("Email Registration Plugin")) {
        	currentSettings.setProperty("Email", out.getLocalSetting("Email Registration Plugin", "Email"));
        	defaultSettings.setProperty("Email", funcs.pluginGetDefaultSettings("Email Registration Plugin").getProperty("Email"));
        	descriptions.setProperty("Email", funcs.pluginGetDescriptions("Email Registration Plugin").getProperty("Email"));
        	components.put("Email", funcs.pluginGetComponents("Email Registration Plugin", out.getLocalSettingSection("Email Registration Plugin")).get("Email"));
        }
                
        currentSettings.setProperty("Colored names", out.getLocalSetting("Swing Gui Plugin", "Colored names"));
        defaultSettings.setProperty("Colored names", funcs.pluginGetDefaultSettings("Swing Gui Plugin").getProperty("Colored names"));
        descriptions.setProperty("Colored names", funcs.pluginGetDescriptions("Swing Gui Plugin").getProperty("Colored names"));
        components.put("Colored names", funcs.pluginGetComponents("Swing Gui Plugin", out.getLocalSettingSection("Swing Gui Plugin")).get("Colored names"));
        
        if(out.pluginIsActive("Commands")) {
        	currentSettings.setProperty("?trigger requires flags", out.getLocalSetting("Commands", "?trigger requires flags"));
        	defaultSettings.setProperty("?trigger requires flags", funcs.pluginGetDefaultSettings("Commands").getProperty("?trigger requires flags"));
        	descriptions.setProperty("?trigger requires flags", funcs.pluginGetDescriptions("Commands").getProperty("?trigger requires flags"));
        	components.put("?trigger requires flags", funcs.pluginGetComponents("Commands", out.getLocalSettingSection("Commands")).get("?trigger requires flags"));

        	currentSettings.setProperty("loud", out.getLocalSetting("Commands", "loud"));
        	defaultSettings.setProperty("loud", funcs.pluginGetDefaultSettings("Commands").getProperty("loud"));
        	descriptions.setProperty("loud", funcs.pluginGetDescriptions("Commands").getProperty("loud"));
        	components.put("loud", funcs.pluginGetComponents("Commands", out.getLocalSettingSection("Commands")).get("loud"));
	    
	    	currentSettings.setProperty("stacked messages", out.getLocalSetting("Commands", "stacked messages"));
	    	defaultSettings.setProperty("stacked messages", funcs.pluginGetDefaultSettings("Commands").getProperty("stacked messages"));
	    	descriptions.setProperty("stacked messages", funcs.pluginGetDescriptions("Commands").getProperty("stacked messages"));
	    	components.put("stacked messages", funcs.pluginGetComponents("Commands", out.getLocalSettingSection("Commands")).get("stacked messages"));

	    	currentSettings.setProperty("trigger", out.getLocalSetting("Commands", "trigger"));
	    	defaultSettings.setProperty("trigger", funcs.pluginGetDefaultSettings("Commands").getProperty("trigger"));
	    	descriptions.setProperty("trigger", funcs.pluginGetDescriptions("Commands").getProperty("trigger"));
	    	components.put("trigger", funcs.pluginGetComponents("Commands", out.getLocalSettingSection("Commands")).get("trigger"));

	    	currentSettings.setProperty("whispers always command", out.getLocalSetting("Commands", "whispers always command"));
	    	defaultSettings.setProperty("whispers always command", funcs.pluginGetDefaultSettings("Commands").getProperty("whispers always command"));
	    	descriptions.setProperty("whispers always command", funcs.pluginGetDescriptions("Commands").getProperty("whispers always command"));
	    	components.put("whispers always command", funcs.pluginGetComponents("Commands", out.getLocalSettingSection("Commands")).get("whispers always command"));
        }
	    	
        right.add(new JScrollPane(rightPrefs = new PreferencesPanel(currentSettings, defaultSettings, descriptions, components)));
        //right.add(new JLabel("Please select a plugin to configure"), BorderLayout.CENTER);
        
        this.setVisible(true);
    }
    
    public void windowActivated(WindowEvent e)
    {
    }

    public void windowClosed(WindowEvent e)
    {

    }

    public void windowClosing(WindowEvent e)
    {
        
        try
        {
            saveRight();
        }
        catch(Exception exc)
        {
            JOptionPane.showMessageDialog(null, "Sorry, was unable to save.  See console output for more information: " + exc);
            exc.printStackTrace();
        }
        
        this.dispose();
    }

    public void windowDeactivated(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowOpened(WindowEvent e)
    {
    }
    
    private void saveRight()
    {
        if(rightPrefs == null || rightPrefs.hasChanged() == false)
            return;

        if(JOptionPane.showConfirmDialog(this, "Save settings?", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;

        Properties newPref = rightPrefs.getValues();
        
        out.putLocalSetting(" Default", "connect automatically", newPref.getProperty("connect automatically"));
        out.putLocalSetting(" Default", "server", newPref.getProperty("server"));

        out.putLocalSetting("Battle.net Login Plugin", "username", newPref.getProperty("username"));
        out.putLocalSetting("Battle.net Login Plugin", "password", newPref.getProperty("password"));
        out.putLocalSetting("Battle.net Login Plugin", "cdkey", newPref.getProperty("cdkey"));
        out.putLocalSetting("Battle.net Login Plugin", "cdkey2", newPref.getProperty("cdkey2"));
        out.putLocalSetting("Battle.net Login Plugin", "game", newPref.getProperty("game"));
        out.putLocalSetting("Battle.net Login Plugin", "home channel", newPref.getProperty("home channel"));
        
        if(out.pluginIsActive("Email RegistrationPlugin"))
        	out.putLocalSetting("Email Registration Plugin", "Email", newPref.getProperty("username"));

        out.putLocalSetting("Swing Gui Plugin", "Colored names", newPref.getProperty("username"));
        
        if(out.pluginIsActive("Commands")) {
        	out.putLocalSetting("Commands", "?trigger requires flags", newPref.getProperty("?trigger requires flags"));
        	out.putLocalSetting("Commands", "loud", newPref.getProperty("loud"));
        	out.putLocalSetting("Commands", "stacked messages", newPref.getProperty("stacked messages"));
        	out.putLocalSetting("Commands", "trigger", newPref.getProperty("trigger"));
        	out.putLocalSetting("Commands", "whispers always command", newPref.getProperty("whispers always command"));
        }
        	
//        Properties newPref = rightPreferences.getValues();
//        
//        String []keys = Uniq.uniq(newPref.keys());
//        for(int i = 0; i < keys.length; i++)
//            out.putLocalSetting(currentPlugin, keys[i], newPref.getProperty(keys[i]));
    }
}
