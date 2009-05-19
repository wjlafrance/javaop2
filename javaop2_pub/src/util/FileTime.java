/*
 * Time.java
 *
 * Created on March 4, 2004, 3:12 PM
 */

package util;

/**
 * This class simplifies conversion from Java's date format (64 bit, milliseconds since Jan 1, 1970) 
 * to windows FILETIME (64-bit, nanoseconds since Jan 1, 1600).  
 *
 * @author  iago
 */
public class FileTime
{
    /**
     * Convert windows 64-bit FILETIME to Java's 64-bit date.
     * @param fileTime The FILETIME to convert.
     * @return the Java-time equivalent.
     */
    public static long fileTimeToMillis(long fileTime)
    {
        return (fileTime / 10000L) - 11644473600000L;
    }
    
    public static long fileTimeToMillis(String ft1, String ft2)
    {
        long l = Long.parseLong(ft1) << 32;
        l |= Long.parseLong(ft2);
        
        return fileTimeToMillis(l);
    }
    
    /**
     * Convert Java's 64-bit date to windows' 64-bit FILETIME.
     * @param millis The Java-time to convert.
     * @return The FILETIME equivalent.
     */
    public static long millisToFileTime(long millis)
    {
        return (millis + 11644473600000L) * 10000L;
    }
}
