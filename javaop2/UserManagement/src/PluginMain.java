package com.javaop.UserManagement;

import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

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


/*
 * Created on Jan 4, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback
{
    private PublicExposedFunctions        out;
    private static StaticExposedFunctions funcs;

    private PersistantMap                 mymap;

    public void load(StaticExposedFunctions staticFuncs)
    {
        funcs = staticFuncs;
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        this.mymap = new PersistantMap(new RelativeFile(out.getName() + ".level"),
                "This is the file that defines text flags.");

        register.registerCommandPlugin(this, "whoami", 0, false, "AGNL", "",
                                       "Shows you all the flags you have", null);
        register.registerCommandPlugin(
                                       this,
                                       "setuser",
                                       0,
                                       false,
                                       "AGN",
                                       "<userlist> <flags>",
                                       "Adds or removes flags from a user.  The flag list is a set of +/- and flags, like +ABC-D+E-F",
                                       null);
        register.registerCommandPlugin(this, "deluser", 0, false, "AGN", "<userlist>",
                                       "Deletes a user from the database", null);
        register.registerCommandPlugin(this, "find", 0, false, "AGN", "<userlist>",
                                       "Finds the flags for the requested user", null);
        register.registerCommandPlugin(this, "findattr", 1, false, "AGN", "<flag>",
                                       "Lists all users with the specified flag", null);
        register.registerCommandPlugin(this, "whatcanichange", 0, false, "AGNL", "",
                                       "Tells you which flags you're allowed to modify", null);
        register.registerCommandPlugin(this, "setaccesslevel", 2, false, "A",
                                       "[access level, flags]",
                                       "Adds a new access level with flags.", null);
        register.registerCommandPlugin(this, "useaccesslevel", 2, false, "A",
                                       "[user, access level]", "Sets a user to a access level.",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "deleteaccesslevel",
                                       1,
                                       false,
                                       "A",
                                       "[access level]",
                                       "Deletes an access level. All users in this access level will remain.",
                                       null);
        register.registerCommandPlugin(this, "listaccesslevels", 0, false, "A", "",
                                       "Gives a list of all access levels.", null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "User management";
    }

    public String getVersion()
    {
        return "2.1.3";
    }

    public String getAuthorName()
    {
        return "iago, Ryan Marcus";
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
        return "Commands for managing users";
    }

    public String getLongDescription()
    {
        return "This has the commands for adding, finding, and removing users from the user database.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("A can change", "BFLS");
        p.setProperty("G can change", "BFJLST");
        p.setProperty("N can change", "ABCDEFGHIJKLNOPQRSTVWXYZ");
        p.setProperty("P can be modified by", "MN");
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("A can change",
                      "This is the lowest flag for members, they can typically change only low level flags.");
        p.setProperty(
                      "G can change",
                      "I put this in as a mid-range access modifier.  Typically, it'll be more powerful than A, but less than N.");
        p.setProperty(
                      "N can change",
                      "This is typically used for a local master (ie, master of this bot).  Can normally change almost everything.");
        p.setProperty("P can be modified by",
                      "Somebody with the \"P\" flag set can't generally be changed unless you're a master.");

        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return null;
    }

    public Properties getGlobalDefaultSettingValues()
    {
        Properties p = new Properties();
        p.setProperty("all flags", "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        return p;
    }

    public Properties getGlobalSettingsDescription()
    {
        Properties p = new Properties();
        p.setProperty("all flags", "These are ALL flags that the bot recognizes.");
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (command.equalsIgnoreCase("whoami"))
        {
            out.sendTextUserPriority(user, out.dbGetFlags(user), loudness, PRIORITY_LOW);
        }
        else if (command.equalsIgnoreCase("find"))
        {
            if (args.length < 1)
                throw new CommandUsedImproperly("Find requires at least one parameter.", user,
                        command);

            for (int i = 0; i < args.length; i++)
                out.sendTextUserPriority(user, args[i] + ": " + out.dbGetFlags(args[i]), loudness,
                                         PRIORITY_LOW);
        }
        else if (command.equalsIgnoreCase("deluser"))
        {
            if (args.length < 1)
                throw new CommandUsedImproperly("Deluser requires at least one parameter", user,
                        command);

            for (int i = 0; i < args.length; i++)
            {
                String thisUser = args[i];

                if (out.dbUserExists(thisUser) == false)
                    out.sendTextUser(user, "Sorry, user \"" + thisUser
                            + "\" not found in the database.", loudness);
                else
                    changeAndNotify(user, thisUser,
                                    funcs.getGlobalSettingDefault(getName(), "all flags",
                                                                  "ABCDEFGHIJKLMNOPQRSTUVWXYZ"),
                                    "", loudness);
            }
        }
        else if (command.equalsIgnoreCase("setuser"))
        {
            if (args.length < 2)
                throw new CommandUsedImproperly("Setuser requires at least two parameters", user,
                        command);

            // The flags are the last argument
            String flags = args[args.length - 1];

            String addFlags = "";
            String removeFlags = "";
            boolean adding = true;

            for (int i = 0; i < flags.length(); i++)
            {
                char thisChar = flags.charAt(i);
                if (thisChar == '+')
                    adding = true;
                else if (thisChar == '-')
                    adding = false;
                else if (adding)
                    addFlags += thisChar;
                else
                    removeFlags += thisChar;
            }

            out.systemMessage(DEBUG, "Flags to add: " + addFlags);
            out.systemMessage(DEBUG, "Flags to remove: " + removeFlags);

            for (int i = 0; i < args.length - 1; i++)
                changeAndNotify(user, args[i], removeFlags, addFlags, loudness);

        }
        else if (command.equalsIgnoreCase("whatcanichange"))
        {
            String changes = getAllowedChanges(user);
            if (changes.length() == 0)
                changes = "<nothing>";

            out.sendTextUser(user, "You're allowed to change flags "
                    + changes
                    + ", and can "
                    + (out.dbHasAny(user, out.getLocalSetting(getName(), "P can be modified by"),
                                    true) ? "" : "not ") + "modify users with P flag set.",
                             loudness);
        }
        else if (command.equalsIgnoreCase("findattr"))
        {
            if (args.length != 1 || args[0].length() != 1)
                throw new CommandUsedImproperly("findattr requires a single character parameter",
                        user, command);

            String[] users = out.dbFindAttr(args[0].charAt(0));

            StringBuffer s = new StringBuffer("Users with " + args[0] + ": ");
            for (int i = 0; i < users.length; i++)
                s.append(users[i] + " ");
            out.sendTextUser(user, s.toString(), loudness);

            // Begin access levels
        }
        else if (command.equalsIgnoreCase("setaccesslevel"))
        {
            if (args.length != 2)
                throw new CommandUsedImproperly("setaccesslevel requires two parameters.", user,
                        command);

            mymap.set("Definitions", args[0], args[1]);
            out.sendTextUser(user, "Access level added: " + args[0] + " => " + args[1], QUIET);
        }
        else if (command.equalsIgnoreCase("useaccesslevel"))
        {

            if (args.length != 2)
                throw new CommandUsedImproperly("useaccesslevel requires two parameters.", user,
                        command);

            String flags = mymap.getNoWrite("Definitions", args[1], "");

            if (flags.equalsIgnoreCase(""))
            {
                out.sendTextUser(user, "That is not an access level!", QUIET);
            }
            else
            {
                changeAndNotify(user, args[0],
                                funcs.getGlobalSettingDefault(getName(), "all flags",
                                                              "ABCDEFGHIJKLMNOPQRSTUVWXYZ"), flags,
                                loudness);
            }
        }
        else if (command.equalsIgnoreCase("deleteaccesslevel"))
        {
            if (args.length != 1)
                throw new CommandUsedImproperly("deleteaccesslevel requires one parameter.", user,
                        command);

            mymap.remove("Definitions", args[0]);
            out.sendTextUser(user, "Access level removed.", loudness);
        }
        else if (command.equalsIgnoreCase("listaccesslevels"))
        {
            String[] thelist = Uniq.uniq(mymap.propertyNames("Definitions"));
            String sFinal = "";

            for (int i = 0; i < thelist.length; i++)
            {
                sFinal = sFinal + thelist[i];
                if (i < (thelist.length - 1))
                {
                    sFinal = sFinal + ",";
                }
            }

            if (sFinal.equalsIgnoreCase(""))
            {
                out.sendTextUser(user, "There are no access levels.", QUIET);
            }
            else
            {
                out.sendTextUser(user, sFinal, loudness);
            }

            // End access levels

        }
        else
        {
            out.sendTextUser(
                             user,
                             "Error: There was an unknown command.  Please report a problem with UserManagement plugin.",
                             loudness);
        }

    }

    /** Returns the flags that that user is allowed to modify */
    private String getAllowedChanges(String user) throws PluginException
    {
        String flags = "";
        if (out.dbHasAny(user, "", true))
            flags += out.getLocalSettingDefault(getName(), "all flags",
                                                "ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        if (out.dbHasAny(user, "A", true))
            flags += out.getLocalSettingDefault(getName(), "A can change", "BFLS");
        if (out.dbHasAny(user, "G", true))
            flags += out.getLocalSettingDefault(getName(), "G can change", "BFLS");
        if (out.dbHasAny(user, "N", true))
            flags += out.getLocalSettingDefault(getName(), "N can change",
                                                "ABCDEFGHIJKLNOPQRSTVWXYZ");

        char[] flagArray = flags.toCharArray();

        TreeSet flagTree = new TreeSet();
        for (int i = 0; i < flagArray.length; i++)
            flagTree.add(new Character(flagArray[i]));

        flags = "";

        Iterator it = flagTree.iterator();

        while (it.hasNext())
            flags = flags + it.next();

        out.systemMessage(DEBUG, "User " + user + " was allowed to change flags " + flags);

        return flags;
    }

    /**
     * Makes the specified changes to the user, and notifies the guy who made
     * the command. Also ensures that the changer is allowed to make the changes
     */
    private void changeAndNotify(String changer, String userToChange, String flagsToRemove,
            String flagsToAdd, int loudness) throws IOException, PluginException
    {
        out.systemMessage(DEBUG, changer + " is trying to change " + userToChange);
        out.systemMessage(DEBUG, "Removing flags: " + flagsToRemove);
        out.systemMessage(DEBUG, "Adding flags: " + flagsToAdd);

        flagsToAdd = ((flagsToAdd == null) ? "" : flagsToAdd.toUpperCase());
        flagsToRemove = ((flagsToRemove == null) ? "" : flagsToRemove.toUpperCase());

        // First, check if the user they're trying to remove is blocked by P:
        if (out.dbHasAny(userToChange, "P", true))
        {
            // Ok, they have P. But it can be overridden:
            String pOverride = out.getLocalSettingDefault(getName(), "P can be modified by", "MN");
            if (out.dbHasAny(changer, pOverride, true) == false)
            {
                // They aren't allowed to override P
                out.sendTextUser(changer,
                                 "Sorry, users with the P flag can only be changed by somebody with "
                                         + pOverride + ".", loudness);
                return;
            }
        }

        // Make sure they aren't doing anything that they shouldn't be
        String allowedChanges = getAllowedChanges(changer);
        flagsToRemove = flagsToRemove.replaceAll("[^" + allowedChanges + "]", "");
        flagsToAdd = flagsToAdd.replaceAll("[^" + allowedChanges + "]", "");

        // Don't bother removing flags that they don't have
        String rawFlags = out.dbGetRawFlags(userToChange);
        if (rawFlags.length() > 0)
            flagsToRemove = flagsToRemove.replaceAll("[^" + rawFlags + "]", "");

        out.systemMessage(DEBUG, "Final flags to remove: " + flagsToRemove);
        out.systemMessage(DEBUG, "Final flags to add: " + flagsToAdd);

        if (flagsToRemove.length() == 0 && flagsToAdd.length() == 0)
        {
            out.sendTextUser(changer,
                             "Sorry, couldn't find any flags that you're allowed to add/remove.",
                             loudness);
            return;
        }

        String oldFlags = out.dbGetRawFlags(userToChange);

        out.dbAddAndRemove(userToChange, flagsToAdd, flagsToRemove);

        String newFlags = out.dbGetRawFlags(userToChange);

        if (oldFlags == null || oldFlags.length() == 0)
            oldFlags = "<none>";
        if (newFlags == null || newFlags.length() == 0)
            newFlags = "<none>";

        out.sendTextUser(changer, userToChange + ": " + oldFlags + " => " + newFlags
                + (oldFlags.equals(newFlags) ? " (no change)" : ""), loudness);
    }

}
