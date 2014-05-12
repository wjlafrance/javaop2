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
		Optional<User> oldUser = removeUser(name);
		User newUser = new UserData(name, ping, flags, oldUser.map(User::getRawStatstring).orElse(statstring));
		users.add(newUser);
		return newUser;
	}

	public Optional<User> removeUser(final String name) {
		Optional<User> user = getUser(name);
		if (user.isPresent()) {
			users.remove(user.get());
		}
		return user;
	}

	public Optional<User> getUser(final String name) {
		return users.stream().filter(
				u -> u.getName().equals(name)
		).findFirst();
	}

	public List<String> matchesName(final String pattern) {
		String regex = UsernameMatcherPattern.fixPattern(pattern);

		return users.stream().filter(
				u -> u.getName().toLowerCase().matches(regex)
		).map(User::getName).collect(Collectors.toList());
	}

	public List<String> getList() {
		return users.stream().map(User::getName).collect(Collectors.toList());
	}

}
