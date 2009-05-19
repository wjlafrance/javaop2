/*
 * Created on Dec 14, 2004
 * By iago
 */
package exceptions;

/**
 * @author iago
 *
 */
public class InvalidCDKey extends PluginException
{
	private static final long serialVersionUID = 1L;

	public InvalidCDKey(String msg)
    {
        super(msg);
    }
}
