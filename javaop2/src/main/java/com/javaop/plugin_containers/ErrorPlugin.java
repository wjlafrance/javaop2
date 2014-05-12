/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.ErrorCallback;


/**
 * @author iago
 *
 */
public class ErrorPlugin extends AbstractPlugin
{
	public ErrorPlugin(ErrorCallback callback, Object data)
	{
		super(callback, data);
	}
}