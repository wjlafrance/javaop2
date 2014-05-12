/*
 * Created on Dec 12, 2004 By iago
 */

/**
 * @author iago
 *
 */

package com.javaop.util;

import java.util.Calendar;


/**
 * Simply displays a nicely formatted timestamp.
 *
 * @author Ron
 */
public class Timestamp
{
	/**
	 * Returns the current time as a nicely formatted timestamp.
	 */
	public static String getTimestamp()
	{
		Calendar c = Calendar.getInstance();

		StringBuilder s = new StringBuilder();
		s.append('[');
		s.append(PadString.padNumber(c.get(Calendar.HOUR_OF_DAY), 2)).append(':');
		s.append(PadString.padNumber(c.get(Calendar.MINUTE), 2)).append(':');
		s.append(PadString.padNumber(c.get(Calendar.SECOND), 2)).append('.');
		s.append(PadString.padNumber(c.get(Calendar.MILLISECOND), 3));
		s.append("] ");

		return s.toString();
	}

	/**
	 * Returns the current date nicely formatted.
	 */
	public static String getDate()
	{
		Calendar c = Calendar.getInstance();

		StringBuilder s = new StringBuilder();
		s.append(PadString.padNumber(c.get(Calendar.YEAR), 4)).append(".");
		s.append(PadString.padNumber(c.get(Calendar.MONTH) + 1, 2)).append(".");
		s.append(PadString.padNumber(c.get(Calendar.DAY_OF_MONTH), 2));

		return s.toString();
	}
}
