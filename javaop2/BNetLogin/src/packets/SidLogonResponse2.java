package packets;

// For all packet classes
import util.BnetPacket;
import constants.PacketConstants;
import exceptions.*;
import callback_interfaces.PublicExposedFunctions;

// For this class only
import password.DoubleHash;

/*
 * Created on Sep 25, 2009 by joe
 */

/**
 * Handles all things SID_LOGONRESPONSE2
 */
public class SidLogonResponse2
{
	public static BnetPacket getOutgoing(PublicExposedFunctions pubFuncs)
		throws LoginException
	{
		String username = pubFuncs.getLocalSetting("Battle.net Login Plugin", "username");
		String password = pubFuncs.getLocalSetting("Battle.net Login Plugin", "password").toLowerCase();
		int clientToken = (Integer)pubFuncs.getLocalVariable("clientToken");
		int serverToken = (Integer)pubFuncs.getLocalVariable("serverToken");
		
		if (username == null || username.isEmpty())
			throw new LoginException("[BNET] Cannot login because username is null.");
		if (password == null || password.isEmpty())
			throw new LoginException("[BNET] Cannot login because password is null.");
		if (clientToken == 0)
			throw new LoginException("[BNET] Cannot login because client token isn't set. ???");
		if (serverToken == 0)
			throw new LoginException("[BNET] Cannot login because server token isn't set. ???");
	
		BnetPacket packet = new BnetPacket(PacketConstants.SID_LOGONRESPONSE2);
		// (DWORD) Client Token
		packet.addDWord(clientToken);
		// (DWORD) Server Token
		packet.addDWord(serverToken);
		// (DWORD[5] Password Hash
		int[] myPass = DoubleHash.doubleHash(password, clientToken, serverToken);
		for(int i = 0; i < 5; i++)
			packet.addDWord(myPass[i]);
		// (STRING) Username
		packet.addNTString(username);

		return packet;
	}
	
	public static void checkIncoming(PublicExposedFunctions pubFuncs,
			BnetPacket SidLogonResponse2) throws LoginException, AccountDneException
	{
		int result = SidLogonResponse2.removeDWord();

		switch(result)
		{
			case 0:
				break;
			case 1:
				throw new AccountDneException("[BNET] Account doesn't exist.");
			case 2:
				throw new LoginException("[BNET] Login failed -- invalid password.");
			default:
				throw new LoginException("[BNET] Login failed with unknown error " +
						"code: 0x" + Integer.toHexString(result));
		}
	}
	
	
}
