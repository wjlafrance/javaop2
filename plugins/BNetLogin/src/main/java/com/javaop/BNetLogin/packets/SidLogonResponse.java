package com.javaop.BNetLogin.packets;

import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.constants.PacketConstants;
import com.javaop.exceptions.LoginException;
import com.javaop.util.BnetPacket;

/**
 * SID_LOGONRESPONSE (0x29), the logon Diablo (DRTL/DSHR) uses on useast. The C>S body is the same double
 * Broken-SHA1 hash as SID_LOGONRESPONSE2; the S>C reply is a single DWORD, 1 = success.
 */
public class SidLogonResponse
{
	public static BnetPacket getOutgoing(PublicExposedFunctions pubFuncs) throws LoginException
	{
		return SidLogonResponse2.getOutgoing(pubFuncs, PacketConstants.SID_LOGONRESPONSE);
	}

	public static void checkIncoming(BnetPacket packet) throws LoginException
	{
		check(packet.removeDWord());
	}

	/** Result 1 is success; 0 (and 2 on older servers) is a refusal. */
	static void check(int result) throws LoginException
	{
		switch(result)
		{
			case 1:
				return;
			case 0:
			case 2:
				throw new LoginException("[BNET] Login failed -- incorrect password or the account doesn't exist "
						+ "(no account is created automatically for Diablo).");
			default:
				throw new LoginException("[BNET] Login failed with unknown error code: 0x"
						+ Integer.toHexString(result));
		}
	}
}
