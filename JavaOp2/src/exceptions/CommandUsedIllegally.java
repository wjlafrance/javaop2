/*
 * Created on Jan 3, 2005
 * By iago
 */
package exceptions;

/**
 * @author iago
 *
 */
public class CommandUsedIllegally extends Exception
{
	private static final long serialVersionUID = 1L;
	
    private String user;
    private String command;
    private String userFlags;
    private String requiredFlags;
    
    public CommandUsedIllegally(String message, String user, String command, String userFlags, String requiredFlags)
    {
        super(message);
        
        this.user = user;
        this.command = command;
        this.userFlags = userFlags;
        this.requiredFlags = requiredFlags;
    }
    
    public String getUser()
    {
        return user;
    }
    public String getCommand()
    {
        return command;
    }
    public String getUserFlags()
    {
        return userFlags;
    }
    public String getRequiredFlags()
    {
        return requiredFlags;
    }
    
    public String toString()
    {
        return "User " + user + " tried to use command " + command + " illegally: it requires " + requiredFlags + " and he has " + userFlags + " -- " + getMessage();
    }
}
