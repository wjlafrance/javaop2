/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.UserErrorCallback;

/**
 * @author iago
 *
 */
public class UserErrorPlugin extends AbstractPlugin {

	public UserErrorPlugin(UserErrorCallback callback, Object data) {
		super(callback, data);
	}

}
