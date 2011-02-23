package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.exceptions.PluginException;


/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public interface ConnectionCallback extends AbstractCallback
{
    public final boolean ABORT    = false;
    public final boolean CONTINUE = true;

    /**
     * The bot is about to connect to a server, but hasn't yet. At this point,
     * the connection can be stopped.
     */
    public boolean connecting(String host, int port, Object data) throws IOException, PluginException;

    /** The bot has just connected to the server. */
    public void connected(String host, int port, Object data) throws IOException, PluginException;

    /**
     * The bot is about to disconnect from the server. This is only called for
     * planned disconnects.
     */
    public boolean disconnecting(Object data);

    /** The bot has disconnected from the server. */
    public void disconnected(Object data);
}
