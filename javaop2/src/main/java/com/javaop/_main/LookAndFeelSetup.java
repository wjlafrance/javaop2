package com.javaop._main;

import javax.swing.UIManager;


/**
 * Installs FlatLaf. Kept separate from {@link BotStart} so a missing FlatLaf
 * jar surfaces as a catchable NoClassDefFoundError at the call site rather
 * than breaking BotStart itself.
 */
final class LookAndFeelSetup {

	private LookAndFeelSetup() {}

	/** Must be called before any Swing component exists. */
	static void install() throws Exception {
		String os = System.getProperty("os.name", "");
		boolean dark = ThemeSelector.isDark(os, ThemeSelector::runCommand);
		String laf = ThemeSelector.lafClassName(os, dark);
		UIManager.setLookAndFeel(laf);
		System.out.println("Look and feel: " + laf + (dark ? " (dark)" : " (light)"));
	}
}
