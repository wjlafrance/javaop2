package com.javaop.SwingGui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.javaop.SwingGui.settings.UserDatabaseWizard;
import com.javaop.SwingGui.util.MenuIcons;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.PriorityConstants;



/*
 * Created on Aug 20, 2005 By iago
 */

/** There should only ever be one instance of this created per bot */
public class JavaOpUserMenu {
	/** Stores a list of custom items that will be displayed in the menu */
	private Vector              					items = new Vector();

	/** Add this to the "items" vector to put a separator in the list */
	private final JMenuItem              			separator = new JMenuItem();

	/** The table of actions for custom items */
	private final Hashtable<String, ActionListener> actions
			= new Hashtable<String, ActionListener>();

	/** The table if icons for custom items */
	private final Hashtable<String, Icon>           icons
			= new Hashtable<String, Icon>();

	private final PublicExposedFunctions pubFuncs;

	public JavaOpUserMenu(PublicExposedFunctions pubFuncs) {
		this.pubFuncs = pubFuncs;
	}

	public void addItem(String name, int index, Icon icon,
			ActionListener callback)
	{
		if (index >= 0)
			items.add(index, name);
		else
			items.add(name);

		if (icon != null)
			icons.put(name, icon);
		if (callback != null)
			actions.put(name, callback);
	}

	public void removeItem(String name) {
		items.remove(name);
		actions.remove(name);
		icons.remove(name);
	}

	public JPopupMenu getMenu(String name) {
		return new UserMenuImpl(name);
	}

	public void addSeparator() {
		items.add(separator);
	}

	private class UserMenuImpl extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final JMenuItem   nameItem;
		private final JMenuItem   whisper          = new JMenuItem("Whisper");
		private final JMenuItem   ban              = new JMenuItem("Ban");
		private final JMenuItem   banMessage
				= new JMenuItem("Ban (message)...");
		private final JMenuItem   kick             = new JMenuItem("Kick");
		private final JMenuItem   kickMessage
				= new JMenuItem("Kick (message)...");
		private final JMenuItem   squelch          = new JMenuItem("Squelch");
		private final JMenuItem   unsquelch        = new JMenuItem("Unsquelch");
		private final JMenuItem   designate        = new JMenuItem("Designate");
		private final JMenuItem   op               = new JMenuItem("Op");
		private final JMenuItem   edit
				= new JMenuItem("Edit flags...");
		private final JMenuItem   profile
				= new JMenuItem("Profile...");
		private final String      name;

		public UserMenuImpl(String name) {
			this.name = name;
			this.nameItem = new JMenuItem(name);

			ban.setIcon(MenuIcons.getIcon("ban"));
			banMessage.setIcon(MenuIcons.getIcon("banm"));
			kick.setIcon(MenuIcons.getIcon("boot"));
			kickMessage.setIcon(MenuIcons.getIcon("bootm"));

			squelch.setIcon(MenuIcons.getIcon("squelch"));
			unsquelch.setIcon(MenuIcons.getIcon("unsquelch"));

			designate.setIcon(MenuIcons.getIcon("designate"));
			op.setIcon(MenuIcons.getIcon("op"));

			edit.setIcon(MenuIcons.getIcon("flags"));

			profile.setIcon(MenuIcons.getIcon("profile"));

			this.add(nameItem);
			this.addSeparator();

			this.add(whisper);
			this.add(ban);
			this.add(banMessage);
			this.add(kick);
			this.add(kickMessage);

			this.addSeparator();
			this.add(squelch);
			this.add(unsquelch);

			this.addSeparator();
			this.add(designate);
			this.add(op);

			this.addSeparator();
			this.add(edit);

			this.addSeparator();
			this.add(profile);

			nameItem.setEnabled(false);

			whisper.addActionListener(this);
			ban.addActionListener(this);
			banMessage.addActionListener(this);
			kick.addActionListener(this);
			kickMessage.addActionListener(this);

			squelch.addActionListener(this);
			unsquelch.addActionListener(this);

			designate.addActionListener(this);
			op.addActionListener(this);

			edit.addActionListener(this);
			profile.addActionListener(this);

			for (int i = 0; i < items.size(); i++) {
				if (items.get(i) == separator) {
					this.addSeparator();
				} else {
					String itemName = (String) items.get(i);
					JMenuItem item = new JMenuItem(itemName);
					item.addActionListener(this);
					this.add(item);
					Icon icon = (Icon) icons.get(itemName);
					if (icon != null)
						item.setIcon(icon);
				}
			}
		}

		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == whisper) {
					pubFuncs.sendTextPriority("/whisper " + name + " " + pubFuncs.getChatboxInput().getText(),
							PriorityConstants.PRIORITY_NORMAL);
					pubFuncs.getChatboxInput().setText(null);
				} else if (e.getSource() == ban) {
					pubFuncs.sendTextPriority("/ban " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == banMessage) {
					String message = JOptionPane.showInputDialog(this,
							"Message to ban with?");
					if (message != null)
						pubFuncs.sendTextPriority("/ban " + name + " "
								+ message, PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == kick) {
					pubFuncs.sendTextPriority("/kick " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == kickMessage) {
					String message = JOptionPane.showInputDialog(this,
							"Message to kick with?");
					if (message != null)
						pubFuncs.sendTextPriority("/kick " + name + " "
								+ message, PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == squelch) {
					pubFuncs.sendTextPriority("/squelch " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == unsquelch) {
					pubFuncs.sendTextPriority("/unsquelch " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == designate) {
					pubFuncs.sendTextPriority("/designate " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == op) {
					pubFuncs.sendTextPriority("/designate " + name,
							PriorityConstants.PRIORITY_HIGH + 1);
					pubFuncs.sendTextPriority("/resign",
							PriorityConstants.PRIORITY_HIGH + 1);
				} else if (e.getSource() == edit) {
					new UserDatabaseWizard(pubFuncs, name);
				} else {
					ActionListener listener = (ActionListener) actions
							.get(((JMenuItem) e.getSource()).getText());
					if (listener == null) {
						JOptionPane.showMessageDialog(null, "To-do");
					} else {
						ActionEvent newEvent = new ActionEvent(
								((JMenuItem) e.getSource()).getText(), 1, name);
						listener.actionPerformed(newEvent);
					}
				}
			} catch (Exception exc) {
				pubFuncs.systemMessage(ErrorLevelConstants.ERROR,
						"Unable to complete command: " + exc);
			}
		}
	}
}
