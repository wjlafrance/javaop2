package com.javaop.util;

import java.io.IOException;

/** An IOException that already carries its classified {@link DisconnectReason}. */
public class ConnectionLostException extends IOException {
	private static final long serialVersionUID = 1L;

	private final transient DisconnectReason reason;

	public ConnectionLostException(DisconnectReason reason) {
		super(reason.getMessage(), reason.getCause());
		this.reason = reason;
	}

	public DisconnectReason getReason() {
		return reason;
	}
}
