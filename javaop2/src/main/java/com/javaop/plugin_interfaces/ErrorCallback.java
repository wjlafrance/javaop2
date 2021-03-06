/*
 * Created on Dec 1, 2004 By iago
 */
package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.util.BnetEvent;
import com.javaop.util.BnetPacket;

import com.javaop.exceptions.LoginException;
import com.javaop.exceptions.PluginException;


/**
 * These are error callbacks that are called when an error occurs. These aren't
 * allowed to throw an exception because that would likely cause infinite
 * recursion. If any of them DO throw an error or exception, it will be caught
 * and displayed to stdout rather than calling one of these.
 *
 * @author iago
 *
 */
public interface ErrorCallback extends AbstractCallback
{
	/** This is called if there is a connection problem. */
	void ioException(IOException e, Object data);

	/** This is called if an exception makes it to the top level. */
	void unknownException(Exception e, Object data);

	/**
	 * This is called if there is an "error". These should never be handled,
	 * they're always something horrible.
	 */
	void error(Error e, Object data);

	/**
	 * If an exception is throw from a plugin, besides an IOException (which
	 * forces a reconnect), this gets called.
	 */
	void pluginException(PluginException e, Object data);

	/** If a login exception occurs, this is called */
	void loginException(LoginException e, Object data);

	/** This is called if an unhandled packet is received. */
	void unknownPacketReceived(BnetPacket packet, Object data);

	/** This is called if an unhandled event is received. */
	void unknownEventReceived(BnetEvent event, Object data);
}
