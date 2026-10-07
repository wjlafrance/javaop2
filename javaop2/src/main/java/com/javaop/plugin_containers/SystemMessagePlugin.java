/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.SystemMessageCallback;

/**
 * @author iago
 */
public class SystemMessagePlugin extends AbstractPlugin {
	private final int minLevel;
	private final int maxLevel;

	public SystemMessagePlugin(SystemMessageCallback callback, int minLevel, int maxLevel, Object data) {
		super(callback, data);
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
	}

	public int getMinLevel() {
		return this.minLevel;
	}

	public int getMaxLevel() {
		return this.maxLevel;
	}
}
