/*
 * Created on Dec 14, 2004
 * By iago
 */
package exceptions;

/**
 * @author iago
 *
 */
public class InvalidVersion extends PluginException
{
	private static final long serialVersionUID = 1L;
	
    public InvalidVersion(String msg)
    {
        super(msg);
    }
}
