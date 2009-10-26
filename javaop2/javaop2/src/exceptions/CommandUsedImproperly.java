/*
 * Created on Jan 3, 2005 By iago
 */
package exceptions;

/**
 * @author iago
 * 
 */
public class CommandUsedImproperly extends Exception
{
    private static final long serialVersionUID = 1L;

    private String            user;
    private String            command;

    public CommandUsedImproperly(String message, String user, String command)
    {
        super(message);
        this.user = user;
        this.command = command;
    }

    public String getUser()
    {
        return user;
    }

    public String getCommand()
    {
        return command;
    }

    public String toString()
    {
        return "User " + user + " tried to use command " + command + " improperly: " + getMessage();
    }
}
