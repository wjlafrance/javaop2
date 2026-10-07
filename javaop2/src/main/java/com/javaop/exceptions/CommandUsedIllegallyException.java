/*
 * Created on Jan 3, 2005 By iago
 */
package com.javaop.exceptions;

/**
 * @author iago
 */
public class CommandUsedIllegallyException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String user;
	private final String command;
	private final String userFlags;
	private final String requiredFlags;

	public CommandUsedIllegallyException(String message, String user, String command, String userFlags, String requiredFlags) {
		super(message);
		this.user = user;
		this.command = command;
		this.userFlags = userFlags;
		this.requiredFlags = requiredFlags;
	}

	public String toString() {
		return String.format("User %s tried to use command %s illegally: it requires %s and he has %s -- %s", user, command, requiredFlags, userFlags, getMessage());
	}

	public String getUser() {
		return this.user;
	}

	public String getCommand() {
		return this.command;
	}

	public String getUserFlags() {
		return this.userFlags;
	}

	public String getRequiredFlags() {
		return this.requiredFlags;
	}
}
