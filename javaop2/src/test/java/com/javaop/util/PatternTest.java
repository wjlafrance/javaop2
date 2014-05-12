package com.javaop.util;

import com.javaop.util.Pattern;
import com.javaop.util.User;
import org.junit.Test;

import static org.junit.Assert.*;

public class PatternTest
{

	public @Test void testSingleCharacterWildcard() {
		assertEquals("testuser.", Pattern.fixPattern("testuser?"));
		assertEquals(".testuser", Pattern.fixPattern("?testuser"));
		assertEquals("test.user", Pattern.fixPattern("test?user"));
	}

	public @Test void testMultiCharacterWildcard() {
		assertEquals("testuser.*", Pattern.fixPattern("testuser*"));
		assertEquals(".*testuser", Pattern.fixPattern("*testuser"));
		assertEquals("test.*user", Pattern.fixPattern("test*user"));
	}

	public @Test void testDecimalWildcard() {
		assertEquals("testuser[0-9]", Pattern.fixPattern("testuser%"));
		assertEquals("[0-9]testuser", Pattern.fixPattern("%testuser"));
		assertEquals("test[0-9]user", Pattern.fixPattern("test%user"));
	}

}
