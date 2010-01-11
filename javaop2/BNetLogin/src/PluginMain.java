import java.io.IOException;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;

import constants.ErrorLevelConstants;

import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;

import cdkey.Decode;

import exceptions.*;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.ConnectionCallback;
import plugin_interfaces.GenericPluginInterface;
import plugin_interfaces.PacketCallback;

import util.BNetPacket;
import util.gui.JTextFieldNumeric;

import versioning.Game;

import packets.*;

/*
 * Created on Dec 7, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements ConnectionCallback, PacketCallback, CommandCallback
{

	private Login login;
	private PublicExposedFunctions out;

	public void load(StaticExposedFunctions staticFuncs)
	{
	}

	public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
	{
		this.out = out;

		register.registerConnectionPlugin(this, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_INFO, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_CHECK, null);
		register.registerIncomingPacketPlugin(this, SID_LOGONRESPONSE2, null);
		register.registerIncomingPacketPlugin(this, SID_CHANGEPASSWORD, null);
		register.registerIncomingPacketPlugin(this, SID_CREATEACCOUNT2, null);
		register.registerIncomingPacketPlugin(this, SID_WARDEN, null);

		register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTLOGON, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTLOGONPROOF, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTCREATE, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTCHANGE, null);
		register.registerIncomingPacketPlugin(this, SID_AUTH_ACCOUNTCHANGEPROOF, null);

		register.registerIncomingPacketPlugin(this, SID_ENTERCHAT, null);

		register.registerCommandPlugin(this, "game", 0, false, "AGLN", "", "Shows the game that the bot is connected as", null);
		register.registerCommandPlugin(this, "home", 0, false, "J", "", "Tells the bot to go to its home channel", null);
	}

	public void deactivate(PluginCallbackRegister bot)
	{
	}

	public boolean connecting(String server, int port, Object data)
	{
		try
		{
			// Verify the cdkey
			versioning.GameData g = new versioning.GameData();
			boolean hasTwoKeys = g.hasTwoKeys(out.getLocalSetting(getName(), "game"));
						
			Decode.getDecoder(out.getLocalSetting(getName(), "cdkey"));
			if(hasTwoKeys)
				Decode.getDecoder(out.getLocalSetting(getName(), "cdkey2"));


			return true;
		}
		catch(Exception e)
		{
			out.systemMessage(CRITICAL, "[BNET] Unable to connect due to exception: " + e);
			return false;
		}
	}

	public void connected(String server, int port, Object data) throws PluginException, IOException
	{
		login = new Login(getName());

		out.systemMessage(INFO, "[BNET] Switching to BnChat protocol. Calculating authorization info..");
		out.sendPacket(login.getProtocolByte());
		
		BNetPacket sidAuthInfo = SidAuthInfo.getOutgoing(out);
		out.systemMessage(INFO, "[BNET] Calculated. Sending authorization info..");
		out.sendPacket(sidAuthInfo);
	}

	public boolean disconnecting(Object data)
	{
		return true;
	}

	public void disconnected(Object data)
	{

	}

	public BNetPacket processingPacket(BNetPacket buf, Object data)
	{
		// SID_ENTERCHAT has to come first
		switch(buf.getCode())
		{
		case SID_ENTERCHAT:
			out.putLocalVariable("username", buf.removeNTString());
			out.putLocalVariable("statstring", buf.removeNTString());
			out.unlock();
			break;
		}

		return buf;
	}

	public String getName()
	{
		return "Battle.net Login Plugin";
	}

	public String getVersion()
	{
		return "2.1.2";
	}

	public String getAuthorName()
	{
		return "iago, wjlafrance";
	}

	public String getAuthorWebsite()
	{
		return "www.javaop.com";
	}

	public String getAuthorEmail()
	{
		return "iago@valhallalegends.com, wjlafrance@gmail.com";
	}

	public String getLongDescription()
	{
		return "This is my version of the Battle.net login.  It starts with sending SID_AUTHINFO when the connection is made, and "
				+ "ends with SID_ENTERCHAT and SID_JOINCHANNEL.  It supports any keyed product, from Starcraft to War3 Expansion.  "
				+ "For the code itself, the CDKey decoding for the legacy products was reversed and written by me.  The CDKey "
				+ "decoding for Warcraft 3 is based on work done by Maddox and Telos, ported to Java by me.  The CheckRevision "
				+ "and SHA1 code used for the legacy products is based on Yobgul's code, again ported to Java by me.  Finally, "
				+ "the SRP (war3 login) code was reversed and written by Maddox, myself, and TheMinistered and ported to Java "
				+ "by me.\n"
				+ "\n"
				+ "Support for RCRS was removed and support for BNLS was added by Joe[x86] in responce to Blizzard's \"lockdown\"";
	}

	public Properties getDefaultSettingValues()
	{
		Properties p = new Properties();

		p.setProperty("auto-change password", "false");
		p.setProperty("auto-change how often", "3");
		p.setProperty("auto-change password display", "false");

		p.setProperty("username", "<USERNAME GOES HERE>");
		p.setProperty("password", "<PASSWORD GOES HERE>");
		p.setProperty("password change", "");
		p.setProperty("cdkey", "<CDKEY GOES HERE>");
		p.setProperty("cdkey2", "");
		p.setProperty("home channel", "clan lw");
		p.setProperty("game", "SEXP");
		p.setProperty("Verify server", "true");

		return p;
	}

	public Properties getSettingsDescription()
	{
		Properties p = new Properties();

		p.setProperty("auto-change password", "If this is set, your password will occasionally be changed to a random string of 10 characters.  This will happen invisibly, but if you use other bots I don't recommend this.  I highly recommend registering your email address if you use this, just in case your config file gets lost.");
		p.setProperty("auto-change how often", "After how many logs to change the password");
		p.setProperty("auto-change password display", "If this is enabled, the password is displayed when it is changed.");
		p.setProperty("username", "The username to log onto Battle.net with.");
		p.setProperty("password", "The password to log onto Battle.net with.");
		p.setProperty("password change", "If this is filled in, on the next login your password will be changed to it and the value will be moved to \"password\".  Leave blank for no change.  If you were thinking, \"How do I set a blank password?\" get away from me.");
		p.setProperty("cdkey", "The CDKey to log in with.");
		p.setProperty("cdkey2", "This is the cdkey for Lord of Destruction or The Frozen Throne.");
		p.setProperty("home channel", "The default channel to join when the bot logs onto Battle.net.");
		p.setProperty("game", "The game client to log on as.");
		p.setProperty("Verify server", "This tells the bot to check if the server is an authentic Battle.net server or an imposter.");
		
		return p;
	}

	public JComponent getComponent(String settingName, String value)
	{
		if(settingName.equalsIgnoreCase("password") || settingName.equalsIgnoreCase("password change"))
		{
			return new JPasswordField(value);
		}
		if(settingName.equalsIgnoreCase("Verify server")
			|| settingName.equalsIgnoreCase("auto-change password")
			|| settingName.equalsIgnoreCase("auto-change password display"))
		{
			return new JCheckBox("", value.equalsIgnoreCase("true"));
		}
		else if(settingName.equalsIgnoreCase("game"))
		{
			JComboBox combo = new JComboBox(Game.getGames());
			combo.setEditable(true);
			combo.setSelectedItem(value);
			return combo;
		}
		else if(settingName.equalsIgnoreCase("auto-change how often"))
		{
			return new JTextFieldNumeric(value);
		}

		return null;
	}


	public Properties getGlobalDefaultSettingValues()
	{
		Properties p = new Properties();
		p.setProperty("BNLS Server", "jailout2000.homeip.org");
		p.setProperty("Enable BNLS", "true");
		return p;
	}

	public Properties getGlobalSettingsDescription()
	{
		Properties p = new Properties();
		p.setProperty("BNLS Server", "The server that is used for versioning information. It does *NOT* process your cdkey or password, those are done locally. If you wish to disble this, set the value to blank.");
		p.setProperty("Enable BNLS", "Allow the bot to use BNLS? Remember, BNLS will never handle your CD-Key or password.");
		return p;
	}

	public JComponent getGlobalComponent(String settingName, String value)
	{
		if(settingName.equalsIgnoreCase("Enable BNLS"))
			return new JCheckBox("", value.equalsIgnoreCase("true"));
		
		return null;
	}

	public void commandExecuted(String user, String command, String[] args, int loudness, Object data)
			throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
	{
		if(command.equalsIgnoreCase("game"))
		{
			if(args.length > 0)
			{
				for(int i = 0; i < args.length; i++)
				{
					try
					{
						out.sendTextUserPriority(user, new Game(args[i]).toString(), loudness, PRIORITY_LOW);
					}
					catch(Exception e)
					{
						out.sendTextUserPriority(user, "Game \"" + args[i] + "\" could not be found.", loudness,
								PRIORITY_LOW);
					}
				}
			}
			else
			{
				out.sendTextUserPriority(user, new Game(out.getLocalSettingDefault(getName(), "game", "STAR")).toString(), loudness, PRIORITY_LOW);
			}
		}
		else if(command.equalsIgnoreCase("home"))
		{
			out.sendPacket(login.getJoinHomeChannel(out));
		}
	}

	public void processedPacket(BNetPacket buf, Object data) throws PluginException, IOException
	{
		switch(buf.getCode())
		{
			case SID_AUTH_INFO:
				out.systemMessage(INFO, "[BNET] Received response, calculating..");
				BNetPacket sidAuthCheck = SidAuthCheck.getOutgoing(out, buf);
				out.systemMessage(INFO, "[BNET] Calculated. Sending version check..");
				out.sendPacket(sidAuthCheck);
				break;
	
			case SID_AUTH_CHECK:
				SidAuthCheck.checkIncoming(out, buf); // will throw exception if
				// version check failed
				out.systemMessage(INFO, "[BNET] CDKey and Version check " +
					"successful. Attempting to log in.");
	
				switch((Integer)out.getLocalVariable("loginType"))
				{
					case 0:
						out.sendPacket(SidLogonResponse2.getOutgoing(out));
						break;
					case 1:
					case 2:
						out.sendPacket(SidAccountLogon.getOutgoing(out));
						break;
					default:
						throw new LoginException("[BNET] Unable to login in with " +
								"type " + (Integer)out.getLocalVariable("loginType"));
				}
				break;
	
			case SID_AUTH_ACCOUNTLOGON:
				out.sendPacket(login.getLogonProof(out, buf));
				out.systemMessage(INFO, "[BNET] NLS Logon: proof has been sent.");
				break;
	
			case SID_LOGONRESPONSE2:
				try
				{
					SidLogonResponse2.checkIncoming(out, buf);
					out.systemMessage(INFO, "[BNET] Logon successful! Entering chat.");
	
					out.sendPacket(login.getEnterChat(out));
					out.sendPacket(login.getJoinHomeChannel(out));
				}
				catch(AccountDneException adne)
				{
					out.systemMessage(ErrorLevelConstants.WARNING,
						"[BNET] Account doesn't exist, attempting to create");
					out.sendPacket(SidCreateAccount2.getOutgoing(out));
				}
				break;
	
			case SID_AUTH_ACCOUNTLOGONPROOF:
				out.systemMessage(INFO, "[BNET] Checking server's proof (that it actually knows your password)");
				login.checkLogonProof(buf);
				out.systemMessage(INFO, "[BNET] NLS Logon successful! Entering chat.");
				out.sendPacket(login.getEnterChat(out));
				out.sendPacket(login.getJoinHomeChannel(out));
	
				break;
	
			case SID_CREATEACCOUNT2:
				out.sendPacket(login.checkCreateAccount(out, buf));
				out.systemMessage(INFO, "[BNET] Account successfully created, trying to log in");
				break;
	
			case SID_AUTH_ACCOUNTCREATE:
				out.sendPacket(login.checkAuthCreateAccount(out, buf));
				out.systemMessage(INFO, "[BNET] Account successfully created, trying to log in");
				break;
	
			case SID_CHANGEPASSWORD:
				out.sendPacket(login.checkPasswordChange(out, buf));
				out.systemMessage(INFO, "[BNET] Password successfully changed, logging in");
				break;
	
			case SID_AUTH_ACCOUNTCHANGE:
				out.systemMessage(INFO, "[BNET] Account change info received, replying with proof..");
				out.sendPacket(login.authCheckAccountChange(out, buf));
				break;
	
			case SID_AUTH_ACCOUNTCHANGEPROOF:
				out.systemMessage(INFO, "[BNET] Account change proof received");
				out.sendPacket(login.authCheckAccountChangeProof(out, buf));
				break;
	
			case SID_WARDEN:
				out.systemMessage(ERROR, "[BNET] Ignoring Warden challenge -- Disconnection in two minutes.");
		}
	}
}
