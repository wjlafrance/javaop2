package constants;
/*
 * Created on Dec 1, 2004
 * By iago
 */

/**
 * @author iago
 *
 */
public interface PacketConstants
{
    public static final byte SID_NULL                       = 0x00;
    public static final byte UNKNOWN_0x01					= 0x01;
    public static final byte SID_STOPADV                    = 0x02;
    public static final byte UNKNOWN_0x02					= 0x03;
    public static final byte SID_SERVERLIST                 = 0x04;
    public static final byte SID_CLIENTID                   = 0x05;
    public static final byte SID_STARTVERSIONING            = 0x06;
    public static final byte SID_REPORTVERSION              = 0x07;
    public static final byte UNKNOWN_0x08					= 0x08;
    public static final byte SID_GETADVLISTEX               = 0x09;
    public static final byte SID_ENTERCHAT                  = 0x0A;
    public static final byte SID_GETCHANNELLIST             = 0x0B;
    public static final byte SID_JOINCHANNEL                = 0x0C;
    public static final byte UNKNOWN_0x0D					= 0x0D;
    public static final byte SID_CHATCOMMAND                = 0x0E;
    public static final byte SID_CHATEVENT                  = 0x0F;
    public static final byte SID_LEAVECHAT                  = 0x10;
    public static final byte UNKNOWN_0x11					= 0x11;
    public static final byte SID_LOCALEINFO                 = 0x12;
    public static final byte SID_FLOODDETECTED              = 0x13;
    public static final byte SID_UDPPINGRESPONSE            = 0x14;
    public static final byte SID_CHECKAD                    = 0x15;
    public static final byte SID_CLICKAD                    = 0x16;
    public static final byte UNKNOWN_0x17					= 0x17;
    public static final byte UNKNOWN_0x18					= 0x18;
    public static final byte SID_MESSAGEBOX                 = 0x19;
    public static final byte UNKNOWN_0x1A					= 0x1A;
    public static final byte UNKNOWN_0x1B					= 0x1B;
    public static final byte SID_STARTADVEX3                = 0x1C;
    public static final byte SID_LOGONCHALLENGEEX           = 0x1D;
    public static final byte SID_CLIENTID2                  = 0x1E;
    public static final byte UNKNOWN_0x1F					= 0x1F;
    public static final byte SID_BROADCAST                  = 0x20;
    public static final byte SID_DISPLAYAD                  = 0x21;
    public static final byte SID_NOTIFYJOIN                 = 0x22;
    public static final byte UNKNOWN_0x23					= 0x23;
    public static final byte UNKNOWN_0x24					= 0x24;
    public static final byte SID_PING                       = 0x25;
    public static final byte SID_READUSERDATA               = 0x26;
    public static final byte SID_WRITEUSERDATA              = 0x27;
    public static final byte SID_LOGONCHALLENGE             = 0x28;
    public static final byte SID_LOGONRESPONSE              = 0x29;
    public static final byte SID_CREATEACCOUNT              = 0x2A;
    public static final byte UNKNOWN_0x2B					= 0x2B;
    public static final byte SID_GAMERESULT                 = 0x2C;
    public static final byte SID_GETICONDATA                = 0x2D;
    public static final byte SID_GETLADDERDATA              = 0x2E;
    public static final byte SID_FINDLADDERUSER             = 0x2F;
    public static final byte SID_CDKEY                      = 0x30;
    public static final byte SID_CHANGEPASSWORD             = 0x31;
    public static final byte UNKNOWN_0x32					= 0x32;
    public static final byte SID_GETFILETIME                = 0x33;
    public static final byte SID_QUERYREALMS                = 0x34;
    public static final byte SID_PROFILE					= 0x35;
    public static final byte SID_CDKEY2                     = 0x36;
    public static final byte UNKNOWN_0x37					= 0x37;
    public static final byte UNKNOWN_0x38					= 0x38;
    public static final byte UNKNOWN_0x39					= 0x39;
    public static final byte SID_LOGONRESPONSE2             = 0x3A;
    public static final byte UNKNOWN_0x3B					= 0x3B;
    public static final byte SID_CHECKDATAFILE2             = 0x3C;
    public static final byte SID_CREATEACCOUNT2             = 0x3D;
    public static final byte SID_LOGONREALMEX               = 0x3E;
    public static final byte SID_STARTVERSIONING2           = 0x3F;
    public static final byte SID_QUERYREALMS2               = 0x40;
    public static final byte SID_QUERYADURL                 = 0x41;
    public static final byte UNKNOWN_0x42					= 0x42;
    public static final byte UNKNOWN_0x43					= 0x43;
    public static final byte UNKNOWN_0x44					= 0x44;
    public static final byte SID_NETGAMEPORT                = 0x45;
    public static final byte SID_NEWS_INFO                  = 0x46;
    public static final byte UNKNOWN_0x47					= 0x47;
    public static final byte UNKNOWN_0x48					= 0x48;
    public static final byte UNKNOWN_0x49					= 0x49;
    public static final byte SID_OPTIONALWORK               = 0x4A;
    public static final byte UNKNOWN_0x4B					= 0x4B;
    public static final byte SID_REQUIREDWORK				= 0x4C;
    public static final byte UNKNOWN_0x4D					= 0x4D;
    public static final byte UNKNOWN_0x4E					= 0x4E;
    public static final byte UNKNOWN_0x4F					= 0x4F;
    public static final byte SID_AUTH_INFO                  = 0x50;
    public static final byte SID_AUTH_CHECK                 = 0x51;
    public static final byte SID_AUTH_ACCOUNTCREATE			= 0x52;
    public static final byte SID_AUTH_ACCOUNTLOGON			= 0x53;
    public static final byte SID_AUTH_ACCOUNTLOGONPROOF		= 0x54;
    public static final byte SID_AUTH_ACCOUNTCHANGE			= 0x55;
    public static final byte SID_AUTH_ACCOUNTCHANGEPROOF	= 0x56;
    public static final byte SID_AUTH_ACCOUNTUPGRADE		= 0x57;
    public static final byte SID_AUTH_ACCOUNTUPGRADEPROOF	= 0x58;
    public static final byte SID_SETEMAIL   				= 0x59;
    public static final byte SID_RESETPASSWORD      		= 0x5A;
    public static final byte UNKNOWN_0x5B					= 0x5B;
    public static final byte UNKNOWN_0x5C					= 0x5C;
    public static final byte UNKNOWN_0x5D					= 0x5D;
    public static final byte UNKNOWN_0x5E					= 0x5E;
    public static final byte UNKNOWN_0x5F					= 0x5F;
    public static final byte UNKNOWN_0x60					= 0x60;
    public static final byte UNKNOWN_0x61					= 0x61;
    public static final byte UNKNOWN_0x62					= 0x62;
    public static final byte UNKNOWN_0x63					= 0x63;
    public static final byte UNKNOWN_0x64					= 0x64;
    public static final byte SID_FRIENDLIST                 = 0x65;
    public static final byte SID_FRIENDUPDATE               = 0x66;
    public static final byte SID_FRIENDADDED                = 0x67;
    public static final byte SID_FRIENDREMOVED              = 0x68;
    public static final byte SID_FRIENDMOVED                = 0x69;
    public static final byte UNKNOWN_0x6A					= 0x6A;
    public static final byte UNKNOWN_0x6B					= 0x6B;
    public static final byte UNKNOWN_0x6C					= 0x6C;
    public static final byte UNKNOWN_0x6D					= 0x6D;
    public static final byte UNKNOWN_0x6E					= 0x6E;
    public static final byte UNKNOWN_0x6F					= 0x6F;
    public static final byte SID_FINDCLANCANDIDATES         = 0x70;
    public static final byte SID_INVITEMULTIPLEUSERS        = 0x71;
    public static final byte UNKNOWN_0x72					= 0x72;
    public static final byte SID_DISBANDCLAN                = 0x73;
    public static final byte UNKNOWN_0x74					= 0x74;
    public static final byte SID_CLANINFO                   = 0x75;
    public static final byte SID_CLANQUITNOTIFY				= 0x76;
    public static final byte SID_CLANREQUEST                = 0x77;
    public static final byte UNKNOWN_0x78					= 0x78;
    public static final byte SID_CLANINVITE                 = 0x79;
    public static final byte UNKNOWN_0x7A					= 0x7A;
    public static final byte SID_SETMOTD                    = 0x7B;
    public static final byte SID_CLANMOTD                   = 0x7C;
    public static final byte SID_CLANMEMBERLIST             = 0x7D;
    public static final byte UNKNOWN_0x7E					= 0x7E;
    public static final byte SID_CLANMEMBERUPDATE           = 0x7F;
    
    public final String []packetConstants = {
            "SID_NULL", 
            "UNKNOWN_0x01", 
            "SID_STOPADV", 
            "UNKNOWN_0x02", 
            "SID_SERVERLIST", 
            "SID_CLIENTID", 
            "SID_STARTVERSIONING", 
            "SID_REPORTVERSION", 
            "UNKNOWN_0x08", 
            "SID_GETADVLISTEX", 
            "SID_ENTERCHAT", 
            "SID_GETCHANNELLIST", 
            "SID_JOINCHANNEL", 
            "UNKNOWN_0x0D", 
            "SID_CHATCOMMAND", 
            "SID_CHATEVENT", 
            "SID_LEAVECHAT", 
            "UNKNOWN_0x11", 
            "SID_LOCALEINFO", 
            "SID_FLOODDETECTED", 
            "SID_UDPPINGRESPONSE", 
            "SID_CHECKAD", 
            "SID_CLICKAD", 
            "UNKNOWN_0x17", 
            "UNKNOWN_0x18", 
            "SID_MESSAGEBOX", 
            "UNKNOWN_0x1A", 
            "UNKNOWN_0x1B", 
            "SID_STARTADVEX3", 
            "SID_LOGONCHALLENGEEX", 
            "SID_CLIENTID2", 
            "UNKNOWN_0x1F", 
            "SID_BROADCAST", 
            "SID_DISPLAYAD", 
            "SID_NOTIFYJOIN", 
            "UNKNOWN_0x23", 
            "UNKNOWN_0x24", 
            "SID_PING", 
            "SID_READUSERDATA", 
            "SID_WRITEUSERDATA", 
            "SID_LOGONCHALLENGE", 
            "SID_LOGONRESPONSE", 
            "SID_CREATEACCOUNT", 
            "UNKNOWN_0x2B", 
            "SID_GAMERESULT", 
            "SID_GETICONDATA", 
            "SID_GETLADDERDATA", 
            "SID_FINDLADDERUSER", 
            "SID_CDKEY", 
            "SID_CHANGEPASSWORD", 
            "UNKNOWN_0x32", 
            "SID_GETFILETIME", 
            "SID_QUERYREALMS", 
            "UNKNOWN_0x35", 
            "SID_CDKEY2", 
            "UNKNOWN_0x37", 
            "UNKNOWN_0x38", 
            "UNKNOWN_0x39", 
            "SID_LOGONRESPONSE2", 
            "UNKNOWN_0x3B", 
            "SID_CHECKDATAFILE2", 
            "SID_CREATEACCOUNT2", 
            "SID_LOGONREALMEX", 
            "SID_STARTVERSIONING2", 
            "SID_QUERYREALMS2", 
            "SID_QUERYADURL", 
            "UNKNOWN_0x42", 
            "UNKNOWN_0x43", 
            "UNKNOWN_0x44", 
            "SID_NETGAMEPORT", 
            "SID_NEWS_INFO", 
            "UNKNOWN_0x47", 
            "UNKNOWN_0x48", 
            "UNKNOWN_0x49", 
            "UNKNOWN_0x4A", 
            "UNKNOWN_0x4B", 
            "UNKNOWN_0x4C", 
            "UNKNOWN_0x4D", 
            "UNKNOWN_0x4E", 
            "UNKNOWN_0x4F", 
            "SID_AUTH_INFO", 
            "SID_AUTH_CHECK", 
            "SID_AUTH_ACCOUNTCREATE", 
            "SID_AUTH_ACCOUNTLOGON", 
            "SID_AUTH_ACCOUNTLOGONPROOF", 
            "SID_AUTH_ACCOUNTCHANGE", 
            "SID_AUTH_ACCOUNTCHANGEPROOF", 
            "SID_AUTH_ACCOUNTUPGRADE", 
            "SID_AUTH_ACCOUNTUPGRADEPROOF", 
            "SID_SETEMAIL", 
            "SID_RESETPASSWORD", 
            "UNKNOWN_0x5B", 
            "UNKNOWN_0x5C", 
            "UNKNOWN_0x5D", 
            "UNKNOWN_0x5E", 
            "UNKNOWN_0x5F", 
            "UNKNOWN_0x60", 
            "UNKNOWN_0x61", 
            "UNKNOWN_0x62", 
            "UNKNOWN_0x63", 
            "UNKNOWN_0x64", 
            "SID_FRIENDLIST", 
            "SID_FRIENDUPDATE", 
            "SID_FRIENDADDED", 
            "SID_FRIENDREMOVED", 
            "SID_FRIENDMOVED", 
            "UNKNOWN_0x6A", 
            "UNKNOWN_0x6B", 
            "UNKNOWN_0x6C", 
            "UNKNOWN_0x6D", 
            "UNKNOWN_0x6E", 
            "UNKNOWN_0x6F", 
            "SID_FINDCLANCANDIDATES", 
            "SID_INVITEMULTIPLEUSERS", 
            "UNKNOWN_0x72", 
            "SID_DISBANDCLAN", 
            "UNKNOWN_0x74", 
            "SID_CLANINFO", 
            "SID_CLANQUITNOTIFY", 
            "SID_CLANREQUEST", 
            "UNKNOWN_0x78", 
            "SID_CLANINVITE", 
            "UNKNOWN_0x7A", 
            "SID_SETMOTD", 
            "SID_CLANMOTD", 
            "SID_CLANMEMBERLIST", 
            "UNKNOWN_0x7E", 
            "SID_CLANMEMBERUPDATE"
    };
}
