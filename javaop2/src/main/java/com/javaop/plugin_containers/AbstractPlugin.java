/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.AbstractCallback;


/**
 * @author iago
 * 
 */
abstract public class AbstractPlugin
{
    protected AbstractCallback callback;
    protected Object           data;

    protected AbstractPlugin(AbstractCallback callback, Object data)
    {
        this.data = data;
        this.callback = callback;
    }

    public AbstractCallback getCallback()
    {
        return callback;
    }

    public Object getData()
    {
        return data;
    }

    public String toString()
    {
        return callback.toString();
    }
}
