import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;
import util.BNetPacket;
import util.ColorConstants;
import util.Uniq;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

/**
 * @author iago
 *
 */
public class PluginMain extends GenericPluginInterface implements PacketCallback, CommandCallback, ClanConstants
{
    private PublicExposedFunctions out;
    private final Hashtable invites = new Hashtable();
    
    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        
        register.registerIncomingPacketPlugin(this, SID_CLANINFO, null);
        register.registerIncomingPacketPlugin(this, SID_CLANQUITNOTIFY, null);
        register.registerIncomingPacketPlugin(this, SID_CLANINVITE, null);
        register.registerIncomingPacketPlugin(this, SID_CLANMOTD, null);
        
        register.registerCommandPlugin(this, "accept", 1, false, "N", "<clan name>", "Accepts an invitation to a clan", null);
        register.registerCommandPlugin(this, "decline", 1, false, "N", "<clan name>", "Rejects an invitation to a clan", null);
        register.registerCommandPlugin(this, "invites", 0, false, "AN", "", "Lists the pending invites", null);
        //register.registerCommandPlugin(this, "invite", 1, false, "N", "", "Invites a user to join the clan", null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "War3 Clan Plugin";
    }

    public String getVersion()
    {
        return "0.1";
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
        return "Clan related stuff";
    }

    public String getLongDescription()
    {
        return "So far, just displays some clan-related information";
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
    
    

    public BNetPacket processingPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        return buf;
    }

    public void processedPacket(BNetPacket buf, Object data) throws IOException, PluginException
    {
        if(buf.getCode() == SID_CLANINFO)
        {
            buf.removeByte();
            String clanTag = getClanTag(buf.removeString(4));
            byte rank = buf.removeByte();
            
            out.showMessage(ColorConstants.getColor("Clan message") + "You're in clan " + ColorConstants.getColor("Clan emphasize") + clanTag + 
                            ColorConstants.getColor("Clan message") + " with rank " + ColorConstants.getColor("Clan emphasize") + clanMemberConstants[rank]);
        }
        else if(buf.getCode() == SID_CLANQUITNOTIFY)
        {
            out.showMessage(ColorConstants.getColor("Clan message") + "Removed from clan.");
        }
        else if(buf.getCode() == SID_CLANINVITE)
        {
            int cookie = buf.removeDWord();
            String clanTag = buf.removeString(4);
            String clanName = buf.removeNTString();
            String inviter = buf.removeNTString();
            
            Invite newInvite = new Invite(cookie, clanTag, clanName, inviter);
            
            invites.put(clanName, newInvite);
            
            out.showMessage(ColorConstants.getColor("Clan message") + "You were invited to a clan:");
            out.showMessage(ColorConstants.getColor("Clan message") + newInvite.toString());
            out.showMessage(ColorConstants.getColor("Clan message") + "Please use '/accept " + clanName + "' or '/decline " + clanName + "'");
            if(invites.size() > 1)
                out.showMessage(ColorConstants.getColor("Clan message") + "You have " + invites.size() + " pending invites.  Use '/invites' for more information");
        }
        else if(buf.getCode() == SID_CLANMOTD)
        {
            buf.removeDWord(); // cookie
            buf.removeDWord(); // Unknown (0)
            String motd = buf.removeNTString();
            
            out.showMessage(ColorConstants.getColor("Clan message") + "Message of the Day:");
            out.showMessage(ColorConstants.getColor("Clan emphasize") + motd);
        }
    }
    
    private String getClanTag(String clan)
    {
        StringBuffer ret = new StringBuffer();
        
        for(int i = clan.length() - 1; i >= 0; i--)
            if(clan.charAt(i) > 0)
                ret.append(clan.charAt(i));
                
        return ret.toString();
    }
    
    public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if(command.equalsIgnoreCase("accept") || command.equalsIgnoreCase("decline"))
        {
            if(args.length != 1)
                throw new CommandUsedImproperly("Accept and decline require the clan name as a parameter", user, command);
            
            Invite invite = (Invite)invites.get(args[0]);
            
            if(invite == null)
            {
                out.sendTextUser(user, "Clan name not found in invites: " + args[0], loudness);
            }
            else
            {
                if(command.equalsIgnoreCase("accept"))
                {
                    out.sendPacket(invite.getAccept());
                    out.sendTextUser(user, "Clan invitation accepted: " + invite, loudness);
                }
                else
                {
                    out.sendPacket(invite.getDecline());
                    out.sendTextUser(user, "Clan invitation rejected: " + invite, loudness);
                }
                
                invites.remove(args[0]);
            }
        }
        else if(command.equalsIgnoreCase("invites"))
        {
            String []clans = Uniq.uniq(invites.keys());
            out.sendTextUserPriority(user, "You have " + clans.length + " pending invitations", loudness, PRIORITY_LOW);
            
            for(int i = 0; i < clans.length; i++)
                out.sendTextUserPriority(user, (i + 1) + ": " + invites.get(clans[i]).toString(), loudness, PRIORITY_LOW);
        }
        else if(command.equalsIgnoreCase("invite"))
        {
            
        }
        else
        {
            out.sendTextUserPriority(user, "Error, unknown command in War3Clan: " + command, loudness, PRIORITY_LOW);
        }
    }


    private class Invite
    {
        final private int cookie;
        final private String clanTag;
        final private String clanName;
        final private String inviter;
        
        public Invite(int cookie, String clanTag, String clanName, String inviter)
        {
            this.cookie = cookie;
            this.clanTag = clanTag;
            this.clanName = clanName;
            this.inviter = inviter;
        }
        
        public String toString()
        {
            return "Tag: " + getClanTag(clanTag) + "; name: " + clanName + "; invited by: " + inviter;
        }
        
        public BNetPacket getAccept()
        {
            BNetPacket accept = new BNetPacket(SID_CLANINVITE);
            accept.add(cookie);
            accept.addString(clanTag);
            accept.addNTString(inviter);
            accept.addByte(CLAN_ACCEPT);
            return accept;
        }
        
        public BNetPacket getDecline()
        {
            BNetPacket decline = new BNetPacket(SID_CLANINVITE);
            decline.add(cookie);
            decline.addString(clanTag);
            decline.addNTString(inviter);
            decline.addByte(CLAN_DECLINE);
            return decline;
        }
    }
}
