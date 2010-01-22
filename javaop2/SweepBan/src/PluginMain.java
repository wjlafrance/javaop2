import java.io.IOException;
import java.util.Properties;

import javax.swing.JComponent;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;
import plugin_interfaces.CommandCallback;
import plugin_interfaces.EventCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;
import util.BnetPacket;


/*
 * Created on Apr 7, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements CommandCallback, PacketCallback,
        EventCallback
{
    private PublicExposedFunctions out;
    private boolean                sweepBan   = false;
    private boolean                sweepBanIp = false;

    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;

        register.registerCommandPlugin(
                                       this,
                                       "sweepban",
                                       1,
                                       true,
                                       "ON",
                                       "<channel>",
                                       "Bans everybody in the selected channel.  Two channels may not be sweepbanned at the same time.",
                                       null);
        register.registerCommandPlugin(
                                       this,
                                       "sweepbanip",
                                       1,
                                       true,
                                       "ON",
                                       "<channel>",
                                       "Bans everybody in the selected channel based on their ip.  Two channels may not be sweepbanned at the same time.",
                                       null);
        register.registerEventPlugin(this, null);
        register.registerIncomingPacketPlugin(this, SID_READUSERDATA, null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Sweepban";
    }

    public String getVersion()
    {
        return "2.1.2";
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
        return "Provides sweep-ban functionality";
    }

    public String getLongDescription()
    {
        return "Adds the .sweepban command which lets a user with access ban everybody in a channel";
    }

    public Properties getDefaultSettingValues()
    {
        return new Properties();
    }

    public Properties getSettingsDescription()
    {
        return new Properties();
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

    public void commandExecuted(String user, String command, String[] args, int loudness,
            Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if (args.length == 0)
            throw new CommandUsedImproperly("Sweepban requires a parameter (the channel name)",
                    user, command);

        if (command.equalsIgnoreCase("sweepban"))
            sweepBan = true;
        else if (command.equalsIgnoreCase("sweepbanip"))
            sweepBanIp = true;

        out.sendTextUser(user, "Sweeping channel " + args[0], loudness);

        // Retrieve the list of users
        BnetPacket who = new BnetPacket(SID_CHATCOMMAND);
        who.addNTString("/who " + args[0]);
        out.sendPacket(who);

        // Send a profile request so we can tell when it's done (thanks to
        // Stealth)
        BnetPacket delay = new BnetPacket(SID_READUSERDATA);
        delay.addDWord(1); // accounts
        delay.addDWord(1); // keys
        delay.addDWord(getName().hashCode()); // cookie
        delay.addNTString("iago");
        delay.addNTString("profile\\sex");
        out.sendPacket(delay);
        out.systemMessage(DEBUG, "Sweepban enabled");
    }

    public void processedPacket(BnetPacket buf, Object data) throws PluginException
    {
        if (buf.getCode() == SID_READUSERDATA)
        {
            buf.removeDWord(); // #accounts
            buf.removeDWord(); // #keys
            if (buf.removeDWord() == getName().hashCode()) // it's ours
            {
                sweepBan = false;
                sweepBanIp = false;
                out.systemMessage(DEBUG, "Sweepban disabled");
            }
        }
    }

    public void info(String user, String message, int ping, int flags) throws IOException, PluginException
    {
        if (sweepBan == false && sweepBanIp == false)
            return;

        if (message.matches("[^ ]+|[^ ]+, [^ ]+") == false)
            return;

        String[] users = message.split(", ");

        if (users.length < 1 || users.length > 2)
        {
            out.systemMessage(WARNING, "Improper sweepban response detected");
            return;
        }

        ban(users[0]);

        if (users.length == 2)
            ban(users[1]);
    }

    private void ban(String user) throws IOException
    {
        if (out.dbHasAny(user, "FS", true))
        {
            out.systemMessage(DEBUG, "Not sweepbanning " + user);
            return;
        }

        out.systemMessage(DEBUG, "Sweepbanning " + user);

        if (sweepBanIp)
            out.sendTextPriority("/squelch " + user, PRIORITY_LOW);
        else
            out.sendTextPriority("/ban " + user + " Sweepban", PRIORITY_LOW);

        if (user.matches("\\[.*\\]"))
        {
            user = user.substring(1, user.length() - 1);

            out.systemMessage(DEBUG, "Sweepbanning " + user);
            if (sweepBanIp)
                out.sendTextPriority("/squelch " + user, PRIORITY_LOW);
            else
                out.sendTextPriority("/ban " + user + " Sweepban", PRIORITY_LOW);
        }

    }

    public BnetPacket processingPacket(BnetPacket buf, Object data) throws IOException, PluginException
    {
        return buf;
    }

    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }

    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
    {
    }
}
