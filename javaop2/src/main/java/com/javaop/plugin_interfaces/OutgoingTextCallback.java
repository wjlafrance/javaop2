/*
 * Created on Dec 13, 2004 By iago
 */
package com.javaop.plugin_interfaces;

/**
 * @author iago WARNING: Do NOT send outgoing text messages from these
 *         callbacks. It causes unpredictable results and overall painful
 *         confusion.
 */
public interface OutgoingTextCallback extends AbstractCallback
{
	/**
	 * Text has just been queued to be sent. It can be changed by returning
	 * different text, or cancelled by returning null.
	 */
	public String queuingText(String text, Object data);

	/** Text has been queued and will wait for its turn. */
	public void queuedText(String text, Object data);

	/**
	 * Indicates that the string is next in line to be called. It's about to be
	 * waited on.
	 */
	public String nextInLine(String text, Object data);

	/** Gets the amount of time to wait before sending the text. */
	public long getDelay(String text, Object data);

	/**
	 * Delay is up, text is about to be sent. Last chance to cancel it -- it'll
	 * still count towards flooding if it's cancelled here
	 */
	public boolean sendingText(String text, Object data);

	/** The text is being sent out. */
	public void sentText(String text, Object data);
}