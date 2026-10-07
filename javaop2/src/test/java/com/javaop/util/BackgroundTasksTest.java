package com.javaop.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import static org.junit.Assert.*;

public class BackgroundTasksTest {

	@Test
	public void runsCallableAndReturnsResult() throws Exception {
		assertEquals("ok", BackgroundTasks.submit(() -> "ok").get(5, TimeUnit.SECONDS));
	}

	@Test
	public void executeRunsFireAndForgetTask() throws Exception {
		final CountDownLatch done = new CountDownLatch(1);
		BackgroundTasks.execute(done::countDown);
		assertTrue(done.await(5, TimeUnit.SECONDS));
	}

	@Test
	public void threadsAreNamedAndNonDaemon() throws Exception {
		Future<String> f = BackgroundTasks.submit(() -> {
			Thread t = Thread.currentThread();
			return t.getName() + "/" + t.isDaemon();
		});
		assertTrue(f.get(5, TimeUnit.SECONDS).matches("javaop-background-\\d+/false"));
	}

	@Test
	public void manyConcurrentTasksAllRun() throws Exception {
		final int n = 50;
		final AtomicInteger ran = new AtomicInteger();
		final CountDownLatch gate = new CountDownLatch(1);
		List<Future<?>> futures = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			futures.add(BackgroundTasks.submit(() -> {
				try {
					gate.await();
				} catch (InterruptedException e) {
					return;
				}
				ran.incrementAndGet();
			}));
		}
		gate.countDown();
		for (Future<?> f : futures) {
			f.get(5, TimeUnit.SECONDS);
		}
		assertEquals(n, ran.get());
	}
}
