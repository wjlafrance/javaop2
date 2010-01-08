import exceptions.*;
import packets.*;

import password.BrokenSHA1;
import password.DoubleHash;
import password.SRP;

import callback_interfaces.PublicExposedFunctions;

import util.BNetPacket;
import util.Buffer;

import constants.ErrorLevelConstants;
import constants.PacketConstants;

/*
 * Created on Dec 9, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class Login
{
	private SRP srp;
	private SRP accountChangeSrp;
	private String prefSection;

	private byte[] salt;
	private byte[] B;

	public Login(String prefSection)
	{
		this.prefSection = prefSection;
	}

	/**
	 * Returns a single byte array, 0x01. This is the initial packet sned on login
	 */
	public Buffer getProtocolByte()
	{
		return new Buffer(new byte[] { 0x01 });
	}


	/****************************
	 * Legacy (non-nls) functions
	 */

	public BNetPacket checkCreateAccount(PublicExposedFunctions out, BNetPacket createAccount) throws InvalidPassword,
			PluginException
	{
		int status = createAccount.removeDWord();

		switch(status)
		{
		case 0: // Account created
			switch((Integer)out.getLocalVariable("loginType"))
			{
				case 0:
					return SidLogonResponse2.getOutgoing(out);
				case 1:
					return SidAccountLogon.getOutgoing(out);
				case 2:
					return SidAccountLogon.getOutgoing(out);
			}
		case 2:
			throw new InvalidPassword("[BNET] Name contained invalid characters");
		case 3:
			throw new InvalidPassword("[BNET] Name contained a banned word");
		case 4:
			throw new InvalidPassword("[BNET] Account already exists");
		case 6:
			throw new InvalidPassword("[BNET] Name did not contain enough alphanumeric characters");
		default:
			throw new InvalidPassword("[BNET] Unknown error creating account: " + status);

		}
	}

	public BNetPacket getChangePassword(PublicExposedFunctions out) throws PluginException
	{
		String username = out.getLocalSetting(prefSection, "username");
		String password = out.getLocalSetting(prefSection, "password").toLowerCase();
		int loginType = (Integer)out.getLocalVariable("loginType");
		int clientToken = (Integer)out.getLocalVariable("clientToken");
		int serverToken = (Integer)out.getLocalVariable("serverToken");
		
		if(loginType == 0)
		{
			BNetPacket changePassword = new BNetPacket(PacketConstants.SID_CHANGEPASSWORD);

			changePassword.add(clientToken);
			changePassword.add(serverToken);

			int[] oldPassword = DoubleHash.doubleHash(password, clientToken, serverToken);
			for(int i = 0; i < 5; i++)
				changePassword.add(oldPassword[i]);

			int[] newPassword = BrokenSHA1.calcHashBuffer(out.getLocalSetting(prefSection, "password change")
					.toLowerCase().getBytes());
			for(int i = 0; i < 5; i++)
				changePassword.add(newPassword[i]);

			changePassword.addNTString(username);

			return changePassword;
		}
		else if(loginType == 1 || loginType == 2)
		{
			BNetPacket changePassword = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCHANGE);
			srp = new SRP(username, password);

			changePassword.add(srp.get_A());
			changePassword.addNTString(username);

			return changePassword;
		}

		throw new LoginException("[BNET] Unable to login in with type " + loginType);
	}

	public BNetPacket checkPasswordChange(PublicExposedFunctions out, BNetPacket passwordChangePacket)
			throws InvalidPassword, PluginException
	{
		int status = passwordChangePacket.removeDWord();

		if(status == 0)
			throw new InvalidPassword("[BNET] Password change failed");

		out.systemMessage(ErrorLevelConstants.DEBUG, "[BNET] Password change successful");
		// Switch the new password to the current password
		out.putLocalSetting(prefSection, "password", out.getLocalSetting(prefSection, "password change"));
		out.putLocalSetting(prefSection, "password change", "");

		switch((Integer)out.getLocalVariable("loginType"))
		{
			case 0:
				return SidLogonResponse2.getOutgoing(out);
			case 1:
				return SidAccountLogon.getOutgoing(out);
			case 2:
				return SidAccountLogon.getOutgoing(out);
			default:
				throw new LoginException("[BNET] Unable to login in with type " +
						(Integer)out.getLocalVariable("loginType"));
		}
	}


	/***************
	 * NLS Functions
	 */
	public BNetPacket getLogonProof(PublicExposedFunctions out, BNetPacket authCheckPacket) throws PluginException,
			InvalidPassword
	{
		int status = authCheckPacket.removeDWord();

		if(srp == null)
			throw new RuntimeException(
					"[BNET] SID_AUTH_ACCOUNTLOGIN was received without SRP being set up -- this shouldn't happen.");

		switch(status)
		{
		case 0:
			salt = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);
			B = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);


			BNetPacket accountLogonProof = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTLOGONPROOF);
			accountLogonProof.add(srp.getM1(salt, B));

			return (accountLogonProof);

		case 1:

			BNetPacket accountCreate = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCREATE);

			// The salt is just a random value
			salt = srp.get_A();

			// 0x52 - SID_AUTH_ACCOUNTCREATE -
			// (DWORD[8]) s
			// (DWORD[8]) v
			// (STRING) Username
			accountCreate.add(salt);
			accountCreate.add(srp.get_v(salt).toByteArray());
			accountCreate.addNTString(out.getLocalSettingDefault(prefSection, "username", "<INSERT USERNAME"));

			return accountCreate;

		case 5:
			throw new InvalidPassword("[BNET] Account needs to be upgraded.");

		default:
			throw new InvalidPassword("[BNET] Login failed with unknown error code: " + status);
		}
	}

	public void checkLogonProof(BNetPacket buf) throws PluginException, InvalidPassword
	{
		int status = buf.removeDWord();

		switch(status)
		{
		case 0:
		case 0xe:

			byte[] serverProof = buf.removeBytes(SRP.SHA_DIGESTSIZE);
			byte[] M2 = srp.getM2(salt, B);

			for(int i = 0; i < serverProof.length; i++)
			{
				if(serverProof[i] != M2[i])
					throw new PluginException("[BNET] Server failed to provide proof that it knows your password!");
			}

			break;

		case 2:
			throw new InvalidPassword("[BNET] Login failed: incorrect password.");

		default:
			throw new InvalidPassword("[BNET] Login failed with unknown error: " + status);
		}
	}

	public BNetPacket checkAuthCreateAccount(PublicExposedFunctions out, BNetPacket buf) throws PluginException,
			InvalidPassword
	{
		int status = buf.removeDWord();

		switch(status)
		{
		case 0x00:
			switch((Integer)out.getLocalVariable("loginType"))
			{
				case 0:
					return SidLogonResponse2.getOutgoing(out);
				case 1:
					return SidAccountLogon.getOutgoing(out);
				case 2:
					return SidAccountLogon.getOutgoing(out);
			}
		case 0x06:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name is in use");
		case 0x07:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name is not long enough");
		case 0x08:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name contains bad characters");
		case 0x09:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name contains bad words");
		case 0x0A:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name needs more alphanumeric characters");
		case 0x0B:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name cannot have adjacent puncuation");
		case 0x0C:
			throw new InvalidPassword("[BNET] NLS Create Account failed: Name has too much puncuation");
		default:
			throw new InvalidPassword("[BNET] NLS Create Account failed with an unknown error: " + status);
		}
	}

	public BNetPacket authCheckAccountChange(PublicExposedFunctions out, BNetPacket buf) throws InvalidPassword
	{
		int status = buf.removeDWord();

		if(status == 1)
			throw new InvalidPassword("[BNET] Account doesn't exist -- create before trying to change password");
		else if(status == 5)
			throw new InvalidPassword("[BNET] Account needs to be upgraded");
		else if(status != 0)
			throw new InvalidPassword("[BNET] Unknown NLS Change Password error code: " + status);

		accountChangeSrp = new SRP(out.getLocalSetting(prefSection, "username"), out.getLocalSetting(prefSection,
				"password change"));
		// accountChangeSrp = srp;

		BNetPacket proof = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCHANGEPROOF);

		salt = buf.removeBytes(SRP.BIGINT_SIZE);
		B = buf.removeBytes(SRP.BIGINT_SIZE);

		proof.add(srp.getM1(salt, B));
		proof.add(salt);
		proof.add(accountChangeSrp.get_v(salt).toByteArray());

		return proof;
	}

	public BNetPacket authCheckAccountChangeProof(PublicExposedFunctions out, BNetPacket buf) throws InvalidPassword,
			PluginException
	{
		int status = buf.removeDWord();

		if(status == 2)
			throw new InvalidPassword("[BNET] Account change failed: invalid old password");
		else if(status != 0)
			throw new InvalidPassword("[BNET] Account change failed: unknown error: " + status);

		byte[] recvM2 = buf.removeBytes(SRP.SHA_DIGESTSIZE);
		byte[] realM2 = srp.getM2(salt, B);

		for(int i = 0; i < realM2.length; i++)
			if(recvM2[i] != realM2[i])
				throw new PluginException("[BNET] Server failed to provide proof that it knows your password!");

		out.systemMessage(ErrorLevelConstants.DEBUG, "[BNET] Password successfully changed!");

		// Switch the new password to the current password
		out.putLocalSetting(prefSection, "password", out.getLocalSetting(prefSection, "password change"));
		out.putLocalSetting(prefSection, "password change", "");

		srp = null;
		accountChangeSrp = null;

		switch((Integer)out.getLocalVariable("loginType"))
		{
			case 0:
				return SidLogonResponse2.getOutgoing(out);
			case 1:
				return SidAccountLogon.getOutgoing(out);
			case 2:
				return SidAccountLogon.getOutgoing(out);
			default:
				throw new LoginException("[BNET] Unable to login in with type " +
						(Integer)out.getLocalVariable("loginType"));
		}
	}

	/*******************
	 * Generic functions
	 */

	public BNetPacket getEnterChat(PublicExposedFunctions out)
	{
		BNetPacket enterChat = new BNetPacket(PacketConstants.SID_ENTERCHAT);
		enterChat.addNTString(out.getLocalSettingDefault(prefSection, "username", "not.iago.x86"));
		enterChat.addNTString("");

		return enterChat;
	}

	public BNetPacket getJoinHomeChannel(PublicExposedFunctions out)
	{
		BNetPacket enterChannel = new BNetPacket();
		enterChannel.setCode(PacketConstants.SID_JOINCHANNEL);
		enterChannel.addDWord(0x02);
		enterChannel.addNTString(out.getLocalSettingDefault(prefSection, "home channel", "op x86"));

		return enterChannel;
	}

}