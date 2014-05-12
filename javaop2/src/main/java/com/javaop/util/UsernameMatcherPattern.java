/*
 * Pattern.java
 *
 * Created on May 30, 2004, 2:44 PM
 */

package com.javaop.util;

import java.util.stream.Collectors;

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
	 * @param pattern The username pattern to convert.
	 * @return Regular expression representation of the pattern argument.
	 */
	public static String fixPattern(String pattern)
	{
		return pattern.chars().mapToObj(c -> {
			switch (c) {
				case '*': return ".*";
				case '?': return ".";
				case '%': return "[0-9]";
				default: return String.format(Character.isLetterOrDigit(c) ? "%c" : "\\%c", c);
			}
		}).collect(Collectors.joining()).toLowerCase();
	}

}
