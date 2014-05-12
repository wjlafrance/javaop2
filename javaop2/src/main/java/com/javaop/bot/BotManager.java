/*
 * Created on Jan 20, 2005 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.util.Hashtable;

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
	private static final Hashtable<String, BotCore> activeBots = new Hashtable<>();

	public static void startBot(String name) throws IOException, PluginException
	{
		if (activeBots.get(name) == null) {
			activeBots.put(name, new BotCore(name));
		} else {
			System.err.println("Attempting to load an already active bot!");
		}
	}

	public static void stopBot(String name) throws IllegalArgumentException
	{
		BotCore bot = (BotCore) activeBots.get(name);

		activeBots.remove(name);

		if (bot != null) {
			bot.stop();
		}
	}

	public static String[] getAllBots()
	{
		return JavaOpFileStuff.getAllBots();
	}

	public static String[] getActiveBots()
	{
		return Uniq.uniq(activeBots.keys());
	}

	public static BotCore getBot(String name)
	{
		return (BotCore) activeBots.get(name);
	}
}
