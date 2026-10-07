package com.javaop.SwingGui;

import java.awt.Desktop;
import java.awt.Image;
import java.awt.Taskbar;
import java.net.URL;

import javax.imageio.ImageIO;

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

		setDockIcon();

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

	/**
	 * Sets the Dock icon from the bundled icon.png. The packaged app already
	 * gets its icon from the .app bundle; this covers runs from a plain jar.
	 * Optional: any problem is ignored.
	 */
	private static void setDockIcon() {
		try {
			URL url = MacIntegration.class.getResource("icon.png");
			if (url != null && Taskbar.isTaskbarSupported()
					&& Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE)) {
				Image icon = ImageIO.read(url);
				if (icon != null) {
					Taskbar.getTaskbar().setIconImage(icon);
				}
			}
		} catch (Exception | LinkageError ignored) {
			// cosmetic only
		}
	}
}
