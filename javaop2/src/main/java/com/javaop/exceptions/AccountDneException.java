/*
 * Created on Sep 25, 2009 by joe
 */
package com.javaop.exceptions;

/**
 * @author joe
 *
 */
public class AccountDneException extends PluginException
{
	private static final long serialVersionUID = 1L;

	public AccountDneException(String msg)
	{
		super(msg);
	}
}
