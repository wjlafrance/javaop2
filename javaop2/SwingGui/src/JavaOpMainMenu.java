package com.javaop.SwingGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.PluginException;
import com.javaop.util.PersistantMap;

import com.javaop.SwingGui.settings.GlobalSettingWizard;
import com.javaop.SwingGui.util.MenuIcons;
import com.javaop.SwingGui.gui.LoadWebsite;


/**
 * Main Menu - Needs access to StaticExposedFunctions for creating bots
 */
public class JavaOpMainMenu extends JMenuBar implements ActionListener,
		ChangeListener
{
    /**
	 * 
	 */
    private static final long            serialVersionUID = 1L;
    private final JavaOpFrame            frame;
    private final StaticExposedFunctions staticFuncs;
    private static boolean               check = false;

    // The main menus
    private final JMenu                  file;
    private final JMenu                  settings;
    private final JMenu                  windows;
    private final JMenu                  help;

    // Under the "file" menu
    private final JMenu                  loadBot
    		= new JMenu("Load bot");
    private final JMenu                  deleteBot
    		= new JMenu("Delete bot");
    private final JMenu                  defaultBots
    		= new JMenu("Load on startup");
    private final JMenuItem              createNew
    		= new JMenuItem("New bot...");
    private final JMenuItem              exit
    		= new JMenuItem("Exit");

    // Under the "Settings" menu
    private final JMenuItem              configureG
    		= new JMenuItem("Global settings...");

    // Under the "Windows" menu
    private final JMenuItem              maximizeAll
    		= new JMenuItem("Maximize All");
    private final JMenuItem              minimizeAll
    		= new JMenuItem("Minimize All");
    private final JMenuItem              cascade
    		= new JMenuItem("Cascade");
    private final JMenuItem              tile
    		= new JMenuItem("Tile");

    // Under the "Help" menu
    private final JMenuItem              news
    		= new JMenuItem("News...");
    private final JMenuItem              introduction   = new JMenuItem("Introduction...");
    private final JMenuItem              botSetup   	= new JMenuItem("Bot setup...");
    private final JMenuItem              moderation     = new JMenuItem("Moderation...");
    private final JMenuItem              writingPlugins = new JMenuItem("Writing plugins...");
    private final JMenuItem              faq            = new JMenuItem("FAQ...");
    private final JMenuItem              credits        = new JMenuItem("Credits...");

    private final BotLoader              loader         = new BotLoader();
    private final BotDelete              deleter        = new BotDelete();
    private final BotChecker             checker        = new BotChecker();

    private final JDesktopPane           desktop;

    public JavaOpMainMenu(StaticExposedFunctions staticFuncs,
    		JDesktopPane desktop, JavaOpFrame frame)
    {
        this.staticFuncs = staticFuncs;
        this.desktop = desktop;
        this.frame = frame;

        // This check is for debugging, to ensure that I don't accidentally load
        // this twice
        if (check) {
            System.err.println("ERROR! Loading JavaOpMainMenu more than once!");
            System.exit(1);
        }
        check = true;

        loadBot.setIcon(MenuIcons.getIcon("loadbot"));
        deleteBot.setIcon(MenuIcons.getIcon("deletebot"));
        defaultBots.setIcon(MenuIcons.getIcon("los"));

        createNew.addActionListener(this);
        createNew.setMnemonic('n');
        createNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
        		ActionEvent.CTRL_MASK));
        createNew.setIcon(MenuIcons.getIcon("newbot"));
        exit.addActionListener(this);
        exit.setMnemonic('x');
        exit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
        		ActionEvent.CTRL_MASK));
        exit.setIcon(MenuIcons.getIcon("exit"));
        file = new JMenu("File");
        file.add(createNew);
        file.addSeparator();
        file.add(loadBot);
        file.add(deleteBot);
        file.add(defaultBots);
        file.addSeparator();
        file.add(exit);
        file.setMnemonic('f');
        file.addChangeListener(this);
        this.add(file);

        configureG.addActionListener(this);
        configureG.setMnemonic('g');
        configureG.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G,
        		ActionEvent.CTRL_MASK));
        configureG.setIcon(MenuIcons.getIcon("global"));
        settings = new JMenu("Settings");
        settings.add(configureG);
        this.add(settings);
        settings.setMnemonic('s');

        minimizeAll.addActionListener(this);
        minimizeAll.setIcon(MenuIcons.getIcon("minimize"));
        maximizeAll.addActionListener(this);
        maximizeAll.setIcon(MenuIcons.getIcon("maximize"));
        cascade.addActionListener(this);
        cascade.setIcon(MenuIcons.getIcon("cascade"));
        tile.addActionListener(this);
        tile.setIcon(MenuIcons.getIcon("tile"));
        windows = new JMenu("Windows");
        windows.addChangeListener(this);
        this.add(windows);

        help = new JMenu("Help");
        help.add(news);
        help.add(introduction);
        help.add(botSetup);
        help.add(moderation);
        help.add(writingPlugins);
        help.add(faq);
        help.add(credits);
        news.addActionListener(new WebpageLoader(
        		"http://javaop.googlecode.com"));
        news.setMnemonic('n');
        news.setIcon(MenuIcons.getIcon("news"));
        introduction.addActionListener(new WebpageLoader(
        		"http://code.google.com/p/javaop/wiki/Introduction"));
        introduction.setMnemonic('i');
        introduction.setIcon(MenuIcons.getIcon("intro"));
        botSetup.addActionListener(new WebpageLoader(
        		"http://code.google.com/p/javaop/wiki/BotSetup"));
        botSetup.setMnemonic('b');
        botSetup.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        botSetup.setIcon(MenuIcons.getIcon("started"));
        moderation.addActionListener(new WebpageLoader("http://www.javaop.com/moderation.html"));
        moderation.setMnemonic('m');
        moderation.setIcon(MenuIcons.getIcon("mod"));
        writingPlugins.addActionListener(new WebpageLoader("http://www.javaop.com/plugins.html"));
        writingPlugins.setMnemonic('w');
        writingPlugins.setIcon(MenuIcons.getIcon("plug"));
        faq.addActionListener(new WebpageLoader("http://www.javaop.com/faq.html"));
        faq.setMnemonic('f');
        faq.setIcon(MenuIcons.getIcon("faq"));
        credits.addActionListener(new WebpageLoader(
        		"http://code.google.com/p/javaop/wiki/Contributers"));
        credits.setMnemonic('c');
        credits.setIcon(MenuIcons.getIcon("credit"));
        this.add(help);

    }

    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == createNew) {
                String name = "";

                for (;;) {
                    name = JOptionPane.showInputDialog(null, "Name?", name);
                    if (name == null)
                        return;
                    if (name.matches("[\\w\\_\\-\\.]+")) {
                    	staticFuncs.botStart(name);
                        return;
                    }
                    JOptionPane.showMessageDialog(null,
                    		"Please enter a name containing at least one character, and only the "
                    		+ "characters a-z, A-Z, 0-9, or '.-_'");
                }
            }
            else if (e.getSource() == exit)
            {
                System.exit(0);
            }
            else if (e.getSource() == configureG)
            {
                new GlobalSettingWizard(staticFuncs);
            }
            else if (e.getSource() == maximizeAll)
            {
                frame.maximizeAll();
            }
            else if (e.getSource() == minimizeAll)
            {
                frame.minimizeAll();
            }
            else if (e.getSource() == cascade)
            {
                frame.cascade();
            }
            else if (e.getSource() == tile)
            {
                frame.tile();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "Unknown menu event!");
            }
        }
        catch (Exception exc)
        {
            JOptionPane.showMessageDialog(null, "Error: " + exc);
            exc.printStackTrace();
        }
    }

    public void updateWindowsMenu()
    {
        windows.removeAll();
        windows.add(maximizeAll);
        windows.add(minimizeAll);
        windows.add(cascade);
        windows.add(tile);
        windows.addSeparator();

        JInternalFrame[] frames = desktop.getAllFrames();

        Arrays.sort(frames, new Comparator()
        {
            public int compare(Object arg0, Object arg1)
            {
                return ((JavaOpPanel) arg0).getTitle().compareTo(((JavaOpPanel) arg1).getTitle());
            }
        });
        for (int i = 0; i < frames.length; i++)
        {
            JCheckBoxMenuItem thisItem = new JCheckBoxMenuItem(frames[i].getTitle());

            if (i < 9)
            {
                thisItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1 + i,
                                                               ActionEvent.CTRL_MASK));
            }

            if (desktop.getSelectedFrame() == frames[i])
                thisItem.setSelected(true);

            thisItem.setIcon(frames[i].getFrameIcon());

            thisItem.addActionListener(new BotSelector((JavaOpPanel) frames[i]));

            windows.add(thisItem);
        }
    }

    public void stateChanged(ChangeEvent e)
    {
        if (e.getSource() == file)
        {
            loadBot.removeAll();
            deleteBot.removeAll();
            defaultBots.removeAll();

            String[] bots = staticFuncs.botGetAllNames();

            for (int i = 0; i < bots.length; i++)
            {
                PersistantMap settings = staticFuncs.botGetSettings(bots[i]);

                String info = "'"
                        + settings.getNoWrite("Battle.net Login Plugin", "username", "<unknown>")
                        + "' on '"
                        + settings.getNoWrite(" Default", "server", "<unknown>")
                        + "' in channel '"
                        + settings.getNoWrite("Battle.net Login Plugin", "home channel",
                                              "<unknown>") + "'; game: "
                        + settings.getNoWrite("Battle.net Login Plugin", "game", "<unknown>");
                JMenuItem item = new JMenuItem(bots[i]);
                item.setToolTipText(info);
                loadBot.add(item);
                item.addActionListener(loader);

                item = new JMenuItem(bots[i]);
                item.setToolTipText(info);
                deleteBot.add(item);
                item.addActionListener(deleter);

                try
                {
                    JCheckBoxMenuItem check = new JCheckBoxMenuItem(bots[i]);
                    check.setToolTipText(info);
                    check.setSelected(staticFuncs.botIsDefault(bots[i]));
                    check.addActionListener(checker);
                    defaultBots.add(check);
                }
                catch (IOException exc)
                {
                    defaultBots.add(new JMenuItem("Error loading default bots: " + exc));
                }
            }

            if (bots.length == 0)
            {
                loadBot.add("-- No bots, please create new --");
                deleteBot.add("-- No bots, please create new --");
                defaultBots.add("-- No bots, please create new --");
            }

            loadBot.validate();
            deleteBot.validate();
            defaultBots.validate();
        }
        else if (e.getSource() == windows)
        {
            updateWindowsMenu();
        }
    }

    private class BotSelector implements ActionListener
    {
        private final JavaOpPanel bot;

        public BotSelector(JavaOpPanel bot)
        {
            this.bot = bot;
        }

        public void actionPerformed(ActionEvent e)
        {
            bot.select();
        }
    }

    private class BotLoader implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                String name = ((JMenuItem) e.getSource()).getText();

                if (frame.getBotByName(name) == null)
                	staticFuncs.botStart(name);
                else
                    frame.select(name);
            }
            catch (PluginException exc)
            {
            	staticFuncs.systemMessage(ErrorLevelConstants.ERROR, "Unable to start bot: " + exc);
            }
            catch (IOException exc)
            {
            	staticFuncs.systemMessage(ErrorLevelConstants.ERROR, "Unable to start bot: " + exc);
            }
        }
    }

    private class BotDelete implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String name = ((JMenuItem) e.getSource()).getText();
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete bot " + name
                    + "?", "Delete?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
            	staticFuncs.botDelete(name);
        }
    }

    private class BotChecker implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                String name = ((JMenuItem) e.getSource()).getText();
                staticFuncs.botToggleDefault(name);
            }
            catch (Exception exc)
            {
            	staticFuncs.systemMessage(ErrorLevelConstants.ERROR, "Unable to set defaults: " + exc);
            }
        }
    }

    class WebpageLoader implements ActionListener
    {
        private final String site;

        public WebpageLoader(String site)
        {
            this.site = site;
        }

        public void actionPerformed(ActionEvent e)
        {
            new LoadWebsite(site);
        }
    }

}
