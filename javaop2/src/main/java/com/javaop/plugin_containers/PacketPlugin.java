/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.PacketCallback;


/**
 * @author iago
 *
 */
public class PacketPlugin extends AbstractPlugin
{
	private int event;

	public PacketPlugin(PacketCallback callback, int event, Object data)
	{
		super(callback, data);
		this.event = event;
	}

	public int getEvent()
	{
		return event;
	}
}
