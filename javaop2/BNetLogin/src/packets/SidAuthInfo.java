package packets;

// For all packet classes
import util.BnetPacket;
import constants.PacketConstants;
import exceptions.*;
import callback_interfaces.PublicExposedFunctions;

// For this class only
import versioning.Versioning;
import versioning.Game;

/*
 * Created on Sep 24, 2009 by joe
 */

/**
 * Handles all things SID_AUTH_INFO
 */
public class SidAuthInfo
{
	public static BnetPacket getOutgoing(PublicExposedFunctions pubFuncs)
		throws LoginException
	{
		// get game and validate
		String game = pubFuncs.getLocalSetting("Battle.net Login Plugin", "game");
		if(game == null)
			throw new LoginException("[BNET] Game not specified. Unable to send" +
					" Authorization Info");
		Game g = new Game(game);
		
		BnetPacket authinfo = new BnetPacket(PacketConstants.SID_AUTH_INFO);
		// (DWORD) Protocol ID
		authinfo.add(0x00000000);
		// (DWORD) Platform ID
		authinfo.add('I' << 24 | 'X' << 16 | '8' << 8 | '6' << 0);
		// (DWORD) Product ID
		authinfo.add(g.getGameCode());
		// (DWORD) Version Byte
		authinfo.add(Versioning.VersionByte(game, pubFuncs));
		// (DWORD) Product Language
		authinfo.add(0x00000000);
		// (DWORD) Local IP
		authinfo.add(0x00000000);
		// (DWORD) Time Zone Bias
		authinfo.add(0x00000000);
		// (DWORD) Locale ID
		authinfo.add(0x00000000);
		// (DWORD) Language ID
		authinfo.add(0x00000000);
		// (STRING) Country Abbreviation
		authinfo.addNTString(pubFuncs.getLocalSettingDefault(
				"Battle.net Login Plugin", "countryCode", "USA"));
		// (STRING) Country
		authinfo.addNTString(pubFuncs.getLocalSettingDefault(
				"Battle.net Login Plugin", "country", "United States"));
		
		return authinfo;
	}
}
