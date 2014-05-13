/*
 * Users.java
 *
 * Created on March 18, 2004, 12:25 PM
 */

package com.javaop.users;

import com.javaop.util.User;
import lombok.Getter;
import lombok.Setter;


/**
 * This class stores a single user, including their icon and lag and the time
 * they joined the channel. It implements User, which is a public class and can
 * be safely given to other functions.
 */
class UserData implements User
{
	private static final long serialVersionUID = 1L;

	private final @Getter int ping;
	private final @Getter String name;

	private final @Getter String prettyStatstring;
	private final @Getter String rawStatstring;

	private @Getter @Setter int flags;

	private final long joinTime;

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
			return name + "(" + ping + "ms, " + prettyStatstring + ")";
		}
		return name + "(" + ping + "ms)";
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

	@Override public long getJoinTime() {
		return joinTime;
	}
}
