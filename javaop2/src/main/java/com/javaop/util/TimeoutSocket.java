/*
 * Created on Jun 19, 2005 By iago
 */
package com.javaop.util;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Opens a socket, giving up after a timeout. Name resolution happens on a background thread too,
 * so a stuck lookup is covered by the timeout.
 */
public class TimeoutSocket
{
	private TimeoutSocket()
	{
	}

	public static Socket getSocket(String server, int port, int timeout) throws SocketException
	{
		final AtomicBoolean abandoned = new AtomicBoolean(false);

		Future<Socket> attempt = BackgroundTasks.submit(() -> {
			Socket s = new Socket(server, port);
			// If the caller already gave up, don't leak the connection
			if (abandoned.get()) {
				s.close();
			}
			return s;
		});

		try
		{
			return attempt.get(timeout, TimeUnit.MILLISECONDS);
		}
		catch (TimeoutException e)
		{
			abandon(attempt, abandoned);
			throw new SocketException("Connection timed out");
		}
		catch (ExecutionException e)
		{
			throw new SocketException(e.getCause().toString());
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			abandon(attempt, abandoned);
			throw new SocketException("Connection interrupted");
		}
	}

	private static void abandon(Future<Socket> attempt, AtomicBoolean abandoned)
	{
		abandoned.set(true);
		attempt.cancel(true);
		// The attempt may have finished between the timeout and now; close that socket too
		if (attempt.isDone() && !attempt.isCancelled())
		{
			try
			{
				attempt.get().close();
			}
			catch (Exception ignored)
			{
			}
		}
	}
}
