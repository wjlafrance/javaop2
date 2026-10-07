package com.javaop.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Shared pool for short-lived fire-and-forget work (lookups, connection attempts) that used to
 * be done by spawning a dedicated {@link Thread}.
 *
 * Threads are named "javaop-background-N" and are non-daemon, like the hand-spawned threads they
 * replace, but idle threads exit after a second so the pool never holds the JVM open for long.
 * {@link #shutdown()} stops accepting work and lets in-flight tasks finish.
 */
public final class BackgroundTasks {
	private static final AtomicInteger COUNTER = new AtomicInteger();

	private static final ExecutorService POOL = new ThreadPoolExecutor(
			0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>(),
			r -> new Thread(r, "javaop-background-" + COUNTER.incrementAndGet()));

	private BackgroundTasks() {
	}

	/** Fire and forget: an uncaught exception is reported like it would be on a hand-made thread. */
	public static void execute(Runnable task) {
		POOL.execute(task);
	}

	/** Result is only observable through the Future; exceptions are captured in it. */
	public static Future<?> submit(Runnable task) {
		return POOL.submit(task);
	}

	public static <T> Future<T> submit(Callable<T> task) {
		return POOL.submit(task);
	}

	public static void shutdown() {
		POOL.shutdown();
	}
}
