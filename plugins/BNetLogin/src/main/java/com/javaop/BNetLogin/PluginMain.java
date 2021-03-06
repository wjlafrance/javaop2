package com.javaop.BNetLogin;

import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;

import com.javaop.constants.ErrorLevelConstants;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;

import com.javaop.exceptions.*;

import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.ConnectionCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.PacketCallback;

import com.javaop.util.BnetPacket;
import com.javaop.util.gui.JTextFieldNumeric;

import com.javaop.BNetLogin.cdkey.Decode;
import com.javaop.BNetLogin.versioning.Game;
import com.javaop.BNetLogin.versioning.GameData;

import com.javaop.BNetLogin.packets.*;

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
	private PublicExposedFunctions pubFuncs;
	private SrpLogin srpLogin;

	public void load(StaticExposedFunctions staticFuncs) {
	}

	public void activate(PublicExposedFunctions pubFuncs, PluginCallbackRegister register) {
		this.pubFuncs = pubFuncs;
		this.srpLogin = new SrpLogin();

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

		register.registerCommandPlugin(this, "game", 0, false, "AGLN", "",
				"Shows the game that the bot is connected as", null);
		register.registerCommandPlugin(this, "home", 0, false, "J", "",
				"Tells the bot to go to its home channel", null);
	}

	public void deactivate(PluginCallbackRegister bot) {
	}

	public boolean connecting(String server, int port, Object data) {
		try {
			// Verify the cdkeys if needed
			int numberOfKeys = new GameData().numberOfKeys(pubFuncs.getLocalSetting(getName(), "game"));
			if (numberOfKeys >= 1) {
				Decode.getDecoder(pubFuncs.getLocalSetting(getName(), "cdkey"));
			}
			if (numberOfKeys >= 2) {
				Decode.getDecoder(pubFuncs.getLocalSetting(getName(), "cdkey2"));
			}
			return true;
		} catch (IllegalArgumentException iae) {
			pubFuncs.systemMessage(CRITICAL, "[BNET] Caught exception while "
					+ "validating CD-Keys: " + iae);
			return false;
		}
	}

	public void connected(String server, int port, Object data) throws
			PluginException, IOException
	{
		login = new Login(getName(), srpLogin);

		pubFuncs.systemMessage(INFO, "[BNET] Switching to BnChat protocol and "
				+ "calculating authorization info..");
		pubFuncs.sendPacket(login.getProtocolByte());

		BnetPacket sidAuthInfo = SidAuthInfo.getOutgoing(pubFuncs);
		pubFuncs.systemMessage(INFO, "[BNET] Calculated. Sending authorization "
				+ "info..");
		pubFuncs.sendPacket(sidAuthInfo);
	}

	public boolean disconnecting(Object data) {
		return true;
	}

	public void disconnected(Object data) {
	}

	public BnetPacket processingPacket(BnetPacket buf, Object data) {
		// SID_ENTERCHAT has to come first
		switch(buf.getCode()) {
			case SID_ENTERCHAT:
				pubFuncs.putLocalVariable("username", buf.removeNTString());
				pubFuncs.putLocalVariable("statstring", buf.removeNTString());
				pubFuncs.unlock();
				break;
		}

		return buf;
	}

	public String getName() {
		return "Battle.net Login Plugin";
	}

	public String getVersion() {
		return "2.1.3";
	}

	public String getAuthorName() {
		return "iago, wjlafrance";
	}

	public String getAuthorWebsite() {
		return "javaop.com";
	}

	public String getAuthorEmail() {
		return "iago@valhallalegends.com, wjlafrance@gmail.com";
	}

	public String getLongDescription() {
		return "This is my version of the Battle.net login.  It starts with "
				+ "sending SID_AUTHINFO when the connection is made, and ends "
				+ "with SID_ENTERCHAT and SID_JOINCHANNEL.  It supports any "
				+ "keyed product, from Starcraft to War3 Expansion. For the "
				+ "code itself, the CD-Key decoding for the legacy products "
				+ "was reversed and written by iago. The CD-Key decoding for "
				+ "Warcraft 3 is based on work done by Maddox and Telos, "
				+ "ported to Java by iago. The CheckRevision and SHA1 code "
				+ "used for the legacy products is based on Yobgul's code, "
				+ "again ported to Java by iago. Finally, the SRP (WarCraft 3 "
				+ "login) code was reversed and written by Maddox, iago, and "
				+ "TheMinistered and ported to Java by iago.";
	}

	public Properties getDefaultSettingValues() {
		Properties p = new Properties();

		p.setProperty("auto-change password", "false");
		p.setProperty("auto-change how often", "3");
		p.setProperty("auto-change password display", "false");

		p.setProperty("username", "<USERNAME GOES HERE>");
		p.setProperty("password", "<PASSWORD GOES HERE>");
		p.setProperty("password change", "");
		p.setProperty("cdkey", "<CDKEY GOES HERE>");
		p.setProperty("cdkey2", "");
		p.setProperty("home channel", "clan bot");
		p.setProperty("game", "SEXP");
		p.setProperty("Verify server", "true");

		return p;
	}

	public Properties getSettingsDescription() {
		Properties p = new Properties();

		p.setProperty("auto-change password", "If this is set, your password "
				+ "will occasionally be changed to a random string of 10 "
				+ "characters.  This will happen invisibly, but if you use "
				+ "other bots I don't recommend this.  I highly recommend "
				+ "registering your email address if you use this, just in "
				+ "case your config file gets lost.");
		p.setProperty("auto-change how often", "After how many logs to change "
				+ "the password");
		p.setProperty("auto-change password display", "If this is enabled, "
				+ "the password is displayed when it is changed.");
		p.setProperty("username", "The username to log onto Battle.net with.");
		p.setProperty("password", "The password to log onto Battle.net with.");
		p.setProperty("password change", "If this is filled in, on the next "
				+ "login your password will be changed to it and the value "
				+ "will be moved to \"password\".  Leave blank for no change. "
				+ "If you were thinking, \"How do I set a blank password?\""
				+ "away from me.");
		p.setProperty("cdkey", "The CDKey to log in with.");
		p.setProperty("cdkey2", "This is the cdkey for Lord of Destruction or "
				+ "The Frozen Throne.");
		p.setProperty("home channel", "The default channel to join when the "
				+ "bot logs onto Battle.net.");
		p.setProperty("game", "The game client to log on as.");
		p.setProperty("Verify server", "Turning this off will still attempt to "
				+ "verify the server, but will force connection even if it's "
				+ "not verified.");

		return p;
	}

	public JComponent getComponent(String settingName, String value) {
		if(settingName.equalsIgnoreCase("password")
				|| settingName.equalsIgnoreCase("password change")) {
			return new JPasswordField(value);
		} if(settingName.equalsIgnoreCase("Verify server")
				|| settingName.equalsIgnoreCase("auto-change password")
				|| settingName.equalsIgnoreCase("auto-change password display"))
		{
			return new JCheckBox("", value.equalsIgnoreCase("true"));
		} else if(settingName.equalsIgnoreCase("game")) {
			Vector<String> gameOptions = Game.getGames().stream().collect(Collectors.toCollection(Vector::new));
			JComboBox combo = new JComboBox(gameOptions);
			combo.setEditable(true);
			combo.setSelectedItem(value);
			return combo;
		} else if(settingName.equalsIgnoreCase("auto-change how often")) {
			return new JTextFieldNumeric(value);
		}

		return null;
	}


	public Properties getGlobalDefaultSettingValues() {
		Properties p = new Properties();
		p.setProperty("BNLS Server", "vultr-chi.bnetdocs.org");
		p.setProperty("Enable BNLS", "true");
		return p;
	}

	public Properties getGlobalSettingsDescription() {
		Properties p = new Properties();
		p.setProperty("BNLS Server", "The server that is used for versioning "
				+ "information. It does *NOT* process your cdkey or password, "
				+ "those are done locally.");
		p.setProperty("Enable BNLS", "Allow the bot to use BNLS? Remember, "
				+ "BNLS will never handle your CD-Key or password.");
		return p;
	}

	public JComponent getGlobalComponent(String settingName, String value) {
		if(settingName.equalsIgnoreCase("Enable BNLS"))
			return new JCheckBox("", value.equalsIgnoreCase("true"));

		return null;
	}

	public void commandExecuted(String user, String command, String[] args,
			int loudness, Object data) throws IOException, LoginException,
			CommandUsedIllegallyException, CommandUsedImproperlyException
	{
		if(command.equalsIgnoreCase("game")) {
			if(args.length > 0) {
				for(int i = 0; i < args.length; i++) {
					try {
						pubFuncs.sendTextUserPriority(user,
								new Game(args[i]).toString(), loudness,
								PRIORITY_LOW);
					} catch(Exception e) {
						pubFuncs.sendTextUserPriority(user, "Game \""
								+ args[i] + "\" could not be found.", loudness,
								PRIORITY_LOW);
					}
				}
			} else {
				pubFuncs.sendTextUserPriority(user, new Game(pubFuncs
						.getLocalSettingDefault(getName(), "game", "STAR"))
						.toString(), loudness, PRIORITY_LOW);
			}
		} else if(command.equalsIgnoreCase("home")) {
			pubFuncs.sendPacket(login.getJoinHomeChannel(pubFuncs));
		}
	}

	public void processedPacket(BnetPacket buf, Object data) throws
			PluginException, IOException
	{
		switch(buf.getCode()) {

			case SID_CHANGEPASSWORD: // 0x31
				pubFuncs.sendPacket(login.checkPasswordChange(pubFuncs, buf));
				pubFuncs.systemMessage(INFO, "[BNET] Password successfully " +
						"changed, logging in..");
				break;

			case SID_LOGONRESPONSE2: // 0x3A
				try {
					SidLogonResponse2.checkIncoming(pubFuncs, buf);
					// exception thrown if SID_LOGINRESPONSE2 fails
					pubFuncs.systemMessage(INFO, "[BNET] Logon successful! "
							+ "Entering chat..");
					pubFuncs.sendPacket(login.getEnterChat(pubFuncs));
					pubFuncs.sendPacket(login.getJoinHomeChannel(pubFuncs));
				} catch(AccountDneException adne) {
					pubFuncs.systemMessage(ErrorLevelConstants.WARNING,
						"[BNET] Account doesn't exist, attempting to create..");
					pubFuncs.sendPacket(SidCreateAccount2.getOutgoing(pubFuncs));
				}
				break;

			case SID_CREATEACCOUNT2: // 0x3D
				pubFuncs.sendPacket(login.checkCreateAccount(pubFuncs, buf));
				pubFuncs.systemMessage(INFO, "[BNET] Account successfully "
						+ "created, trying to log in..");
				break;

			case SID_AUTH_INFO: // 0x50
				pubFuncs.systemMessage(INFO, "[BNET] Received response, "
						+ "calculating..");
				BnetPacket sidAuthCheck = SidAuthCheck.getOutgoing(pubFuncs, buf);
				pubFuncs.systemMessage(INFO, "[BNET] Calculated. Sending "
						+ "CD-Key and version check..");
				pubFuncs.sendPacket(sidAuthCheck);
				break;

			case SID_AUTH_CHECK: // 0x51
				SidAuthCheck.checkIncoming(pubFuncs, buf);
				// exception thrown if SID_AUTH_CHECK fails
				pubFuncs.systemMessage(INFO, "[BNET] CD-Key and version check "
					+ "successful. Attempting to log in..");

				switch((Integer)pubFuncs.getLocalVariable("loginType")) {
					case 0:
						pubFuncs.sendPacket(SidLogonResponse2.getOutgoing(pubFuncs));
						break;
					case 1:
					case 2:
						pubFuncs.sendPacket(srpLogin.getSidAuthAccountLogon(pubFuncs));
						break;
					default:
						throw new LoginException("[BNET] Unable to login in "
								+ "with login type " + (Integer)pubFuncs
								.getLocalVariable("loginType"));
				}
				break;

			case SID_AUTH_ACCOUNTCREATE: // 0x52
				srpLogin.checkSidAuthCreateAccount(pubFuncs, buf);
				pubFuncs.systemMessage(INFO, "[BNET] Account successfully " +
						"created, trying to log in..");
				pubFuncs.sendPacket(srpLogin.getSidAuthAccountLogon(pubFuncs));
				break;

			case SID_AUTH_ACCOUNTLOGON: // 0x53
				pubFuncs.systemMessage(INFO, "[BNET] Login proof requested..");
				pubFuncs.sendPacket(srpLogin.getSidAuthAccountLogonProof(
						pubFuncs, buf));
				break;

			case SID_AUTH_ACCOUNTLOGONPROOF: // 0x54
				pubFuncs.systemMessage(INFO, "[BNET] Checking NLS proof..");
				srpLogin.checkSidAuthAccountLogonProof(pubFuncs, buf);
				pubFuncs.systemMessage(INFO, "[BNET] NLS logon successful!");
				pubFuncs.sendPacket(login.getEnterChat(pubFuncs));
				pubFuncs.sendPacket(login.getJoinHomeChannel(pubFuncs));
				break;

			case SID_AUTH_ACCOUNTCHANGE: // 0x55
				pubFuncs.systemMessage(INFO, "[BNET] Password change response "
						+ "received, replying with proof..");
				pubFuncs.sendPacket(srpLogin.getSidAuthAccountChangeProof(
						pubFuncs, buf));
				break;

			case SID_AUTH_ACCOUNTCHANGEPROOF: // 0x56
				pubFuncs.systemMessage(INFO, "[BNET] Account change proof "
						+ "received.");
				srpLogin.checkSidAuthAccountLogonProof(pubFuncs, buf);
				break;

			case SID_WARDEN: // 0x5E
				pubFuncs.systemMessage(ERROR, "[BNET] Ignoring Warden "
						+ "challenge. You'll most likely be disconnected in "
						+ "two minutes.");
				break;
		}
	}
}
