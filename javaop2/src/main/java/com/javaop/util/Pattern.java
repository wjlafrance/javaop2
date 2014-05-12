/*
 * Pattern.java
 *
 * Created on May 30, 2004, 2:44 PM
 */

package com.javaop.util;

/**
 *
 * @author Ron - Home
 */
public class Pattern
{
	/**
	 * Fixes the name to escape and replace characters.
	 *
	 * @param str
	 *            The original string.
	 * @return The new string.
	 */
	public static String fixPattern(String str)
	{
		StringBuffer ret = new StringBuffer();

		for (int i = 0; i < str.length(); i++)
		{
			char thisChar = str.charAt(i);

			if (thisChar == '*')
				ret.append(".*");
			else if (thisChar == '?')
				ret.append(".");
			else if (thisChar == '%')
				ret.append("[0-9]");
			else if (!Character.isLetterOrDigit(thisChar))
				ret.append("\\").append(thisChar);
			else
				ret.append(str.charAt(i));
		}
		return ret.toString().toLowerCase();
	}

}
