package constants;

/*
 * Created on Dec 1, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public interface EventConstants
{
    public static final byte EID_SHOWUSER            = 0x01;
    public static final byte EID_JOIN                = 0x02;
    public static final byte EID_LEAVE               = 0x03;
    public static final byte EID_WHISPER             = 0x04;
    public static final byte EID_TALK                = 0x05;
    public static final byte EID_BROADCAST           = 0x06;
    public static final byte EID_CHANNEL             = 0x07;
    public static final byte EID_USERFLAGS           = 0x09;
    public static final byte EID_WHISPERSENT         = 0x0A;
    public static final byte EID_CHANNELFULL         = 0x0D;
    public static final byte EID_CHANNELDOESNOTEXIST = 0x0E;
    public static final byte EID_INFO                = 0x12;
    public static final byte EID_ERROR               = 0x13;
    public static final byte EID_EMOTE               = 0x17;
    public static final byte MAX_EVENT               = 0x17;

    public final String[]    eventConstants          =
                                                     {
            "Unknown (0)", "EID_SHOWUSER", "EID_JOIN", "EID_LEAVE", "EID_WHISPER", "EID_TALK",
            "EID_BROADCAST", "EID_CHANNEL", "Unknown (8)", "EID_USERFLAGS", "EID_WHISPERSENT",
            "Unknown (B)", "Unknown (C)", "EID_CHANNELFULL", "EID_CHANNELDOESNOTEXIST",
            "Unknown (F)", "Unknown (10)", "Unknown (11)", "EID_INFO", "EID_ERROR", "Unknown (14)",
            "Unknown (15)", "Unknown (16)", "EID_EMOTE" };
}
