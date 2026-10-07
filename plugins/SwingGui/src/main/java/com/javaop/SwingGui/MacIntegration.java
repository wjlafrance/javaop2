package com.javaop.SwingGui;

import java.awt.Desktop;

import javax.swing.JOptionPane;

import com.javaop.SwingGui.settings.GlobalSettingWizard;
import com.javaop.callback_interfaces.StaticExposedFunctions;

/**
 * Hooks the macOS application menu (About / Preferences / Quit) up to the
 * app. Does nothing on platforms or desktops that don't support the actions.
 */
final class MacIntegration {

	private MacIntegration() {}

	static void install(StaticExposedFunctions staticFuncs, JavaOpFrame frame) {
		if (!System.getProperty("os.name", "").toLowerCase().startsWith("mac")
				|| !Desktop.isDesktopSupported()) {
			return;
		}
		Desktop desktop = Desktop.getDesktop();

		if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
			desktop.setAboutHandler(e -> JOptionPane.showMessageDialog(frame,
					"JavaOp2 " + staticFuncs.getVersion(), "About JavaOp2",
					JOptionPane.INFORMATION_MESSAGE));
		}
		if (desktop.isSupported(Desktop.Action.APP_PREFERENCES)) {
			// Same thing as Settings > Global settings...
			desktop.setPreferencesHandler(e -> new GlobalSettingWizard(staticFuncs));
		}
		if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
			// Same path as File > Exit
			desktop.setQuitHandler((e, response) -> System.exit(0));
		}
	}
}
