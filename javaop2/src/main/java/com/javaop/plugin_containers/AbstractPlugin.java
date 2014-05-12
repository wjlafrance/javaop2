/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.AbstractCallback;
import lombok.Getter;

/**
 * @author iago
 *
 */
abstract public class AbstractPlugin
{
	protected @Getter AbstractCallback callback;
	protected @Getter Object           data;

	protected AbstractPlugin(AbstractCallback callback, Object data) {
		this.data = data;
		this.callback = callback;
	}

	public String toString() {
		return callback.toString();
	}
}
