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
public class SidCreateAccount2 {

	public static BNetPacket getOutgoing(PublicExposedFunctions pubFuncs) throws LoginException {
		String password = pubFuncs.getLocalSetting("Battle.net Login Plugin",
			"password").toLowerCase();
		String username = pubFuncs.getLocalSetting("Battle.net Login Plugin",
			"username");
		
		if(username == null || username.isEmpty())
			throw new LoginException("[BNET] Cannot create account, username is null.");
		if(password == null || password.isEmpty())
			throw new LoginException("[BNET] Cannot create account, password is null.");

		BNetPacket sidCreateAccount2 = new BNetPacket(PacketConstants.SID_CREATEACCOUNT2);
		// (DWORD[5]) Password
		int[] passwordHash = BrokenSHA1.calcHashBuffer(password.getBytes());
		for(int i = 0; i < 5; i++)
			sidCreateAccount2.addDWord(passwordHash[i]);
		// (STRING) Username
		sidCreateAccount2.addNTString(username);
	
		return sidCreateAccount2;
	}
	
}
