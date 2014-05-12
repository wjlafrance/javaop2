package com.javaop.SwingGui;

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.javaop.util.gui.JTextFieldNumeric;
import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.plugin_interfaces.GenericPluginInterface;


/*
 * Created on Dec 16, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface {
    private static JavaOpFrame            frame;
    private JavaOpPanel                   panel;

    private PublicExposedFunctions        out   = null;
    private static StaticExposedFunctions funcs = null;

    public void load(StaticExposedFunctions staticFuncs) {
        funcs = staticFuncs;

        try {
            frame = new JavaOpFrame(funcs);
        } catch (Throwable e) {
            System.err.println("Warning: Unable to load the SwingGUI (you're "
            		+ "probably on a commandline)");
            System.err.println("FYI, the error was: " + e);
        }
    }
    
    public void activate(PublicExposedFunctions out, PluginCallbackRegister
            register)
    {
        this.out = out;
            
        try {
            panel = frame.addBot(out);
            panel.registerCallbacks(register);
        } catch (Throwable t) {
            System.err.println("An error occurred while activating the "
                    + "SwingGUI: " + t);
            t.printStackTrace();
        }
    }
    
    public void deactivate(PluginCallbackRegister register) {
        frame.removeBot(out.getName());
    }
    
    public String getName() {
        return "SwingGui";
    }
    
    public String getVersion() {
        return "2.1.4";
    }
    
    public String getAuthorName() {
        return "iago";
    }
    
    public String getAuthorWebsite() {
        return "http://javaop.googecode.com";
    }
    
    public String getAuthorEmail() {
        return "iago@valhallalegends.com";
    }
    
    public String getShortDescription() {
        return "Displays a Swing-based GUI";
    }
    
    public String getLongDescription() {
        return "Dislays a fully function Swing gui for the bots.";
    }
    
    public Properties getSettingsDescription() {
        Properties p = new Properties();
        p.setProperty("Show join/leave", "Shows notifications when users join "
                + "or leave the channel.");
        p.setProperty("Show status updates", "Shows notifications when users "
                + "have a status update.");
        p.setProperty("Show \"/commands\"", "Shows /commands (like, /ban, "
                + "/join, etc.) in the display window.");
        p.setProperty("Chat max length", "When the chat box passes this "
                + "length, the length will be reset (0 to disable this).  Must "
                + "reload bot for this to take effect!");
        p.setProperty("Chat reset length", "When it crosses max length, the "
                + "chat box will be reset to this length.  Must reload bot for "
                + "this to take effect!");
        p.setProperty("Ops on top", "If this is set, users with ops (or "
                + "special flags) are put at the top of the channel list. "
                + "Must reload bot to change this.");
        p.setProperty("Show away", "If this is turned off, messages like 'You "
                + "are no longer away' aren't shown.  Those messages annoy me, "
                + "so I defaulted this to off.");
        p.setProperty("On Message", "The action to take when a message "
                + "arrives.  Nothing = do nothing, Highlight = make the tab "
                + "red, and Switch = switch to that tab (very annoying).");
        p.setProperty("Colored names", "When this is set, every user's name "
                + "will be colored in a unique(ish) color, based on their "
                + "name.");
        return p;
    }

    public Properties getGlobalSettingsDescription() {
        Properties p = new Properties();
        p.setProperty("Loudness", "This allows you to select the amount of "
                + "detail displayed; very quiet, quiet, standard, or debug.");
        p.setProperty("Hold at bottom", "When this is set, the main text box "
                + "will _always_ be at the bottom.  Otherwise, it will only "
                + "scroll down if you're already all the way down. It's easier "
                + "to copy/read back chat, but it's confusing for some. NOTE: "
                + "must restart bot to change this.");
        p.setProperty("Select new bots", "When set, any time a new bot starts "
                + "it will be switched to.  This might get annoying, but is "
                + "often handy.");
        p.setProperty("Bot icons", "If this is selected, bots wil have icons "
                + "corresponding to which client they're running.  Must "
                + "restart or reload each bot to take affect.");
        p.setProperty("Font", "The font used in the chat pane. Must reload bot "
                + "for this to take effect!");
        p.setProperty("Font size", "The font size used in the chat pane. Must "
                + "reload bot for this to take effect!");
        return p;
    }

    public Properties getDefaultSettingValues() {
        Properties p = new Properties();

        p.setProperty("Show join/leave", "true");
        p.setProperty("Show status updates", "false");
        p.setProperty("Show \"/commands\"", "false");
        p.setProperty("Chat max length", "100000");
        p.setProperty("Chat reset length", "80000");
        p.setProperty("Ops on top", "true");
        p.setProperty("Show away", "false");
        p.setProperty("On Message", "Highlight");
        p.setProperty("Colored names", "true");

        return p;
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("Loudness", "standard");
        p.setProperty("Hold at bottom", "false");
        p.setProperty("Select new bots", "true");
        p.setProperty("Bot icons", "true");
        p.setProperty("Font", "Serif");
        p.setProperty("Font size", "15");
        return p;
    }

    public JComponent getComponent(String settingName, String value) {
        if (settingName.equalsIgnoreCase("ops on top")
                || settingName.equalsIgnoreCase("Show join/leave")
                || settingName.equalsIgnoreCase("Show status updates")
                || settingName.equalsIgnoreCase("Show \"/commands\"")
                || settingName.equalsIgnoreCase("Colored names")
                || settingName.equalsIgnoreCase("Show away"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        } else if (settingName.equalsIgnoreCase("On message")) {
            JComboBox combo = new JComboBox(new String[]
            { "Nothing", "Highlight", "Switch" });
            combo.setSelectedItem(value);
            combo.setEditable(false);
            return combo;
        } else if (settingName.equalsIgnoreCase("Chat max length")
                || settingName.equalsIgnoreCase("Chat reset length"))
        {
            return new JTextFieldNumeric(value);
        }

        return null;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        if (settingName.equalsIgnoreCase("Hold at bottom")
                || settingName.equalsIgnoreCase("Bot icons")
                || settingName.equalsIgnoreCase("Select new bots"))
        {
            return new JCheckBox("", value.equalsIgnoreCase("true"));
        } else if (settingName.equalsIgnoreCase("Loudness")) {
            JComboBox combo = new JComboBox(new String[] {
                    "packet",   "debug", "info",    "notice",
                    "warning",  "error", "critical", "emergency"
            });
            combo.setSelectedItem(value);
            combo.setEditable(false);
            return combo;
        } else if (settingName.equalsIgnoreCase("Font size")) {
        	return new JTextFieldNumeric(value);
        }

        return null;
    }

}
