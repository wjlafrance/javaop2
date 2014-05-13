/*
 * Created on Feb 4, 2005 By iago
 */
package com.javaop.util;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

/**
 * These methods currently return a List&lt;String&gt;, but may return a Set later on. Treat the result as an
 * Iterable if you don't want your code to break.
 */
public class Uniq {

	public static <T> List<String> uniq(Enumeration<T> input) {
		if (input == null) {
			return null;
		}

		ImmutableSet.Builder<String> ret = ImmutableSet.builder();
		while (input.hasMoreElements()) {
			ret.add(input.nextElement().toString());
		}
		return Lists.newArrayList(ret.build());
	}

	public static <T> List<String> uniq(List<T> input) {
		if (input == null) {
			return null;
		}

		ImmutableSet.Builder<String> ret = ImmutableSet.builder();
		for (T e : input) {
			ret.add(e.toString());
		}
		return Lists.newArrayList(ret.build());
	}

	public static <T> List<String> uniq(T[] input) {
		if (input == null) {
			return null;
		}

		ImmutableSet.Builder<String> ret = ImmutableSet.builder();
		for (T e : input) {
			ret.add(e.toString());
		}
		return Lists.newArrayList(ret.build());
	}
}
