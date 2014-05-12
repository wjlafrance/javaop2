/*
 * Created on Dec 18, 2004 By iago
 */
package com.javaop._main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileFilter;

import com.javaop.pluginmanagers.PluginManager;

import com.javaop.bot.JavaOpFileStuff;

import com.javaop.util.RelativeFile;
import com.javaop.util.gui.Gui;


/**
 * @author iago
 *
 */
public class ConfigurePlugins extends JFrame implements WindowListener
{
	private static final long serialVersionUID = 1L;

	private JList             list;
	private boolean           dirty            = true;

	private DefaultListModel  listData;

	public ConfigurePlugins()
	{
		super("Configure loaded plugins");

		try
		{
			listData = new DefaultListModel();
			String[] files = JavaOpFileStuff.getRawPluginPaths();
			for (String file : files) {
				listData.addElement(file);
			}

			this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			this.getContentPane().setLayout(new BorderLayout(5, 5));
			this.getContentPane().add(new Form(), BorderLayout.CENTER);

			this.getContentPane().add(new JLabel(), BorderLayout.NORTH);
			this.getContentPane().add(new JLabel(), BorderLayout.EAST);
			this.getContentPane().add(new JLabel(), BorderLayout.SOUTH);
			this.getContentPane().add(new JLabel(), BorderLayout.WEST);

			this.setSize(750, 550);
			Gui.center(this);
			this.setVisible(true);

			this.addWindowListener(this);
		}
		catch (Throwable t)
		{
			t.printStackTrace();
			JOptionPane.showMessageDialog(null, "Error populating plugin list: " + t);
		}

	}

	private class Form extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public Form()
		{
			this.setLayout(new BorderLayout(10, 10));

			this.add(new ListSection(), BorderLayout.CENTER);
			this.add(new AddSection(), BorderLayout.SOUTH);
		}
	}

	private class ListSection extends JPanel
	{
		private static final long serialVersionUID = 1L;

		public ListSection()
		{
			this.setLayout(new BorderLayout(5, 5));
			this.add(new JLabel("Loaded paths:"), BorderLayout.NORTH);

			this.add(new JScrollPane(list = new JList(listData)), BorderLayout.CENTER);

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.BLACK, 2),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

			list.setCellRenderer(new MyCellRenderer());
		}

		private class MyCellRenderer implements ListCellRenderer
		{
			public Component getListCellRendererComponent(JList list, Object value, int index,
					boolean isSelected, boolean cellHasFocus)
			{
				Component c;

				c = new JLabel(value.toString());

				c.setBackground(Color.WHITE);
				c.setForeground(Color.BLACK);

				JPanel ret = new JPanel();
				ret.setLayout(new GridLayout());
				ret.setBackground(Color.WHITE);
				ret.setForeground(Color.BLACK);
				ret.add(c);

				if (isSelected) {
					ret.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
				} else {
					ret.setBorder(BorderFactory.createLineBorder(list.getBackground(), 2));
				}

				return ret;
			}
		}

	}

	private class AddSection extends JPanel implements ActionListener
	{
		private static final long serialVersionUID = 1L;

		private JButton           addFile;
		private JButton           addUrl;
		private JButton           remove;
		private JButton           save;

		public AddSection()
		{
			this.setLayout(new GridLayout(0, 3, 5, 5));

			this.add(new JLabel());
			this.add(addFile = new JButton("Add from file..."));
			this.add(new JLabel());

			this.add(new JLabel());
			this.add(addUrl = new JButton("Add from url..."));
			this.add(new JLabel());

			this.add(new JLabel());
			this.add(remove = new JButton("Remove"));
			this.add(new JLabel());

			this.add(new JLabel());
			this.add(save = new JButton("Save"));
			this.add(new JLabel());

			addFile.addActionListener(this);
			addUrl.addActionListener(this);
			remove.addActionListener(this);
			save.addActionListener(this);

			this.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Color.BLACK, 2),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		}

		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == addFile)
			{
				JFileChooser chooser = new JFileChooser(new RelativeFile(""));
				chooser.setFileHidingEnabled(false);
				chooser.setMultiSelectionEnabled(true);
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				chooser.setFileFilter(new FindJarFiles());
				if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				{
					File[] files = chooser.getSelectedFiles();
					for (File file : files) {
						listData.addElement(file.getAbsolutePath());
					}
					dirty = true;
				}

			}
			else if (e.getSource() == addUrl)
			{
				String input = JOptionPane.showInputDialog(this, "Please type the url here.");

				if (input != null)
				{
					listData.addElement(input);
					dirty = true;
				}
			}
			else if (e.getSource() == remove)
			{
				int[] selected = list.getSelectedIndices();

				if (selected != null)
				{
					// for(int i = selected.length - 1; i >= 0; i++)
					for (int aSelected : selected) {
						listData.remove(aSelected);
					}

					if (selected[0] >= listData.getSize()) {
						list.setSelectedIndex(listData.getSize() - 1);
					} else {
						list.setSelectedIndex(selected[0]);
					}

					dirty = true;
				}
			}
			else if (e.getSource() == save)
			{
				try
				{
					save();
				}
				catch (IOException exception)
				{
					JOptionPane.showMessageDialog(null, "An error occurred writing to the file: "
							+ exception.toString());
				}

			}
		}
	}

	private class FindJarFiles extends FileFilter implements java.io.FileFilter
	{

		public boolean accept(File pathname)
		{
			return pathname.isDirectory() || pathname.getName().matches(".*\\.jar");
		}

		public String getDescription()
		{
			return "JavaOp2 Plugin Files (*.jar)";
		}
	}

	private void save() throws IOException
	{
		File pluginFile = JavaOpFileStuff.getPluginPathsFile();

		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(pluginFile)));
		Enumeration elements = listData.elements();
		while (elements.hasMoreElements()) {
			out.println(elements.nextElement());
		}

		out.close();

		dirty = false;

	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowClosed(WindowEvent e)
	{
	}

	public void windowClosing(WindowEvent e)
	{
		if (dirty)
		{
			int result = JOptionPane.showConfirmDialog(this, "Do you want to save before exiting?",
					"Save?", JOptionPane.YES_NO_CANCEL_OPTION);

			if (result == JOptionPane.CANCEL_OPTION) {
				return;
			} else if (result == JOptionPane.YES_OPTION) {
				try {
					save();
					System.exit(0);
				} catch (IOException exception) {
					JOptionPane.showMessageDialog(null, "An error occurred writing to the file: "
							+ exception.toString());
				}
			}
		}

		System.exit(0);
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public void windowDeiconified(WindowEvent e)
	{
	}

	public void windowIconified(WindowEvent e)
	{
	}

	public void windowOpened(WindowEvent e)
	{
	}

	public static void main(String args[])
	{
		JavaOpFileStuff.setBaseDirectory();
		PluginManager.initialize(false);

		new ConfigurePlugins();
	}

}
