package com.javaop.bot;

import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Finds the plugin jars that ship inside the application itself (a
 * {@code plugins} folder next to the app jar, as laid out by the packaged
 * app). These are never written to the user's saved plugin paths, so moving
 * or updating the app keeps working.
 */
public final class BuiltInPlugins {

	/** Name of the folder, next to the app jar, that holds the bundled plugins. */
	public static final String PLUGINS_DIR = "plugins";

	/** jpackage launchers set this to e.g. JavaOp2.app/Contents/MacOS/JavaOp2. */
	private static final String JPACKAGE_APP_PATH = "jpackage.app-path";

	private BuiltInPlugins() {}

	/**
	 * Pure resolution: the bundled plugin jars, sorted by name.
	 *
	 * @param codeSource the jar (or classes directory) the app was loaded from; may be null
	 * @param jpackageAppPath value of the jpackage.app-path property; may be null
	 * @return jars in {@code <appdir>/plugins}; empty if there is no such folder
	 */
	public static List<File> find(File codeSource, String jpackageAppPath) {
		List<File> candidates = new ArrayList<>();

		// Running from a jar: the folder next to it. (A classes directory, as in
		// an IDE or dev run, is skipped on purpose.)
		if (codeSource != null && codeSource.isFile()) {
			candidates.add(new File(codeSource.getAbsoluteFile().getParentFile(), PLUGINS_DIR));
		}

		// jpackage: <App>.app/Contents/MacOS/<launcher> -> Contents/app/plugins,
		// Windows/Linux: <root>/<launcher> or <root>/bin/<launcher> -> <root>/app/plugins
		if (jpackageAppPath != null && !jpackageAppPath.isEmpty()) {
			File launcher = new File(jpackageAppPath).getAbsoluteFile();
			File launcherDir = launcher.getParentFile();
			if (launcherDir != null) {
				File root = launcherDir.getParentFile();
				if (root != null) {
					candidates.add(new File(new File(root, "app"), PLUGINS_DIR));
				}
				candidates.add(new File(new File(launcherDir, "app"), PLUGINS_DIR));
			}
		}

		for (File dir : candidates) {
			File[] jars = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".jar"));
			if (jars != null && jars.length > 0) {
				Arrays.sort(jars);
				return Arrays.asList(jars);
			}
		}
		return Collections.emptyList();
	}

	/** Resolves from the running app: this class's code source and jpackage.app-path. */
	public static List<File> find() {
		File codeSource = null;
		try {
			CodeSource cs = BuiltInPlugins.class.getProtectionDomain().getCodeSource();
			if (cs != null && cs.getLocation() != null) {
				codeSource = new File(cs.getLocation().toURI());
			}
		} catch (URISyntaxException | RuntimeException e) {
			// fall through; jpackage.app-path may still work
		}
		return find(codeSource, System.getProperty(JPACKAGE_APP_PATH));
	}
}
