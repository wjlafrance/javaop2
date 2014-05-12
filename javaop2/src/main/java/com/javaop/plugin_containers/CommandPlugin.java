/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.CommandCallback;
import lombok.Getter;

/**
 * @author iago
 *
 */
public class CommandPlugin extends AbstractPlugin
{
	private final @Getter String name;
	private final @Getter int args;
	private final @Getter boolean requiresOps;
	private final @Getter String requiredFlags;
	private final @Getter String usage;
	private final @Getter String help;

	public CommandPlugin(CommandCallback callback, String name, int args, boolean requiresOps, String requiredFlags,
			String usage, String help, Object data)
	{
		super(callback, data);
		this.name = name;
		this.args = args;
		this.requiresOps = requiresOps;
		this.requiredFlags = requiredFlags;
		this.usage = usage;
		this.help = help;
	}
}
