/*
 * Game.java
 *
 * Created on March 9, 2004, 9:03 AM
 */

package versioning;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import util.Buffer;
import util.PadString;
import util.RelativeFile;
import cdkey.CDKeyDecode;
import exceptions.InvalidCDKey;
import exceptions.InvalidVersion;

public class Game
{
    private static GameData gameData;
    
    private final String game;
    
    static
    {
        gameData = new GameData();
    }

    public Game(String game) throws InvalidVersion
    {
        game = game.toLowerCase();
        
        if(game.equals("starcraft") || game.equals("star"))
            this.game = "STAR";
        else if(game.equals("sexp") || game.equals("brood war") || game.equals("broodwar"))
            this.game = "SEXP";
        else if(game.equals("w2bn") || game.equals("war2") || game.equals("warcraft2") || game.equals("war 2") || game.equals("warcraft 2") || game.equals("warcraft ii") || game.equals("war ii") || game.equals("warcraft ii bne"))
            this.game = "W2BN";
        else if(game.equals("d2dv") || game.equals("diablo ii") || game.equals("diablo2") || game.equals("diablo 2"))
            this.game = "D2DV";
        else if(game.equals("d2xp") || game.equals("lod") || game.equals("diablo ii: lod"))
            this.game = "D2XP";
        else if(game.equals("war3") || game.equals("warcraft3") || game.equals("war 3") || game.equals("warcraft 3") || game.equals("warcraft iii") || game.equals("war iii"))
            this.game = "WAR3";
        else if(game.equals("w3xp") || game.equals("tft") || game.equals("war 3: tft") || game.equals("warcraft 3: tft") || game.equals("Warcraft III: TFT"))
            this.game = "W3XP";
        else
            throw new InvalidVersion("Game not found - " + game);
    }
    
    public int getVersionByte()
    {
        return gameData.getVersionByte(game);
    }
    
    public int getVersionHash()
    {
    	return gameData.getVersionHash(game);
    }
    
    public int getGameCode()
    {
        return (game.charAt(0)  << 24) |
            (game.charAt(1) << 16) |
            (game.charAt(2) << 8) |
            (game.charAt(3) << 0);
    }
    
    public String getGame()
    {
    	return game;
    }
    
    public String getName()
    {
        return game;
    }
    
    public String getExeInfo()
    {
        //return "starcraft.exe 03/28/03 04:21:56 1064960";

        RelativeFile f = new RelativeFile(gameData.getFiles(game)[0]);
        
        // Set up a calendar to point at the last modified time
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(f.lastModified()));
        
        StringBuffer exeInfo = new StringBuffer();
        
        // Write to the exeInfo buffer
        exeInfo.append(f.getName()).append(" ");
        
        // date
        exeInfo.append(PadString.padNumber(c.get(Calendar.MONTH), 2)).append("/");
        exeInfo.append(PadString.padNumber(c.get(Calendar.DAY_OF_MONTH), 2)).append("/");
        exeInfo.append(PadString.padNumber((c.get(Calendar.YEAR) % 100), 2)).append(" ");
        
        // time
        exeInfo.append(PadString.padNumber(c.get(Calendar.HOUR_OF_DAY), 2)).append(":");
        exeInfo.append(PadString.padNumber(c.get(Calendar.MINUTE), 2)).append(":");
        exeInfo.append(PadString.padNumber(c.get(Calendar.SECOND), 2)).append(" ");
        
        // size
        exeInfo.append(f.length());
        
        return exeInfo.toString();
    }
    
    public int checkRevision(String valueString, String mpqFile) throws InvalidVersion
    {
        int num = mpqFile.charAt(mpqFile.indexOf('.') - 1) - '0';

        String[] files = gameData.getFiles(game);

        try
        {
            return CheckRevision.checkRevision(valueString, files, num);
        }
        catch(InvalidVersion iv)
        {
        	throw iv;
        }
        catch(IOException e)
        {
            StringBuffer error = new StringBuffer();
            error.append("Error: you are missing files necessary to connect.\n");
            error.append("The error message is: " + e + "\n");
            error.append("Please ensure that you have the latest version of the appropriate files:\n");
            for(int i = 0; i < files.length; i++)
            {
                RelativeFile thisFile = new RelativeFile(files[i]);
                thisFile.getParentFile().mkdirs();
                error.append(thisFile.getAbsolutePath() + "\n");
            }

            throw new InvalidVersion(error.toString());
        }
    }
    
    /** Gets a Vector of all possible games.
     * @return A Vector of all possible games.
     */    
    public static Vector getGames()
    {
        Vector v = new Vector();
        v.add("Starcraft");
        v.add("Brood War");
        v.add("Warcraft II");
        v.add("Diablo II");
        v.add("Diablo II: LoD");
        v.add("Warcraft III");
        v.add("Warcraft III: TFT");
        
        return v;
    }
    
    /** Gets the number of keys, usingspawn, and key hash for the cdkey.
     * @param out Required for access to temporary variables, etc.
     * @throws InvalidCDKey If there is an error with their cdkey.
     * @return The 160-bit hash put into a Buffer, as well as the spawn byte,
     * the number of keys, etc.
     */    
    public Buffer getKeyBuffer(String cdkey1, String cdkey2, int clientToken, int serverToken) throws InvalidCDKey
    {
	    Buffer ret = new Buffer();
	    
	    //        (DWORD)          Number of keys in this packet
	    if(gameData.hasTwoKeys(game))
	        ret.addDWord(2);
	    else
	        ret.addDWord(1);
	    
	    //        (BOOLEAN)        Using Spawn (32-bit)
	    ret.addDWord(0);
	
	    ret.add(getKeyHash(cdkey1, clientToken, serverToken));
	    if(gameData.hasTwoKeys(game))
        {
            try
            {
                ret.add(getKeyHash(cdkey2, clientToken, serverToken));
            }
            catch(InvalidCDKey e)
            {
                throw new InvalidCDKey(e.getMessage() + " [second cdkey]");
            }
        }
	    
	    return ret;
    }
    
    private Buffer getKeyHash(String cdkey, int clientToken, int serverToken) throws InvalidCDKey
    {
        //CDKeyDecode key = out.getGame().getKeyDecoder(cdkey);
        CDKeyDecode key = CDKeyDecode.getInstance(cdkey);
    
        Buffer ret = new Buffer();
//
//        For Each Key:
//        (DWORD)          Key Length
        ret.addDWord(cdkey.length());
//        (DWORD)          Product
        ret.addDWord(key.getProduct());
//        (DWORD)          CDKEY Value 1
        ret.addDWord(key.getVal1());
//        (DWORD)          Unknown (0)
        ret.addDWord(0);
//        (DWORD[5])       Hashed Key Data
        
        int []hashedKey = key.getKeyHash(clientToken, serverToken);
        for(int i = 0; i < 5; i++)
            ret.addDWord(hashedKey[i]);
                
        return ret;
    }
    
    public String toString()
    {
        return game + " (" + getExeInfo() + ", Version Hash = 0x" + Integer.toHexString(gameData.getVersionHash(game)) + ", Version Byte = 0x" + Integer.toHexString(gameData.getVersionByte(game)) + ")";
    }
}
