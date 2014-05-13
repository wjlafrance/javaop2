/*
 * UserDB.java
 *
 * Created on April 27, 2004, 11:16 AM
 */

package com.javaop.util;

import java.io.File;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Vector;

import com.javaop.util.PersistantMap;


public class UserDB
{
	private PersistantMap userDB;

	public UserDB(File filename)
	{
		userDB = new PersistantMap(filename,
				"User database file.  All lines are name=FLAGS, ensuring that FLAGS are all uppercase.");
	}

	public void addFlag(String user, char flag)
	{
		if (user == null) {
			return;
		}
		flag = Character.toUpperCase(flag);
		user = user.toLowerCase();

		String current = userDB.getNoWrite(null, user, "");

		if (current.length() == 0) {
			userDB.set(null, user, flag + "");
		} else if (!(current.indexOf(flag) >= 0))
		{
			current = current + flag;
			char[] currentArray = current.toCharArray();
			Arrays.sort(currentArray);
			current = new String(currentArray);
			userDB.set(null, user, current);
		}
		else
		{
			// he already has the flag, who cares?
		}
	}

	public void addFlags(String user, String flags)
	{
		if (user == null) {
			return;
		}

		for (int i = 0; i < flags.length(); i++) {
			addFlag(user, flags.charAt(i));
		}
	}

	/**
	 * Remove a flag from the user or pattern.
	 *
	 * @param user
	 *            The user or pattern to remove the flag from.
	 * @param flag
	 *            The flag - any single character.
	 * @throws SettingException
	 *             If the save was unsuccessful.
	 */
	public void removeFlag(String user, char flag)
	{
		if (user == null) {
			return;
		}

		flag = Character.toUpperCase(flag);
		user = user.toLowerCase();

		String current = userDB.getNoWrite(null, user, "");

		if (current.length() > 0) {
			userDB.set(null, user, current.replaceAll(flag + "", ""));
		}

		if (userDB.getNoWrite(null, user, "").length() == 0) {
			userDB.remove(null, user);
		}
	}

	public void removeFlags(String user, String flags)
	{
		if (user == null) {
			return;
		}

		for (int i = 0; i < flags.length(); i++) {
			removeFlag(user, flags.charAt(i));
		}
	}

	/**
	 * Checks if the user, or any pattern that matches the user, has this flag.
	 *
	 * @param user
	 *            The user to search the database for. If any pattern can match
	 *            this user, it is used.
	 * @param flag
	 *            The flag to search for. As soon as it is found, the search
	 *            terminates.
	 * @return true if some pattern with this flag matches the user.<br>
	 *         false otherwise.
	 */
	private boolean hasFlag(String user, char flag)
	{
		if (user == null)
		{
			if (flag == 'M' || flag == 'U') {
				return true;
			}
			return false;
		}

		user = user.toLowerCase();
		flag = Character.toUpperCase(flag);

		if (findFlag(user, flag)) {
			return true;
		}

		Enumeration e = userDB.propertyNames(null);

		if (e == null) {
			return false;
		}

		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();

			if (user.matches(fixPattern(name))) {
				if (findFlag(name, flag)) {
					return true;
				}
			}
		}

		return false;
	}

	public String getRawFlags(String user)
	{
		if (user == null) {
			return "MU";
		}

		user = user.toLowerCase();

		return userDB.getNoWrite(null, user, "");
	}

	/**
	 * Gets a list (in alphabetical order) of all flags that a user has. This
	 * includes patterns that match the user's name.
	 *
	 * @param user
	 *            The user we're searching for.
	 * @return A string representation of their flags, as well as the patterns
	 *         that contribute do it. For example,
	 *         "*[vl]* *iago* iago[vL] => ABOS"
	 */
	public String getFlags(String user)
	{
		if (user == null) {
			return "<local user> => MU (always)";
		}

		user = user.toLowerCase();

		Enumeration e = userDB.propertyNames(null);

		StringBuilder patterns = new StringBuilder();
		TreeSet flags = new TreeSet();

		// patterns.append(user + ": ");

		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();

			if (user.matches(fixPattern(name)))
			{
				patterns.append(name).append(" ");
				String userFlags = userDB.getNoWrite(null, name, "");

				for (int i = 0; i < userFlags.length(); i++) {
					flags.add(userFlags.charAt(i));
				}
			}
		}

		if (patterns.length() == 0 || flags.size() == 0)
		{
			return "User " + user + " was not found.";
		}

		patterns.append("=> ");

		for (Object flag : flags) {
			patterns.append(flag);
		}

		return patterns.toString();
	}

	/**
	 * Deletes the specified user from the database.
	 *
	 * @param name
	 *            The name of the user to delete.
	 * @throws SettingException
	 *             If there is an error saving.
	 */
	public void deleteUser(String user)
	{
		if (user == null) {
			return;
		}

		user = user.toLowerCase();

		userDB.remove(null, user);
	}

	/**
	 * Returns the number of users/patterns currently in the database.
	 *
	 * @return The number of users/patterns currently in the database.
	 */
	public int getCount()
	{
		return userDB.size(null);
	}

	/**
	 * Checks if the user has any of the flags in the list. <BR>
	 * For example, for channel protection, you might want to do
	 * hasAny(username, "SF") to check if they should be banned.
	 *
	 * @param user
	 *            The name of the user to be looked up.
	 * @param flagList
	 *            The flags we want to return true for.
	 * @return true if the user has at least one of the flags in flagList.
	 */
	public boolean hasAny(String user, String flagList, boolean allowMOverride)
	{
		if (user != null) {
			user = user.toLowerCase();
		}

		if (allowMOverride && !flagList.equals("U")) {
			flagList = flagList + "M";
		}

		for (int i = 0; i < flagList.length(); i++) {
			if (hasFlag(user, flagList.charAt(i))) {
				return true;
			}
		}

		return false;
	}

	public boolean hasAll(String user, String flagList)
	{
		user = user.toLowerCase();

		for (int i = 0; i < flagList.length(); i++) {
			if (!hasFlag(user, flagList.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	public boolean userExists(String user)
	{
		if (user == null) {
			return true;
		}

		user = user.toLowerCase();

		return userDB.getNoWrite(null, user, "").length() > 0;
	}

	public Iterable<String> findAttr(char flag)
	{
		flag = Character.toUpperCase(flag);

		Vector users = new Vector();

		Enumeration e = userDB.propertyNames(null);

		while (e.hasMoreElements())
		{
			String thisUser = (String) e.nextElement();
			if (userDB.getNoWrite(null, thisUser, "").indexOf(flag) >= 0) {
				users.add(thisUser);
			}
		}

		if (flag == 'M' || flag == 'U') {
			users.add("<local user>");
		}

		return Uniq.uniq(users);
	}

	public String[] getUserList()
	{
		Enumeration e = userDB.propertyNames(null);

		Vector ret = new Vector();

		if (e != null) {
			while (e.hasMoreElements()) {
				ret.add((String) e.nextElement());
			}
		}

		return (String[]) ret.toArray(new String[ret.size()]);
	}

	/**
	 * Checks if the specified user has the specified flags. Does not search for
	 * the pattern.
	 *
	 * @param user
	 *            The exact username or pattern.
	 * @param flag
	 *            The flag to search for.
	 * @return true if it had that flag; false otherwise.
	 */
	protected boolean findFlag(String user, char flag)
	{
		String current = userDB.getNoWrite(null, user, "");
		flag = Character.toUpperCase(flag);

		// M overrides all other flags
		return (current != null && (current.indexOf(flag) >= 0));
	}

	private static String fixPattern(String str)
	{
		StringBuilder ret = new StringBuilder();

		for (int i = 0; i < str.length(); i++)
		{
			char thisChar = str.charAt(i);

			if (thisChar == '*') {
				ret.append(".*");
			} else if (thisChar == '?') {
				ret.append(".");
			} else if (thisChar == '%') {
				ret.append("[0-9]");
			} else if (!Character.isLetterOrDigit(thisChar)) {
				ret.append("\\").append(thisChar);
			} else {
				ret.append(str.charAt(i));
			}
		}
		return ret.toString().toLowerCase();
	}

	public static void main(String[] args) throws Exception
	{
		UserDB db = new UserDB(new File("/tmp/blah"));
		db.addFlags("test", "ABCD");
	}
}
