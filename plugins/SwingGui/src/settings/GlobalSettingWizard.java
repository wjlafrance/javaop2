/*
 * Created on Apr 8, 2005 By iago
 */
package com.javaop.SwingGui.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.util.Uniq;
import com.javaop.util.gui.Gui;
import com.javaop.util.gui.PreferencesPanel;


public class GlobalSettingWizard extends JFrame implements ListSelectionListener, WindowListener
{
    /**
	 * 
	 */
    private static final long      serialVersionUID = 1L;
    private JPanel                 form;
    private JList                  list;

    private StaticExposedFunctions funcs;

    private PreferencesPanel       rightPreferences;
    private JScrollPane            oldRight         = null;
    private String                 currentPlugin    = "Default";

    public GlobalSettingWizard(StaticExposedFunctions funcs)
    {
        super("Configuring static settings");

        this.funcs = funcs;

        // Add "default"

        form = new JPanel();
        this.getContentPane().add(form);
        form.setLayout(new BorderLayout(3, 3));

        // Add the list
        String[] pluginNames = funcs.pluginGetNames();
        Vector usefulPlugins = new Vector();
        for (int i = 0; i < pluginNames.length; i++)
        {
            if (funcs.getGlobalKeys(pluginNames[i]).length != 0)
            {
                usefulPlugins.add(pluginNames[i]);
                System.out.println("Useful: " + pluginNames[i]);
            }
            else
            {
                System.out.println("Not useful: " + pluginNames[i]);
            }
        }

        this.getContentPane().add(new JScrollPane(list = new JList(usefulPlugins)),
                                  BorderLayout.WEST);
        list.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        list.getSelectionModel().addListSelectionListener(this);
        list.getSelectionModel().setSelectionInterval(0, 0);
        list.setPreferredSize(new Dimension(200, 0));

        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);
        this.setSize(700, 500);
        Gui.center(this);
        this.setVisible(true);
    }

    public void valueChanged(ListSelectionEvent e)
    {
        saveRight();

        // They clicked a new plugin on the list. Load a new panel for the
        // plugin
        String thisPlugin = (String) list.getSelectedValue();
        String[] settingNames = funcs.getGlobalKeys(thisPlugin);

        Properties defaultSettings = funcs.pluginGetGlobalDefaultSettings(thisPlugin);
        Properties descriptions = funcs.pluginGetGlobalDescriptions(thisPlugin);

        Properties settings = new Properties();
        for (int i = 0; i < settingNames.length; i++)
            settings.setProperty(
                                 settingNames[i],
                                 funcs.getGlobalSettingDefault(
                                                               thisPlugin,
                                                               settingNames[i],
                                                               defaultSettings.getProperty(settingNames[i])));

        Hashtable components = funcs.pluginGetGlobalComponents(thisPlugin, settings);

        if (oldRight != null)
            form.remove(oldRight);

        this.currentPlugin = thisPlugin;

        form.add(oldRight = new JScrollPane(rightPreferences = new PreferencesPanel(settings,
                defaultSettings, descriptions, components)), BorderLayout.CENTER);
        oldRight.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        oldRight.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        form.validate();
        oldRight.getVerticalScrollBar().setUnitIncrement(10);
        oldRight.getVerticalScrollBar().getModel().setValue(
                                                            oldRight.getVerticalScrollBar().getModel().getMinimum());
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
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(null,
                                          "Sorry, was unable to save.  See console output for more information: "
                                                  + exc);
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
        if (rightPreferences == null || rightPreferences.hasChanged() == false)
            return;

        if (JOptionPane.showConfirmDialog(this, "Save the settings for this plugin?", "Save?",
                                          JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;

        Properties newPref = rightPreferences.getValues();

        String[] keys = Uniq.uniq(newPref.keys());
        for (int i = 0; i < keys.length; i++)
            funcs.setGlobalSetting(currentPlugin, keys[i], newPref.getProperty(keys[i]));
    }
}
