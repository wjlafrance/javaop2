/*
 * Created on Feb 17, 2005 By iago
 */

/**
 * @author iago
 * 
 */
public interface ClanConstants
{
    // 0x00: Success
    // 0x01: In use
    // 0x02: Too soon
    // 0x03: Not enough members
    // 0x04: Invitation was declined
    // 0x05: Decline
    // 0x06: Accept
    // 0x07: Not authorized
    // 0x08: User not found
    // 0x09: Clan is full
    // 0x0A: Bad tag
    // 0x0B: Bad name
    //
    // Rank Codes
    //
    // 0x00: Initiate
    // 0x01: Partial member
    // 0x02: Member
    // 0x03: Officer
    // 0x04: Leader
    public static byte     CLAN_SUCCESS          = 0x00;
    public static byte     CLAN_INUSE            = 0x01;
    public static byte     CLAN_TOOSOON          = 0x02;
    public static byte     CLAN_NOTENOUGHMEMBERS = 0x03;
    public static byte     CLAN_DECLINED         = 0x04;
    public static byte     CLAN_DECLINE          = 0x05;
    public static byte     CLAN_ACCEPT           = 0x06;
    public static byte     CLAN_NOTAUTHORIZED    = 0x07;
    public static byte     CLAN_USERNOTFOUND     = 0x08;
    public static byte     CLAN_FULL             = 0x09;
    public static byte     CLAN_BADTAG           = 0x0A;
    public static byte     CLAN_BADNAME          = 0x0B;

    public static String[] clanConstants         =
                                                 {
            "Success", "In use", "Too soon", "Not enough members", "Declined", "Decline", "Accept",
            "Not authorized", "User not found", "Full", "Bad tag", "Bad name" };

    public static byte     CLAN_MEMBER_INITIATE  = 0x00;
    public static byte     CLAN_MEMBER_PARTIAL   = 0x01;
    public static byte     CLAN_MEMBER_MEMBER    = 0x02;
    public static byte     CLAN_MEMBER_OFFICER   = 0x03;
    public static byte     CLAN_MEMBER_LEADER    = 0x04;

    public static String[] clanMemberConstants   =
                                                 {
            "Initiate (Peon)", "Partial member (Peon)", "Full member (Grunt)", "Officer (Shaman)",
            "Leader (Chieftan)"                 };
}
