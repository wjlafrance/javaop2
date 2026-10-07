/*
 * Created on Jan 16, 2005 By iago
 */
package com.javaop.util;

import java.util.ArrayList;
import java.util.List;


/**
 * @author iago
 *
 */
public class Splitter
{
	private static final int maxLength = 100;

	public static List<String> split(String str, boolean moreTag)
	{
		List<String> ret = new ArrayList<>();

		String[] allWords = str.split(" ");
		// padLength is the amount of extra space on each line
		int padLength = 1 + ((moreTag && allWords.length < maxLength) ? 6 : 0);

		// Sanity check -- make sure that no single word will break this
		for (String allWord : allWords) {
			if (allWord.length() + padLength > maxLength) {
				ret.add(str);
				return ret;
			}
		}

		int i = 0;

		while (i < allWords.length)
		{
			String currentLine = "";

			while (i < allWords.length
					&& (currentLine.length() + allWords[i].length() + padLength) < maxLength)
			{
				currentLine = currentLine + allWords[i] + " ";
				i++;
			}

			ret.add(currentLine + ((moreTag && i < allWords.length) ? "<more>" : ""));

		}

		return ret;
	}
}
