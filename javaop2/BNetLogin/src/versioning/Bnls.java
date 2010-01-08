/*
 * Created on Sep 17, 2009
 * 
 * By wjlafrance
 */
package versioning;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

import callback_interfaces.PublicExposedFunctions;
import exceptions.LoginException;
import util.BNLSPacket;
import util.TimeoutSocket;
import constants.PacketConstants;

/**
 * @author wjlafrance
 * 
 * Handles the BNLS connection
 */
public class Bnls {

	public static int VersionByte(PublicExposedFunctions pubFuncs, Game game) throws LoginException, IOException {
		String server = pubFuncs.getStaticExposedFunctionsHandle().getGlobalSetting("Battle.net Login Plugin", "BNLS Server");
		int timeout = Integer.parseInt(pubFuncs.getLocalSetting("Battle.net Login Plugin", "timeout"));
		
		Socket socket = TimeoutSocket.getSocket(server, 9367, timeout);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		BNLSPacket packet = new BNLSPacket(PacketConstants.BNLS_REQUESTVERSIONBYTE);
		packet.addDWord(BnlsProductId(game));
		out.write(packet.getBytes());
		out.flush();

		packet = ReadBnlsPacket(in);
		packet.removeDWord(); // Game ID
		return packet.removeDWord();
	}
	
	public static CheckRevisionResults CheckRevision(Game game, PublicExposedFunctions pubFuncs,
			String filename, long timestamp, byte[] formula)
		    throws LoginException, IOException
	{
		String server = pubFuncs.getStaticExposedFunctionsHandle().getGlobalSetting("Battle.net Login Plugin", "BNLS Server");
		int timeout = Integer.parseInt(pubFuncs.getLocalSetting("Battle.net Login Plugin", "timeout"));
		
		Socket socket = TimeoutSocket.getSocket(server, 9367, timeout);
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();

		BNLSPacket packet = new BNLSPacket(PacketConstants.BNLS_VERSIONCHECKEX2);
		packet.addDWord(BnlsProductId(game)); 	// (DWORD) Product ID
		packet.addDWord(0); 					// (DWORD) Flags**
		packet.addDWord(0); 					// (DWORD) Cookie
		packet.addLong(timestamp); 				// (ULONGLONG) Timestamp for version check archive
		packet.addNTString(filename); 			// (STRING) Version check archive filename.
		packet.addNtByteArray(formula); 		// (STRING) Checksum formula.
		out.write(packet.getBytes());
		out.flush();

		// TODO: Debugging
		System.out.println("-> BNLS CheckRevision: ");
		System.out.println(packet.toString());

		packet = ReadBnlsPacket(in);

		// TODO: Debugging
		System.out.println("<- BNLS CheckRevision: ");
		System.out.println(packet.toString());
		/*
		 * (BOOL)   Success*
		 * (DWORD)  Version.
		 * (DWORD)  Checksum.
		 * (STRING) Version check stat string.
		 * (DWORD)  Cookie.
		 * (DWORD)  The latest version code for this product.
		 */
		if(packet.removeDWord() == 0)
			throw new LoginException("[BNLS] Check revision failed.");

		int version = packet.removeDWord();
		int checksum = packet.removeDWord();
		byte[] statstring = packet.removeNtByteArray();
		
		return new CheckRevisionResults(version, checksum, statstring);
	}

	/**
	 * @param game
	 * @return BNLS product ID
	 */
	public static int BnlsProductId(Game g) throws LoginException {
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
	public static boolean IsEnabled(PublicExposedFunctions pubFuncs) {
		return pubFuncs.getStaticExposedFunctionsHandle()
			    .getGlobalSetting("Battle.net Login Plugin", "Enable BNLS")
			    .equalsIgnoreCase("true");
	}
	
	/**
	 * Returns a <i>BNLSPacket</i> representing the next incoming packet on <i>in</i>.
	 * This <b>will</b> block so only use it if you know BNLS will respond.
	 * 
	 * @param in InputStream to read from
	 * @return A BNLSPacket representing the next packet received
	 * @throws IOException A socket error
	 */
	private static BNLSPacket ReadBnlsPacket(InputStream in) throws IOException {
		int length = in.read() | (in.read() << 8);
		int id = in.read();
		byte[] data = new byte[length - 3];
		for(int i = 0; i < length - 3; i++) {
			data[i] = (byte) in.read();
		}
		return new BNLSPacket((byte)id, data);
	}

}
