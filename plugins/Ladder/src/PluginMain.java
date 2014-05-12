package com.javaop.Ladder;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.PacketCallback;
import com.javaop.util.BnetPacket;
import com.javaop.util.FileTime;


/*
 * Created on May 14, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements PacketCallback, CommandCallback
{
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(
                                       this,
                                       "starladder",
                                       0,
                                       false,
                                       "AN",
                                       "[starting number] [sort method]",
                                       "Displays the Starcraft ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "broodladder",
                                       0,
                                       false,
                                       "AN",
                                       "[starting number] [sort method]",
                                       "Displays the Brood War ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "war2ladder",
                                       0,
                                       false,
                                       "AN",
                                       "[starting number] [sort method]",
                                       "Displays the Warcraft 2 ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
                                       null);

        register.registerIncomingPacketPlugin(this, SID_GETLADDERDATA, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Ladder";
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

    public String getLongDescription()
    {
        return "This plugin can pull information from the ladder page.  Overall, pretty useless, but not a bad toy to play with.";
    }

    public Properties getDefaultSettingValues()
    {
        Properties p = new Properties();
        return p;
    }

    public Properties getSettingsDescription()
    {
        Properties p = new Properties();
        return p;
    }

    public JComponent getComponent(String settingName, String value)
    {
        return null;
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

    public BnetPacket processingPacket(BnetPacket buf, Object data) throws IOException, PluginException
    {
        return buf;
    }

    public void processedPacket(BnetPacket buf, Object data) throws IOException, PluginException
    {
        if (buf.getCode() == SID_GETLADDERDATA)
        {
            buf.removeDWord(); // (DWORD) Ladder type
            buf.removeDWord(); // (DWORD) League
            buf.removeDWord(); // (DWORD) Sort method
            buf.removeDWord(); // (DWORD) Starting rank
            int number = buf.removeDWord(); // (DWORD) Number of ranks listed
            // (Count of items in list)

            // silent
            if (number == 9)
            {
                out.sendTextUser(
                                 null,
                                 "Rank. Name : Rating (High) : Wins : Losses : Disconnects : Last Game",
                                 SILENT);
            }
            else
            {
                out.sendText("Rank. Name : Rating (High) : Wins : Losses : Disconnects : Last Game");
            }

            for (int i = 0; i < number; i++)
            {
                buf.removeDWord(); // (DWORD) Wins
                buf.removeDWord(); // (DWORD) Losses
                buf.removeDWord(); // (DWORD) Disconnects
                buf.removeDWord(); // (DWORD) Rating
                buf.removeDWord(); // (DWORD) Rank
                int wins = buf.removeDWord(); // (DWORD) Official wins
                int losses = buf.removeDWord(); // (DWORD) Official losses
                int discs = buf.removeDWord(); // (DWORD) Official disconnects
                int rating = buf.removeDWord(); // (DWORD) Official rating
                buf.removeDWord(); // (DWORD) Unknown
                int rank = 1 + buf.removeDWord(); // (DWORD) Official rank
                buf.removeDWord(); // (DWORD) Unknown
                buf.removeDWord(); // (DWORD) Unknown
                int highRating = buf.removeDWord(); // (DWORD) Highest rating
                buf.removeDWord(); // (DWORD) Unknown
                buf.removeDWord(); // (DWORD) Season
                long lastGame = buf.removeLong(); // (FILETIME) Last game time
                buf.removeLong(); // (FILETIME) Official last game time
                String name = buf.removeNTString(); // (STRING) Name

                // Silent
                if (number == 9)
                {
                    out.sendTextUser(null,
                                     (rank > 0 ? "" + rank : "[unranked]") + ". " + name + " : "
                                             + rating + " (" + highRating + ") : " + wins + " : "
                                             + losses + " : " + discs + " : "
                                             + new Date(FileTime.fileTimeToMillis(lastGame)),
                                     SILENT);
                }
                else
                {
                    out.sendText((rank > 0 ? "" + rank : "[unranked]") + ". " + name + " : "
                            + rating + " (" + highRating + ") : " + wins + " : " + losses + " : "
                            + discs + " : " + new Date(FileTime.fileTimeToMillis(lastGame)));
                }
            }
        }
    }

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {

        // register.registerCommandPlugin(this, "starladder", 0, false, "AN",
        // "[starting number] [sort method]",
        // "Displays the Starcraft ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
        // null);
        // register.registerCommandPlugin(this, "broodladder", 0, false, "AN",
        // "[starting number] [sort method]",
        // "Displays the Brood War ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
        // null);
        // register.registerCommandPlugin(this, "war2ladder", 0, false, "AN",
        // "[starting number] [sort method]",
        // "Displays the Warcraft 2 ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
        // null);
        // register.registerCommandPlugin(this, "ladder", 0, false, "AN",
        // "[starting number] [sort method]",
        // "Displays the Warcraft 2 ladder.  'Starting number' is the first number displayed; 'Sort method' is either 'rating', 'wins', or 'games'",
        // null);

        ((String) null).toString();

        // Get the starting rank
        int startingRank = 0;
        if (args.length > 0)
        {
            startingRank = Integer.parseInt(args[0]) - 1;
            if (startingRank > 990)
            {
                out.sendTextUser(user, "Error: the ranking can't be over 990", QUIET);
                return;
            }
        }

        // Get the sort method
        int sortMethod = 0;
        if (args.length > 1)
        {
            if (args[1].equalsIgnoreCase("rating"))
                sortMethod = 0;
            else if (args[1].equalsIgnoreCase("wins"))
                sortMethod = 2;
            else if (args[1].equalsIgnoreCase("games"))
                sortMethod = 3;
            else
                throw new CommandUsedImproperly(
                        "Unknown sort method (valid ones are 'rating', 'wins', and 'games')", user,
                        command);
        }

        // Get the client code
        String client = "RATS";
        if (command.equalsIgnoreCase("broodladder"))
            client = "PXES";
        else if (command.equalsIgnoreCase("war2ladder"))
            client = "NB2W";

        // Get the number to list
        int number;
        if (loudness == SILENT)
            number = 9;
        else
            number = 3;

        BnetPacket getLadder = new BnetPacket(SID_GETLADDERDATA);

        getLadder.addString(client); // (DWORD) Product ID
        getLadder.addDWord(1); // (DWORD) League
        getLadder.addDWord(sortMethod); // (DWORD) Sort method
        getLadder.addDWord(startingRank); // (DWORD) Starting rank
        getLadder.addDWord(number); // (DWORD) Number of ranks to list

        out.sendPacket(getLadder);
    }
}
