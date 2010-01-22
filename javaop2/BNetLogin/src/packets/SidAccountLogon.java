package packets;

// For all packet classes
import util.BnetPacket;
import constants.PacketConstants;
import exceptions.*;
import callback_interfaces.PublicExposedFunctions;

// For this class only
import password.SRP;

/*
 * Created on Sep 25, 2009 by joe
 */

/**
 * Handles all things SID_ACCOUNTLOGON
 */
public class SidAccountLogon
{
	public static BnetPacket getOutgoing(PublicExposedFunctions pubFuncs)
		throws LoginException
	{
		String username = pubFuncs.getLocalSetting("Battle.net Logon Plugin", "username");
		String password = pubFuncs.getLocalSetting("Battle.net Logon Plugin", "password").toLowerCase();
		int clientToken = (Integer)pubFuncs.getLocalVariable("clientToken");
		int serverToken = (Integer)pubFuncs.getLocalVariable("serverToken");
		
		if(username == null || username.isEmpty())
			throw new LoginException("[BNET] Cannot login because username is null.");
		if(password == null || password.isEmpty())
			throw new LoginException("[BNET] Cannot login because password is null.");
		if(clientToken == 0)
			throw new LoginException("[BNET] Cannot login because client token isn't set. ???");
		if(serverToken == 0)
			throw new LoginException("[BNET] Cannot login because server token isn't set. ???");
		
		SRP srp = new SRP(username, password);
		BnetPacket accountLogon = new BnetPacket(PacketConstants.SID_AUTH_ACCOUNTLOGON);

		accountLogon.add(srp.get_A());
		accountLogon.addNTString(username);

		return accountLogon;
	}
}
