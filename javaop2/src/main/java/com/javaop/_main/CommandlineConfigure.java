/*
 * Created on Feb 12, 2005 By iago
 */

package com.javaop._main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.pluginmanagers.PluginManager;
import com.javaop.util.PersistantMap;

import com.javaop.bot.JavaOpFileStuff;


/**
 * @author iago
 *
 */
public class CommandlineConfigure
{
	private static final boolean  CLEAR = true;

	private static BufferedReader in    = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws IOException {
		JavaOpFileStuff.setBaseDirectory();
		PluginManager.initialize(false);

		while (true) {
			clear();

			System.out.println("1. Configure paths searched for plugins");
			System.out.println("2. Configure which bots are loaded");
			System.out.println("3. Manage bots");
			System.out.println("4. Add a new bot");
			System.out.println("5. Copy a bot");
			System.out.println("6. Remove a bot");
			System.out.println();
			System.out.println("0. Quit");
			System.out.println();

			int choice = Integer.parseInt(getPatternInput("Please make a selection", null, "[1234560]"));

			switch (choice) {
				case 1:
					configurePaths();
					break;
				case 2:
					configureLoadedBots();
					break;
				case 3:
					manageBots();
					break;
				case 4:
					addBot();
					break;
				case 5:
					copyBot();
					break;
				case 6:
					removeBot();
					break;
				case 0:
					System.out.println("-- exiting");
					System.exit(0);
			}
		}
	}

	private static void addBot() throws IOException {
		clear();

		System.out.println("The current bots:");
		getAndPrintAllBots(false);
		System.out.println();

		String newBot = getPatternInput("Name of the new bot?", null, "[a-zA-Z1-9. -_]+");
		JavaOpFileStuff.newBot(newBot);
		JavaOpFileStuff.setActivePlugins(newBot, PluginManager.getAllNames());
	}

	private static void copyBot() throws IOException {
		clear();

		System.out.println("The current bots:");
		List<String> bots = getAndPrintAllBots(true);
		System.out.println();
		System.out.println("0. Back");
		System.out.println();

		int base = getNumericInput("Which bot are we making a copy of?", null, bots.size());

		if (base == 0) {
			return;
		}

		String newName = getPatternInput("What would you like to name the copy?", null, "[a-zA-Z1-9. -_]+");

		JavaOpFileStuff.copyBot(bots.get(base - 1), newName);
	}

	private static void removeBot() throws IOException {
		clear();

		System.out.println("The current bots:");
		List<String> bots = getAndPrintAllBots(true);
		System.out.println();
		System.out.println("0. Back");
		System.out.println();

		int remove = getNumericInput("Which bot would you like to remove?", null, bots.size());

		if (remove == 0) {
			return;
		}

		JavaOpFileStuff.deleteBot(bots.get(remove - 1));
	}

	private static void configurePaths() throws IOException {
		while (true) {
			clear();

			System.out.println("Current paths are:");
			getAndPrintPluginPaths(false);
			System.out.println();
			System.out.println();
			System.out.println("1. Add path");
			System.out.println("2. Remove path");
			System.out.println();
			System.out.println("0. Back");
			System.out.println();

			int choice = Integer.parseInt(getPatternInput("Please make a selection", null, "[120]"));

			switch (choice) {
				case 1:
					String path = getPatternInput("Please enter the full path", null, ".+");
					JavaOpFileStuff.addPluginPath(path);
					break;
				case 2:
					List<String> bots = getAndPrintPluginPaths(true);
					int bot = getNumericInput("Which bot?", null, bots.size());
					JavaOpFileStuff.removePluginPath(bots.get(bot - 1));
					break;
				case 0:
					return;
			}
		}
	}

	private static void configureLoadedBots() throws IOException {
		while (true) {
			clear();
			System.out.println("The bots currently loading at startup:");
			getAndPrintDefaultBots(false);
			System.out.println();
			System.out.println();
			System.out.println("1. Add bot");
			System.out.println("2. Remove bot");
			System.out.println();
			System.out.println("0. Back");
			System.out.println();

			int choice = Integer.parseInt(getPatternInput("Please make a selection", null, "[120]"));
			switch (choice) {
				case 1:
					List<String> allBots = getAndPrintAllBots(true);
					int bot = getNumericInput("Which would you like to load at startup?", null, allBots.size());
					JavaOpFileStuff.addDefaultBot(allBots.get(bot - 1));
					break;
				case 2:
					List<String> defaultBots = getAndPrintDefaultBots(true);
					int num = getNumericInput("Which bot would you like to remove?", null, defaultBots.size());
					JavaOpFileStuff.removeDefaultBot(defaultBots.get(num - 1));
				default:
					return;
			}
		}
	}

	private static void manageBots() throws IOException {
		while (true) {
			clear();
			List<String> bots = getAndPrintAllBots(true);
			System.out.println();
			System.out.println("0. Back");
			System.out.println();
			int bot = getNumericInput("Please choose a bot", null, bots.size());

			if (bot == 0) {
				return;
			}

			manageBot(bots.get(bot - 1));
		}
	}

	private static void manageBot(String bot) throws IOException {
		while (true) {
			clear();

			System.out.println("Editing: " + bot);
			System.out.println();
			System.out.println("1. Enable a plugin");
			System.out.println("2. Disable a plugin");
			System.out.println("3. Enable all plugins");
			System.out.println("4. Disable all plugins");
			System.out.println("5. Configure a plugin");
			System.out.println("6. Get information on a plugin");
			System.out.println();
			System.out.println("0. Back");
			System.out.println();

			int choice = Integer.parseInt(getPatternInput("Please make a selection", null, "[1234560]"));

			switch (choice) {
				case 1: {
					List<String> plugins = getAndPrintAllPlugins(true);

					if (plugins.size() == 0) {
						System.out.println("(all plugins are being loaded already)");
					}
					System.out.println();
					System.out.println("0. Back");
					System.out.println();

					int plugin = getNumericInput("Please select a plugin to add", null, plugins.size());

					if (plugin != 0) {
						JavaOpFileStuff.addActivePlugin(bot, plugins.get(plugin - 1));
					}
					break;
				}
				case 2: {
					List<String> plugins = getAndPrintAllActivePlugins(bot, true);
					if (plugins.size() == 0) {
						System.out.println("(no plugins are being loaded yet)");
					}

					System.out.println();
					System.out.println("0. Back");
					System.out.println();

					int plugin = getNumericInput("Please select a plugin to remove", null, plugins.size());
					if (plugin != 0) {
						JavaOpFileStuff.removeActivePlugin(bot, plugins.get(plugin - 1));
					}
					break;
				}
				case 3:
					JavaOpFileStuff.setActivePlugins(bot, PluginManager.getAllNames());
					break;
				case 4:
					JavaOpFileStuff.setActivePlugins(bot, Collections.emptyList());
					break;
				case 5: {
					List<String> plugins = getAndPrintAllPlugins(true);
					System.out.println();
					System.out.println("0, Back");
					System.out.println();

					int plugin = getNumericInput("Please select a plugin to configure", null, plugins.size());

					if (plugin != 0) {
						plugin--;

						GenericPluginInterface thisPlugin = PluginManager.getPlugin(plugins.get(plugin));

						Properties defaults = thisPlugin.getDefaultSettingValues();
						Properties descriptions = thisPlugin.getSettingsDescription();
						PersistantMap settings = JavaOpFileStuff.getSettings(bot);
						String pluginName = thisPlugin.getName();

						Object[] keys = sortEnumeration(defaults.keys());

						for (Object key : keys) {
							clear();

							String currentSetting = settings.getNoWrite(pluginName, (String) key, defaults.getProperty((String) key));
							System.out.println();
							System.out.println(key + ":");
							System.out.println("Default setting: " + defaults.getProperty((String) key));
							System.out.println("Current setting: " + currentSetting);
							System.out.println("Description: " + descriptions.getProperty((String) key));
							System.out.println();

							settings.set(thisPlugin.getName(), (String) key, getPatternInput("New value?", currentSetting, ".*"));
						}
					}

					break;
				}
				case 6: {
					List<String> plugins = getAndPrintAllPlugins(true);
					if (plugins.size() == 0) {
						System.out.println("(no plugins are being loaded yet)");
					}

					System.out.println();
					System.out.println("0. Back");
					System.out.println();

					int num = getNumericInput("Please select a plugin to get info on", null, plugins.size());
					if (num != 0) {
						num--;

						GenericPluginInterface plugin = PluginManager.getPlugin(plugins.get(num));
						clear();
						System.out.println("Name: " + plugin.getFullName());
						System.out.println();
						System.out.println("Author: " + plugin.getAuthorName() + " <" + plugin.getAuthorEmail() + ">");
						System.out.println();
						System.out.println("Website: " + plugin.getAuthorWebsite());
						System.out.println();
						System.out.println("Description: " + plugin.getLongDescription());
						System.out.println();
						System.out.println("Press enter to continue...");
						System.in.read();
					}
					break;
				}
				case 0:
					return;
			}
		}
	}

	private static int getNumericInput(String prompt, String defaultIn, int max) {
		while (true) {
			int input = Integer.parseInt(getPatternInput(
					prompt + " (an integer 0 and " + max + ")",
					defaultIn, "[0-9]+"));

			if (input <= max) {
				return input;
			}

			System.out.println("Error: invalid value");
		}
	}

	private static String getPatternInput(String prompt, String defaultIn, String pattern) {
		while (true) {
			try {
				System.out.print(prompt + (defaultIn == null ? "" : " [" + defaultIn + "]") + " --> ");

				String text = in.readLine();

				if (text.equals("") && defaultIn != null) {
					return defaultIn;
				}

				if (text.matches(pattern)) {
					return text;
				}

				System.out.println("Error, please enter a valid value");
			} catch (IOException e) {
				System.err.println("This should never happen");
				e.printStackTrace();
			}
		}
	}

	private static void clear() {
		if (CLEAR) {
			System.out.println("Yes, this is a crappy way to clear; but it's the only easy platform independant one, and Windows doesn't support ANSI by default, so shush.");
			for (int i = 0; i < 100; i++) {
				System.out.println();
			}
		} else {
			System.out.println();
			System.out.println();
		}
	}

	private static List<String> getAndPrintPluginPaths(boolean printNumbers) throws IOException {
		List<String> allPlugins = JavaOpFileStuff.getRawPluginPaths();
		for (int i = 0; i < allPlugins.size(); i++) {
			System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + allPlugins.get(i));
		}
		return allPlugins;
	}

	private static List<String>getAndPrintDefaultBots(boolean printNumbers) throws IOException {
		List<String> defaultBots = JavaOpFileStuff.getDefaultBots();
		for (int i = 0; i < defaultBots.size(); i++) {
			System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + defaultBots.get(i));
		}
		return defaultBots;
	}

	private static List<String> getAndPrintAllBots(boolean printNumbers) throws IOException {
		List<String> allBots = JavaOpFileStuff.getAllBots();
		for (int i = 0; i < allBots.size(); i++) {
			System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + allBots.get(i));
		}
		return allBots;
	}

	private static List<String> getAndPrintAllPlugins(boolean printNumbers) throws IOException {
		List<String> plugins = PluginManager.getAllNames();

		for (int i = 0; i < plugins.size() - 1; i++) {
			System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + plugins.get(i));
		}

		return plugins;
	}

	private static List<String> getAndPrintAllActivePlugins(String bot, boolean printNumbers) throws IOException {
		List<String> plugins = JavaOpFileStuff.getActivePlugins(bot);

		for (int i = 0; i < plugins.size(); i++) {
			System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + plugins.get(i));
		}

		return plugins;
	}

	private static Object[] sortEnumeration(Enumeration<Object> e) {
		Vector<Object> objectVector = new Vector<>();

		while (e.hasMoreElements()) {
			objectVector.add(e.nextElement());
		}

		Object[] ret = objectVector.toArray();
		Arrays.sort(ret);

		return ret;
	}
}