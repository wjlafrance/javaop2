package com.javaop.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

public class PersistantMapTest {

	@Rule
	public final TemporaryFolder tmp = new TemporaryFolder();

	private PersistantMap newMap() throws IOException {
		return new PersistantMap(new File(tmp.newFolder(), "settings.ini"), "test");
	}

	@Test
	public void setAndGet() throws IOException {
		PersistantMap map = newMap();
		map.set("sec", "Key", "value");
		assertEquals("value", map.getNoWrite("sec", "key", "dflt"));
		assertEquals("dflt", map.getNoWrite("sec", "missing", "dflt"));
	}

	@Test
	public void propertyNamesReturnsKeysOrNullForMissingSection() throws IOException {
		PersistantMap map = newMap();
		map.set("sec", "a", "1");
		map.set("sec", "b", "2");
		assertEquals(new HashSet<>(Arrays.asList("a", "b")), map.propertyNames("sec"));
		assertNull(map.propertyNames("nope"));
	}

	@Test
	public void sectionNamesIncludesDefaultAndNamedSections() throws IOException {
		PersistantMap map = newMap();
		map.set(null, "k", "v");
		map.set("other", "k", "v");
		Set<String> names = new TreeSet<>(map.sectionNames());
		assertTrue(names.containsAll(Arrays.asList("default", "other")));
	}
}
