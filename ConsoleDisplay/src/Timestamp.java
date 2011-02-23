package com.javaop.ConsoleDisplay;

/*
 * Created on Dec 12, 2004 By iago
 */

/**
 * @author iago
 * 
 */

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
     * 
     * @return A String representation of the current time. It might look
     *         something like:<BR>
     * 
     *         <PRE>
     * &quot;[13:05:17.133] &quot;
     * </PRE>
     * 
     *         Note that it's always same same length, by padding the numbers.
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

    /**
     * Returns the current date nicely formatted.
     * 
     * @return a String representation of the current date. It might look
     *         something like:<BR>
     * 
     *         <PRE>
     * &quot;2004.03.27&quot;
     * </PRE>
     */
    public static String getDate()
    {
        Calendar c = Calendar.getInstance();

        StringBuffer s = new StringBuffer();
        s.append(PadString.padNumber(c.get(Calendar.YEAR), 4)).append(".");
        s.append(PadString.padNumber(c.get(Calendar.MONTH) + 1, 2)).append(".");
        s.append(PadString.padNumber(c.get(Calendar.DAY_OF_MONTH), 2));

        return s.toString();
    }

    public static void main(String args[])
    {
        System.out.println(getDate());
    }
}
