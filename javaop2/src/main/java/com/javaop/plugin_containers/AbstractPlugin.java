/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.AbstractCallback;

/**
 * @author iago
 */
public abstract class AbstractPlugin {
	protected AbstractCallback callback;
	protected Object data;

	protected AbstractPlugin(AbstractCallback callback, Object data) {
		this.data = data;
		this.callback = callback;
	}

	public String toString() {
		return callback.toString();
	}

	public AbstractCallback getCallback() {
		return this.callback;
	}

	public Object getData() {
		return this.data;
	}
}
