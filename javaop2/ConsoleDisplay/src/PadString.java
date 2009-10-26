/*
 * PadString.java
 * 
 * Created on March 9, 2004, 3:25 PM
 */

/**
 * Since Java has no utility (that I could find) for converting an integer to a
 * String and padding it with the appropriate number of 0's, I wrote this class
 * that takes care of that for me.
 * 
 * @author iago
 */
public class PadString
{
    /**
     * Pads a number with 0's up to the requested length.
     * 
     * @param number
     *            The number we're going to pad.
     * @param length
     *            The length to pad it to.
     */
    static public String padNumber(int number, int length)
    {
        return padString("" + number, length, '0');
    }

    /**
     * Converts the number to hex, then pads it with 0's up to the requested
     * length.
     * 
     * @param number
     *            The number to convert to hex and pad.
     * @param length
     *            The length to pad it to.
     */
    static public String padHex(int number, int length)
    {
        return padString(Integer.toHexString(number), length, '0');
    }

    /**
     * Pads the requested string to the requested length with the specified
     * character. For example, this:<BR>
     * 
     * <PRE>
     * System.out.println(PadString.padString(&quot;TEST&quot;, 8, '/');
     * </PRE>
     * 
     * would display:
     * 
     * <PRE>
     * // //TEST
     * </PRE>
     * 
     * @param str
     *            The string to pad.
     * @param length
     *            The length to pad it to.
     * @param c
     *            The character to pad it with.
     */
    static public String padString(String str, int length, char c)
    {
        while (str.length() < length)
            str = c + str;

        return str;
    }

}