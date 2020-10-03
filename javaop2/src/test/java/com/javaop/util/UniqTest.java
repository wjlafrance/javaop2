package com.javaop.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class UniqTest {

    final List<String> expect = Arrays.asList("a", "b", "c");
    final List<String> input = Arrays.asList("a", "a", "b", "b", "c");

	public @Test void testUniqEnumeration() {
		assertEquals(expect, Uniq.uniq(Collections.enumeration(input)));
	}

	public @Test void testUniqArray() {
		assertEquals(expect, Uniq.uniq(input.toArray()));
	}

	public @Test void testUniqList() {
		assertEquals(expect, Uniq.uniq(input));
	}

}
