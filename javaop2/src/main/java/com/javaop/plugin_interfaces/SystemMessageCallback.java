/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_interfaces;

/**
 * These are callback functions for system messages, not for
 * Battle.net-generated messages. I'm stealing ideas for levels from the Kernel.
 * :)
 *
 * @author iago
 *
 */
public interface SystemMessageCallback extends AbstractCallback
{
	/**
	 * These are called for system messages. There are several levels, defined
	 * by the various constants in ErrorLevelConstants: DEBU G - A lot of crap
	 * that you'll never need to see. For debugging; INFO - Standard useful
	 * messages to the user; NOTICE - Something is happening that should be
	 * looked at; WARNING - A warning about something; ERROR - An error has
	 * occurred; CRITICAL - Conditions are critical; ALERT - Action must be
	 * taken immediately; EMERGENCY - All hell's going on, we're unusable; If
	 * you want to know where I got the ideas for these levels, "man syslog" :)
	 */
	void systemMessage(int level, String message, Object data);

	/**
	 * Show a message. This can contain color codes, see
	 * javaop2_pub/util/ColorConstants.java
	 */
	void showMessage(String message, Object data);
}
