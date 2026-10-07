/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.CommandCallback;

/**
 * @author iago
 */
public class CommandPlugin extends AbstractPlugin {
	private final String name;
	private final int args;
	private final boolean requiresOps;
	private final String requiredFlags;
	private final String usage;
	private final String help;

	public CommandPlugin(CommandCallback callback, String name, int args, boolean requiresOps, String requiredFlags, String usage, String help, Object data) {
		super(callback, data);
		this.name = name;
		this.args = args;
		this.requiresOps = requiresOps;
		this.requiredFlags = requiredFlags;
		this.usage = usage;
		this.help = help;
	}

	public String getName() {
		return this.name;
	}

	public int getArgs() {
		return this.args;
	}

	public boolean isRequiresOps() {
		return this.requiresOps;
	}

	public String getRequiredFlags() {
		return this.requiredFlags;
	}

	public String getUsage() {
		return this.usage;
	}

	public String getHelp() {
		return this.help;
	}
}
