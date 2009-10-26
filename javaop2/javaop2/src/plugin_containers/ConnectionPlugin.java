/*
 * Created on Dec 2, 2004 By iago
 */
package plugin_containers;

import plugin_interfaces.ConnectionCallback;


/**
 * @author iago
 * 
 */
public class ConnectionPlugin extends AbstractPlugin
{
    public ConnectionPlugin(ConnectionCallback callback, Object data)
    {
        super(callback, data);
    }
}