package com.javaop.util;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Why a Battle.net connection ended. A lost connection is a normal event, not a bug: the one-line
 * {@link #getMessage() message} is what the user sees, the cause (if any) is for DEBUG output and logs only.
 */
public final class DisconnectReason {

	public enum Kind {
		/** The user (or a plugin, e.g. Reconnect) asked for the disconnect. */
		REQUESTED,
		/** Read timed out, or the OS gave up on the connection ("Operation timed out"). */
		TIMEOUT,
		/** The server closed the connection (end of stream). */
		CLOSED_BY_SERVER,
		/** "Connection reset" by the peer or a network device. */
		RESET,
		/** The server sent something that is not a valid BNCS packet. */
		PROTOCOL_ERROR,
		/** The login was refused (already reported separately). */
		LOGIN_FAILED,
		/** Any other I/O failure. */
		IO_ERROR
	}

	public static final DisconnectReason REQUESTED = new DisconnectReason(Kind.REQUESTED, "Disconnected", null);
	public static final DisconnectReason LOGIN_FAILED = new DisconnectReason(Kind.LOGIN_FAILED, "Login failed", null);

	private final Kind kind;
	private final String message;
	private final Throwable cause;

	public DisconnectReason(Kind kind, String message, Throwable cause) {
		this.kind = kind;
		this.message = message;
		this.cause = cause;
	}

	public Kind getKind() {
		return kind;
	}

	/** A single line suitable for the chat window, without the "[BNET] Disconnected:" prefix. */
	public String getMessage() {
		return message;
	}

	public Throwable getCause() {
		return cause;
	}

	/** Network failures that happen in normal operation. Only the others deserve a stack trace (at DEBUG). */
	public boolean isExpected() {
		return kind != Kind.IO_ERROR && kind != Kind.PROTOCOL_ERROR;
	}

	@Override public String toString() {
		return kind + ": " + message;
	}

	/** Classifies an exception from the socket. */
	public static DisconnectReason of(IOException e) {
		if (e instanceof ConnectionLostException) {
			return ((ConnectionLostException) e).getReason();
		}
		String text = String.valueOf(e.getMessage()).toLowerCase();
		if (e instanceof SocketTimeoutException) {
			return new DisconnectReason(Kind.TIMEOUT, "Read timed out", e);
		}
		if (e instanceof EOFException) {
			return new DisconnectReason(Kind.CLOSED_BY_SERVER, "Connection closed by server", e);
		}
		if (text.contains("timed out")) {
			return new DisconnectReason(Kind.TIMEOUT, "Connection timed out", e);
		}
		if (text.contains("connection reset") || text.contains("broken pipe")) {
			return new DisconnectReason(Kind.RESET, "Connection reset by peer", e);
		}
		return new DisconnectReason(Kind.IO_ERROR, "I/O error: " + e, e);
	}
}
