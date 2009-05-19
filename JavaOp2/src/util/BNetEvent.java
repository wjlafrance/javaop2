/*
 * Created on Dec 11, 2004
 * By iago
 */
package util;

/**
 * @author iago
 *
 */
public class BNetEvent
{
    final private int code;
    final private String username;
    final private String message;
    final private int ping;
    final private int flags;
    
    public BNetEvent(int code, String username, String message, int ping, int flags)
    {
        this.code = code;
        this.username = username;
        this.message = message;
        this.ping = ping;
        this.flags = flags;
    }
    
    public BNetEvent(BNetEvent source)
    {
        this.code = source.code;
        this.username = source.username;
        this.message = source.message;
        this.ping = source.ping;
        this.flags = source.flags;
    }
    
    public BNetEvent(Buffer data)
    {
        code = data.removeDWord();			//        (DWORD)		 Event ID
        flags = data.removeDWord();			//        (DWORD)		 User's Flags
        ping = data.removeDWord();			//        (DWORD)		 Ping
        data.removeDWord();					//        (DWORD)		 IP Address (Defunct)
        data.removeDWord();					//        (DWORD)		 Account number (Defunct)
        data.removeDWord();					//        (DWORD)		 Registration Authority (Defunct)
        username = data.removeNTString();	//        (STRING) 	 Username
        message = data.removeNTString();	//        (STRING) 	 Text
    }
    
    public int getCode()
    {
        return code;
    }
    public String getUsername()
    {
        return username;
    }
    public String getMessage()
    {
        return message;
    }
    public int getPing()
    {
        return ping;
    }
    public int getFlags()
    {
        return flags;
    }
}
