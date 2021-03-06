/*
 * Created on Dec 4, 2004 By iago
 */
package com.javaop.plugin_interfaces;

import java.io.IOException;

import com.javaop.exceptions.PluginException;


/**
 * @author iago
 *
 */
public interface BotCallback extends AbstractCallback
{
	/** This is called as soon as the instance of the bot is started. */
	void botInstanceStarting(Object data) throws IOException, PluginException;

	/** This is called when the instance of the bot is ending */
	void botInstanceStopping(Object data) throws IOException, PluginException;
}
