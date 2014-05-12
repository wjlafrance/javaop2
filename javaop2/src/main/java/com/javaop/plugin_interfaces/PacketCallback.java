package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.exceptions.PluginException;
import com.javaop.util.BnetPacket;


/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * @author iago
 *
 */
public interface PacketCallback extends AbstractCallback
{
	public final boolean ABORT    = false;
	public final boolean CONTINUE = true;

	/**
	 * This is called when a packet is about to be sent or received. It can be
	 * changed/dropped here. If null is returned, the packet is dropped. If the
	 * packet is changed, it stays changed.
	 */
	public BnetPacket processingPacket(BnetPacket buf, Object data) throws IOException, PluginException;

	/**
	 * This is called when a packet has completed being send , and it can no
	 * longer be modified/dropped.
	 */
	public void processedPacket(BnetPacket buf, Object data) throws IOException, PluginException;
}
