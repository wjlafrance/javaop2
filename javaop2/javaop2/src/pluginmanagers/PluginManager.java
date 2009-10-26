/*
 * Created on Jan 18, 2005 By iago
 */
package pluginmanagers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import constants.ErrorLevelConstants;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;

import plugin_interfaces.GenericPluginInterface;
import util.ErrorMessage;
import util.RelativeFile;
import util.Uniq;

import bot.BotCoreStatic;
import bot.JavaOpFileStuff;
import bot.PluginMain;


/**
 * @author iago
 * 
 */
public class PluginManager
{
    private static Hashtable allPlugins    = new Hashtable();
    private Hashtable        activePlugins = new Hashtable();

    /**************
     * These static functions are run exactly once, when the bot loads.
     */
    public static void initialize(boolean load)
    {
        try
        {
            String[] searchPaths = JavaOpFileStuff.getAllPlugins();
            for (int i = 0; i < searchPaths.length; i++)
            {
                if (searchPaths[i].toLowerCase().indexOf("stayconnected") >= 0)
                    continue;

                loadFile(searchPaths[i], load);
            }

            if (allPlugins.size() == 0)
            {
                ErrorMessage.error(
                                   "Unable to find any plugins.\nPlease download the plugin package from\nhttp://www.javaop.com/download.html\nand extract them somewhere.  You will be prompted to find them.",
                                   false);

                try
                {
                    JFileChooser chooser = new JFileChooser(new RelativeFile(""));
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    chooser.setDialogTitle("Please choose the folder where the plugins were extracted to (in the future, place all plugins in this folder!)");
                    int selection = chooser.showOpenDialog(null);
                    if (selection == JFileChooser.CANCEL_OPTION)
                    {
                        JOptionPane.showMessageDialog(null,
                                                      "The plugin folder must be selected before the bot can load.");
                        System.exit(1);
                    }

                    JavaOpFileStuff.addPluginPath(chooser.getSelectedFile().getAbsolutePath());
                }
                catch (Throwable e)
                {
                    System.err.print("Unable to find any plugins.  Please enter the path where the plugins can be found --> ");
                    String path = new BufferedReader(new InputStreamReader(System.in)).readLine();
                    JavaOpFileStuff.addPluginPath(path);
                }

                searchPaths = JavaOpFileStuff.getAllPlugins();
                for (int i = 0; i < searchPaths.length; i++)
                    loadFile(searchPaths[i], load);
            }

            GenericPluginInterface defaultPlugin = new PluginMain();
            allPlugins.put(defaultPlugin.getName(), defaultPlugin);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            try
            {
                JOptionPane.showMessageDialog(null, e.toString());
            }
            catch (Exception ignored)
            {
            }
        }
    }

    private static void loadFile(String url, boolean load)
    {
        try
        {
            loadFile(new URL(url), load);
        }
        catch (MalformedURLException e)
        {
            try
            {
                loadFile(new URL("file:///" + url), load);
            }
            catch (MalformedURLException e2)
            {
                System.err.println("--> Unable to load plugin: " + url);
                System.out.println(e);
                System.out.println(e2);
            }
        }
    }

    private static void loadFile(URL url, boolean load)
    {
        try
        {
            System.out.println("Loading plugin: " + url);

            URL[] urls =
            { url };
            URLClassLoader ucl = new URLClassLoader(urls);
            Class cl = ucl.loadClass("PluginMain");
            GenericPluginInterface plugin = (GenericPluginInterface) cl.newInstance();

            allPlugins.put(plugin.getName(), plugin);

            if (load)
                plugin.load(BotCoreStatic.getInstance());

            plugin.setGlobalDefaultSettings(BotCoreStatic.getInstance());
        }
        catch (ClassNotFoundException e)
        {
            System.err.println("   --> Load failed (Plugin '" + url
                    + "' doesn't have required PluginMain.class.");
            System.err.println(e);
            e.printStackTrace();
        }
        catch (ClassCastException e)
        {
            System.err.println("   --> Load failed (Plugin '" + url
                    + "''s PluginMain.class doesn't implement PluginInterface");
        }
        catch (Exception e)
        {
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
    public void activatePlugins(PublicExposedFunctions out, PluginCallbackRegister register) throws IOException
    {
        Enumeration e = allPlugins.elements();

        while (e.hasMoreElements())
        {
            try
            {
                GenericPluginInterface plugin = (GenericPluginInterface) (e.nextElement().getClass().newInstance());

                out.pluginSetDefaultSettings(plugin.getName());

                if (JavaOpFileStuff.isActivePlugin(out.getName(), plugin.getName()) == false)
                {
                    if (plugin.getName().equalsIgnoreCase("Battle.net Login Plugin"))
                        out.systemMessage(ErrorLevelConstants.CRITICAL,
                                          "WARNING!!! BATTLE.NET LOGIN PLUGIN IS DISABLED!! BOT WILL NOT WORK");
                    if (plugin.getName().equalsIgnoreCase("SwingGui"))
                        out.systemMessage(ErrorLevelConstants.ALERT,
                                          "WARNING!!! SWING GUI PLUGIN IS DISABLED!");
                }
                else
                {
                    System.out.println("Activating plugin: " + plugin.getName());

                    plugin.activate(out, register);
                    activePlugins.put(plugin.getName(), plugin);
                }
            }
            catch (IllegalAccessException exc)
            {
                System.err.println("Unable to load plugin:");
                exc.printStackTrace();
            }
            catch (InstantiationException exc)
            {
                System.err.println("Unable to load plugin:");
                exc.printStackTrace();
            }
        }

        if (activePlugins.size() < 2)
        {
            e = allPlugins.elements();

            while (e.hasMoreElements())
            {
                try
                {
                    GenericPluginInterface plugin = (GenericPluginInterface) (e.nextElement().getClass().newInstance());
                    System.out.println("Plugin: " + plugin.getName());
                }
                catch (IllegalAccessException exc)
                {
                    System.err.println("Unable to load plugin:");
                    exc.printStackTrace();
                }
                catch (InstantiationException exc)
                {
                    System.err.println("Unable to load plugin:");
                    exc.printStackTrace();
                }
            }
            out.systemMessage(
                              ErrorLevelConstants.ALERT,
                              "It appears that this bot is new.  The important plugins have been enabled.  To connect, please select 'Configure' under the 'Settings' menu, click on 'Battle.net Login Plugin', and fill in your username, password, cdkey, and game client.  Then, under the 'Connection' menu, choose 'Connect'");
        }
    }

    /**
     * This unloads a plugin from memory for the current instance, calling the
     * plugin's deactivate() function
     */
    public void deactivatePlugins(PluginCallbackRegister register)
    {
        Enumeration e = activePlugins.elements();

        while (e.hasMoreElements())
            ((GenericPluginInterface) e.nextElement()).deactivate(register);
    }

    public static String[] getAllNames()
    {
        return Uniq.uniq(allPlugins.keys());
    }

    public static GenericPluginInterface getPlugin(String name)
    {
        return (GenericPluginInterface) allPlugins.get(name);
    }
}
