/*
 * Created on Feb 4, 2005 By iago
 */
package com.javaop.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Uniq {

	public static <T> List<String> uniq(Enumeration<T> input) {
		return uniq(Optional.of(input).map(Collections::list));
	}

	public static <T> List<String> uniq(T[] input) {
		return uniq(Optional.of(input).map(Arrays::asList));
	}

	public static <T> List<String> uniq(List<T> input) {
		return uniq(Optional.of(input));
	}

	public static <T> List<String> uniq(Optional<List<T>> input) {
		return input.map(z -> z.stream()
			.map(x -> x.toString())
			.distinct()
			.collect(Collectors.toList())
		).orElse(Collections.emptyList());
	}
}
