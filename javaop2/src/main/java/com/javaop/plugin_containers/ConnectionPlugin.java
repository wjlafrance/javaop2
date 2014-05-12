/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.ConnectionCallback;


/**
 * @author iago
 *
 */
public class ConnectionPlugin extends AbstractPlugin {

	public ConnectionPlugin(ConnectionCallback callback, Object data) {
		super(callback, data);
	}

}