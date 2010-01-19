import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import settings.QuickSettings;
import settings.SettingWizard;
import settings.UserDatabaseWizard;
import util.ColorConstants;
import util.MenuIcons;

import callback_interfaces.PublicExposedFunctions;


/**
 * Menu - Each bot's menu needs access to its instance of PublicExposedFunctions
 * - Each menu needs to be informed of changes to it [Implement GuiCallback]
 */

public class JavaOpBotMenu extends JMenuBar implements ActionListener
{
    /**
	 * 
	 */
    private static final long         	serialVersionUID = 1L;
    // The main menus
    private final JMenu               	file;
    private final JMenu                 edit;
    private final JMenu                 connection;
    private final JMenu                 settings;

    // Under the "file" menu
    private final JMenuItem             reload           = new JMenuItem("Reload");
    private final JMenuItem             close            = new JMenuItem("Close");

    // Under the "Edit" menu
    private final JMenuItem             clear            = new JMenuItem("Clear");

    // Under the "Connect" menu
    private final JMenuItem             connect          = new JMenuItem("Connect");
    private final JMenuItem             reconnect        = new JMenuItem("Reconnect");
    private final JMenuItem             disconnect       = new JMenuItem("Disconnect");

    // Under the "Settings" menu
    private final JMenuItem             quickConfigure   = new JMenuItem("Quick configuration...");
    private final JMenuItem             configure        = new JMenuItem("This bot's settings...");
    private final JMenuItem             userdb           = new JMenuItem("Edit database...");
    private final JMenuItem             colors           = new JMenuItem("Edit colors...");

    private final PublicExposedFunctions out;
    private final JavaOpPanel            panel;

    private final Hashtable<String, JMenu>
    		menus = new Hashtable<String, JMenu>();
    private final Hashtable<JMenuItem, ActionListener>
    		callbacks = new Hashtable<JMenuItem, ActionListener>();

    public JavaOpBotMenu(PublicExposedFunctions out, JavaOpPanel panel)
    {
        this.out = out;
        this.panel = panel;

        // File
        reload.addActionListener(this);
        reload.setMnemonic('r');
        reload.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        reload.setIcon(MenuIcons.getIcon("reloadbot"));

        close.addActionListener(this);
        close.setMnemonic('c');
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        close.setIcon(MenuIcons.getIcon("closebot"));
        file = new JMenu("File");
        file.add(reload);
        file.add(close);
        file.setMnemonic('f');
        this.add(file);
        menus.put("file", file);

        // Edit
        clear.addActionListener(this);
        clear.setMnemonic('c');
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F7, 0));
        clear.setIcon(MenuIcons.getIcon("clearscreen"));
        edit = new JMenu("Edit");
        edit.add(clear);
        this.add(edit);
        menus.put("edit", edit);

        // Connection
        connect.addActionListener(this);
        connect.setMnemonic('c');
        connect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        connect.setIcon(MenuIcons.getIcon("connect"));
        disconnect.addActionListener(this);
        disconnect.setMnemonic('d');
        disconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        disconnect.setIcon(MenuIcons.getIcon("disconnect"));
        reconnect.addActionListener(this);
        reconnect.setMnemonic('r');
        reconnect.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        reconnect.setIcon(MenuIcons.getIcon("reconnect"));
        connection = new JMenu("Connection");
        connection.add(connect);
        connection.add(disconnect);
        connection.add(reconnect);
        connection.setMnemonic('o');
        this.add(connection);
        menus.put("connection", connection);

        // Settings
        quickConfigure.addActionListener(this);
        quickConfigure.setMnemonic('q');
        quickConfigure.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        quickConfigure.setIcon(MenuIcons.getIcon("quick"));
        configure.addActionListener(this);
        configure.setMnemonic('c');
        configure.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        configure.setIcon(MenuIcons.getIcon("setting"));
        userdb.addActionListener(this);
        userdb.setMnemonic('u');
        userdb.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        userdb.setIcon(MenuIcons.getIcon("database"));
        colors.addActionListener(this);
        colors.setMnemonic('o');
        colors.setIcon(MenuIcons.getIcon("color"));
        settings = new JMenu("Settings");
        settings.add(quickConfigure);
        settings.addSeparator();
        settings.add(configure);
        settings.add(userdb);
        settings.add(colors);
        settings.setMnemonic('s');
        this.add(settings);
        menus.put("settings", settings);
    }

    public void actionPerformed(ActionEvent e)
    {
        try
        {
            if (e.getSource() == reload)
            {
                String name = out.getName();
                out.getStaticExposedFunctionsHandle().botStop(name);
                out.getStaticExposedFunctionsHandle().botStart(name);
            }
            else if (e.getSource() == close)
            {
                out.getStaticExposedFunctionsHandle().botStop(out.getName());
            }
            else if (e.getSource() == clear)
            {
                panel.clear();
            }
            else if (e.getSource() == connect)
            {
                out.connect();
            }
            else if (e.getSource() == reconnect)
            {
                out.reconnect();
            }
            else if (e.getSource() == disconnect)
            {
                out.disconnect();
            }
            else if (e.getSource() == quickConfigure)
            {
                new QuickSettings(out);
            }
            else if (e.getSource() == configure)
            {
                new SettingWizard(out);
            }
            else if (e.getSource() == userdb)
            {
                new UserDatabaseWizard(out);
            }
            else if (e.getSource() == colors)
            {
                ColorConstants.showColorEditor();
            }
            else
            {
                ActionListener callback = (ActionListener) callbacks.get(e.getSource());

                if (callback == null)
                {
                    JOptionPane.showMessageDialog(null, "Unknown menu event!");
                }
                else
                {
                    ActionEvent newEvent = new ActionEvent(((JMenuItem) e.getSource()).getText(),
                            1, ((JMenuItem) e.getSource()).getText());
                    callback.actionPerformed(newEvent);
                }
            }
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(null, "Error: " + exc);
            exc.printStackTrace();
        }
    }

    public void addItem(String name, String whichMenu, int index, char mnemonic, KeyStroke hotkey,
            Icon icon, ActionListener callback)
    {
        JMenu menu = (JMenu) menus.get(whichMenu.toLowerCase());

        if (menu == null)
        {
            menu = new JMenu(whichMenu);
            this.add(menu);
            this.validate();
            menus.put(whichMenu.toLowerCase(), menu);
        }

        JMenuItem newItem = new JMenuItem(name);
        newItem.setMnemonic(mnemonic);
        newItem.addActionListener(this);
        if (hotkey != null)
            menu.setAccelerator(hotkey);
        if (icon != null)
            menu.setIcon(icon);
        if (index >= 0)
            menu.add(newItem, index);
        else
            menu.add(newItem);

        if (callback != null)
            callbacks.put(newItem, callback);

        this.invalidate();
        this.validate();
    }

    public void removeItem(String name, String whichMenu)
    {
        JMenu menu = (JMenu) menus.get(whichMenu.toLowerCase());

        if (menu == null)
            return;

        Component[] items = menu.getMenuComponents();
        for (int i = 0; i < items.length; i++)
            if (((JMenuItem) items[i]).getText().equalsIgnoreCase(name))
                menu.remove(items[i]);
    }

    public void addSeparator(String whichMenu)
    {
        JMenu menu = (JMenu) menus.get(whichMenu.toLowerCase());

        if (menu == null)
        {
            menu = new JMenu(whichMenu);
            this.add(menu);
            this.invalidate();
            this.validate();
            menus.put(whichMenu.toLowerCase(), menu);
        }

        menu.addSeparator();
    }

    public void addMenu(String name, int index, char mnemonic, Icon icon, ActionListener callback)
    {
        JMenu menu = (JMenu) menus.get(name.toLowerCase());

        if (menu != null)
            return;

        menu = new JMenu(name);
        menu.setMnemonic(mnemonic);
        if (icon != null)
            menu.setIcon(icon);
        if (callback != null)
            menu.addActionListener(callback);

        if (index != -1)
            this.add(menu, index);
        else
            this.add(menu);
        this.invalidate();
        this.validate();

        menus.put(name.toLowerCase(), menu);
    }

    public void removeMenu(String name)
    {
        JMenu menu = (JMenu) menus.get(name.toLowerCase());

        if (menu == null)
            return;

        menus.remove(name);

        this.remove(menu);
        this.invalidate();
        this.validate();
        this.repaint();
    }
}
