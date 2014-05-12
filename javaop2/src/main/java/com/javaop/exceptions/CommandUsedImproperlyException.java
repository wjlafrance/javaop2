/*
 * Created on Jan 3, 2005 By iago
 */
package com.javaop.exceptions;

import lombok.Getter;

/**
 * @author iago
 *
 */
public class CommandUsedImproperlyException extends Exception
{
	private static final long serialVersionUID = 1L;

	private final @Getter String user;
	private final @Getter String command;

	public CommandUsedImproperlyException(String message, String user, String command) {
		super(message);
		this.user = user;
		this.command = command;
	}

	public String toString() {
		return "User " + user + " tried to use command " + command + " improperly: " + getMessage();
	}
}
