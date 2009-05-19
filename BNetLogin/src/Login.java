import java.io.IOException;
import java.util.Date;

import password.BrokenSHA1;
import password.DoubleHash;
import password.SRP;
import util.BNetPacket;
import util.BigIntegerEx;
import util.Buffer;
import util.FileTime;
import versioning.BNLSWrapper;
import versioning.CheckRevisionResults;
import callback_interfaces.PublicExposedFunctions;
import constants.ErrorLevelConstants;
import constants.PacketConstants;
import exceptions.InvalidCDKey;
import exceptions.InvalidPassword;
import exceptions.InvalidVersion;
import exceptions.PluginException;

/*
 * Created on Dec 9, 2004
 * By iago
 */

/**
 * @author iago
 *
 */
public class Login
{
    private final BNLSWrapper bnls;
    
    private int logonType;
    private int clientToken;
    private int serverToken;
    private SRP srp;
    private SRP accountChangeSrp;
    private String prefSection;
    
    private byte []salt;
    private byte []B;
    
    public Login(BNLSWrapper bnls, String prefSection)
    {
    	this.bnls = bnls;
        this.prefSection = prefSection;
    }
    
    /** Returns a single byte array, 0x01.  This is the initial packet sned on login
     */
    public Buffer getProtocolByte()
    {
        return new Buffer(new byte[] { 0x01 });
    }
    
    /** This is the initial packet sent out, 0x50
     */
    public BNetPacket getAuthInfo(String countryCode, String country, PublicExposedFunctions out) throws PluginException
    {
    	out.putLocalVariable("game", bnls.getGame());
		BNetPacket authinfo = new BNetPacket(PacketConstants.SID_AUTH_INFO);
		authinfo.add(0x00000000); // protocol id
        
		//authinfo.add(0x49583836); // platform id = IX86
        authinfo.add( 'I' << 24 | 'X' << 16 | '8' <<  8 | '6' <<  0);
		authinfo.add(bnls.getGameCode());
		authinfo.add(bnls.getVersionByte());
        // These can just be nulled-out
        authinfo.add(0x00000000); // product language
        authinfo.add(0x00000000); // Local ip
        authinfo.add(0x00000000); // Timezone bias
        authinfo.add(0x00000000); // Locale Id
        authinfo.add(0x00000000); // Language Id

        authinfo.addNTString(countryCode);
        authinfo.addNTString(country);
        return authinfo;
    }
    
    /** This does the version and cdkey checks, putting them into a single packet (0x51).  If the server is Warcraft III, 
     * it will also attempt to verify the authenticity of the server.  
     */
    public BNetPacket getAuthCheck(BNetPacket authInfoPacket, PublicExposedFunctions out) throws PluginException
    {
        // Read the variables out of the package
		logonType 				= authInfoPacket.removeDWord();					// (DWORD)		Logon Type
		serverToken 			= authInfoPacket.removeDWord();					// (DWORD)		Server Token
		int udpValue 			= authInfoPacket.removeDWord();					// (DWORD)		UDPValue**
		long mpqTime 			= authInfoPacket.removeLong();					// (FILETIME)	MPQ filetime
		String mpqFilename 		= authInfoPacket.removeNTString();				// (STRING)		IX86ver filename
		byte []formula			= authInfoPacket.removeNtByteArray();			// (STRING)		Version check formula
		
		byte []signature = null;
		if(authInfoPacket.size() > 4)
		    signature = authInfoPacket.removeBytes(128);						// (VOID)		 128-byte Server signature
		
		String cdkey1 = out.getLocalSetting(prefSection, "cdkey");
		String cdkey2 = out.getLocalSetting(prefSection, "cdkey2");
		
		if(cdkey1 == null)
		    throw new InvalidCDKey("No CDKey was specified in the configuration");
		
		
		// Display the variables if we're debugging, and because it looks cool
		out.systemMessage(ErrorLevelConstants.DEBUG, "Logon type: " 				+ Integer.toHexString(logonType));
		out.systemMessage(ErrorLevelConstants.DEBUG, "Server token: " 				+ Integer.toHexString(serverToken));
		out.systemMessage(ErrorLevelConstants.DEBUG, "UDP Value: " 					+ udpValue);
		out.systemMessage(ErrorLevelConstants.DEBUG, "MPQ Time: " 					+ new Date(FileTime.fileTimeToMillis(mpqTime)));
		out.systemMessage(ErrorLevelConstants.DEBUG, "Version Check MPQ: " 			+ mpqFilename);
		//out.systemMessage(ErrorLevelConstants.DEBUG, "Version Check Formula: " 	+ formula);
		out.systemMessage(ErrorLevelConstants.DEBUG, "Server signature is " 		+ (signature == null ? "not " : "") + "present.");
        
        if(logonType == 2 && signature == null)
        {
            if(out.getLocalSettingDefault(prefSection, "Verify server", "true").equalsIgnoreCase("true"))
                throw new PluginException("NLS Server failed to send authentication (this is most likely NOT a Blizzard server!); not connecting.  To connect anyway, change the setting \"Verify server\" to false.");
            else
            	out.systemMessage(ErrorLevelConstants.WARNING, "NLS Server failed to send authentication.  The connection will continue; however, this server probably doesn't belong to Blizzard.");
        }

        
        // Check the server's signature before proceeding
        if(signature != null)
        {
            out.systemMessage(ErrorLevelConstants.INFO, "Checking server's signature...");

            try
            {
                checkServerSignature(signature, (byte[]) out.getLocalVariable("address"));
                out.systemMessage(ErrorLevelConstants.INFO, "Server successfully authenticated -- it's Blizzard's");
            }
            catch(IOException e)
            {
                if(out.getLocalSettingDefault(prefSection, "Verify server", "true").equalsIgnoreCase("true"))
                    throw new PluginException("NLS Server failed authentication (this is most likely NOT a Blizzard server!); not connecting.  To connect anyway, change the setting \"Verify server\" to false.");
                else
                	// They don't care if it's vaild, so just print a warning
                	out.systemMessage(ErrorLevelConstants.WARNING, "The server's authentication failed.  This probably isn't a real Battle.net server!");
            }
            
        }
        
		// Generate the client token, which is a random value
		clientToken = ((int) (Math.random() * 0x7FFFFFFF) ^ (int) (Math.random() * 0x7FFFFFFF) ^ (int) (Math.random() * 0x7FFFFFFF)); 				// (DWORD)		 Client Token

		//		 Encrypt the cdkey
		byte []cdkey    = bnls.getCDKey(clientToken, serverToken, cdkey1, cdkey2);
		
		//		Use BNLS to do the checkrevision
		CheckRevisionResults crev = bnls.getVersionCheck(mpqFilename, formula, mpqTime);
		
		// CD-Key Owner name
		String exeOwner = out.getLocalSettingDefault(prefSection, "username", "<USERNAME>");																// (STRING) 	 CD Key owner name
		
        out.systemMessage(ErrorLevelConstants.DEBUG, "Client token: 0x" 			+ Integer.toHexString(clientToken)); 
        out.systemMessage(ErrorLevelConstants.DEBUG, "Version Hash: 0x" 			+ Integer.toHexString(crev.getVerhash()));
        out.systemMessage(ErrorLevelConstants.DEBUG, "Checksum: 0x" 				+ Integer.toHexString(crev.getChecksum())); 
        //out.systemMessage(ErrorLevelConstants.DEBUG, "Version Check Stat String: " 	+ crev.getStatstring()); 
        out.systemMessage(ErrorLevelConstants.DEBUG, "CDKey owner: " 				+ exeOwner); 
		
		
		// Build the SID_AUTH_CHECK packet
		BNetPacket authCheck = new BNetPacket(PacketConstants.SID_AUTH_CHECK);
		authCheck.addDWord(clientToken);
		authCheck.addDWord(crev.getVerhash());
		authCheck.addDWord(crev.getChecksum());
		authCheck.add(cdkey);
		authCheck.addNtByteArray(crev.getStatstring());
		authCheck.addNTString(exeOwner);
		
        return authCheck;
    }
    
    public BNetPacket getLogin(BNetPacket authCheckPacket, PublicExposedFunctions out) throws PluginException 
    {
        int status = authCheckPacket.removeDWord();
        
        switch(status)
        {
            case 0x000: break;
            case 0x100: throw new InvalidVersion("Old game version (it wants you to download " + authCheckPacket.removeNTString() + ")");
            case 0x101: throw new InvalidVersion("Invalid game version");
            case 0x200: throw new InvalidCDKey("Invalid CD Key");
            case 0x210: throw new InvalidCDKey("Invalid expansion CD Key");
            case 0x201: throw new InvalidCDKey("CD key in use by " + authCheckPacket.removeNTString());
            case 0x211: throw new InvalidCDKey("Expansion CD key in use by " + authCheckPacket.removeNTString());
            case 0x202: throw new InvalidCDKey("Your CD Key is banned");
            case 0x212: throw new InvalidCDKey("Your expansion CD Key is banned");
            case 0x203: throw new InvalidCDKey("Wrong product value for your CD Key");
            case 0x213: throw new InvalidCDKey("Wrong product value for your expansion CD Key");
            default:    throw new InvalidVersion("Unknown status code: 0x" + Integer.toHexString(status) + " (bad cdkey or version probably)");
        }
        
        if(out.getLocalSettingDefault(prefSection, "password change", "").length() > 0)
        {
            out.systemMessage(ErrorLevelConstants.NOTICE, "Changing password...");
            return getChangePassword(out);
        }

        return doPassword(out);
    }
    
    private BNetPacket doPassword(PublicExposedFunctions out) throws PluginException 
    {
        String username = out.getLocalSettingDefault(prefSection, "username", "<USERNAME>");
        String password = out.getLocalSettingDefault(prefSection, "password", "<PASSWORD>").toLowerCase();
        
        if(logonType == 0)
        {
            BNetPacket response = new BNetPacket(PacketConstants.SID_LOGONRESPONSE2);

            response.addDWord(clientToken);
            response.addDWord(serverToken);
            
            int[] myPass = DoubleHash.doubleHash(password, clientToken, serverToken);
            
            for(int i = 0; i < 5; i++)
                response.addDWord(myPass[i]);
            
            response.addNTString(username);
            
            return response;
        }
        else if(logonType == 1 || logonType == 2)
        {
            srp = new SRP(username, password); 
            BNetPacket accountLogon = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTLOGON);
            
            accountLogon.add(srp.get_A());
            accountLogon.addNTString(username);

            return accountLogon;
        }

        throw new PluginException("I don't know how to logon for logon type " + logonType + ".  Sorry.");
    }
    

    /****************************
     * Legacy (non-nls) functions
     */
    public boolean checkLogonResponse(PublicExposedFunctions out, BNetPacket logonResponse) throws PluginException, IOException, InvalidPassword
    {
        int response = logonResponse.removeDWord();
        
        switch(response)
        {
            case 0:
                return true;
                
            case 1:
                out.systemMessage(ErrorLevelConstants.WARNING, "Account doesn't exist, attempting to create");
                
                int []passwordHash = BrokenSHA1.calcHashBuffer(out.getLocalSettingDefault(prefSection, "password", "<INSERT PASSWORD>").toLowerCase().getBytes());
                String username = out.getLocalSettingDefault(prefSection, "username", "<INSERT USERNAME>");
                
                BNetPacket createAccount = new BNetPacket(PacketConstants.SID_CREATEACCOUNT2);
                for(int i = 0; i < 5; i++)
                    createAccount.addDWord(passwordHash[i]);
                createAccount.addNTString(username);
                
                out.sendPacket(createAccount);
                
                return false;

            case 2:
                throw new InvalidPassword("Login failed -- invalid password.");
                
            default:
                throw new InvalidPassword("Login failed with unknown error code --> " + response);
        }
    }
    
    public BNetPacket checkCreateAccount(PublicExposedFunctions out, BNetPacket createAccount) throws InvalidPassword, PluginException
    {
        int status = createAccount.removeDWord();
        
        switch(status)
        {
            case 0: // Account created
                return doPassword(out);
            case 2: 
                throw new InvalidPassword("Name contained invalid characters");
            case 3: 
                throw new InvalidPassword("Name contained a banned word");
            case 4: 
                throw new InvalidPassword("Account already exists");
            case 6: 
                throw new InvalidPassword("Name did not contain enough alphanumeric characters");
            default:
                throw new InvalidPassword("Unknown error creating account: " + status);
            
        }
    }
    
    public BNetPacket getChangePassword(PublicExposedFunctions out) throws PluginException
    {
        String username = out.getLocalSettingDefault(prefSection, "username", "<USERNAME>");
        String password = out.getLocalSettingDefault(prefSection, "password", "<PASSWORD>").toLowerCase();

        if(logonType == 0)
        {
            BNetPacket changePassword = new BNetPacket(PacketConstants.SID_CHANGEPASSWORD);
            
            changePassword.add(clientToken);
            changePassword.add(serverToken);
            
            int []oldPassword = DoubleHash.doubleHash(password, clientToken, serverToken);
    //        int []oldPassword = BrokenSHA1.calcHashBuffer(out.getLocalSetting(prefSection, "password").getBytes());
            for(int i = 0; i < 5; i++)
                changePassword.add(oldPassword[i]);
            
            int []newPassword = BrokenSHA1.calcHashBuffer(out.getLocalSetting(prefSection, "password change").toLowerCase().getBytes());
            for(int i = 0; i < 5; i++)
                changePassword.add(newPassword[i]);
            
            changePassword.addNTString(username);
            
            return changePassword;
        }
        else if(logonType == 1 || logonType == 2)
        {
            BNetPacket changePassword = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCHANGE);
            srp = new SRP(username, password);
            
            changePassword.add(srp.get_A());
            changePassword.addNTString(username);
            
            return changePassword;
        }

        throw new PluginException("Unknown logon type: " + logonType); 
    }
    
    public BNetPacket checkPasswordChange(PublicExposedFunctions out, BNetPacket passwordChangePacket) throws InvalidPassword, PluginException
    {
        int status = passwordChangePacket.removeDWord();
        
        if(status == 0)
            throw new InvalidPassword("Password change failed");
        
        out.systemMessage(ErrorLevelConstants.DEBUG, "Password change successful");
        // Switch the new password to the current password
        out.putLocalSetting(prefSection, "password", out.getLocalSetting(prefSection, "password change"));
        out.putLocalSetting(prefSection, "password change", "");
        
        
        return doPassword(out);
    }
    
    
    /***************
     * NLS Functions
     */
    public BNetPacket getLogonProof(PublicExposedFunctions out, BNetPacket authCheckPacket) throws PluginException, InvalidPassword 
    {
        int status = authCheckPacket.removeDWord();
        
        if(srp == null)
            throw new RuntimeException("SID_AUTH_ACCOUNTLOGIN was received without SRP being set up -- this shouldn't happen.");
        
        switch(status)
        {
        case 0:
            salt = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);
            B = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);

            
	        BNetPacket accountLogonProof = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTLOGONPROOF);
	        accountLogonProof.add(srp.getM1(salt, B));
	        
	        return (accountLogonProof);

	    case 1:
            
	        BNetPacket accountCreate = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCREATE);
	        
	        // The salt is just a random value
	        salt = srp.get_A();
	        
//	        0x52 - SID_AUTH_ACCOUNTCREATE - 
//	                (DWORD[8]) s
//	                (DWORD[8]) v
//	                (STRING) Username  
	        accountCreate.add(salt);
	        accountCreate.add(srp.get_v(salt).toByteArray());
	        accountCreate.addNTString(out.getLocalSettingDefault(prefSection, "username", "<INSERT USERNAME")); 
	        
	        return accountCreate;
	        
	    case 5:
	        throw new InvalidPassword("Account needs to be upgraded.");
	        
	    default:
            throw new InvalidPassword("Login failed with unknown error code: " + status);
        }
    }
    
    public void checkLogonProof(BNetPacket buf) throws PluginException, InvalidPassword 
    {
    	int status = buf.removeDWord();

    	switch(status)
    	{
    	case 0:
    	case 0xe:
            
            byte []serverProof = buf.removeBytes(SRP.SHA_DIGESTSIZE);
            byte []M2 = srp.getM2(salt, B);
            
            for(int i = 0; i < serverProof.length; i++)
            {
                if(serverProof[i] != M2[i])
                    throw new PluginException("Server failed to provide proof that it knows your password!");
            }
            
            break;
            
        case 2:
            throw new InvalidPassword("Login failed: incorrect password.");
            
        default:
    		throw new InvalidPassword("Login failed with unknown error: " + status);
    	}
    }
    
    public BNetPacket checkAuthCreateAccount(PublicExposedFunctions out, BNetPacket buf) throws PluginException, InvalidPassword
    {
        int status = buf.removeDWord();
        
        switch(status)
        {
            case 0x00:
                return doPassword(out);
                
			case 0x06: throw new InvalidPassword("NLS Create Account failed: Name is in use");
			case 0x07: throw new InvalidPassword("NLS Create Account failed: Name is not long enough");
			case 0x08: throw new InvalidPassword("NLS Create Account failed: Name contains bad characters");
			case 0x09: throw new InvalidPassword("NLS Create Account failed: Name contains bad words");
			case 0x0A: throw new InvalidPassword("NLS Create Account failed: Name needs more alphanumeric characters");
			case 0x0B: throw new InvalidPassword("NLS Create Account failed: Name cannot have adjacent puncuation");
			case 0x0C: throw new InvalidPassword("NLS Create Account failed: Name has too much puncuation");
            default  : throw new InvalidPassword("NLS Create Account failed with an unknown error: " + status);
        }
    }
    
    // I don't really like this function being here, but I can't think of anywhere else it might belong :-/
    private void checkServerSignature(byte []sig, byte []ip) throws IOException
    {
        // The constants
        BigIntegerEx key = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, new byte[] { 0x01, 0x00, 0x01, 0x00 });
        BigIntegerEx mod = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, new byte[] 
        { 
                (byte) 0xD5, (byte) 0xA3, (byte) 0xD6, (byte) 0xAB, (byte) 0x0F, (byte) 0x0D, (byte) 0xC5, (byte) 0x0F, (byte) 0xC3, (byte) 0xFA, (byte) 0x6E, (byte) 0x78, (byte) 0x9D, (byte) 0x0B, (byte) 0xE3, (byte) 0x32,
                (byte) 0xB0, (byte) 0xFA, (byte) 0x20, (byte) 0xE8, (byte) 0x42, (byte) 0x19, (byte) 0xB4, (byte) 0xA1, (byte) 0x3A, (byte) 0x3B, (byte) 0xCD, (byte) 0x0E, (byte) 0x8F, (byte) 0xB5, (byte) 0x56, (byte) 0xB5,
                (byte) 0xDC, (byte) 0xE5, (byte) 0xC1, (byte) 0xFC, (byte) 0x2D, (byte) 0xBA, (byte) 0x56, (byte) 0x35, (byte) 0x29, (byte) 0x0F, (byte) 0x48, (byte) 0x0B, (byte) 0x15, (byte) 0x5A, (byte) 0x39, (byte) 0xFC,
                (byte) 0x88, (byte) 0x07, (byte) 0x43, (byte) 0x9E, (byte) 0xCB, (byte) 0xF3, (byte) 0xB8, (byte) 0x73, (byte) 0xC9, (byte) 0xE1, (byte) 0x77, (byte) 0xD5, (byte) 0xA1, (byte) 0x06, (byte) 0xA6, (byte) 0x20,
                (byte) 0xD0, (byte) 0x82, (byte) 0xC5, (byte) 0x2D, (byte) 0x4D, (byte) 0xD3, (byte) 0x25, (byte) 0xF4, (byte) 0xFD, (byte) 0x26, (byte) 0xFC, (byte) 0xE4, (byte) 0xC2, (byte) 0x00, (byte) 0xDD, (byte) 0x98,
                (byte) 0x2A, (byte) 0xF4, (byte) 0x3D, (byte) 0x5E, (byte) 0x08, (byte) 0x8A, (byte) 0xD3, (byte) 0x20, (byte) 0x41, (byte) 0x84, (byte) 0x32, (byte) 0x69, (byte) 0x8E, (byte) 0x8A, (byte) 0x34, (byte) 0x76,
                (byte) 0xEA, (byte) 0x16, (byte) 0x8E, (byte) 0x66, (byte) 0x40, (byte) 0xD9, (byte) 0x32, (byte) 0xB0, (byte) 0x2D, (byte) 0xF5, (byte) 0xBD, (byte) 0xE7, (byte) 0x57, (byte) 0x51, (byte) 0x78, (byte) 0x96,
                (byte) 0xC2, (byte) 0xED, (byte) 0x40, (byte) 0x41, (byte) 0xCC, (byte) 0x54, (byte) 0x9D, (byte) 0xFD, (byte) 0xB6, (byte) 0x8D, (byte) 0xC2, (byte) 0xBA, (byte) 0x7F, (byte) 0x69, (byte) 0x8D, (byte) 0xCF
        });
        
        // Do the calculation
        byte []result = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, sig).modPow(key, mod).toByteArray();
        
        // Create the array of the correct result
        byte []correctResult = new byte[result.length];
        // Put the ip into the array
        correctResult[0] = ip[0];
        correctResult[1] = ip[1];
        correctResult[2] = ip[2];
        correctResult[3] = ip[3];

        // Pad the result with 0xBB's
        for(int i = 4; i < correctResult.length; i++)
            correctResult[i] = (byte) 0xBB;
        
        for(int i = 0; i < result.length; i++)
            if(result[i] != correctResult[i])
                throw new IOException("Error! Server failed validation check!");
    }
    
    public BNetPacket authCheckAccountChange(PublicExposedFunctions out, BNetPacket buf) throws InvalidPassword
    {
        int status = buf.removeDWord();
        
        if(status == 1)
            throw new InvalidPassword("Account doesn't exist -- create before trying to change password");
        else if(status == 5)
            throw new InvalidPassword("Account needs to be upgraded");
        else if(status != 0)
            throw new InvalidPassword("Unknown NLS Change Password error code: " + status);
        
        accountChangeSrp = new SRP(out.getLocalSetting(prefSection, "username"), out.getLocalSetting(prefSection, "password change"));
        //accountChangeSrp = srp;
        
        BNetPacket proof = new BNetPacket(PacketConstants.SID_AUTH_ACCOUNTCHANGEPROOF);
        
        salt = buf.removeBytes(SRP.BIGINT_SIZE);
        B = buf.removeBytes(SRP.BIGINT_SIZE);
        
        proof.add(srp.getM1(salt, B));
        proof.add(salt);
        proof.add(accountChangeSrp.get_v(salt).toByteArray());
        
        return proof;
    }
    
    public BNetPacket authCheckAccountChangeProof(PublicExposedFunctions out, BNetPacket buf) throws InvalidPassword, PluginException
    {
        int status = buf.removeDWord();
        
        if(status == 2)
            throw new InvalidPassword("Account change failed: invalid old password");
        else if(status != 0)
            throw new InvalidPassword("Account change failed: unknown error: " + status);
        
        byte []recvM2 = buf.removeBytes(SRP.SHA_DIGESTSIZE);
        byte []realM2 = srp.getM2(salt, B);
        
        for(int i = 0; i < realM2.length; i++)
            if(recvM2[i] != realM2[i])
                throw new PluginException("Server failed to provide proof that it knows your password!");
        
        out.systemMessage(ErrorLevelConstants.DEBUG, "Password successfully changed!");
        
        // Switch the new password to the current password
        out.putLocalSetting(prefSection, "password", out.getLocalSetting(prefSection, "password change"));
        out.putLocalSetting(prefSection, "password change", "");

        srp = null;
        accountChangeSrp = null;
        
        
        return doPassword(out);
    }
    

    
    /*******************
     * Generic functions
     */
    
    public BNetPacket getEnterChat(PublicExposedFunctions out)
    {
        BNetPacket enterChat = new BNetPacket(PacketConstants.SID_ENTERCHAT);
        enterChat.addNTString(out.getLocalSettingDefault(prefSection, "username", "not.iago.x86"));
        enterChat.addNTString("");

        return enterChat;
    }
    
    public BNetPacket getJoinHomeChannel(PublicExposedFunctions out)
    {
        BNetPacket enterChannel = new BNetPacket();
        enterChannel.setCode(PacketConstants.SID_JOINCHANNEL);
        enterChannel.addDWord(0x02);
        enterChannel.addNTString(out.getLocalSettingDefault(prefSection, "home channel", "op x86-test"));
        
        return enterChannel;
    }   

}
