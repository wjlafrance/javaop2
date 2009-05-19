/*
 * Created on Dec 2, 2004
 * By iago
 */
package plugin_containers;

import plugin_interfaces.OutgoingTextCallback;

/**
 * @author iago
 *
 */
public class OutgoingTextPlugin extends AbstractPlugin
{
    public OutgoingTextPlugin(OutgoingTextCallback callback, Object data)
    {
        super(callback, data);
    }
}