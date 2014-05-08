package com.javaop.bot;

/*
 * Created on Dec 27, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class MissingPluginError extends Error
{
    private static final long serialVersionUID = 1L;

    public MissingPluginError(String s)
    {
        super(s);
    }
}
