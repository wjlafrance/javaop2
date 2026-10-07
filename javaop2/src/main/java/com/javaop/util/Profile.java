package com.javaop.util;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
	private static final Map<String, String> users    = new ConcurrentHashMap<>();
	private static final Map<String, String[]> requests = new ConcurrentHashMap<>();

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

	public static Map<String, String> processProfileRequest(int profileCookie, BnetPacket profile)
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

		String user = users.remove("request-" + profileCookie);
		String[] fields = requests.remove("request-" + profileCookie);

		if (user == null || fields == null) {
			return null;
		}

		if (fields.length != keys) {
			return null;
		}

		// (STRING[]) Requested Key Values
		Map<String, String> h = new HashMap<>();
		h.put("username", user);
		for (int i = 0; i < keys; i++) {
			h.put(fields[i], profile.removeNTString());
		}

		return h;
	}
}
