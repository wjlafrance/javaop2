/*
 * Created on Dec 2, 2004
 * By iago
 */
package com.javaop.constants;

/** A list of constants that I stole from Linux.
 *
 * @author iago
 *
 */
public interface ErrorLevelConstants
{
	public final int PACKET = 0;	/* Display each packet -- very annoying */
	public final int DEBUG = 1;     /* Debug-level messages */
	public final int INFO = 2;      /* Informational */
	public final int NOTICE = 3;    /* Normal but significant conditions */
	public final int WARNING = 4;   /* Warning conditions */
	public final int ERROR = 5;     /* Error conditions */
	public final int CRITICAL = 6;  /* Critical conditions */
	public final int ALERT = 7;     /* Action must be taken immediately */
	public final int EMERGENCY = 8; /* System is unusable */

	public final String[] errorLevelConstants = new String[] {
			"PACKET",
			"DEBUG",
			"INFO",
			"NOTICE",
			"WARNING",
			"ERROR",
			"CRITICAL",
			"ALERT",
			"EMERGENCY"
	};
}
