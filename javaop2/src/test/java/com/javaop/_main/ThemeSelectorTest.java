package com.javaop._main;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThemeSelectorTest {

	@Test
	public void picksMacThemesOnMac() {
		assertEquals(ThemeSelector.FLAT_MAC_LIGHT, ThemeSelector.lafClassName("Mac OS X", false));
		assertEquals(ThemeSelector.FLAT_MAC_DARK, ThemeSelector.lafClassName("Mac OS X", true));
	}

	@Test
	public void picksPlainThemesElsewhere() {
		assertEquals(ThemeSelector.FLAT_LIGHT, ThemeSelector.lafClassName("Windows 11", false));
		assertEquals(ThemeSelector.FLAT_DARK, ThemeSelector.lafClassName("Linux", true));
	}

	@Test
	public void macDarkDetection() {
		assertTrue(ThemeSelector.isDark("Mac OS X", cmd -> "Dark\n"));
		assertFalse(ThemeSelector.isDark("Mac OS X", cmd -> null)); // key absent in light mode
	}

	@Test
	public void windowsDarkDetection() {
		String dark = "\nHKEY_CURRENT_USER\\...\\Personalize\n    AppsUseLightTheme    REG_DWORD    0x0\n";
		String light = dark.replace("0x0", "0x1");
		assertTrue(ThemeSelector.isDark("Windows 11", cmd -> dark));
		assertFalse(ThemeSelector.isDark("Windows 11", cmd -> light));
		assertFalse(ThemeSelector.isDark("Windows 11", cmd -> null));
	}

	@Test
	public void linuxDarkDetection() {
		assertTrue(ThemeSelector.isDark("Linux", cmd -> "'prefer-dark'\n"));
		assertFalse(ThemeSelector.isDark("Linux", cmd -> "'default'\n"));
	}

	@Test
	public void runnerFailureFallsBackToLight() {
		assertFalse(ThemeSelector.isDark("Mac OS X", cmd -> { throw new IllegalStateException(); }));
	}
}
