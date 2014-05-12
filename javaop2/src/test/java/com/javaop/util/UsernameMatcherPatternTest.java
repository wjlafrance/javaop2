package com.javaop.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class UsernameMatcherPatternTest {

	public @Test void testSingleCharacterWildcard() {
		assertEquals("testuser.", UsernameMatcherPattern.fixPattern("testuser?"));
		assertEquals(".testuser", UsernameMatcherPattern.fixPattern("?testuser"));
		assertEquals("test.user", UsernameMatcherPattern.fixPattern("test?user"));
	}

	public @Test void testMultiCharacterWildcard() {
		assertEquals("testuser.*", UsernameMatcherPattern.fixPattern("testuser*"));
		assertEquals(".*testuser", UsernameMatcherPattern.fixPattern("*testuser"));
		assertEquals("test.*user", UsernameMatcherPattern.fixPattern("test*user"));
	}

	public @Test void testDecimalWildcard() {
		assertEquals("testuser[0-9]", UsernameMatcherPattern.fixPattern("testuser%"));
		assertEquals("[0-9]testuser", UsernameMatcherPattern.fixPattern("%testuser"));
		assertEquals("test[0-9]user", UsernameMatcherPattern.fixPattern("test%user"));
	}

	public @Test void testRegexUnsafeCharacters() {
		assertEquals("\\(\\)\\[\\]", UsernameMatcherPattern.fixPattern("()[]"));
	}

}
