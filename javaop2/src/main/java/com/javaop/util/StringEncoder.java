/*
 * Created on Mar 25, 2005 By iago
 */
package com.javaop.util;

public class StringEncoder
{
	public static String encode(Object o)
	{
		StringBuffer ret = new StringBuffer();

		if (o == null)
			return "&null;";

		if (o instanceof String)
		{
			String str = (String) o;
			for (int i = 0; i < str.length(); i++)
			{
				char thisChar = str.charAt(i);

				if (thisChar == '&')
				{
					ret.append("&amp;");
				}
				else if (thisChar >= 0x20 && thisChar <= 0x7F)
				{
					ret.append(thisChar);
				}
				else
				{
					ret.append("&").append((int) thisChar).append(";");
				}
			}

			return ret.toString();
		}

		return null;
	}

	public static Object decode(String str)
	{
		StringBuffer ret = new StringBuffer();

		if (str.matches("&null;"))
			return null;

		if (!str.matches(".*&[0-9]+;.*"))
			return str.replaceAll("&amp;", "&");

		for (; str.length() > 0; str = str.substring(1))
		{
			if (str.matches("&amp;.*"))
			{
				ret.append("&");
				str = str.replaceAll("^&amp", "");
			}
			else if (str.matches("\\&[0-9]+;.*"))
			{
				str = str.substring(1);
				int code = Integer.parseInt(str.replaceAll(";.*", ""));
				ret.append((char) code);
				str = str.replaceAll("^[0-9]*", "");
			}
			else
			{
				ret.append(str.charAt(0));
			}
		}

		return ret.toString();
	}

	public static void main(String[] args)
	{
		System.out.println(decode(encode("This & that\n")));
		// System.out.println(decode(encode(decode(encode("This is a string & this is another string~\n")))));
	}
}
