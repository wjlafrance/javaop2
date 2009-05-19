/*
 * Created on Dec 2, 2004
 * By iago
 */
package plugin_containers;

import plugin_interfaces.ErrorCallback;

/**
 * @author iago
 *
 */
public class ErrorPlugin extends AbstractPlugin
{
    public ErrorPlugin(ErrorCallback callback, Object data)
    {
        super(callback, data);
    }
}