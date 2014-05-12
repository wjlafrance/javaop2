/*
 * Created on Aug 27, 2004 By iago
 */
package com.javaop.users;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.javaop.util.UsernameMatcherPattern;
import com.javaop.util.User;

/**
 * @author iago
 *
 */
public class UserList {

	/** Not an absolute, but a good rule of thumb. */
	private static final int CHANNEL_MAXIMUM_CAPACITY = 40;

	private final List<User> users = new ArrayList<>(CHANNEL_MAXIMUM_CAPACITY);

	public void clear() {
		users.clear();
	}

	public int size() {
		return users.size();
	}

	public User addUser(final String name, final int flags, final int ping, final String statstring) {
		Optional<User> oldUser = _removeUser(name);
		User newUser = new UserData(name, ping, flags, oldUser.map(User::getRawStatstring).orElse(statstring));
		users.add(newUser);
		return newUser;
	}

	@Deprecated
	public User removeUser(final String name) {
		return _removeUser(name).orElse(null);
	}

	@Deprecated
	public User getUser(final String name) {
		return _getUser(name).orElse(null);
	}

	@Deprecated
	public String[] matchNames(final String pattern) {
		List<String> ret = _matchNames(pattern);
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	@Deprecated
	public String[] getList() {
		List<String> ret = _getList();
		return (String[]) ret.toArray(new String[ret.size()]);
	}

	// These methods will soon replace the above-deprecated methods.

	public Optional<User> _removeUser(final String name) {
		Optional<User> user = _getUser(name);
		if (user.isPresent()) {
			users.remove(user.get());
		}
		return user;
	}

	public Optional<User> _getUser(final String name) {
		return users.stream().filter(
				u -> u.getName().equals(name)
		).findFirst();
	}

	public List<String> _matchNames(final String pattern) {
		String regex = UsernameMatcherPattern.fixPattern(pattern);

		return users.stream().filter(
				u -> u.getName().toLowerCase().matches(regex)
		).map(User::getName).collect(Collectors.toList());
	}

	public List<String> _getList() {
		return users.stream().map(User::getName).collect(Collectors.toList());
	}

}
