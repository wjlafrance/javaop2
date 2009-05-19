/*
 * Created on Apr 8, 2005
 * By iago
 */
package settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import plugin_interfaces.GenericPluginInterface;
import util.Uniq;
import util.gui.Gui;
import util.gui.PreferencesPanel;
import callback_interfaces.PublicExposedFunctions;

public class SettingWizard extends JFrame implements ListSelectionListener, WindowListener
{
	private static final long serialVersionUID = 1L;
	
	private JPanel form;
    private JTable list;
    
    private PublicExposedFunctions out;
    
    private PreferencesPanel rightPreferences;
    private String currentPlugin = "Default";
    private SelectPlugin []pluginList;
    
    private final JPanel right = new JPanel();
    
    public SettingWizard(PublicExposedFunctions out)
    {
        super("Configuring " + out.getName());

        this.out = out;

        // Add "default"

        form = new JPanel();
        this.getContentPane().add(form);
        this.setSize(700, 500);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(this);       
        Gui.center(this);
        form.setLayout(new BorderLayout(3, 3));
        
        // Add the list
        GenericPluginInterface []allPlugins = out.getStaticExposedFunctionsHandle().pluginGetAll(true);
        pluginList = new SelectPlugin[allPlugins.length];
        for(int i = 0; i < allPlugins.length; i++)
            pluginList[i] = new SelectPlugin(allPlugins[i], out.pluginIsActive(allPlugins[i].getName()));
        
        JScrollPane listScroller = new JScrollPane(list = new PluginTable(pluginList));
        listScroller.setPreferredSize(new Dimension(195, -1));
        this.getContentPane().add(listScroller, BorderLayout.WEST);
        list.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        list.getSelectionModel().addListSelectionListener(this);
        list.getSelectionModel().setSelectionInterval(0, 0);
        
        this.getContentPane().add(right, BorderLayout.CENTER);
        right.setLayout(new BorderLayout(10, 0));
        right.add(new JLabel("Please select a plugin to configure"), BorderLayout.CENTER);
        
        this.valueChanged(null);
        this.setVisible(true);
    }
    
    
    
    public void valueChanged(ListSelectionEvent e)
    {
        saveRight();
        
        // They clicked a new plugin on the list.  Load a new panel for the plugin
        String thisPlugin = (String) list.getModel().getValueAt(list.getSelectedRow(), 1);
        //String []settingNames = out.getLocalKeys(thisPlugin);
        
        Properties defaultSettings = out.getStaticExposedFunctionsHandle().pluginGetDefaultSettings(thisPlugin);
        Properties descriptions = out.getStaticExposedFunctionsHandle().pluginGetDescriptions(thisPlugin);
        
        Properties settings = out.getLocalSettingSection(thisPlugin);
//        for(int i = 0; i < settingNames.length; i++)
//            settings.setProperty(settingNames[i], out.getLocalSettingDefault(thisPlugin, settingNames[i], defaultSettings.getProperty(settingNames[i])));
        
        Hashtable components = out.getStaticExposedFunctionsHandle().pluginGetComponents(thisPlugin, settings);
        
        this.currentPlugin = thisPlugin;
        
        right.removeAll();
        right.add(new PluginPanel(thisPlugin, out.getStaticExposedFunctionsHandle()), BorderLayout.NORTH);
        JScrollPane scroller = new JScrollPane(rightPreferences = new PreferencesPanel(settings, defaultSettings, descriptions, components)); 
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.getVerticalScrollBar().setUnitIncrement(10);
        scroller.getVerticalScrollBar().setValueIsAdjusting(true);
        scroller.getVerticalScrollBar().setValue(0);
        scroller.getVerticalScrollBar().setValueIsAdjusting(false);
        
        right.add(scroller, BorderLayout.CENTER);
        right.validate();
        form.validate();
    }
    
    private class SelectPlugin
    {
        private final GenericPluginInterface plugin;
        
        private JCheckBox check;
        
        public SelectPlugin(GenericPluginInterface plugin, boolean selected)
        {
            this.plugin = plugin;
            this.check = new JCheckBox(plugin.getName(), selected);
        }
        public void toggleSelected()
        {
            out.pluginToggleActive(plugin.getName());
            
            check.setSelected(out.pluginIsActive(plugin.getName()));
        }
        public void setSelected(boolean selected)
        {
            check.setSelected(selected);
        }
        public boolean getSelected()
        {
            return check.isSelected();
        }
        public JCheckBox getCheck()
        {
            return check;
        }
        public GenericPluginInterface getPlugin()
        {
            return plugin;
        }
        public String toString()
        {
            return plugin.getName();
        }
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
        if(rightPreferences == null || rightPreferences.hasChanged() == false)
            return;

        if(JOptionPane.showConfirmDialog(this, "Save the settings for this plugin?", "Save?", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            return;
        
        Properties newPref = rightPreferences.getValues();
        
        String []keys = Uniq.uniq(newPref.keys());
        for(int i = 0; i < keys.length; i++)
            out.putLocalSetting(currentPlugin, keys[i], newPref.getProperty(keys[i]));
    }
    
    private class PluginTable extends JTable
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public PluginTable(SelectPlugin []plugins)
        {
            super(new BooleanTableModel(plugins));

            this.getColumnModel().getColumn(0).setPreferredWidth(20);
            this.getColumnModel().getColumn(0).setWidth(20);
            this.getColumnModel().getColumn(1).setPreferredWidth(175);
            this.getColumnModel().getColumn(1).setWidth(175);
            
            this.setCellSelectionEnabled(false);
            this.setShowGrid(false);
            this.setTableHeader(null);
            this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            this.validate();
        }
    }
    
    private class BooleanTableModel extends DefaultTableModel
    {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private final SelectPlugin []plugins;
        
        public BooleanTableModel(SelectPlugin []plugins)
        {
            super(plugins.length, 2);
            
            this.plugins = plugins;
        }

        public Class getColumnClass(int col) 
        {
            return col == 0 ? Boolean.class : super.getColumnClass(col);
        }
        
        public boolean isCellEditable(int row, int col)
        {
            return col == 0 ? true : false;
        }
        
        public Object getValueAt(int row, int col)
        {
            if(col == 0)
                return new Boolean((plugins[row]).getCheck().isSelected());
            
            return plugins[row].toString();
        }
        
        public void setValueAt(Object val, int row, int col)
        {
            plugins[row].toggleSelected();
        }
    }

}
