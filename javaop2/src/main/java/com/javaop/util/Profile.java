package com.javaop.util;

import java.util.Hashtable;

import com.javaop.constants.PacketConstants;

import com.javaop.util.BnetPacket;


/*
 * Created on Feb 18, 2005 By iago
 */

/**
 * @author iago
 *
 */
public class Profile
{
	private static final Hashtable users    = new Hashtable();
	private static final Hashtable requests = new Hashtable();

	public static BnetPacket getProfileRequest(int profileCookie, String user, String[] fields)
	{
		BnetPacket packet = new BnetPacket(PacketConstants.SID_READUSERDATA);

		// (DWORD) Number of Accounts
		packet.add(1);
		// (DWORD) Number of Keys
		packet.add(fields.length);
		// (DWORD) Request ID
		packet.add(profileCookie);
		// (STRING[]) Requested Accounts
		packet.addNTString(user);
		// (STRING[]) Requested Keys
		for (String field : fields) {
			packet.addNTString(field);
		}

		users.put("request-" + profileCookie, user);
		requests.put("request-" + profileCookie, fields);

		return packet;
	}

	public static Hashtable processProfileRequest(int profileCookie, BnetPacket profile)
	{
		// (DWORD) Number of accounts
		if (profile.removeDWord() != 1) {
			return null;
		}
		// (DWORD) Number of keys
		int keys = profile.removeDWord();

		// (DWORD) Request ID
		if (profile.removeDWord() != profileCookie) {
			return null;
		}

		String user = (String) users.remove("request-" + profileCookie);
		String[] fields = (String[]) requests.remove("request-" + profileCookie);

		if (user == null || fields == null) {
			return null;
		}

		if (fields.length != keys) {
			return null;
		}

		// (STRING[]) Requested Key Values
		Hashtable h = new Hashtable();
		h.put("username", user);
		for (int i = 0; i < keys; i++) {
			h.put(fields[i], profile.removeNTString());
		}

		return h;
	}
}
