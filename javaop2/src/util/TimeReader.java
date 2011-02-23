/*
 * Created on Aug 2, 2004
 * 
 * By iago
 */
package com.javaop.util;

public class TimeReader
{
    public final static long SECOND = 1000;
    public final static long MINUTE = SECOND * 60;
    public final static long HOUR   = MINUTE * 60;
    public final static long DAY    = HOUR * 24;

    public static long stringToTime(String time) throws IllegalArgumentException
    {
        if (time == null || time.length() < 1)
            return 0;

        time = time.toLowerCase();

        char lastChar = time.charAt(time.length() - 1);

        if (Character.isDigit(lastChar))
        {
            try
            {
                return Long.parseLong(time) * SECOND;
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException(
                        "Invalid absolute time specified.  Time must be all in numbers.");
            }
        }

        time = time.substring(0, time.length() - 1);

        if (time.length() == 0)
            return 0;

        long baseTime = Long.parseLong(time);

        if (lastChar == 's')
            return baseTime * SECOND;
        else if (lastChar == 'm')
            return baseTime * MINUTE;
        else if (lastChar == 'h')
            return baseTime * HOUR;
        else if (lastChar == 'd')
            return baseTime * DAY;

        throw new IllegalArgumentException(
                "Invalid time units.  Valid units are D=Days, M=Minutes, H=Hours, S=Seconds");
    }

    public static String timeToString(long time)
    {
        if (time == 0)
            return "0ms";

        long days = time / DAY;
        time = time % DAY;

        long hours = time / HOUR;
        time = time % HOUR;

        long minutes = time / MINUTE;
        time = time % MINUTE;

        long seconds = time / SECOND;
        time = time % SECOND;

        StringBuffer s = new StringBuffer();

        s.append(days > 0 ? days + " day" + (days == 1 ? "" : "s") + ", " : "");
        s.append(hours > 0 ? hours + " hour" + (hours == 1 ? "" : "s") + ", " : "");
        s.append(minutes > 0 ? minutes + " minute" + (minutes == 1 ? "" : "s") + ", " : "");
        s.append(seconds + " second" + (seconds == 1 ? "" : "s"));
        // s.append(time > 0 ? time + "ms" : "");

        return s.toString();
    }

    public static void main(String args[])
    {
        System.out.println(stringToTime("1000"));
        System.out.println(stringToTime("10s"));
        System.out.println(stringToTime("15m"));
        System.out.println(stringToTime("20d"));

        System.out.println(timeToString(stringToTime("1000")));
        System.out.println(timeToString(stringToTime("10s")));
        System.out.println(timeToString(stringToTime("15m")));
        System.out.println(timeToString(stringToTime("20d")));
        System.out.println(timeToString(stringToTime("20d") + stringToTime("15m")
                + stringToTime("10s")));
    }
}
