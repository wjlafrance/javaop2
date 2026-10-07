/*
 * Created on Jan 20, 2005 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.javaop.util.Uniq;

import com.javaop.exceptions.PluginException;


/**
 * This manages bots. It creates, destroys, and lists them.
 *
 * @author iago
 *
 */
public class BotManager
{
	private static final Map<String, BotCore> activeBots = new ConcurrentHashMap<>();

	public static synchronized void startBot(String name) throws IOException, PluginException
	{
		if (activeBots.get(name) == null) {
			activeBots.put(name, new BotCore(name));
		} else {
			System.err.println("Attempting to load an already active bot!");
		}
	}

	public static synchronized void stopBot(String name) throws IllegalArgumentException
	{
		BotCore bot = activeBots.get(name);

		activeBots.remove(name);

		if (bot != null) {
			bot.stop();
		}
	}

	public static List<String> getAllBots()
	{
		return JavaOpFileStuff.getAllBots();
	}

	public static List<String>getActiveBots()
	{
		return Uniq.uniq(activeBots.keySet());
	}

	public static BotCore getBot(String name)
	{
		return activeBots.get(name);
	}
}
