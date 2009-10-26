/*
 * Created on Feb 12, 2005 By iago
 */

package _main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import plugin_interfaces.GenericPluginInterface;
import pluginmanagers.PluginManager;
import util.PersistantMap;

import bot.JavaOpFileStuff;


/**
 * @author iago
 * 
 */
public class CommandlineConfigure
{
    private static final boolean  CLEAR = true;

    private static BufferedReader in    = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException
    {
        JavaOpFileStuff.setBaseDirectory();
        PluginManager.initialize(false);

        while (true)
        {
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

            int choice = Integer.parseInt(getPatternInput("Please make a selection", null,
                                                          "[1234560]"));

            if (choice == 1)
                configurePaths();
            else if (choice == 2)
                configureLoadedBots();
            else if (choice == 3)
                manageBots();
            else if (choice == 4)
                addBot();
            else if (choice == 5)
                copyBot();
            else if (choice == 6)
                removeBot();
            else if (choice == 0)
            {
                System.out.println("-- exiting");
                System.exit(0);
            }
        }
    }

    private static void addBot() throws IOException
    {
        clear();

        System.out.println("The current bots:");
        getAndPrintAllBots(false);
        System.out.println();

        String newBot = getPatternInput("Name of the new bot?", null, "[a-zA-Z1-9. -_]+");
        JavaOpFileStuff.newBot(newBot);
    }

    private static void copyBot() throws IOException
    {
        clear();

        System.out.println("The current bots:");
        String[] bots = getAndPrintAllBots(true);
        System.out.println();
        System.out.println("0. Back");
        System.out.println();

        int base = getNumericInput("Which bot are we making a copy of?", null, bots.length);

        if (base == 0)
            return;

        String newName = getPatternInput("What would you like to name the copy?", null,
                                         "[a-zA-Z1-9. -_]+");

        JavaOpFileStuff.copyBot(bots[base - 1], newName);
    }

    private static void removeBot() throws IOException
    {
        clear();

        System.out.println("The current bots:");
        String[] bots = getAndPrintAllBots(true);
        System.out.println();
        System.out.println("0. Back");
        System.out.println();

        int remove = getNumericInput("Which bot would you like to remove?", null, bots.length);

        if (remove == 0)
            return;

        JavaOpFileStuff.deleteBot(bots[remove - 1]);
    }

    private static void configurePaths() throws IOException
    {
        while (true)
        {
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

            if (choice == 1)
            {
                String path = getPatternInput("Please enter the full path", null, ".+");
                JavaOpFileStuff.addPluginPath(path);
            }
            else if (choice == 2)
            {
                String[] bots = getAndPrintPluginPaths(true);
                int bot = getNumericInput("Which bot?", null, bots.length);

                JavaOpFileStuff.removePluginPath(bots[bot - 1]);
            }
            if (choice == 0)
            {
                return;
            }
        }
    }

    private static void configureLoadedBots() throws IOException
    {
        while (true)
        {
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

            if (choice == 1)
            {
                String[] bots = getAndPrintAllBots(true);
                int bot = getNumericInput("Which would you like to load at startup?", null,
                                          bots.length);
                JavaOpFileStuff.addDefaultBot(bots[bot - 1]);
            }
            else if (choice == 2)
            {
                String[] bots = getAndPrintDefaultBots(true);
                int num = getNumericInput("Which bot would you like to remove?", null, bots.length);
                JavaOpFileStuff.removeDefaultBot(bots[num - 1]);
            }
            else if (choice == 0)
            {
                return;
            }
        }
    }

    private static void manageBots() throws IOException
    {
        while (true)
        {
            clear();
            String[] bots = getAndPrintAllBots(true);
            System.out.println();
            System.out.println("0. Back");
            System.out.println();
            int bot = getNumericInput("Please choose a bot", null, bots.length);

            if (bot == 0)
                return;

            manageBot(bots[bot - 1]);
        }
    }

    private static void manageBot(String bot) throws IOException
    {
        while (true)
        {
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

            int choice = Integer.parseInt(getPatternInput("Please make a selection", null,
                                                          "[1234560]"));

            if (choice == 1)
            {
                String[] plugins = getAndPrintAllPlugins(true);

                if (plugins.length == 0)
                    System.out.println("(all plugins are being loaded already)");
                System.out.println();
                System.out.println("0. Back");
                System.out.println();

                int plugin = getNumericInput("Please select a plugin to add", null, plugins.length);

                if (plugin != 0)
                    JavaOpFileStuff.addActivePlugin(bot, plugins[plugin - 1]);
            }
            else if (choice == 2)
            {
                String[] plugins = getAndPrintAllActivePlugins(bot, true);
                if (plugins.length == 0)
                    System.out.println("(no plugins are being loaded yet)");

                System.out.println();
                System.out.println("0. Back");
                System.out.println();

                int plugin = getNumericInput("Please select a plugin to remove", null,
                                             plugins.length);
                if (plugin != 0)
                    JavaOpFileStuff.removeActivePlugin(bot, plugins[plugin - 1]);
            }
            else if (choice == 3)
            {
                JavaOpFileStuff.setActivePlugins(bot, PluginManager.getAllNames());
            }
            else if (choice == 4)
            {
                JavaOpFileStuff.setActivePlugins(bot, new String[] {});
            }
            else if (choice == 5)
            {
                String[] plugins = getAndPrintAllPlugins(true);
                System.out.println();
                System.out.println("0, Back");
                System.out.println();

                int plugin = getNumericInput("Please select a plugin to configure", null,
                                             plugins.length);

                if (plugin != 0)
                {
                    plugin--;

                    GenericPluginInterface thisPlugin = PluginManager.getPlugin(plugins[plugin]);

                    Properties defaults = thisPlugin.getDefaultSettingValues();
                    Properties descriptions = thisPlugin.getSettingsDescription();
                    PersistantMap settings = JavaOpFileStuff.getSettings(bot);
                    String pluginName = thisPlugin.getName();

                    Object[] keys = sortEnumeration(defaults.keys());

                    for (int i = 0; i < keys.length; i++)
                    {
                        clear();

                        String currentSetting = settings.getNoWrite(
                                                                    pluginName,
                                                                    (String) keys[i],
                                                                    defaults.getProperty((String) keys[i]));
                        System.out.println();
                        System.out.println(keys[i] + ":");
                        System.out.println("Default setting: "
                                + defaults.getProperty((String) keys[i]));
                        System.out.println("Current setting: " + currentSetting);
                        System.out.println("Description: "
                                + descriptions.getProperty((String) keys[i]));
                        System.out.println();

                        settings.set(thisPlugin.getName(), (String) keys[i],
                                     getPatternInput("New value?", currentSetting, ".*"));
                    }
                }

            }
            else if (choice == 6)
            {
                String[] plugins = getAndPrintAllPlugins(true);
                if (plugins.length == 0)
                    System.out.println("(no plugins are being loaded yet)");

                System.out.println();
                System.out.println("0. Back");
                System.out.println();

                int num = getNumericInput("Please select a plugin to get info on", null,
                                          plugins.length);
                if (num != 0)
                {
                    num--;

                    GenericPluginInterface plugin = PluginManager.getPlugin(plugins[num]);
                    clear();
                    System.out.println("Name: " + plugin.getFullName());
                    System.out.println();
                    System.out.println("Author: " + plugin.getAuthorName() + " <"
                            + plugin.getAuthorEmail() + ">");
                    System.out.println();
                    System.out.println("Website: " + plugin.getAuthorWebsite());
                    System.out.println();
                    System.out.println("Description: " + plugin.getLongDescription());
                    System.out.println();
                    System.out.println("Press enter to continue...");
                    System.in.read();
                }
            }
            else if (choice == 0)
            {
                return;
            }
        }
    }

    private static int getNumericInput(String prompt, String defaultIn, int max)
    {
        while (true)
        {
            int input = Integer.parseInt(getPatternInput(
                                                         prompt + " (an integer 0 and " + max + ")",
                                                         defaultIn, "[0-9]+"));

            if (input <= max)
                return input;

            System.out.println("Error: invalid value");
        }
    }

    private static String getPatternInput(String prompt, String defaultIn, String pattern)
    {
        while (true)
        {
            try
            {
                System.out.print(prompt + (defaultIn == null ? "" : " [" + defaultIn + "]")
                        + " --> ");

                String text = in.readLine();

                if (text.equals("") && defaultIn != null)
                    return defaultIn;

                if (text.matches(pattern))
                    return text;

                System.out.println("Error, please enter a valid value");
            }
            catch (IOException e)
            {
                System.err.println("This should never happen");
                e.printStackTrace();
            }
        }
    }

    private static void clear()
    {
        if (CLEAR)
        {
            System.out.println("Yes, this is a crappy way to clear; but it's the only easy platform independant one, and Windows doesn't support ANSI by default, so shush.");
            for (int i = 0; i < 100; i++)
                System.out.println();
        }
        else
        {
            System.out.println();
            System.out.println();
        }
    }

    private static String[] getAndPrintPluginPaths(boolean printNumbers) throws IOException
    {
        String[] allPlugins = JavaOpFileStuff.getRawPluginPaths();
        for (int i = 0; i < allPlugins.length; i++)
            System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + allPlugins[i]);
        return allPlugins;
    }

    private static String[] getAndPrintDefaultBots(boolean printNumbers) throws IOException
    {
        String[] defaultBots = JavaOpFileStuff.getDefaultBots();
        for (int i = 0; i < defaultBots.length; i++)
            System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + defaultBots[i]);
        return defaultBots;
    }

    private static String[] getAndPrintAllBots(boolean printNumbers) throws IOException
    {
        String[] allBots = JavaOpFileStuff.getAllBots();
        for (int i = 0; i < allBots.length; i++)
            System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + allBots[i]);
        return allBots;
    }

    private static String[] getAndPrintAllPlugins(boolean printNumbers) throws IOException
    {
        String[] plugins = PluginManager.getAllNames();

        for (int i = 0; i < plugins.length - 1; i++)
            System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + plugins[i]);

        return plugins;
    }

    private static String[] getAndPrintAllActivePlugins(String bot, boolean printNumbers) throws IOException
    {
        String[] plugins = JavaOpFileStuff.getActivePlugins(bot);

        for (int i = 0; i < plugins.length; i++)
            System.out.println((printNumbers ? ((i + 1) + ". ") : "* ") + plugins[i]);

        return plugins;
    }

    private static Object[] sortEnumeration(Enumeration<Object> e)
    {
        Vector<Object> objectVector = new Vector<Object>();

        while (e.hasMoreElements())
            objectVector.add(e.nextElement());

        Object[] ret = objectVector.toArray();
        Arrays.sort(ret);

        return ret;
    }
}