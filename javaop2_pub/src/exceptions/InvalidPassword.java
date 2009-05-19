/*
 * Created on Dec 14, 2004
 * By iago
 */
package exceptions;

/**
 * @author iago
 *
 */
public class InvalidPassword extends PluginException
{
	private static final long serialVersionUID = 1L;
	
    public InvalidPassword(String msg)
    {
        super(msg);
    }
}
