/*
 * Created on Feb 14, 2005 By iago
 */
package com.javaop.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Vector;


/**
 * @author iago
 *
 */
public class FileManagement
{
	public static void addLine(File file, String line) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(file, true));
		out.println(line);
		out.close();
	}

	public static void removeLine(File file, String remove) throws IOException
	{
		BufferedReader in = new BufferedReader(new FileReader(file));
		Vector<String> lines = new Vector<String>();

		String line;
		while ((line = in.readLine()) != null)
		{
			if (!line.equalsIgnoreCase(remove))
			{
				lines.add(line);
			}
		}
		in.close();

		PrintWriter out = new PrintWriter(new FileOutputStream(file, false));
		Enumeration<String> e = lines.elements();
		while (e.hasMoreElements()) {
			out.println(e.nextElement());
		}

		out.close();
	}

	public static boolean findLine(File file, String search) throws IOException
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(file));

			boolean found = false;
			String line;
			while ((line = in.readLine()) != null && !found) {
				if (line.equalsIgnoreCase(search)) {
					found = true;
				}
			}
			in.close();

			return found;
		}
		catch (FileNotFoundException e)
		{
			return false;
		}
	}

	public static void setFile(File file, String[] data) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileOutputStream(file));

		for (int i = 0; i < data.length; i++) {
			out.println(data[i]);
		}

		out.close();
	}

	public static String[] getUniqueLines(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			return new String[0];
		}

		return Uniq.uniq(getFile(file));
	}

	public static Vector<String> getFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			return new Vector<String>();
		}

		BufferedReader in = new BufferedReader(new FileReader(file));
		Vector<String> lines = new Vector<String>();

		String line;
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();

		return lines;
	}

	public static Vector<File> search(File base, String pattern)
	{
		if (!base.exists())
		{
			base.getParentFile().mkdirs();
			return new Vector<File>();
		}

		Vector<File> ret = new Vector<File>();

		if (base.isDirectory())
		{
			File[] files = base.listFiles();

			for (int i = 0; i < files.length; i++)
			{
				if (files[i].isDirectory()) {
					ret.addAll(search(files[i], pattern));
				} else if (files[i].getName().matches(pattern)) {
					ret.add(new File(files[i].getAbsolutePath()));
				}
			}
		}
		else if (base.exists())
		{
			ret.add(base);
		}

		return ret;
	}

	public static void copyFile(File oldFile, File newFile) throws IOException
	{
		Vector<String> oldData = getFile(oldFile);

		String[] oldArray = (String[]) oldData.toArray(new String[oldData.size()]);
		setFile(newFile, oldArray);
	}

	public static void deleteFile(File file)
	{
		file.delete();
	}
}
