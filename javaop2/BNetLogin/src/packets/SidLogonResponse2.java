package packets;

// For all packet classes
import util.BNetPacket;
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
	public static BNetPacket getOutgoing(PublicExposedFunctions pubFuncs)
		throws LoginException
	{
		String username = pubFuncs.getLocalSetting("Battle.net Login Plugin", "username");
		String password = pubFuncs.getLocalSetting("Battle.net Login Plugin", "password").toLowerCase();
		int clientToken = (Integer)pubFuncs.getLocalVariable("clientToken");
		int serverToken = (Integer)pubFuncs.getLocalVariable("serverToken");
		
		if(username == null)
			throw new LoginException("[BNET] Cannot login because username is null.");
		if(password == null)
			throw new LoginException("[BNET] Cannot login because password is null.");
		if(clientToken == 0)
			throw new LoginException("[BNET] Cannot login because client token isn't set. ???");
		if(serverToken == 0)
			throw new LoginException("[BNET] Cannot login because server token isn't set. ???");
	
		BNetPacket response = new BNetPacket(PacketConstants.SID_LOGONRESPONSE2);
		// (DWORD) Client Token
		response.addDWord(clientToken);
		// (DWORD) Server Token
		response.addDWord(serverToken);
		// (DWORD[5] Password Hash
		int[] myPass = DoubleHash.doubleHash(password, clientToken, serverToken);
		for(int i = 0; i < 5; i++)
			response.addDWord(myPass[i]);
		// (STRING) Username
		response.addNTString(username);

		return response;
	}
	
	public static void checkIncoming(PublicExposedFunctions pubFuncs,
			BNetPacket SidLogonResponse2) throws LoginException, AccountDneException
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
