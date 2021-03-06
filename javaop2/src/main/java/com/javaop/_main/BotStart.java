package com.javaop._main;

import java.util.List;
import java.util.Arrays;
import javax.swing.JOptionPane;

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
			// If we're on OS X, make it look pretty
			System.setProperty("apple.laf.brushMetalLook", "true");
			System.setProperty("apple.awt.graphics.UseQuartz", "true");
			System.setProperty("apple.laf.useScreenMenuBar", "true");
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

	/** This will only return if one or more bots were found to load */
	private static List<String> getBots(List<String> base) {
		// If not bots were specified on the commandline, read the
		// _DefaultBots.txt file.
		return base.size() != 0 ? base : JavaOpFileStuff.getDefaultBots();
	}

}
