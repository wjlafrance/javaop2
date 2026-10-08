package com.javaop.ChannelList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;

/** The plugin's job is to keep the core's user list in step with the connection. */
public class PluginMainTest {

	private final List<String> calls = new ArrayList<>();
	private final List<Object> connectionRegistrations = new ArrayList<>();

	private PluginMain activated() {
		PublicExposedFunctions out = (PublicExposedFunctions) Proxy.newProxyInstance(
			getClass().getClassLoader(), new Class<?>[] { PublicExposedFunctions.class },
			(proxy, method, args) -> {
				StringBuilder sb = new StringBuilder(method.getName());
				if (args != null) {
					for (Object a : args) {
						sb.append(' ').append(a);
					}
				}
				calls.add(sb.toString());
				return null;
			});
		PluginCallbackRegister register = (PluginCallbackRegister) Proxy.newProxyInstance(
			getClass().getClassLoader(), new Class<?>[] { PluginCallbackRegister.class },
			(proxy, method, args) -> {
				if (method.getName().equals("registerConnectionPlugin")) {
					connectionRegistrations.add(args[0]);
				}
				return null;
			});
		PluginMain plugin = new PluginMain();
		plugin.activate(out, register);
		return plugin;
	}

	@Test public void registersForConnectionCallbacks() {
		PluginMain plugin = activated();
		assertEquals(1, connectionRegistrations.size());
		assertSame(plugin, connectionRegistrations.get(0));
	}

	@Test public void disconnectedClearsUsersAndResetsChannelName() {
		PluginMain plugin = activated();
		plugin.userShow("joe[x86]", "", 0, 0);
		plugin.channel("", "Open Tech Support", 0, 0);
		calls.clear();

		plugin.disconnected(null);

		assertEquals(2, calls.size());
		assertTrue(calls.contains("channelClear"));
		assertTrue(calls.contains("channelSetName <not logged in>"));
	}

	@Test public void disconnectedTwiceIsHarmless() {
		PluginMain plugin = activated();
		plugin.disconnected(null);
		plugin.disconnected(null);
		assertEquals(4, calls.size());
	}

	@Test public void rejoinAfterDisconnectPopulatesAgain() {
		PluginMain plugin = activated();
		plugin.disconnected(null);
		calls.clear();

		plugin.channel("", "Open Tech Support", 0, 0);
		plugin.userShow("joe[x86]", "W2BN", 10, 0);

		assertEquals(Arrays.asList("channelSetName Open Tech Support", "channelClear"), calls.subList(0, 2));
		assertEquals(3, calls.size());
		assertTrue(calls.get(2).startsWith("channelAddUser joe[x86]"));
	}

	@Test public void connectingAndDisconnectingDoNotVeto() {
		PluginMain plugin = activated();
		assertTrue(plugin.connecting("h", 6112, null));
		assertTrue(plugin.disconnecting(null));
	}
}
