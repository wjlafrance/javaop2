/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.EventCallback;

/**
 * @author iago
 *
 */
public class EventPlugin extends AbstractPlugin {

	public EventPlugin(EventCallback callback, Object data) {
		super(callback, data);
	}

}