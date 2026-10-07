package com.javaop._main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Picks which FlatLaf theme to install. Everything here is a pure function of
 * the OS name and the output of an OS "is it dark?" query, so it can be tested
 * without a display.
 */
public final class ThemeSelector {

	public static final String FLAT_LIGHT     = "com.formdev.flatlaf.FlatLightLaf";
	public static final String FLAT_DARK      = "com.formdev.flatlaf.FlatDarkLaf";
	public static final String FLAT_MAC_LIGHT = "com.formdev.flatlaf.themes.FlatMacLightLaf";
	public static final String FLAT_MAC_DARK  = "com.formdev.flatlaf.themes.FlatMacDarkLaf";

	private ThemeSelector() {}

	public static boolean isMac(String osName) {
		return osName != null && osName.toLowerCase(Locale.ROOT).startsWith("mac");
	}

	public static boolean isWindows(String osName) {
		return osName != null && osName.toLowerCase(Locale.ROOT).startsWith("windows");
	}

	/** Theme class name for the given OS and appearance. */
	public static String lafClassName(String osName, boolean dark) {
		if (isMac(osName)) {
			return dark ? FLAT_MAC_DARK : FLAT_MAC_LIGHT;
		}
		return dark ? FLAT_DARK : FLAT_LIGHT;
	}

	/**
	 * Decide dark vs. light. {@code runner} runs a command and returns its
	 * stdout (or null on failure); that's the only side-effecting piece.
	 * Falls back to light.
	 */
	public static boolean isDark(String osName, Function<String[], String> runner) {
		try {
			if (isMac(osName)) {
				// Prints "Dark" in dark mode; errors (null output) in light mode
				return parseMacDefaults(runner.apply(
						new String[] {"defaults", "read", "-g", "AppleInterfaceStyle"}));
			}
			if (isWindows(osName)) {
				return parseWindowsRegistry(runner.apply(new String[] {
						"reg", "query",
						"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
						"/v", "AppsUseLightTheme"}));
			}
			return parseGsettings(runner.apply(new String[] {
					"gsettings", "get", "org.gnome.desktop.interface", "color-scheme"}));
		} catch (RuntimeException e) {
			return false;
		}
	}

	static boolean parseMacDefaults(String out) {
		return out != null && out.trim().equalsIgnoreCase("Dark");
	}

	static boolean parseWindowsRegistry(String out) {
		if (out == null) {
			return false;
		}
		for (String line : out.split("\\R")) {
			if (line.contains("AppsUseLightTheme")) {
				return line.trim().endsWith("0x0");
			}
		}
		return false;
	}

	static boolean parseGsettings(String out) {
		return out != null && out.contains("prefer-dark");
	}

	/** Real command runner: 2 second timeout, null on any failure or non-zero exit. */
	public static String runCommand(String[] command) {
		try {
			Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
			StringBuilder sb = new StringBuilder();
			try (BufferedReader r = new BufferedReader(
					new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = r.readLine()) != null) {
					sb.append(line).append('\n');
				}
			}
			if (!p.waitFor(2, TimeUnit.SECONDS)) {
				p.destroyForcibly();
				return null;
			}
			return p.exitValue() == 0 ? sb.toString() : null;
		} catch (Exception e) {
			return null;
		}
	}
}
