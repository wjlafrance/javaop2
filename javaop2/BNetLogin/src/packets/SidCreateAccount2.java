package packets;

// For all packet classes
import util.BNetPacket;
import constants.PacketConstants;
import exceptions.*;
import callback_interfaces.PublicExposedFunctions;

// For this class only
import password.BrokenSHA1;

/*
 * Created on Sep 25, 2009 by joe
 */

/**
 * Handles all things SID_CREATEACCOUNT2
 */
public class SidCreateAccount2
{

	public static BNetPacket getOutgoing(PublicExposedFunctions pubFuncs)
		throws LoginException
	{
		String password = pubFuncs.getLocalSetting( "Battle.net Login Plugin",
			"password").toLowerCase();
		String username = pubFuncs.getLocalSetting("Battle.net Login Plugin",
			"username");
		
		if(password == null)
			throw new LoginException("[BNET] Cannot create account, password is null.");
		if(username == null)
			throw new LoginException("[BNET] Cannot create account, username is null.");

		BNetPacket SidCreateAccount2 = new BNetPacket(
				PacketConstants.SID_CREATEACCOUNT2);
		
		int[] passwordHash = BrokenSHA1.calcHashBuffer(password.getBytes());
		for(int i = 0; i < 5; i++)
			SidCreateAccount2.addDWord(passwordHash[i]);
		
		SidCreateAccount2.addNTString(username);
	
		return SidCreateAccount2;
	}
}
