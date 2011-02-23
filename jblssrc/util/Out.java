/*
 * Created on Feb 20, 2005
 */
package util;

/**
 *
 * This class controls output from the server, specifying where it goes
 *
 * Currently supports 4 "types" of output, but the system I use now sucks
 * and the formatting is ugly.
 */

import java.io.PrintStream;
import java.util.Calendar;

public class Out {

	private static PrintStream outStream;

    static
    {
        //System.out.println(new Date() + "");
    }

	/**
	 @param text: Text to be send to the output stream directly(no formatting)*/
	public static void print(String text)
	{
		outStream.print(text);
	}

	/**@param source: source of the info
	//@param text:text to show*/
	public static void println(String source, String text)
	{
		outStream.println(getTimestamp() + "{" + source + "} " + text);
	}

	/**Displays errors
	//@param source: source of the info
	//@param text: text to show*/
	public static void error(String source, String text)
	{
		outStream.println(getTimestamp() + "{" + source + " - Error} " + text);
	}

	/**Displays debug information, if wanted
	//@param source - source of the info
	//@param text -text to show*/
	public static void debug(String source, String text)
	{
		if(Constants.debugInfo)
			outStream.println(getTimestamp() + "{" + source + " - Debug} " + text);
	}

	/**Displays "info"
	@param source - source of the info
	@param text -text to show*/
	public static void info(String source, String text)
	{
		outStream.println(getTimestamp() + "{" + source + "} " + text);
	}

	/**Sets the output stream for the information to be displayed to.
	Can be set to system.out, admin output stream, file logging, etc..
	@param s
	PrintStream to send information to.
	*/
	public static void setOutputStream(PrintStream s)
	{
		outStream=s;
		if(outStream==null)
			setDefaultOutputStream();
	}

	/**Sets the default PrintStream, currently system.out*/
	public static void setDefaultOutputStream()
	{
		outStream=System.out;
	}

	/**
	 * Simply displays a nicely formatted timestamp.
	 *
	 * @author Ron
	 * -Fool Change: Moved in here instead of own TimeStamp Class
	 *     Don't really know why, but I did
	 */
	public static String getTimestamp()
    {
        Calendar c = Calendar.getInstance();

        StringBuffer s = new StringBuffer();
        s.append('[');
        s.append(PadString.padNumber(c.get(Calendar.HOUR_OF_DAY), 2)).append(':');
        s.append(PadString.padNumber(c.get(Calendar.MINUTE), 2)).append(':');
        s.append(PadString.padNumber(c.get(Calendar.SECOND), 2)).append('.');
        s.append(PadString.padNumber(c.get(Calendar.MILLISECOND), 3));
        s.append("] ");

        return s.toString();
    }
	public static String getDatestamp()
    {
        Calendar c = Calendar.getInstance();
        StringBuffer s = new StringBuffer();
        s.append(PadString.padNumber(c.get(Calendar.MONTH), 2)).append("/");
        s.append(PadString.padNumber(c.get(Calendar.DAY_OF_MONTH), 2)).append("/");
        s.append(c.get(Calendar.YEAR)).append(" ");
        s.append(PadString.padNumber(c.get(Calendar.HOUR_OF_DAY), 2)).append(':');
        s.append(PadString.padNumber(c.get(Calendar.MINUTE), 2)).append(':');
        s.append(PadString.padNumber(c.get(Calendar.SECOND), 2)).append('.');
        s.append(PadString.padNumber(c.get(Calendar.MILLISECOND), 3));

        return s.toString();
    }

}
