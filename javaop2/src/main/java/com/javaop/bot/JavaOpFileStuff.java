/*
 * Created on Dec 21, 2004 By iago
 */
package com.javaop.bot;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Vector;

import com.javaop.plugin_interfaces.GenericPluginInterface;

import com.javaop.util.FileManagement;
import com.javaop.util.PersistantMap;
import com.javaop.util.RelativeFile;
import com.javaop.util.Uniq;
import com.javaop.util.UserDB;


/**
 * @author iago
 *
 */
public class JavaOpFileStuff
{
	public static final String EXT_SETTINGS       = ".jbb";
	public static final String EXT_SETTINGS_MATCH = ".*\\.jbb";
	public static final String EXT_DATABASE       = ".jdb";
	public static final String EXT_DATABASE_MATCH = ".*\\.jdb";
	public static final String EXT_PLUGIN         = ".plugin";
	public static final String EXT_PLUGIN_MATCH   = ".*\\.plugin";
	public static final String EXT_ALIASES        = ".aliases";
	public static final String EXT_CUSTOMFLAGS    = ".flags";

	public static final String GLOBAL_SETTINGS    = "_GlobalSettings.txt";
	public static final String PLUGIN_PATHS       = "_PluginPaths.txt";
	public static final String DEFAULT_BOTS       = "_DefaultBots.txt";

	public static void setBaseDirectory()
	{
		/*
		 * First thing we're going to do is set our correct directory up. After
		 * this, if you use "RelativeFile" for an operation, it'll automatically
		 * put the file in this directory. Stupid, I know, but Java is like
		 * that.
		 */
		System.setProperty("user.dir", System.getProperty("user.home") + "/.javaop2");
	}

	public static File getDefaultBotsFile()
	{
		return new RelativeFile(DEFAULT_BOTS);
	}

	public static File getPluginPathsFile()
	{
		return new RelativeFile(PLUGIN_PATHS);
	}

	public static File getPluginFile(String name)
	{
		return new RelativeFile(name + EXT_PLUGIN);
	}

	public static File getAliasesFile(String name)
	{
		return new RelativeFile(name + EXT_ALIASES);
	}

	public static File getCustomFlagsFile(String name)
	{
		return new RelativeFile(name + EXT_CUSTOMFLAGS);
	}

	/******************
	 * Plugin Paths
	 */
	public static String[] getRawPluginPaths()
	{
		return Uniq.uniq(getPluginPaths());
	}

	public static void addPluginPath(String plugin)
	{
		try
		{
			FileManagement.addLine(JavaOpFileStuff.getPluginPathsFile(), plugin);
		}
		catch (IOException e)
		{
			System.err.println("Error adding plugin path: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void removePluginPath(String plugin)
	{
		try
		{
			FileManagement.removeLine(JavaOpFileStuff.getPluginPathsFile(), plugin);
		}
		catch (IOException e)
		{
			System.err.println("Error removing plugin path: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	/****************
	 * Plugins
	 */
	public static String[] getAllPlugins()
	{
		String[] paths = Uniq.uniq(getPluginPaths());
		Vector<File> jars = new Vector<File>();

		for (String path : paths) {
			jars.addAll(FileManagement.search(new RelativeFile(path), ".*\\.jar"));
		}

		return Uniq.uniq(jars);
	}

	private static Vector<String> getPluginPaths()
	{
		try
		{
			File pluginFile = new RelativeFile(PLUGIN_PATHS);
			Vector<String> ret = FileManagement.getFile(pluginFile);

			if (ret == null) {
				System.err.println("Plugin paths file not found -- using defaults (" + pluginFile
						+ ")");
			}

			addIfExists(ret, new RelativeFile("Plugins"));
			addIfExists(ret, new File("C:\\JavaOp-Plugins"));
			addIfExists(ret, new File("C:\\Program Files\\JavaOp2\\JavaOp-Plugins"));
			addIfExists(ret, new File("/usr/local/JavaOp-Plugins"));

			return ret;
		}
		catch (IOException e)
		{
			System.err.println("Error reading plugin paths: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	private static void addIfExists(Vector v, File f)
	{
		if (f.exists() && !v.contains(f)) {
			v.add(f);
		}
	}

	/**
	 * Gets a new PluginMain instance from <b>location</b>
	 * @param location Location (web or local) of the JAR
	 * @return PluginMain instance
	 */
	public static GenericPluginInterface loadPlugin(String location) throws
		ClassNotFoundException, MalformedURLException,
		InstantiationException, IllegalAccessException
	{
		URL url;

		try
		{
			url = new URL(location);
		}
		catch (MalformedURLException e)
		{
			url = new URL("file:///" + location);
		}

		return loadPlugin(url);
	}

	/**
	 * Loads PluginMain from <b>url</b>
	 * @param url Location to load from, local or web
	 */
	public static GenericPluginInterface loadPlugin(URL url)
		throws ClassNotFoundException, IllegalAccessException,
		InstantiationException
	{
		URL[] urls =
		{ url };
		URLClassLoader ucl = new URLClassLoader(urls);
		Class cl = ucl.loadClass("PluginMain");
		return (GenericPluginInterface) cl.newInstance();
	}

	/****************
	 * Active plugins
	 */
	public static String[] getActivePlugins(String bot)
	{
		try
		{
			return FileManagement.getUniqueLines(JavaOpFileStuff.getPluginFile(bot));
		}
		catch (IOException e)
		{
			System.err.println("Error getting active plugins: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void setActivePlugins(String bot, String[] plugins)
	{
		FileManagement.deleteFile(JavaOpFileStuff.getPluginFile(bot));

		for (String plugin : plugins) {
			addActivePlugin(bot, plugin);
		}
	}

	public static boolean isActivePlugin(String bot, String plugin)
	{
		try
		{
			if (plugin.equalsIgnoreCase(new PluginMain().getName())) {
				return true;
			}

			return FileManagement.findLine(getPluginFile(bot), plugin);
		}
		catch (IOException e)
		{
			System.err.println("Error checking if active plugin: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void addActivePlugin(String bot, String plugin)
	{
		try
		{
			if (plugin.equalsIgnoreCase(new PluginMain().getName())) {
				return;
			}

			FileManagement.addLine(JavaOpFileStuff.getPluginFile(bot), plugin);
		}
		catch (IOException e)
		{
			System.err.println("Error adding active plugin: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void removeActivePlugin(String bot, String plugin)
	{
		try
		{
			if (plugin.equalsIgnoreCase(new PluginMain().getName())) {
				return;
			}

			FileManagement.removeLine(JavaOpFileStuff.getPluginFile(bot), plugin);
		}
		catch (IOException e)
		{
			System.err.println("Error removing active plugin: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void toggleActivePlugin(String bot, String plugin)
	{
		if (plugin.equalsIgnoreCase(new PluginMain().getName())) {
			return;
		}

		if (isActivePlugin(bot, plugin)) {
			removeActivePlugin(bot, plugin);
		} else {
			addActivePlugin(bot, plugin);
		}
	}

	/****************
	 * Bots
	 */

	/** The bots are all the *.jbb files in the current folder */
	public static String[] getAllBots()
	{
		String[] bots = Uniq.uniq(FileManagement.search(new RelativeFile(""), ".*\\.jbb"));

		for (int i = 0; i < bots.length; i++)
		{
			bots[i] = bots[i].replaceAll("\\" + EXT_SETTINGS + "$", "");
			bots[i] = bots[i].replaceAll(".*[\\\\/]", "");
		}

		return bots;
	}

	public static void deleteBot(String name)
	{
		removeDefaultBot(name);
		BotManager.stopBot(name);

		new RelativeFile(name + ".jbb").delete();
		new RelativeFile(name + ".jdb").delete();
		new RelativeFile(name + ".aliases").delete();
		new RelativeFile(name + ".jbb.remote").delete();
		new RelativeFile(name + ".plugin").delete();
		new RelativeFile(name + ".quotes").delete();
		new RelativeFile(name + ".seen").delete();
		new RelativeFile(name + ".flags").delete();
	}

	public static void newBot(String name)
	{
		getSettings(name);
	}

	public static void copyBot(String oldName, String newName)
	{
		try
		{
			FileManagement.copyFile(new RelativeFile(oldName + ".jbb"), new RelativeFile(newName
					+ ".jbb"));
			FileManagement.copyFile(new RelativeFile(oldName + ".aliases"), new RelativeFile(
					newName + ".aliases"));
			FileManagement.copyFile(new RelativeFile(oldName + ".jdb"), new RelativeFile(newName
					+ ".jdb"));
			FileManagement.copyFile(new RelativeFile(oldName + ".plugin"), new RelativeFile(newName
					+ ".plugin"));
		}
		catch (IOException e)
		{
			System.err.println("Error copying bot: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	/****************
	 * Default bots
	 */

	public static String[] getDefaultBots()
	{
		try
		{
			return FileManagement.getUniqueLines(JavaOpFileStuff.getDefaultBotsFile());
		}
		catch (IOException e)
		{
			System.err.println("Error getting default bots: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static boolean isDefaultBot(String bot)
	{
		try
		{
			return FileManagement.findLine(JavaOpFileStuff.getDefaultBotsFile(), bot);
		}
		catch (IOException e)
		{
			System.err.println("Error checking if default bot: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void toggleDefault(String bot)
	{
		try
		{
			if (isDefaultBot(bot)) {
				FileManagement.removeLine(JavaOpFileStuff.getDefaultBotsFile(), bot);
			} else {
				FileManagement.addLine(JavaOpFileStuff.getDefaultBotsFile(), bot);
			}
		}
		catch (IOException e)
		{
			System.err.println("Error toggling default bot: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void setDefaultBots(String[] bots)
	{
		try
		{
			FileManagement.setFile(JavaOpFileStuff.getDefaultBotsFile(), bots);
		}
		catch (IOException e)
		{
			System.err.println("Error setting default bots: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void addDefaultBot(String bot)
	{
		try
		{
			FileManagement.addLine(JavaOpFileStuff.getDefaultBotsFile(), bot);
		}
		catch (IOException e)
		{
			System.err.println("Error adding default bot: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	public static void removeDefaultBot(String bot)
	{
		try
		{
			FileManagement.removeLine(JavaOpFileStuff.getDefaultBotsFile(), bot);
		}
		catch (IOException e)
		{
			System.err.println("Error removing default bot: " + e);
			System.exit(0);
			throw new RuntimeException();
		}
	}

	/***************
	 * Settings
	 */
	public static PersistantMap getGlobalSettings()
	{
		return new PersistantMap(new RelativeFile(GLOBAL_SETTINGS), "These are the bot's settings");
	}

	public static PersistantMap getSettings(String botName)
	{
		return new PersistantMap(new RelativeFile(botName + ".jbb"), "These are the bot's settings");
	}

	public static PersistantMap getAliases(String botName)
	{
		return new PersistantMap(getAliasesFile(botName),
				"These are the aliases; each line is in the format alias=command");
	}

	public static PersistantMap getCustomFlags(String botName)
	{
		return new PersistantMap(
				getCustomFlagsFile(botName),
				"These are customized flags for commands.  You may add 'command=FLAGS' for any command, and it will be overridden.");
	}

	public static UserDB getUserDB(String botName)
	{
		return new UserDB(new RelativeFile(botName + ".jdb"));
	}

	public static File getDB(String bot)
	{
		return new RelativeFile(bot + EXT_DATABASE);
	}
}
