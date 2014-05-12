/*
 * Created on Jun 19, 2005 By iago
 */
package com.javaop.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;


public class TimeoutSocket extends Thread
{
	public static Socket getSocket(String server, int port, int timeout) throws SocketException
	{
		long endTime = System.currentTimeMillis() + timeout;

		TimeoutSocket thisSocket = new TimeoutSocket(server, port);
		thisSocket.start();

		while (true)
		{
			// Success
			if (thisSocket.getSocket() != null)
			{
				return thisSocket.getSocket();
			}

			if (System.currentTimeMillis() > endTime)
			{
				thisSocket.cancel();
				throw new SocketException("Connection timed out");
			}

			try
			{
				Thread.sleep(100);
			}
			catch (Exception e)
			{
			}
		}
	}

	private Socket       s      = null;
	private boolean      cancel = false;
	private String       failed = null;

	private final String server;
	private final int    port;

	private TimeoutSocket(String server, int port)
	{
		this.server = server;
		this.port = port;
	}

	public void run()
	{
		try
		{
			s = new Socket(server, port);

			if (cancel)
				s.close();
		}
		catch (IOException e)
		{
			failed = e.toString();
		}
	}

	private Socket getSocket() throws SocketException
	{
		if (failed != null)
			throw new SocketException(failed);
		return s;
	}

	private void cancel()
	{
		cancel = true;
	}
}
