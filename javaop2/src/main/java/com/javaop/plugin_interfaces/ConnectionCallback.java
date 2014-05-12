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
	final boolean ABORT    = false;
	final boolean CONTINUE = true;

	/**
	 * The bot is about to connect to a server, but hasn't yet. At this point,
	 * the connection can be stopped.
	 */
	boolean connecting(String host, int port, Object data) throws IOException, PluginException;

	/** The bot has just connected to the server. */
	void connected(String host, int port, Object data) throws IOException, PluginException;

	/**
	 * The bot is about to disconnect from the server. This is only called for
	 * planned disconnects.
	 */
	boolean disconnecting(Object data);

	/** The bot has disconnected from the server. */
	void disconnected(Object data);
}
