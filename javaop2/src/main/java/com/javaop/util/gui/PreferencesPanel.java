/*
 * Created on Mar 20, 2005 By iago
 */
package com.javaop.util.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.javaop.util.Uniq;


public class PreferencesPanel extends JPanelEx
{
	private static final long serialVersionUID = 1L;

	private final Hashtable   components       = new Hashtable();
	private final Properties  initial;

	/**
	 * Get the settings panel. This can be overridden to change how the settings
	 * look
	 */
	public PreferencesPanel(Properties currentSettings, Properties defaultSettings,
			Properties descriptions, Hashtable components)
	{
		super(new GridBagLayout());

		String[] keys = Uniq.uniq(defaultSettings.keys());

		if (keys.length == 0) {
			this.add(new JLabel("There are no settings for this plugin"));
		}

		for (int i = 0; i < keys.length; i++)
		{
			String defaultSetting = defaultSettings.getProperty(keys[i], "");
			String currentSetting = currentSettings.getProperty(keys[i], defaultSetting);
			JComponent currentComponent = (JComponent) components.get(keys[i]);
			String description = descriptions.getProperty(keys[i], "<ERROR no description set>");

			if (currentComponent == null) {
				currentComponent = new JTextField(currentSetting);
			}

			JPanel thisPanel = getSettingPanel(keys[i], defaultSetting, description,
											   currentComponent);
			this.add(thisPanel, new GridBagConstraints(0, i, 1, 1, 4, 1, GridBagConstraints.NORTH,
					GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		}

		initial = getValues();
	}

	/** Get the panel for a single setting. Each setting calls this */
	protected JPanel getSettingPanel(String name, final String defaultValue, String description,
			final JComponent comp)
	{
		// GridBagLayout layout;
		final JButton defaults = new JButton("Default");
		final JTextArea desc = new JTextArea(description);
		components.put(name, comp);

		JPanelEx p = new JPanelEx(new GridBagLayout());

		p.setBorder(BorderFactory.createTitledBorder(
													 BorderFactory.createLineBorder(Color.BLACK, 2),
													 name));

		p.add(comp, new GridBagConstraints(0, 1, 1, 1, 2, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		p.add(defaults, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.WEST,
				GridBagConstraints.HORIZONTAL, new Insets(1, 1, 1, 1), 0, 0));
		p.add(desc, new GridBagConstraints(0, 2, 2, 1, 0, 1, GridBagConstraints.WEST,
				GridBagConstraints.BOTH, new Insets(1, 1, 1, 1), 0, 0));

		// nameLabel.setBorder(BorderFactory.createEtchedBorder());

		desc.setEditable(false);
		desc.setOpaque(false);
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.setFocusable(false);

		defaults.addActionListener(e -> Gui.setTextFromComponent(comp, defaultValue));

		return p;
	}

	public String getValue(String key)
	{
		return Gui.getTextFromComponent((JComponent) components.get(key));
	}

	public Properties getValues()
	{
		Properties p = new Properties();
		Enumeration e = components.keys();

		while (e.hasMoreElements())
		{
			String thisElement = (String) e.nextElement();
			p.setProperty(thisElement, getValue(thisElement));
		}

		return p;
	}

	public boolean hasChanged()
	{
		return !initial.equals(getValues());
	}
}
