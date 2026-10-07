/*
 * Created on Jan 3, 2005 By iago
 */
package com.javaop.exceptions;

/**
 * @author iago
 */
public class CommandUsedImproperlyException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String user;
	private final String command;

	public CommandUsedImproperlyException(String message, String user, String command) {
		super(message);
		this.user = user;
		this.command = command;
	}

	public String toString() {
		return "User " + user + " tried to use command " + command + " improperly: " + getMessage();
	}

	public String getUser() {
		return this.user;
	}

	public String getCommand() {
		return this.command;
	}
}
