/*
 * Created on Jan 18, 2005 By iago
 */
package com.javaop.constants;

/**
 * @author iago
 *
 */
public interface Flags
{
	public static final int CHANNEL_PUBLIC          = 0x01;
	public static final int CHANNEL_MODERATED       = 0x02;
	public static final int CHANNEL_RESTRICTED      = 0x04;
	public static final int CHANNEL_SILENT          = 0x08;
	public static final int CHANNEL_SYSTEM          = 0x10;
	public static final int CHANNEL_PRODUCTSPECIFIC = 0x20;
	public static final int CHANNEL_GLOBAL          = 0x1000;

	public static final int USER_BLIZZREP           = 0x01;
	public static final int USER_CHANNELOP          = 0x02;
	public static final int USER_SPEAKER            = 0x04;
	public static final int USER_ADMIN              = 0x08;
	public static final int USER_NOUDP              = 0x10;
	public static final int USER_SQUELCHED          = 0x20;
	public static final int USER_GUEST              = 0x40;
	public static final int USER_BEEPENABLED        = 0x100;
	public static final int USER_PGLPLAYER          = 0x200;
	public static final int USER_PGLOFFICIAL        = 0x400;
	public static final int USER_KBKPLAYER          = 0x800;
	public static final int USER_KBKOFFICIAL        = 0x1000;
	public static final int USER_JAILED             = 0x100000;
	public static final int USER_GFPLAYER           = 0x200000;
}
