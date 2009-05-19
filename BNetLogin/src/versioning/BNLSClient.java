/*
 * Created on Aug 20, 2004
 *
 * By iago
 */
package versioning;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import util.BNLSPacket;
import util.TimeoutSocket;
import callback_interfaces.PublicExposedFunctions;
import exceptions.InvalidVersion;


public class BNLSClient
{
    private String game;
    private String server;
    private String port;
    
    private PublicExposedFunctions out;
    
    public static final byte BNLS_VERSIONCHECK = 0x09;
    public static final byte BNLS_REQUESTVERSIONBYTE = 0x10;
    public static final byte BNLS_VERSIONCHECKEX2 = 0x1A;
    
    public BNLSClient(String server, String game, PublicExposedFunctions out)
    {
        this.game = game;
        this.out = out;
        
        String [] serverport = server.split(":", 2);
        if(serverport.length == 1)
        {
            this.server = server;
            this.port = "9367";
        }
        else if(serverport.length == 2)
        {
            this.server = serverport[0];
            this.port = serverport[1];
        }
        else
        {
            throw new IllegalArgumentException("BNLS Server must be in the form of <server> or <server>:<port>.");
        }
    }
    
    private Socket getConnection() throws IOException 
    {    	
    	int timeout = Integer.parseInt(out.getStaticExposedFunctionsHandle().getGlobalSettingDefault(null, "timeout", "5000"));
    	return TimeoutSocket.getSocket(server, Integer.parseInt(port), timeout);
    }
    
    public int getVersionByte() throws IOException
    {
    	Socket s = getConnection();
        InputStream in = s.getInputStream();
        OutputStream out = s.getOutputStream();
        
		BNLSPacket pkt = new BNLSPacket(BNLS_REQUESTVERSIONBYTE);
		pkt.addDWord(getBnlsProductId(game));
		out.write(pkt.getBytes());
		out.flush();
		
		BNLSPacket inPkt = getNextPacket(in);
		inPkt.removeDWord();				// Game ID
		return inPkt.removeDWord();
    }
	
    /**
     * This performs a CheckRevision and GetEXEInfo through BNLS's VERSIONCHECKEX2 packet.
     * This is generally inefficient and slow compared to local hashing, but now because of
     * lockdown, hashing locally is now impossible for some clients.
     * @param filename The filename of the version check MPQ given by BNET in SID_AUTH_INFO
     * @param formula The formula given by BNET in SID_AUTH_INFO
     * @param filetime The specified filetime for the MPQ archive.
     * @return An instance of CheckRevisionResults, containing the data BNLS returned.
     */
    public CheckRevisionResults getVersionCheck(String filename, byte[] formula, long filetime) throws IOException, InvalidVersion
    {
        Socket s = getConnection();
        InputStream in = s.getInputStream();
        OutputStream out = s.getOutputStream();
        
        int verhash, checksum;
        byte[] statstring;
		
    	BNLSPacket pkt = new BNLSPacket(BNLS_VERSIONCHECKEX2);
    	pkt.addDWord(getBnlsProductId(game));						// (DWORD) 		Product ID
    	pkt.addDWord(0);											// (DWORD) 		Flags**
    	pkt.addDWord(0);											// (DWORD) 		Cookie
    	pkt.addLong(filetime);										// (ULONGLONG) 	Timestamp for version check archive
    	pkt.addNTString(filename);									// (STRING)		Version check archive filename.
    	pkt.addNtByteArray(formula);								// (STRING)		Checksum formula.
    	out.write(pkt.getBytes());
    	out.flush();
    		
    	BNLSPacket inPkt = getNextPacket(in);
    	/*	(BOOL) 		Success*
    		(DWORD) 	Version.
    		(DWORD) 	Checksum.
    		(STRING) 	Version check stat string.
    		(DWORD) 	Cookie.
    		(DWORD) 	The latest version code for this product.*/
    	if(inPkt.removeDWord() == 0)
    	{
    		throw new InvalidVersion("BNLS returned failure in 0x1A.");
    	}
    	verhash = inPkt.removeDWord();
    	checksum = inPkt.removeDWord();
    	statstring = inPkt.removeNtByteArray();
		
		return new CheckRevisionResults(verhash, checksum, statstring);    	
    }
    
    /**
     * Returns a <i>BNLSPacket</i> representing the next incomming packet on <i>in</i>.
     * This <b>will</b> block so only use it if you <b>know</b> BNLS will respond.
     * @param in InputStream to read from
     * @return A BNLSPacket representing the next packet received
     * @throws IOException A socket error
     */
	private BNLSPacket getNextPacket(InputStream in) throws IOException
	{
		int length1 = in.read();
		int length2 = (byte) in.read();
		int length = length1 | (length2 << 8);
		byte[] data = new byte[length];
		data[0] = (byte)length1;
		data[1] = (byte)length2;
		for(int i = 2; i < length; i++)
		{
			data[i] = (byte) in.read();
		}
		return new BNLSPacket(data);
	}
	
	/**
	 * Gets the product ID for BNLS use
	 * @param productId 	BNCS C>S 0x50 (DWORD) Product ID (cast to string)
	 * @return				BNLS C>S 0x10 (DWORD) Product ID
	 */
	public static int getBnlsProductId(String productId)
	{
		if(productId.equalsIgnoreCase("STAR")) return 0x01;
		if(productId.equalsIgnoreCase("SEXP")) return 0x02;
		if(productId.equalsIgnoreCase("W2BN")) return 0x03;
		if(productId.equalsIgnoreCase("D2DV")) return 0x04;
		if(productId.equalsIgnoreCase("D2XP")) return 0x05;
		if(productId.equalsIgnoreCase("JSTR")) return 0x06;
		if(productId.equalsIgnoreCase("WAR3")) return 0x07;
		if(productId.equalsIgnoreCase("W3XP")) return 0x08;
		return 0;
	}
	
}