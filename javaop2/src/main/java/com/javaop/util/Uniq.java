/*
 * Created on Feb 4, 2005 By iago
 */
package com.javaop.util;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * These methods currently return a List&lt;String&gt;, but may return a Set later on. Treat the result as an
 * Iterable if you don't want your code to break.
 */
public class Uniq {

	public static <T> List<String> uniq(Enumeration<T> input) {
		if (input == null) {
			return null;
		}
		//return Collections.list(input).stream().map(x -> x.toString()).collect(Collectors.toList());

		TreeSet<String> set = new TreeSet<String>();
		while (input.hasMoreElements()) {
			set.add(input.nextElement().toString());
		}
		return List.copyOf(set);
	}

	public static <T> List<String> uniq(List<T> input) {
		if (input == null) {
			return null;
		}
		//return input.stream().map(x -> x.toString()).collect(Collectors.toList());

		TreeSet<String> set = new TreeSet<String>();
		for (T e: input) {
			set.add(e.toString());
		}
		return List.copyOf(set);
	}

	public static <T> List<String> uniq(T[] input) {
		if (input == null) {
			return null;
		}
		//return Arrays.asList(input).stream().map(x -> x.toString()).collect(Collectors.toList());

		TreeSet<String> set = new TreeSet<String>();
		for (T e: input) {
			set.add(e.toString());
		}
		return List.copyOf(set);
	}
}
