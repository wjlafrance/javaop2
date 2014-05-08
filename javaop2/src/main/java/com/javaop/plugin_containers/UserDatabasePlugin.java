package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.UserDatabaseCallback;


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
