/*
 * Created on Dec 14, 2004 By iago
 */
package com.javaop.exceptions;

/**
 * @author iago
 *
 */
public class PluginException extends Exception {

	private static final long serialVersionUID = 1L;

	public PluginException(String message) {
		super(message);
	}

	public PluginException(Throwable cause) {
		super(cause);
	}

}
