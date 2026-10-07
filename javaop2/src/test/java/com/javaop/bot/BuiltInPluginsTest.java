package com.javaop.bot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BuiltInPluginsTest {

	@Rule public TemporaryFolder tmp = new TemporaryFolder();

	private File touch(File dir, String name) throws IOException {
		dir.mkdirs();
		File f = new File(dir, name);
		assertTrue(f.createNewFile());
		return f;
	}

	@Test public void findsJarsNextToAppJarSortedAndSkipsOtherFiles() throws IOException {
		File app = tmp.newFolder("app");
		File appJar = touch(app, "javaop2.jar");
		File plugins = new File(app, "plugins");
		File b = touch(plugins, "b.jar");
		File a = touch(plugins, "a.jar");
		touch(plugins, "readme.txt");

		List<File> found = BuiltInPlugins.find(appJar, null);
		assertEquals(Arrays.asList(a, b), found);
	}

	@Test public void noPluginsFolderMeansEmpty() throws IOException {
		File appJar = touch(tmp.newFolder("app"), "javaop2.jar");
		assertTrue(BuiltInPlugins.find(appJar, null).isEmpty());
	}

	@Test public void classesDirectoryDevRunIsIgnored() throws IOException {
		File target = tmp.newFolder("target");
		File classes = new File(target, "classes");
		classes.mkdirs();
		touch(new File(classes, "plugins"), "x.jar");
		assertTrue(BuiltInPlugins.find(classes, null).isEmpty());
	}

	@Test public void usesJpackageAppPathLayout() throws IOException {
		File contents = tmp.newFolder("JavaOp2.app", "Contents");
		File launcher = touch(new File(contents, "MacOS"), "JavaOp2");
		File jar = touch(new File(contents, "app/plugins"), "p.jar");

		assertEquals(Arrays.asList(jar), BuiltInPlugins.find(null, launcher.getPath()));
	}

	@Test public void nullInputsAreSafe() {
		assertTrue(BuiltInPlugins.find(null, null).isEmpty());
		assertTrue(BuiltInPlugins.find(null, "").isEmpty());
	}
}
