/*
 * Created on Aug 5, 2004
 *
 * By iago
 */
package com.javaop.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


/**
 * This is a file designed to hold configuration data. It generates a file that
 * looks something like this:
 *
 * [default] a=b c=d e=f
 *
 * [section 3] other=thing this=that
 *
 * etc. The sections, as well as the names inside the sections are alphabetical
 * order. The keys are no case sensitive, but the values are. This will
 * automatically be saved and loaded, if necessary.
 *
 * It is also nested into a hashtable for clean "Sections".
 *
 * *** If this class fails to load, it will print an error and die. If any piece
 * of the data fails to load, it *** will print an error and die.
 *
 */

public class PersistantMap
{
	private final File   tempFile   = new File(System.getProperty("user.home") + "/.javaop2-tmp-" + Math.random());
	private final File   file;
	private final String comment;
	private long         lastUpdate = 0;

	private Hashtable<String, Properties> sections = new Hashtable<>();


	/**
	 * If an IOException is thrown, that means that there was no data file
	 * found, and it has been created. If this happens and you have some default
	 * data, you should probably load it up.
	 */
	public PersistantMap(File file, String comment)
	{
		this.file = file;
		this.comment = comment;

		load();

		save();
	}

	private void load()
	{
		if (lastUpdate >= file.lastModified()) {
			return;
		}

		synchronized (this)
		{
			try
			{
				// System.err.println("Loading");

				Hashtable<String, Properties> temp = new Hashtable<>();
				Properties currentSection = new Properties();
				String sectionName = "default";

				BufferedReader in = new BufferedReader(new FileReader(file));

				String line;
				while ((line = in.readLine()) != null)
				{
					// System.out.println("LINE: " + line);
					line = line.trim();

					// Check if it's an empty line or a comment
					if (line.length() == 0) {
						continue;
					}
					if (line.charAt(0) == '#') {
						continue;
					}

					// Check if it's a new section
					if (line.matches("\\[.*\\]"))
					{
						temp.put(sectionName, currentSection);
						currentSection = new Properties();
						sectionName = line.trim().substring(1, line.length() - 1).toLowerCase();
						// System.err.println("Loading section: " +
						// sectionName);
					}
					else if (line.matches(".+=.*"))
					{
						int location = line.replaceFirst("[^\\\\]=.*", "").length() + 1;

						String name = line.substring(0, location).toLowerCase().replaceAll("\\\\=",
																						   "=");
						String value = line.substring(location + 1);

						currentSection.setProperty(name, StringEncoder.decode(value).toString());
						// System.err.println("Loading property: " + name +
						// " = " + value);
					}
					else
					{
						// System.err.println("Invalid line in file " + file +
						// ":");
						// System.err.println(line);
					}
				}

				// Save the final section
				temp.put(sectionName, currentSection);

				// Close the file
				in.close();

				// Copy back to our variable
				sections = temp;

				lastUpdate = System.currentTimeMillis();
			}
			catch (FileNotFoundException e)
			{
				// System.err.println("ERROR: Unable to load settings: " + e);
				// System.err.println("(if this is the first time, then it probably created the file)");
			}
			catch (Exception e)
			{
				// System.err.println("ERROR: Couldn't open settings file: " +
				// e);
				// System.err.println("Quitting (to make sure you don't lose anything if it's a file error)");
				System.exit(1);
			}
		}
	}

	private void save()
	{
		synchronized (this)
		{
			try
			{
				// System.out.println("Saving to temp file: " + tempFile);
				Object[] sectionArray = sortEnumeration(sections.keys());
				file.getParentFile().mkdirs();
				PrintWriter out = new PrintWriter(new FileWriter(file));

				// Print the headers
				out.println("# " + file.getAbsolutePath());
				out.println("# " + "Also stored  in: " + tempFile);
				out.println("# Created on: " + new Date());
				out.println("# By: " + System.getProperty("user.name"));
				out.println("# Comment: " + comment);

				for (Object aSectionArray : sectionArray) {
					String name = aSectionArray.toString().toLowerCase();

					Properties data = (Properties) sections.get(name);

					// Print a blank line, then a header for the section
					out.println();
					out.println("[" + name + "]");
					// System.err.println("Saving section: " + name);

					// Now get the list of keys from data and add them to a
					// vector
					Object[] keys = sortEnumeration(data.keys());
					for (Object key : keys) {
						out.println(key.toString().toLowerCase().replaceAll("=", "\\\\=") + "="
								+ StringEncoder.encode(data.getProperty((String) key)));
						// System.err.println("Saving value: " + keys[j] + " = "
						// + data.getProperty((String) keys[j]));
					}
				}

				out.close();
				tempFile.renameTo(file);
				tempFile.delete();
				lastUpdate = System.currentTimeMillis();
			}
			catch (IOException e)
			{
				// System.err.println("ERROR: Unable to save settings: " + e);
				e.printStackTrace();
				System.exit(1);
			}
		}
	}

	private String fix(String value)
	{
		return value == null ? "default" : value.toLowerCase();
	}

	private Object[] sortEnumeration(Enumeration e)
	{
		Vector objectVector = new Vector();

		while (e.hasMoreElements()) {
			objectVector.add(e.nextElement());
		}

		Object[] ret = objectVector.toArray();
		Arrays.sort(ret);

		return ret;
	}

	public void set(String sectionName, String key, String value)
	{
		sectionName = fix(sectionName);
		key = fix(key);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			section = new Properties();
		}

		if (value == null) {
			section.remove(key);
		} else {
			section.setProperty(key, value);
		}

		sections.put(sectionName, section);

		save();
	}

	public String getNoWrite(String sectionName, String key, String defaultValue)
	{
		sectionName = fix(sectionName);
		key = fix(key);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			return defaultValue;
		}

		return section.getProperty(key, defaultValue);
	}

	public String getWrite(String sectionName, String key, String defaultValue)
	{
		sectionName = fix(sectionName);
		key = fix(key);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null)
		{
			// System.err.println("Section " + sectionName +
			// " not found.. creating");
			section = new Properties();
		}

		String oldValue = section.getProperty(key, null);

		String value = section.getProperty(key, defaultValue);
		section.put(key, value);

		sections.put(sectionName, section);

		if (oldValue == null)
		{
			save();
		}

		return value;
	}

	public void remove(String sectionName, String key)
	{
		sectionName = fix(sectionName);
		key = fix(key);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			return;
		}

		section.remove(key);
		if (section.size() == 0) {
			sections.remove(sectionName);
		}

		save();
	}

	public Enumeration propertyNames(String sectionName)
	{
		sectionName = fix(sectionName);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			return null;
		}

		return section.keys();
	}

	public Enumeration sectionNames()
	{
		load();

		return sections.keys();
	}

	public int size(String sectionName)
	{
		load();

		sectionName = fix(sectionName);

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			return 0;
		}

		return section.size();
	}

	public int sections()
	{
		load();

		return sections.size();
	}

	public boolean contains(String sectionName, String key)
	{
		sectionName = fix(sectionName);
		key = fix(key);

		load();

		Properties section = (Properties) sections.get(sectionName);
		if (section == null) {
			return false;
		}

		return section.containsKey(key);
	}

	/** This will return one section. The section will NOT be updated. */
	public Properties getSection(String section)
	{
		section = fix(section);

		load();

		return ((Properties) sections.get(section));
	}
}
