/*
 * BNetBuffer.java
 *
 * Created on March 2, 2004, 11:59 AM
 */

package util;

/**
 * This is a Buffer specifically written for battle.net packets. When
 * getPacket() is called, it adds "FF [code] [size]" to the front before
 * returning.
 *
 * @author iago
 */
public class BnetPacket extends Buffer
{
    /** The packet's code. */
    protected byte code;

    /** Initializes the variables to default value. */
    public BnetPacket()
    {
        super();
    }

    /**
     * Initializes the variables and sets the packet code.
     *
     * @param code
     *            The packet code.
     */
    public BnetPacket(byte code)
    {
        super();
        this.setCode(code);
    }

    /**
     * Initializes the buffer's contents and code to match another buffer's.
     *
     * @param old
     *            The buffer to copy.
     */
    public BnetPacket(BnetPacket old)
    {
        super();

        addBytes(old.getRawBuffer());
        this.code = old.code;
    }

    /**
     * Set the packet's code. This is probably unnecessary if you're using the
     * constructor that sets it.
     *
     * @param code
     *            Sets the packet's code.
     */
    public void setCode(byte code)
    {
        this.code = code;
    }

    /**
     * Returns the code that was set for this packet.
     *
     * @return The packet's code.
     */
    public byte getCode()
    {
        return code;
    }

    /**
     * Returns the entire buffer as an array of bytes.
     *
     * @return An array of bytes representing the buffer.
     */
    public byte[] getBytes()
    {
        byte[] ret = new byte[size()];

        System.arraycopy(super.getBytes(), 0, ret, 4, size() - 4);
        ret[0] = (byte) -1;
        ret[1] = code;
        ret[2] = (byte) ((ret.length >> 0) & 0x000000FF);
        ret[3] = (byte) ((ret.length >> 8) & 0x000000FF);

        return ret;
    }

    /**
     * Returns the buffer without its header
     *
     * @return The array of bytes representing the buffer without the 4 header
     *         bytes, used for copying.
     */
    public byte[] getRawBuffer()
    {
        return super.getBytes();
    }

    /**
     * Returns the full size of the buffer, including the four header bytes.
     *
     * @return The size of the buffer.
     */
    public int size()
    {
        return super.size() + 4;
    }

    /**
     * Displays a String representation of the Buffer. See
     * {@link bot.util.Buffer } for more information.
     *
     * @return The human-readable String representation of the buffer.
     */
    public String toString()
    {
        byte[] buffer = getBytes();

        StringBuffer returnString = new StringBuffer((size() * 3) + // The hex
                (size()) + // The ascii
                (size() / 4) + // The tabs/\n's
                30); // The text

        returnString.append("Buffer contents:\n");
        int i, j; // Loop variables
        for (i = 0; i < size(); i++)
        {
            if ((i != 0) && (i % 16 == 0))
            {
                // If it's a multiple of 16 and i isn't null, show the ascii
                returnString.append('\t');
                for (j = i - 16; j < i; j++)
                {
                    if (buffer[j] < 0x20 || buffer[j] > 0x7F)
                        returnString.append('.');
                    else
                        returnString.append((char) buffer[j]);
                }
                // Add a linefeed after the string
                returnString.append("\n");
            }

            returnString.append(Integer.toString((buffer[i] & 0xF0) >> 4, 16)
                    + Integer.toString((buffer[i] & 0x0F) >> 0, 16));
            returnString.append(' ');
        }

        // Add padding spaces if it's not a multiple of 16
        if (i != 0 && i % 16 != 0)
        {
            for (j = 0; j < ((16 - (i % 16)) * 3); j++)
            {
                returnString.append(' ');
            }
        }
        // Add the tab for alignment
        returnString.append('\t');

        // Add final chararacters at right, after padding

        // If it was at the end of a line, print out the full line
        if (i > 0 && (i % 16) == 0)
        {
            j = i - 16;
        }
        else
        {
            j = (i - (i % 16));
        }

        for (; i >= 0 && j < i; j++)
        {
            if (buffer[j] < 0x20 || buffer[j] > 0x7F)
                returnString.append('.');
            else
                returnString.append((char) buffer[j]);
        }

        // Finally, tidy it all up with a newline
        returnString.append('\n');
        returnString.append("Length: " + size() + '\n');

        return returnString.toString();
    }

    public boolean equals(Object o)
    {
        if (o instanceof BnetPacket == false)
            return false;

        BnetPacket packet = (BnetPacket) o;

        if (packet.getCode() != getCode())
            return false;

        byte[] thisPacket = packet.getBytes();
        byte[] thatPacket = getBytes();

        if (thisPacket.length != thatPacket.length)
            return false;

        for (int i = 0; i < thisPacket.length; i++)
            if (thisPacket[i] != thatPacket[i])
                return false;

        return true;
    }
}
