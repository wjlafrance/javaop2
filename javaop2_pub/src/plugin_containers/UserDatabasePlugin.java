package plugin_containers;

import plugin_interfaces.UserDatabaseCallback;

/**
 * @author iago
 *
 */
public class UserDatabasePlugin extends AbstractPlugin
{
    public UserDatabasePlugin(UserDatabaseCallback callback, Object data)
    {
        super(callback, data);
    }
}
