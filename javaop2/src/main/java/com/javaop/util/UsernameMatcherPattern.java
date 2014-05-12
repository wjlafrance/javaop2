/*
 * Pattern.java
 *
 * Created on May 30, 2004, 2:44 PM
 */

package com.javaop.util;

/**
 * Simple utility to convert username patterns into regular expressions.
 *
 * @author Ron - Home
 */
public class UsernameMatcherPattern
{
	/**
	 * Fixes the name to escape and replace characters.
	 *
	 * ? will match a single character
	 * % will match a single numeric character (0-9)
	 * * will match any number of characters
	 *
	 * @param str
	 *            The original string.
	 * @return Regular expression representation of the pattern argument.
	 */
	public static String fixPattern(String pattern)
	{
		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < pattern.length(); i++)
		{
			char thisChar = pattern.charAt(i);

			if (thisChar == '*') {
				ret.append(".*");
			} else if (thisChar == '?') {
				ret.append(".");
			} else if (thisChar == '%') {
				ret.append("[0-9]");
			} else if (!Character.isLetterOrDigit(thisChar)) {
				ret.append("\\").append(thisChar);
			} else {
				ret.append(pattern.charAt(i));
			}
		}
		return ret.toString().toLowerCase();
	}

}
