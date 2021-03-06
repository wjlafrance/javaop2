package com.javaop.users;

import java.util.Arrays;

import com.javaop.util.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserListTest {

	public @Test void testSize() {
		// It starts at zero
		UserList testList = new UserList();
		assertEquals(0, testList.size());

		// It goes up when a user is added
		testList.addUser("testuser", 0, 0, "");
		assertEquals(1, testList.size());

		// It goes down when a user is removed
		testList.removeUser("testuser");
		assertEquals(0, testList.size());
	}

	public @Test void testClear() {
		UserList testList = new UserList();
		testList.addUser("testuser1", 0, 0, "");
		testList.addUser("testuser2", 0, 0, "");
		testList.addUser("testuser3", 0, 0, "");
		testList.addUser("testuser4", 0, 0, "");

		// Size is not zero before clearing
		assertEquals(4, testList.size());

		// Size is zero after clearing
		testList.clear();
		assertEquals(0, testList.size());
	}

	public @Test void testAddUser() {
		UserList testList = new UserList();

		// It sets the User object's properties correctly
		testList.addUser("testuser", 15, 30, "STATSTRING");
		User testuser = testList.getUser("testuser").get();
		assertEquals("testuser", testuser.getName());
		assertEquals(15, testuser.getFlags());
		assertEquals(30, testuser.getPing());
		assertEquals("STATSTRING", testuser.getRawStatstring());

		// It replaces the user properly when readded
		testList.addUser("testuser", 45, 60, "NEW STATSTRING");
		assertEquals(1, testList.size());

		// The new properties are correct
		testuser = testList.getUser("testuser").get();
		assertEquals("testuser", testuser.getName());
		assertEquals(45, testuser.getFlags());
		assertEquals(60, testuser.getPing());

		// The new user should have the old statstring
		// TODO: Why is this? It's how the original code worked.
		assertEquals("STATSTRING", testuser.getRawStatstring());
	}

	public @Test void testRemoveUser() {
		UserList testList = new UserList();
		testList.addUser("testuser1", 0, 0, "");
		testList.addUser("testuser2", 0, 0, "");

		// testuser1 remains after testuser2 leaves
		testList.removeUser("testuser2").get();
		assertEquals(Arrays.asList("testuser1"), testList.getList());

		// does not crash if user is not in list
		assertNull(testList.removeUser("imnothere").orElse(null));
	}

	public @Test void testMatchNames() {
		UserList testList = new UserList();
		testList.addUser("testuser1", 0, 0, "");
		testList.addUser("testuser2", 0, 0, "");
		testList.addUser("bestuser1", 0, 0, "");

		// wildcard matching
		assertEquals(Arrays.asList("testuser1", "testuser2"), testList.matchesName("testu*"));
		// wildcard matching
		assertEquals(Arrays.asList("testuser2"), testList.matchesName("*user2"));
		// decimal wildcard matching
		assertEquals(Arrays.asList("testuser1", "testuser2"), testList.matchesName("testuser%"));
		// character wildcard matching
		assertEquals(Arrays.asList("testuser1", "bestuser1"), testList.matchesName("?estuser1"));
		// matching all users
		assertEquals(Arrays.asList("testuser1", "testuser2", "bestuser1"), testList.matchesName("*"));
	}

	public @Test void testGetList() {
		UserList testList = new UserList();
		testList.addUser("testuser1", 0, 0, "");
		testList.addUser("testuser2", 0, 0, "");

		assertEquals(Arrays.asList("testuser1", "testuser2"), testList.getList());
	}

}
