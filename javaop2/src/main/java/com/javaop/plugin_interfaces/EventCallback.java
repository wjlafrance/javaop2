package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.exceptions.PluginException;


/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * @author iago These are the processed events, after they've been through
 *         anti-flood protection and any filtering and the user has been
 *         physically added to the channel and everything. This is the best
 *         place to actually display and use the events, but the Raw event
 *         handler is the better place to go if you need real control.
 */
public interface EventCallback extends AbstractCallback
{
	void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void error(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void info(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException;

	void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException;
}
