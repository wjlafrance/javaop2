/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.RawEventCallback;
import lombok.Getter;

/**
 * @author iago
 *
 */
public class RawEventPlugin extends AbstractPlugin
{
	private final @Getter
	int event;

	public RawEventPlugin(RawEventCallback callback, int event, Object data) {
		super(callback, data);
		this.event = event;
	}

}