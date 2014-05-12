/*
 * Created on Dec 10, 2004 By iago
 */

package com.javaop.callback_interfaces;

import com.javaop.plugin_interfaces.BotCallback;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.ConnectionCallback;
import com.javaop.plugin_interfaces.ErrorCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GuiCallback;
import com.javaop.plugin_interfaces.RawEventCallback;
import com.javaop.plugin_interfaces.OutgoingTextCallback;
import com.javaop.plugin_interfaces.PacketCallback;
import com.javaop.plugin_interfaces.SystemMessageCallback;
import com.javaop.plugin_interfaces.UserDatabaseCallback;
import com.javaop.plugin_interfaces.UserErrorCallback;


/**
 * @author iago
 *
 */
public interface PluginCallbackRegister
{
	/** Register a bot plugin */
	public abstract void registerBotPlugin(BotCallback callback, Object data);

	/**
	 * Register a command plugin.
	 *
	 * @param name
	 *            The name of the command
	 * @param args
	 *            The number of arguments to split apart, 0 will split each
	 *            word, and 1 will split nothing
	 * @param requiresOps
	 *            Set to true if the command requires ops. Isn't working yet.
	 * @param requiredFlags
	 *            The flags required to use the command.
	 * @param usage
	 *            Generally a parameter list, such as "<user> <flags>"
	 * @param help
	 *            Generally a help string for the command, like
	 *            "Sets the specified user to the specified flags."
	 */
	public abstract void registerCommandPlugin(CommandCallback callback, String name, int args,
			boolean requiresOps, String requiredFlags, String usage, String help, Object data);

	/** Register a connection plugin */
	public abstract void registerConnectionPlugin(ConnectionCallback callback, Object data);

	/** Register an error plugin */
	public abstract void registerErrorPlugin(ErrorCallback callback, Object data);

	/** Register an event plugin */
	public abstract void registerEventPlugin(EventCallback callback, Object data);

	/** Register a GUI plugin */
	public abstract void registerGuiPlugin(GuiCallback callback, Object data);

	/** Register an outgoing text plugin */
	public abstract void registerOutgoingTextPlugin(OutgoingTextCallback callback, Object data);

	/** Register an incoming packet plugin for a single packet */
	public abstract void registerIncomingPacketPlugin(PacketCallback callback, int packet,
			Object data);

	/** Register an incoming packet plugin for a range of packets */
	public abstract void registerIncomingPacketPlugin(PacketCallback callback, int minPacket,
			int maxPacket, Object data);

	/** Register an outgoing packet plugin for a single packet */
	public abstract void registerOutgoingPacketPlugin(PacketCallback callback, int packet,
			Object data);

	/** Register an outgoing packet plugin for a range of packets */
	public abstract void registerOutgoingPacketPlugin(PacketCallback callback, int minPacket,
			int maxPacket, Object data);

	/** Register a raw event plugin for a single event */
	public abstract void registerRawEventPlugin(RawEventCallback callback, int event, Object data);

	/** Register a raw event plugin for a range of events */
	public abstract void registerRawEventPlugin(RawEventCallback callback, int minEvent,
			int maxEvent, Object data);

	/** Register a system message plugin for a single error level */
	public abstract void registerSystemMessagePlugin(SystemMessageCallback callback, int level,
			Object data);

	/** Register a system message plugin for a range of error levels */
	public abstract void registerSystemMessagePlugin(SystemMessageCallback callback, int minLevel,
			int maxLevel, Object data);

	/** Register a user database plugin */
	public abstract void registerUserDatabasePlugin(UserDatabaseCallback callback, Object data);

	/** Register a user error plugin */
	public abstract void registerUserErrorPlugin(UserErrorCallback callback, Object data);
}