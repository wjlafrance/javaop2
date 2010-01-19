import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import plugin_interfaces.CommandCallback;
import plugin_interfaces.GenericPluginInterface;


/*
 * Created on Dec 12, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback {
	
    private PublicExposedFunctions pubFuncs;

    public void load(StaticExposedFunctions staticFuncs) {
    }

    public void activate(PublicExposedFunctions pubFuncs, PluginCallbackRegister register) {
        this.pubFuncs = pubFuncs;

        String banzFlags = pubFuncs.getLocalSettingDefault(getName(), "S can be banned/kicked by", "N");
        String banFlags = pubFuncs.getLocalSettingDefault(getName(), "F can be banned/kicked by", "ON");
        String banfFlags = pubFuncs.getLocalSettingDefault(getName(),
                                                      "Anybody else can be banned/kicked by", "ON");

        register.registerCommandPlugin(this, "banf", 2, true, banfFlags, "<user> [message]",
        		"Bans the requested user from the channel, as long as they don't have F or S flags.",
                null);
        register.registerCommandPlugin(this, "ban", 2, true, banFlags, "<user> [message]",
                "Bans the requested user from the channel, as long as they don't have the S flag.",
                null);
        register.registerCommandPlugin(this, "banz", 2, true, banzFlags, "<user> [message]",
                "Bans the requested user from the channel, regardless of which flags they have.",
                null);
        register.registerCommandPlugin(this, "kickf", 2, true, banfFlags, "<user> [message]",
                "Kicks the requested user from the channel, as long as they don't have F or S flags.",
                null);
        register.registerCommandPlugin(this, "kick", 2, true, banFlags, "<user> [message]",
                "Kicks the requested user from the channel, as long as they don't have the S flag.",
                null);
        register.registerCommandPlugin(this, "kickz", 2, true, banzFlags, "<user> [message]",
                "Kicks the requested user from the channel, regardless of which flags they have.",
                null);
        register.registerCommandPlugin(this, "unban", 0, true, banFlags, "<users[s]>",
                "Unbans the requested user.", null);

        pubFuncs.addAlias("ban", "b");
        pubFuncs.addAlias("kick", "k");
        pubFuncs.addAlias("banf", "bf");
        pubFuncs.addAlias("kickf", "kf");
        pubFuncs.addAlias("banz", "bz");
        pubFuncs.addAlias("kickz", "kz");
        pubFuncs.addAlias("unban", "u");
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public String getName() {
        return "Moderation plugin";
    }

    public String getVersion() {
        return "2.1.2";
    }

    public String getAuthorName() {
        return "iago";
    }

    public String getAuthorWebsite() {
        return "www.javaop.com";
    }

    public String getAuthorEmail() {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription() {
        return "Basic channel moderation commands";
    }

    public String getLongDescription() {
        return "This includes the various basic moderation commands, such as kick and ban.  The flags "
                + "required to use the various bans/kicks are configurable, so you can set up who can ban "
                + "who in your clan.";
    }

    public Properties getDefaultSettingValues() {
        Properties p = new Properties();
        p.setProperty("S can be banned/kicked by", "N");
        p.setProperty("F can be banned/kicked by", "ON");
        p.setProperty("Anybody else can be banned/kicked by", "ON");
        p.setProperty("Flags to ban outside the channel", "N");
        return p;
    }

    public Properties getSettingsDescription() {
        Properties p = new Properties();
        p.setProperty("S can be banned/kicked by", "The flag or choice of flags required to ban or "
        		+ "kick somebody with the \"S\" flag.  The command to do this is .banz, which can ban "
        		+ "anybody.  Bot must be restarted for some changes.");
        p.setProperty("F can be banned/kicked by", "The flag or choice of flags required to ban or "
        		+ "kick somebody with the \"F\" flag.  The command to do this is .ban, which can ban "
        		+ "anybody without S.  Bot must be restarted for some changes.");
        p.setProperty("Anybody else can be banned/kicked by", "The flag or flags required to ban or "
        		+ "kick anybody else.  The command to do this is .banf, which bans anybody without F. "
        		+ "Bot must be restarted for some changes.");
        p.setProperty("Flags to ban outside the channel", "The flag required to ban people outside "
        		+ "the channel.  BE AWARE that this is unsafe, because it lets you override safelist, "
        		+ "and ban, say, iago@uswest");
        return p;
    }

    public JComponent getComponent(String settingName, String value) {
        return null;
    }

    public Properties getGlobalDefaultSettingValues() {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription() {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws IOException, CommandUsedImproperly, CommandUsedIllegally {
        if (args.length == 0 || args[0].length() == 0)
            throw new CommandUsedImproperly(
                    "All ban and kick commands require at least one parameter", user, command);

        if (command.toLowerCase().matches("ban.*") || command.toLowerCase().matches("kick.*")) {
            // The first parameter is name
            String name = args[0];
        	// Account for a server bug allowing iago,lolwut to represent iago.
        	name = name.split(",")[0];
        	
            // The second parameter is message, if it exists; otherwise, message
            // is 0
            String message = args.length > 1 ? args[1] : "";

            // Set users to an empty array of strings
            String[] users = new String[0];

            // If it wasn't a wildcard ban, and the user had access to do a
            // straight up ban, then just ban them
            if (pubFuncs.dbHasAny(user, pubFuncs.getLocalSettingDefault(getName(),
            		"Flags to ban outside the channel", "N"), true)
                    && name.indexOf('*') < 0 && name.indexOf('?') < 0) {
                // User is just the name of the person they asked to ban, unless
                // it was a wildcard
                users = new String[]
                { name };
            } else {
                // Check the level of kick, then assign the proper users
                if (command.equalsIgnoreCase("banz") || command.equalsIgnoreCase("kickz"))
                    users = pubFuncs.channelMatchGetListWithoutAny(name, "M");
                else if (command.equalsIgnoreCase("ban") || command.equalsIgnoreCase("kick"))
                    users = pubFuncs.channelMatchGetListWithoutAny(name, "MS");
                else if (command.equalsIgnoreCase("banf") || command.equalsIgnoreCase("kickf"))
                    users = pubFuncs.channelMatchGetListWithoutAny(name, "FMS");
            }

            // If no users were found, then inform the user
            if (users.length == 0) {
            	pubFuncs.sendTextUser(user, "No possible bans found.", PRIORITY_HIGH);
                return;
            }

            // Loop through the users that were found, making sure they're
            // allowed to ban them
            for (int i = 0; i < users.length; i++) {
                // Make sure they're allowed to kick or ban the person. This
                // will only ever fail if changes were made without
                // restarting the bot.
                allowedToBan(user, users[i]);

                if (command.equalsIgnoreCase("ban") || command.equalsIgnoreCase("banf")
                        || command.equalsIgnoreCase("banz")) {
                    // Bans are going to be PRIORITY_VERY_HIGH so they will
                    // always go to the top of the queue
                	pubFuncs.sendTextPriority("/ban " + users[i] + " " + message, PRIORITY_VERY_HIGH);
                } else if (command.equalsIgnoreCase("kick") || command.equalsIgnoreCase("kickf")
                        || command.equalsIgnoreCase("kickz")) {
                    // Kicks will be PRIORITY_HIGH so they will go ahead in the
                    // queue, but bans still get to go first
                	pubFuncs.sendTextPriority("/kick " + users[i] + " " + message, PRIORITY_HIGH);
                }
            }
        } else if (command.equalsIgnoreCase("unban")) {
            boolean notified = false;
            for (int i = 0; i < args.length; i++) {
                if (args[i].indexOf('*') >= 0 || args[i].indexOf('?') >= 0) {
                    if (!notified) {
                    	pubFuncs.sendTextUser(user, "Sorry, can't unban by pattern yet (" + args[i]
                                + ")", loudness);
                        notified = true;
                    }
                } else {
                	pubFuncs.sendTextPriority("/unban " + args[i], PRIORITY_HIGH);
                }
            }
        }
    }

    /** This is a fail-safe function -- to add one more layer of validation. */
    private void allowedToBan(String banner, String banned) throws CommandUsedIllegally {
    	
        // First of all, users with M can never be banned
        if (pubFuncs.dbHasAny(banned, "M", true))
            throw new CommandUsedIllegally("M can't be banned", banner, "ban/kick",
            		pubFuncs.dbGetFlags(banner), "N/A");

        // A user with S can be banned by somebody with N
        String sCanBeBanned = pubFuncs.getLocalSettingDefault(getName(), "S can be banned/kicked by",
                                                         "N");
        if (pubFuncs.dbHasAny(banned, "S", true) && pubFuncs.dbHasAny(banner, sCanBeBanned, true) == false)
            throw new CommandUsedIllegally("Illegally tried to ban a Safelisted user", banner,
                    "ban/kick", pubFuncs.dbGetFlags(banner), sCanBeBanned);

        // A user with F can be banned by anybody with A or N
        String fCanBeBanned = pubFuncs.getLocalSettingDefault(getName(), "F can be banned/kicked by",
                                                         "ON");
        if (pubFuncs.dbHasAny(banned, "F", true) && pubFuncs.dbHasAny(banner, fCanBeBanned, true) == false)
            throw new CommandUsedIllegally("Illegally tried to ban a Friendlisted user", banner,
                    "ban/kick", pubFuncs.dbGetFlags(banner), fCanBeBanned);

        // A user without M, S, or F can be banned by anybody with A or N
        String everybodyElse = pubFuncs.getLocalSettingDefault(getName(),
                                                          "Anybody else can be banned/kicked by",
                                                          "ON");
        if (pubFuncs.dbHasAny(banner, everybodyElse, true) == false)
            throw new CommandUsedIllegally("Illegally tried to ban somebody", banner, "ban/kick",
            		pubFuncs.dbGetFlags(banner), everybodyElse);
    }

}
