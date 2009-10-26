/*
 * Created on Dec 2, 2004 By iago
 */
package plugin_containers;

import plugin_interfaces.EventCallback;


/**
 * @author iago
 * 
 */
public class EventPlugin extends AbstractPlugin
{
    public EventPlugin(EventCallback callback, Object data)
    {
        super(callback, data);
    }
}