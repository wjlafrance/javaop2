package com.javaop._main;

import java.util.List;
import java.util.Arrays;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.javaop.pluginmanagers.PluginManager;

import com.javaop.bot.BotManager;
import com.javaop.bot.JavaOpFileStuff;


/*
 * Created on Dec 4, 2004 By iago
 */

/**
 * This is the main class that is called when the bot starts. It does the small
 * set up things, like: - Sets the directory to ~/.javaop2 - Creates the vector
 * of Plugin directories - Initializes the plugin manager - Creates a single
 * instance of BotCore for each bot we're loading - Handles the errors for
 * missing config file and missing database file
 *
 * @author iago
 *
 */
public class BotStart
{
	public static void main(String args[]) throws Throwable
	{
		try
		{
			configureLookAndFeel();
			/*
			 * First thing we're going to do is set our correct directory up.
			 * After this, if you use "RelativeFile" for an operation, it'll
			 * automatically put the file in this directory. Stupid, I know, but
			 * Java is like that.
			 */
			JavaOpFileStuff.setBaseDirectory();
			PluginManager.initialize(true);

			List<String> bots = getBots(Arrays.asList(args));

			for (String bot : bots) {
				System.out.println("Loading " + bot);
				BotManager.startBot(bot);
				Thread.sleep(2000);
			}
		} catch (Throwable t) {
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error loading bots: " + t);
		}
	}

	/**
	 * macOS system properties have to be set before AWT initializes; the
	 * theme has to be installed before any Swing component is created.
	 */
	private static void configureLookAndFeel() {
		if (ThemeSelector.isMac(System.getProperty("os.name"))) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("apple.awt.application.name", "JavaOp2");
			System.setProperty("apple.awt.application.appearance", "system");
		}
		try {
			Runnable install = () -> {
				try {
					LookAndFeelSetup.install();
				} catch (Exception | LinkageError e) {
					System.err.println("FlatLaf unavailable (" + e + "); using the system look and feel");
					useSystemLookAndFeel();
				}
			};
			if (SwingUtilities.isEventDispatchThread()) {
				install.run();
			} else {
				SwingUtilities.invokeAndWait(install);
			}
		} catch (Exception e) {
			System.err.println("Unable to set up the look and feel: " + e);
		}
	}

	private static void useSystemLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// no native look and feel? no big deal
		}
	}

	/** This will only return if one or more bots were found to load */
	private static List<String> getBots(List<String> base) {
		// If not bots were specified on the commandline, read the
		// _DefaultBots.txt file.
		return base.size() != 0 ? base : JavaOpFileStuff.getDefaultBots();
	}

}
