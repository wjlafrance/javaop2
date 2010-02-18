/*
 * Created on Dec 14, 2004 By iago
 */
package com.javaop.exceptions;

/**
 * @author iago
 * 
 */
public class PluginException extends Exception
{
    private static final long serialVersionUID = 1L;

    public PluginException(String msg)
    {
        super(msg);
    }

    public PluginException(Exception e)
    {
        super(e);
    }
}
