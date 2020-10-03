/*
 * Created on Dec 6, 2004 By iago
 */
package com.javaop.bot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.javaop.callback_interfaces.PublicExposedFunctions;

import com.javaop.pluginmanagers.PluginRegistration;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.PacketConstants;
import com.javaop.exceptions.LoginException;
import com.javaop.exceptions.PluginException;

import com.javaop.util.BnetEvent;
import com.javaop.util.BnetPacket;
import com.javaop.util.TimeoutSocket;
import com.javaop.util.Buffer;


/**
 * This runs as a separate thread. It is basically a receive loop. It receives
 * messages from the socket, and calls back to the receivedPacket callback. All
 * it stores is a handle to the Callbacks class, and the input stream that it's
 * reading from.
 *
 * @author iago
 *
 */
public class PacketThread extends Thread {
	final private PluginRegistration     callbacks;
	final private PublicExposedFunctions out;

	protected Socket                     s      = null;
	protected OutputStream               output = null;
	protected InputStream                input;

	private boolean                      stop   = false;

	public PacketThread(PluginRegistration callbacks, PublicExposedFunctions out) {
		this.callbacks = callbacks;
		this.out = out;

		this.setName("Packet-thread-" + out.getName());
	}

	public void stopThread() {
		synchronized (this) {
			stop = true;

			try {
				input.close();
			} catch (Exception e) {
			}
			try {
				output.close();
			} catch (Exception e) {
			}
			try {
				s.close();
			} catch (Exception e) {
			}
		}
	}

	public void run() {
		// Connect

		try {
			String server = out.getLocalSettingDefault("_default", "server", "uswest.battle.net");
			int port = Integer.parseInt(out.getLocalSettingDefault("_default", "port", "6112"));

			if (!callbacks.connecting(server, port)) {
				return;
			}

			out.lock();

			out.systemMessage(ErrorLevelConstants.INFO, "[BNET] Looking up " + server + "..");

			// Get a list of addresses
			List<InetAddress> addresses = Arrays.asList(InetAddress.getAllByName(server))
				.stream()
				.filter(x -> x.getAddress().length == 4) // IPv4 only
				.collect(Collectors.toList());
			int index = (int) (Math.random() * addresses.size());
			if (stop) {
				return;
			}
			InetAddress chosenAddress = addresses.get(index);
			out.systemMessage(ErrorLevelConstants.INFO, "[BNET] Got " + addresses.size()
					+ " addresses. Connecting to " + chosenAddress + "..");
			s = TimeoutSocket.getSocket(chosenAddress.getHostAddress(), port,
			  Integer.parseInt(BotCoreStatic.getInstance().getGlobalSettingDefault("JavaOp2", "timeout", "30000")));

			if (stop) {
				return;
			}

			callbacks.systemMessage(ErrorLevelConstants.INFO, "[BNET] Connected to "
					+ s.getRemoteSocketAddress());

			out.putLocalVariable("address", chosenAddress.getAddress());

			output = s.getOutputStream();
			input = s.getInputStream();

			callbacks.connected(server, port);
		} catch (IOException | PluginException e) {
			if (stop) {
				return;
			}
			out.systemMessage(ErrorLevelConstants.ERROR, "[BNET] Connect failed: " + e);
			disconnected();
			return;
		}

		if (stop) {
			return;
		}

		try {
			out.systemMessage(ErrorLevelConstants.DEBUG, "[BNET] PacketThread entering receive loop.");
			// Loop and receive packets
			while (true) {
				// This try loop will catch exceptions that are thrown from
				// plugins. If an IOException occurs,
				// it assumes that it's lost the Battle.net connection and calls
				// the disconnect() function and
				// kills this thread
				try {
					int FF = input.read();
					byte code = (byte) input.read();
					int len1 = (input.read() & 0x000000FF);
					int len2 = (input.read() & 0x000000FF) << 8;

					if (FF == -1) {
						throw new IOException("Connection lost");
					}

					if (FF != 0x000000FF) {
						throw new IOException("Packet didn't start with 0xFF (it started with 0x"
								+ Integer.toHexString(FF) + ") -- Battle.net broke or something.");
					}

					int length = len1 | len2;

					byte[] packet = new byte[length - 4];

					for (int i = 0; i < packet.length; i++) {
						packet[i] = (byte) input.read();
					}

					BnetPacket buf = new BnetPacket(code);
					buf.add(packet);
					out.systemMessage(ErrorLevelConstants.PACKET, "In:\n" + buf.toString());

					buf = callbacks.processingIncomingPacket(buf);
					if (stop) {
						return;
					}

					if (buf == null) {
						continue;
					}

					if (code == PacketConstants.SID_CHATEVENT) {
						BnetEvent event = new BnetEvent(buf);
						event = callbacks.eventOccurring(event);

						if (event == null) {
							continue;
						}

						callbacks.eventOccurred(new BnetEvent(event));
					} else {
						callbacks.processedIncomingPacket(buf);
					}

					if (stop) {
						return;
					}
				} catch (LoginException | IOException e) {
					throw e;
				} catch (PluginException e) {
					if (stop) {
						return;
					}

					callbacks.pluginException(e);
				} catch (Exception e) {
					if (stop) {
						return;
					}

					callbacks.unknownException(e);
				}
			} // while(true)
		} catch (LoginException e) {
			if (stop) {
				return;
			}

			callbacks.loginException(e);
		} catch (IOException e) {
			if (stop) {
				return;
			}

			callbacks.ioException(e);
		}

		disconnected();
	}

	private void disconnected() {
		try {
			input.close();
			output.close();
			s.close();
		} catch (Exception e) {
		}

		callbacks.disconnected();
		stop = true;
	}

	public void send(byte[] data) throws IOException {
		if (output != null) {
			output.write(data);
			output.flush();
			out.systemMessage(ErrorLevelConstants.PACKET, "Out:\n"
					+ new Buffer(data).toString());
		}
	}

	public void setTcpNoDelay(boolean delay) throws IOException {
		s.setTcpNoDelay(delay);
	}
}
