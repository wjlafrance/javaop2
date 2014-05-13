/*
 * Created on Jan 18, 2005 By iago
 */
package com.javaop.pluginmanagers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.jar.Attributes;
import java.net.JarURLConnection;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import com.javaop.constants.ErrorLevelConstants;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;

import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.ErrorMessage;
import com.javaop.util.RelativeFile;
import com.javaop.util.Uniq;

import com.javaop.bot.BotCoreStatic;
import com.javaop.bot.JavaOpFileStuff;
import com.javaop.bot.PluginMain;


/**
 * @author iago
 *
 */
public class PluginManager {
	private static Hashtable<String, GenericPluginInterface> allPlugins =
			new Hashtable<>();
	private Hashtable<String, GenericPluginInterface>  activePlugins =
			new Hashtable<>();

	/**************
	 * These static functions are run exactly once, when the bot loads.
	 */
	public static void initialize(boolean load) {
		try {
			for (String searchPath : JavaOpFileStuff.getAllPlugins()) {
				loadFile(searchPath, load);
			}

			if (allPlugins.size() == 0) {
				ErrorMessage.error("Unable to find any plugins. Please download the plugin "
						+ "package and extract them somewhere. You will be prompted to find "
						+ "them.", false);

				try {
					JFileChooser chooser = new JFileChooser(new RelativeFile(""));
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					chooser.setDialogTitle("Please choose the folder where the plugins were "
							+ "extracted to (in the future, place all plugins in this folder!)");
					int selection = chooser.showOpenDialog(null);
					if (selection == JFileChooser.CANCEL_OPTION) {
						JOptionPane.showMessageDialog(null, "The plugin folder must be selected "
								+ "before the bot can load.");
						System.exit(1);
					}

					JavaOpFileStuff.addPluginPath(chooser.getSelectedFile().getAbsolutePath());
				} catch (Throwable e) {
					System.err.print("Unable to find any plugins.  Please enter the path where the plugins can be found --> ");
					String path = new BufferedReader(new InputStreamReader(System.in)).readLine();
					JavaOpFileStuff.addPluginPath(path);
				}

				for (String searchPath : JavaOpFileStuff.getAllPlugins()) {
					loadFile(searchPath, load);
				}
			}

			GenericPluginInterface defaultPlugin = new PluginMain();
			allPlugins.put(defaultPlugin.getName(), defaultPlugin);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				JOptionPane.showMessageDialog(null, e.toString());
			} catch (Exception ignored) {
			}
		}
	}

	private static void loadFile(String url, boolean load) {
		try {
			loadFile(new URL(url), load);
		} catch (MalformedURLException e) {
			try {
				loadFile(new URL("jar:file:///" + url + "!/"), load);
			} catch (MalformedURLException e2) {
				System.err.println("--> Unable to load plugin: " + url);
				System.out.println(e);
				System.out.println(e2);
			}
		}
	}

	private static void loadFile(URL url, boolean load) {
		String mainClass = "PluginMain";
		try {
			URL[] urls = { url };
			URLClassLoader ucl = new URLClassLoader(urls);
			JarURLConnection uc = (JarURLConnection)url.openConnection();
			Attributes attr = uc.getMainAttributes();

			if (attr != null) {
				mainClass = attr.getValue(Attributes.Name.MAIN_CLASS);
			}
			if (mainClass == null) {
				throw new Exception("Plugin specifies null main class.");
			}

			System.out.println("Loading plugin: " + mainClass);

			Class cl = ucl.loadClass(mainClass);
			GenericPluginInterface plugin = (GenericPluginInterface) cl.newInstance();

			allPlugins.put(plugin.getName(), plugin);

			if (load) {
				plugin.load(BotCoreStatic.getInstance());
			}

			plugin.setGlobalDefaultSettings(BotCoreStatic.getInstance());
		} catch (ClassNotFoundException e) {
			System.err.println("   --> Load failed (Plugin '" + url
					+ "' doesn't have main class: " + mainClass);
			e.printStackTrace();
		} catch (ClassCastException e) {
			System.err.println("   --> Load failed (Plugin '" + url
					+ "''s main class doesn't implement PluginInterface");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Unable to load plugin file: " + url);
			e.printStackTrace();
		}
	}

	/***************
	 * These non-static functions are run for each bot
	 */
	/**
	 * This loads a plugin into memory for the current instance, calling the
	 * plugin's activate() function
	 */
	public void activatePlugins(PublicExposedFunctions out, PluginCallbackRegister register) throws IOException {
		Enumeration<GenericPluginInterface> e = allPlugins.elements();

		while (e.hasMoreElements()) {
			try {
				GenericPluginInterface plugin = (GenericPluginInterface) (e.nextElement().getClass().newInstance());

				out.pluginSetDefaultSettings(plugin.getName());

				if (JavaOpFileStuff.isActivePlugin(out.getName(), plugin.getName())) {
					System.out.println("Activating plugin: " + plugin.getName());

					plugin.activate(out, register);
					activePlugins.put(plugin.getName(), plugin);
				}
			} catch (IllegalAccessException exc) {
				System.err.println("Unable to load plugin: IllegalAccessException. Stack trace on console.");
				exc.printStackTrace();
			} catch (InstantiationException exc) {
				System.err.println("Unable to load plugin: InstantiationException. Stack trace on console.");
				exc.printStackTrace();
			}
		}

		if (activePlugins.size() < 2) {
			e = allPlugins.elements();

			while (e.hasMoreElements()) {
				try {
					GenericPluginInterface plugin = (GenericPluginInterface) (e.nextElement().getClass().newInstance());
					System.out.println("Plugin: " + plugin.getName());
				} catch (IllegalAccessException exc) {
					System.err.println("Unable to load plugin: IllegalAccessException. Stack trace on console.");
					exc.printStackTrace();
				} catch (InstantiationException exc) {
					System.err.println("Unable to load plugin: InstantiationException. Stack trace on console.");
					exc.printStackTrace();
				}
			}
			out.systemMessage(ErrorLevelConstants.ALERT, "It appears that this bot is new. "
					+ "The important plugins have been enabled.  To connect, please select "
					+ "'Configure' under the 'Settings' menu, click on 'Battle.net Login "
					+ "Plugin', and fill in your username, password, cdkey, and game client. "
					+ "Then, under the 'Connection' menu, choose 'Connect'");
		}
	}

	/**
	 * This unloads a plugin from memory for the current instance, calling the
	 * plugin's deactivate() function
	 */
	public void deactivatePlugins(PluginCallbackRegister register) {
		Enumeration<GenericPluginInterface> e = activePlugins.elements();

		while (e.hasMoreElements()) {
			((GenericPluginInterface) e.nextElement()).deactivate(register);
		}
	}

	public static List<String> getAllNames() {
		return Uniq.uniq(allPlugins.keys());
	}

	public static GenericPluginInterface getPlugin(String name) {
		return (GenericPluginInterface) allPlugins.get(name);
	}
}
