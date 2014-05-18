package com.javaop.BotMail;

import java.util.Date;
import java.util.Vector;

import com.javaop.util.PersistantMap;
import com.javaop.util.RelativeFile;


/*
 * Created on Apr 14, 2005 By iago
 */

public class Mail
{
	private PersistantMap       messages;

	private static final String ID             = "_current id";
	private static final String LOW_ID         = "_lowest id";
	private static final String SENDER_PREFIX  = "from-";
	private static final String MESSAGE_PREFIX = "msg-";
	private static final String DATE_PREFIX    = "date-";

	public Mail(String file)
	{
		messages = new PersistantMap(new RelativeFile(file + ".mail"),
				"This is the mail messages.  One section per user.");
	}

	public synchronized void add(String to, String from, String message)
	{
		// Get the current key
		int key = Integer.parseInt(messages.getWrite(to, ID, "0"));
		// Increment to the next key
		key++;
		messages.set(to, ID, key + "");

		// Get the keys for the current message
		String senderKey = SENDER_PREFIX + key;
		String messageKey = MESSAGE_PREFIX + key;
		String dateKey = DATE_PREFIX + key;

		messages.set(to, senderKey, from);
		messages.set(to, messageKey, message);
		messages.set(to, dateKey, new Date().toString());
	}

	public synchronized void remove(String user, String id)
	{
		int thisId = Integer.parseInt(id);
		int lowId = Integer.parseInt(messages.getWrite(user, LOW_ID, "1"));
		int highId = Integer.parseInt(messages.getWrite(user, ID, "0"));

		messages.remove(user, MESSAGE_PREFIX + id);
		messages.remove(user, SENDER_PREFIX + id);
		messages.remove(user, DATE_PREFIX + id);

		// If it's the lowest id, bump up the current lowest id
		if (thisId == lowId)
		{
			System.err.println("Low Id removed, bumping it up");

			// Find the second lowest id
			for (int i = lowId + 1; i <= highId; i++)
			{
				if (exists(user, i + ""))
				{
					messages.set(user, LOW_ID, i + "");
					break;
				}

				messages.set(user, LOW_ID, i + "");
			}
		}
	}

	public synchronized String getMessage(String user, String id)
	{
		return messages.getNoWrite(user, MESSAGE_PREFIX + id, "<no such message>");
	}

	public synchronized String getSender(String user, String id)
	{
		return messages.getNoWrite(user, SENDER_PREFIX + id, "<no such message>");
	}

	public synchronized String getDate(String user, String id)
	{
		return messages.getNoWrite(user, DATE_PREFIX + id, "<no such message>");
	}

	public synchronized String getFullMessage(String user, String id)
	{
		return "#" + id + ": <From: " + getSender(user, id) + "> " + getMessage(user, id) + " ["
				+ getDate(user, id) + "]";
	}

	public synchronized boolean exists(String user, String id)
	{
		return messages.getNoWrite(user, MESSAGE_PREFIX + id, null) != null;
	}

	public synchronized String[] getMessages(String user)
	{
		int low = Integer.parseInt(messages.getNoWrite(user, LOW_ID, "1"));
		int high = Integer.parseInt(messages.getNoWrite(user, ID, "0"));

		Vector ids = new Vector();

		for (int i = low; i <= high; i++)
			if (exists(user, i + ""))
				ids.add(i + "");

		return (String[]) ids.toArray(new String[ids.size()]);
	}

	public synchronized int getCount(String user)
	{
		int low = Integer.parseInt(messages.getNoWrite(user, LOW_ID, "1"));
		int high = Integer.parseInt(messages.getNoWrite(user, ID, "0"));

		int count = 0;

		for (int i = low; i <= high; i++)
			if (exists(user, i + ""))
				count++;

		return count;

	}

	public static void main(String[] args) throws Exception
	{
		Mail m = new Mail("/tmp/test");

		m.add("iago", "Joe", "1 Hey iago, it's Joe");
		m.add("iago", "Joe", "2 Hey iago, it's Joe");

		m.remove("iago", "1");

		m.add("iago", "Joe", "3 Hey iago, it's Joe");

		m.remove("iago", "3");
		m.add("iago", "Joe", "4 Hey iago, it's Joe");
		m.add("iago", "Joe", "5 Hey iago, it's Joe");

		System.out.println("Message 2, 4, and 5:");
		String[] msgs = m.getMessages("iago");
		for (int i = 0; i < msgs.length; i++)
			System.out.println(m.getFullMessage("iago", msgs[i]));

		System.out.println("iago = " + m.getCount("iago"));
		System.out.println("Joe = " + m.getCount("joe"));
	}
}
