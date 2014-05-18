/*
 * Created on Sep 17, 2009
 *
 * By wjlafrance
 */
package com.javaop.BNetLogin.versioning;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

import com.javaop.bot.BotCoreStatic;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.LoginException;
import com.javaop.util.BnlsPacket;
import com.javaop.util.TimeoutSocket;
import com.javaop.constants.PacketConstants;

/**
 * @author wjlafrance
 *
 * Handles the BNLS connection
 */
public class Bnls {

	public static int versionByte(Game game)
			throws LoginException, IOException
	{
		String server = BotCoreStatic.getInstance().getGlobalSettingDefault("Battle.net Login Plugin",
				"BNLS Server", "wjlafrance.net");
		int timeout = Integer.parseInt(BotCoreStatic.getInstance().getGlobalSettingDefault(
				"JavaOp2", "timeout", "30000"));

		Socket socket = TimeoutSocket.getSocket(server, 9367, timeout);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		BnlsPacket packet = new BnlsPacket(PacketConstants
				.BNLS_REQUESTVERSIONBYTE);
		packet.addDWord(BnlsProductId(game));
		out.write(packet.getBytes());
		out.flush();

		packet = ReadBnlsPacket(in);
		packet.removeDWord(); // Game ID
		return packet.removeDWord();
	}

	public static CheckRevisionResults CheckRevision(Game game, String filename, long timestamp,
			byte[] formula) throws LoginException, IOException
	{
		String server = BotCoreStatic.getInstance().getGlobalSettingDefault("Battle.net Login Plugin",
				"BNLS Server", "wjlafrance.net");
		int timeout = Integer.parseInt(BotCoreStatic.getInstance().getGlobalSettingDefault(
				"JavaOp2", "timeout", "30000"));

		Socket socket = TimeoutSocket.getSocket(server, 9367, timeout);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		BnlsPacket packet = new BnlsPacket(PacketConstants
				.BNLS_VERSIONCHECKEX2);
		packet.addDWord(BnlsProductId(game));   // (DWORD) Product ID
		packet.addDWord(0);                     // (DWORD) Flags
		packet.addDWord(0);                     // (DWORD) Cookie
		packet.addLong(timestamp);              // (ULONGLONG) MPQ Timestamp
		packet.addNTString(filename);           // (STRING) MPQ filename
		packet.addNtByteArray(formula);         // (STRING) Checksum formula
		out.write(packet.getBytes());
		out.flush();

		packet = ReadBnlsPacket(in);
		if(packet.removeDWord() == 0)           // (BOOL) Success
			throw new LoginException("[BNLS] Check revision failed.");

		int version = packet.removeDWord();     // (DWORD) Verhash
		int checksum = packet.removeDWord();    // (DWORD) Checksum
		byte[] statstring = packet.removeNtByteArray(); // Statstring
												// (DWORD) Cookie
												// (DWORD) Latest version code

		return new CheckRevisionResults(version, checksum, statstring);
	}

	/**
	 * @param game
	 * @return BNLS product ID
	 */
	private static int BnlsProductId(Game g) throws LoginException {
		if(g.getName().equalsIgnoreCase("STAR")) return 0x01;
		if(g.getName().equalsIgnoreCase("SEXP")) return 0x02;
		if(g.getName().equalsIgnoreCase("W2BN")) return 0x03;
		if(g.getName().equalsIgnoreCase("D2DV")) return 0x04;
		if(g.getName().equalsIgnoreCase("D2XP")) return 0x05;
		if(g.getName().equalsIgnoreCase("JSTR")) return 0x06;
		if(g.getName().equalsIgnoreCase("WAR3")) return 0x07;
		if(g.getName().equalsIgnoreCase("W3XP")) return 0x08;

		throw new LoginException("[BNLS] Invalid product: " + g.getName());
	}

	/**
	 * Checks to see if BNLS is enabled in BNetLogin's global configuration
	 */
	public static boolean isEnabled(StaticExposedFunctions staticFuncs) {
		return staticFuncs.getGlobalSetting("Battle.net Login Plugin",
				"Enable BNLS").equalsIgnoreCase("true");
	}

	/**
	 * Returns a <i>BNLSPacket</i> representing the next incoming packet on
	 * <i>in</i>. This <b>will</b> block so only use it if you know BNLS will
	 * respond.
	 *
	 * @param in InputStream to read from
	 * @return A BNLSPacket representing the next packet received
	 * @throws IOException A socket error
	 */
	private static BnlsPacket ReadBnlsPacket(InputStream in) throws IOException
	{
		int length = in.read() | (in.read() << 8);
		int id = in.read();
		byte[] data = new byte[length - 3];
		for(int i = 0; i < length - 3; i++) {
			data[i] = (byte) in.read();
		}
		return new BnlsPacket((byte)id, data);
	}

}
