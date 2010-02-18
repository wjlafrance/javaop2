package com.javaop.FunStuff;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.PersistantMap;
import com.javaop.util.RelativeFile;
import com.javaop.util.Uniq;
import com.javaop.util.gui.JTextFieldNumeric;


/*
 * Created on Apr 7, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements CommandCallback
{
    private PublicExposedFunctions out;
    private final Random           random = new Random();
    private PersistantMap          quotes;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        quotes = new PersistantMap(new RelativeFile(out.getName() + ".quotes"),
                "These are the quotes, one per line");

        register.registerCommandPlugin(
                                       this,
                                       "quote",
                                       1,
                                       false,
                                       "L",
                                       "[num]",
                                       "Displays the specified quote (or a random one if none is specified)",
                                       null);
        register.registerCommandPlugin(this, "addquote", 1, false, "L", "<quote>",
                                       "Adds a quote to the quote database", null);
        register.registerCommandPlugin(this, "removequote", 1, false, "A", "<?????>",
                                       "Removes a quote from the quote database", null);
        register.registerCommandPlugin(
                                       this,
                                       "pickrandom",
                                       1,
                                       false,
                                       "L",
                                       "<option list>",
                                       "Picks a random option from the list.  Options are separated by commas",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "random",
                                       0,
                                       false,
                                       "L",
                                       "[min] [max]",
                                       "Picks a random number between min and max.  If only one parameter is given, it picks between 1 and <max>.  If no parameters are given, it picks between 1 and 10.",
                                       null);
        register.registerCommandPlugin(this, "host", 1, false, "L", "<hostname>",
                                       "Resolves the given hostname to its ip(s)", null);
        register.registerCommandPlugin(this, "time", 0, false, "L", "",
                                       "Displays the current time/date", null);
        register.registerCommandPlugin(this, "define", 1, false, "L", "",
                                       "Tries to get the dictionary definition of the word", null);

        out.addAlias("pickrandom heads,tails", "flip");
        out.addAlias("time", "date");
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Fun stuff";
    }

    public String getVersion()
    {
        return "2.1.3";
    }

    public String getAuthorName()
    {
        return "iago";
    }

    public String getAuthorWebsite()
    {
        return "www.javaop.com";
    }

    public String getAuthorEmail()
    {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription()
    {
        return "A plugin for fun stuff";
    }

    public String getLongDescription()
    {
        return "This plugin provides useless functionality such as flipping a coin, picking a random number, etc.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("definitions", "3");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty(
                      "definitions",
                      "The number of definitions that ?define returns.  I recommend around 3 because this will kill the message queue.  0 = unlimited.");
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return new JTextFieldNumeric(value);
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("quote"))
        {
            Properties quote = quotes.getSection(null);
            String[] elements = Uniq.uniq(quote.keys());

            int quoteNum;

            if (args.length == 0)
                quoteNum = random.nextInt(elements.length);
            else
                quoteNum = Integer.parseInt(args[0]) - 1;

            if (quoteNum < 0 || quoteNum >= elements.length)
                out.sendTextUser(user, "Error: quote #" + (quoteNum + 1) + " not found!", QUIET);
            else
                out.sendTextPriority((quoteNum + 1) + ": " + quote.getProperty(elements[quoteNum]),
                                     PRIORITY_VERY_LOW);
        }
        else if (command.equalsIgnoreCase("removequote"))
        {
            if (args.length != 1)
                throw new CommandUsedImproperly("removequote requires a parameter", user, command);

            Properties quote = quotes.getSection(null);
            String[] elements = Uniq.uniq(quote.keys());

            int quoteNum = Integer.parseInt(args[0]) - 1;

            if (quoteNum < 0 || quoteNum >= elements.length)
            {
                out.sendTextUser(user, "Error: quote #" + (quoteNum + 1) + " not found!", QUIET);
            }
            else
            {
                quotes.remove(null, elements[quoteNum]);
                out.sendTextUser(user, "Quote #" + (quoteNum + 1) + " removed", QUIET);
            }
        }
        else if (command.equalsIgnoreCase("addquote"))
        {
            if (args.length != 1)
                throw new CommandUsedImproperly(
                        "addquote requires a single parameter (the quote to add)", user, command);

            quotes.set(null, System.currentTimeMillis() + "", args[0]);
            out.sendTextUserPriority(user, "Quote added", loudness, PRIORITY_LOW);
        }
        else if (command.equalsIgnoreCase("clearquotes"))
        {
            Properties quote = quotes.getSection(null);
            String[] keys = Uniq.uniq(quote.keys());
            for (int i = 0; i < keys.length; i++)
                quotes.remove(null, keys[i]);
        }
        else if (command.equalsIgnoreCase("flip"))
        {
            if (random.nextInt(2) == 0)
                out.sendTextPriority("Heads", PRIORITY_VERY_LOW);
            else
                out.sendTextPriority("Tails", PRIORITY_VERY_LOW);
        }
        else if (command.equalsIgnoreCase("pickrandom"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly(
                        "pickrandom requires a comma-separated list of choices", user, command);

            String[] choices = args[0].split(",");

            out.sendTextPriority(" " + choices[random.nextInt(choices.length)].trim(),
                                 PRIORITY_VERY_LOW);
        }
        else if (command.equalsIgnoreCase("random"))
        {
            int choice;

            if (args.length == 0)
            {
                choice = random.nextInt(10) + 1;
            }
            else if (args.length == 1)
            {
                choice = random.nextInt(Integer.parseInt(args[0])) + 1;
            }
            else
            {
                int min = Integer.parseInt(args[0]);
                int max = Integer.parseInt(args[1]);

                if (min > max)
                    throw new CommandUsedImproperly(
                            "In random, the minimum value has to be below the maximum value", user,
                            command);
                choice = random.nextInt(max - min + 1) + min;
            }

            out.sendTextPriority(choice + "", PRIORITY_VERY_LOW);
        }
        else if (command.equalsIgnoreCase("host"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly("Must specify an address to look up", user, command);

            try
            {
                InetAddress[] addresses = InetAddress.getAllByName(args[0]);

                String[] strAddresses = new String[addresses.length];

                for (int i = 0; i < addresses.length; i++)
                    strAddresses[i] = addresses[i].getHostAddress();

                Arrays.sort(strAddresses);

                StringBuffer s = new StringBuffer();
                s.append(args[0] + ": ");
                for (int i = 0; i < addresses.length; i++)
                    s.append(strAddresses[i] + (((i + 1) < addresses.length) ? ", " : ""));

                out.sendTextUserPriority(user, s.toString(), loudness, PRIORITY_LOW);
            }
            catch (UnknownHostException e)
            {
                out.sendTextUser(user, "Unknown host: " + e.getMessage(), loudness);
            }
        }
        else if (command.equalsIgnoreCase("time"))
        {
            out.sendTextUserPriority(user, new Date().toString(), loudness, PRIORITY_LOW);
        }
        else if (command.equalsIgnoreCase("define"))
        {
            if (args.length == 0)
                throw new CommandUsedImproperly("Must specify a word to define", user, command);

            new DefineThread(user, loudness, Integer.parseInt(out.getLocalSetting(getName(),
                                                                                  "definitions")),
                    args[0]).start();
        }
        else
        {
            out.sendTextUser(
                             user,
                             "An unknown command was used in FunStuff plugin.  Please report to iago.",
                             loudness);
        }
    }

    private class DefineThread extends Thread
    {
        private final String word;
        private final int    loudness;
        private final int    max;
        private final String user;

        public DefineThread(String user, int loudness, int max, String word)
        {
            this.user = user;
            this.loudness = loudness;
            this.max = max;
            this.word = word;

            this.setName("Define-thread");
        }

        public void run()
        {
            try
            {
                String[] defs = Define.define(word);

                if (defs.length == 0)
                    out.sendTextUser(user, "No definitions found for word '" + word + "'.",
                                     loudness);

                System.out.println("retrieving " + max + " definitions");
                for (int i = 0; i < defs.length && (max == 0 || i < max); i++)
                {
                    out.sendTextUserPriority(user, (i + 1) + ": " + defs[i], loudness,
                                             PRIORITY_VERY_LOW);
                }
            }
            catch (IOException e)
            {
                try
                {
                    out.sendTextUser(user, "Error looking up word: " + e.toString(), loudness);
                }
                catch (Exception e2)
                {
                    e2.printStackTrace();
                }
            }
        }
    }
}
