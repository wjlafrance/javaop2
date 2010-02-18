package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.util.BnetEvent;

import com.javaop.exceptions.PluginException;


/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * These are the raw events, straight from battle.net. You'll only want to use
 * this plugin type if you're writing a filter or anti-floodbot script.
 * EventCallback is what you would want to use to display the events.
 * 
 * @author iago
 * 
 */
public interface RawEventCallback extends AbstractCallback
{
    /** An event is occurring. It can be modified or cancelled at this point. */
    public BnetEvent eventOccurring(BnetEvent event, Object data) throws IOException, PluginException;

    /** An event has occurred. It can't be modified at this point. */
    public void eventOccurred(BnetEvent event, Object data) throws IOException, PluginException;
}
