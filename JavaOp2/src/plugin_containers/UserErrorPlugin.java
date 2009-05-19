/*
 * Created on Dec 2, 2004
 * By iago
 */
package plugin_containers;

import plugin_interfaces.UserErrorCallback;

/**
 * @author iago
 *
 */
public class UserErrorPlugin extends AbstractPlugin
{
    public UserErrorPlugin(UserErrorCallback callback, Object data)
    {
        super(callback, data);
    }
}
