/*
 * Created on Aug 27, 2004 By iago
 */
package com.javaop.users;

import java.util.Vector;

import com.javaop.util.Pattern;
import com.javaop.util.User;


/**
 * @author iago
 *
 */
public class UserList
{
	private Vector users;

	public UserList()
	{
		users = new Vector();
	}

	public void clear()
	{
		users.clear();
	}

	public int size()
	{
		return users.size();
	}

	public User addUser(String name, int flags, int ping, String statstring)
	{
		User old = removeUser(name);
		User u = null;

		if (old == null)
		{
			u = new UserData(name, ping, flags, statstring);
		}
		else
		{
			u = new UserData(name, ping, flags, old.getRawStatstring());
		}
		users.add(u);

		return u;

	}

	public User removeUser(String name)
	{
		for (int i = 0; i < users.size(); i++) {
			if (((UserData) users.get(i)).equals(name)) {
				return (User) users.remove(i);
			}
		}

		return null;
	}

	public User getUser(String name)
	{
		for (Object user : users) {
			if (((UserData) user).equals(name)) {
				return (User) user;
			}
		}

		return null;
	}

	public String[] matchNames(String pattern)
	{
		Vector ret = new Vector();
		pattern = Pattern.fixPattern(pattern);

		for (Object user1 : users) {
			UserData user = (UserData) user1;

			if (user.getName().toLowerCase().matches(pattern)) {
				ret.add(user.getName());
			}
		}

		return (String[]) ret.toArray(new String[ret.size()]);
	}

	public String[] getList()
	{
		Vector ret = new Vector();
		for (Object user : users) {
			ret.add(((UserData) user).getName());
		}

		return (String[]) ret.toArray(new String[ret.size()]);
	}

}
