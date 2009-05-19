/*
 * Created on Dec 2, 2004
 * By iago
 */
package plugin_containers;

import plugin_interfaces.RawEventCallback;

/**
 * @author iago
 *
 */
public class RawEventPlugin extends AbstractPlugin
{
    private int event;
    
    public RawEventPlugin(RawEventCallback callback, int event, Object data)
    {
        super(callback, data);

        this.event = event;
    }
    public int getEvent()
    {
        return event;
    }
}