/*
 * Created on Apr 24, 2005 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.PacketConstants;

import com.javaop.pluginmanagers.PluginRegistration;

import com.javaop.util.BnetPacket;
import com.javaop.util.ChatMessage;

import com.javaop.callback_interfaces.PublicExposedFunctions;


public class Queue
{
	private final PublicExposedFunctions out;
	private final PluginRegistration     plugins;

	private final TreeSet<ChatMessage>   queue   = new TreeSet<ChatMessage>();
	private final Timer                  timer   = new Timer();

	private TimerTask                    current = null;
	private ChatMessage                  up      = null;

	public Queue(PublicExposedFunctions out, PluginRegistration plugins)
	{
		this.out = out;
		this.plugins = plugins;
	}

	public synchronized void send(String text, int priority)
	{
		text = plugins.queuingText(text);
		if (text == null) {
			return;
		}
		queue.add(new ChatMessage(text, priority));
		plugins.queuedText(text);

		scheduleNext();
	}

	public synchronized void scheduleNext()
	{
		String text = null;
		do
		{
			if (up != null) {
				return;
			}

			if (queue.size() == 0) {
				return;
			}

			up = (ChatMessage) queue.first();
			queue.remove(up);

			text = plugins.nextInLine(up.getText());
		} while (text == null);

		up = new ChatMessage(text, up.getPriority());
		long delay = plugins.getDelay(up.getText());
		timer.schedule(current = new QueueTask(), delay);
	}

	public synchronized void clear()
	{
		if (current != null) {
			current.cancel();
		}

		queue.clear();
		up = null;
		current = null;
	}

	private class QueueTask extends TimerTask
	{
		public void run()
		{
			try
			{
				if (out.isLocked())
				{
					out.systemMessage(ErrorLevelConstants.WARNING, "Output locked; message cancelled:");
					out.systemMessage(ErrorLevelConstants.WARNING, up.getText());
				}
				else
				{
					BnetPacket command = new BnetPacket(PacketConstants.SID_CHATCOMMAND);
					command.addNTString(up.getText());
					if (plugins.sendingText(up.getText()))
					{
						out.sendPacket(command);
						plugins.sentText(up.getText());
					}
				}

				up = null;
				scheduleNext();
			}
			catch (IOException e)
			{
				out.systemMessage(ErrorLevelConstants.ERROR, "Unable to send packet: " + e);
			}
		}
	}
}
