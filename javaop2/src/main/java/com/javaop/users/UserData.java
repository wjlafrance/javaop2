/*
 * Users.java
 *
 * Created on March 18, 2004, 12:25 PM
 */
package com.javaop.users;

import com.javaop.util.User;

/**
 * This class stores a single user, including their icon and lag and the time
 * they joined the channel. It implements User, which is a public class and can
 * be safely given to other functions.
 */
class UserData implements User {
	private static final long serialVersionUID = 1L;
	private final int ping;
	private final String name;
	private final long joinTime;
	private final String prettyStatstring;
	private final String rawStatstring;
	private int flags;

	public UserData(String name, int ping, int flags, String stats) {
		this.name = name;
		this.ping = ping;
		this.flags = flags;
		this.joinTime = System.currentTimeMillis();
		this.rawStatstring = stats;
		this.prettyStatstring = stats;
	}

	public String toString() {
		if (prettyStatstring != null) {
			return String.format("%s (%dms, %s)", name, ping, prettyStatstring);
		}
		return String.format("%s (%dms)", name, ping);
	}

	public boolean equals(Object o) {
		if (o instanceof UserData && ((UserData) o).getName().equalsIgnoreCase(getName())) {
			return true;
		} else if (o instanceof String && ((String) o).equalsIgnoreCase(getName())) {
			return true;
		} else {
			return false;
		}
	}

	public int getPing() {
		return this.ping;
	}

	public String getName() {
		return this.name;
	}

	public long getJoinTime() {
		return this.joinTime;
	}

	public String getPrettyStatstring() {
		return this.prettyStatstring;
	}

	public String getRawStatstring() {
		return this.rawStatstring;
	}

	public int getFlags() {
		return this.flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}
}
