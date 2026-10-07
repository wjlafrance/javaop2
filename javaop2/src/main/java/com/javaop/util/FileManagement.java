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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
		List<String> lines = new ArrayList<>();

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
		for (String l : lines) {
			out.println(l);
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

		for (String aData : data) {
			out.println(aData);
		}

		out.close();
	}

	public static List<String> getUniqueLines(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			return Collections.emptyList();
		}

		return Uniq.uniq(getFile(file));
	}

	public static List<String> getFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			return new ArrayList<>();
		}

		BufferedReader in = new BufferedReader(new FileReader(file));
		List<String> lines = new ArrayList<>();

		String line;
		while ((line = in.readLine()) != null) {
			lines.add(line);
		}
		in.close();

		return lines;
	}

	public static List<File> search(File base, String pattern)
	{
		if (!base.exists())
		{
			base.getParentFile().mkdirs();
			return new ArrayList<>();
		}

		List<File> ret = new ArrayList<>();

		if (base.isDirectory())
		{
			File[] files = base.listFiles();

			for (File file : files) {
				if (file.isDirectory()) {
					ret.addAll(search(file, pattern));
				} else if (file.getName().matches(pattern)) {
					ret.add(new File(file.getAbsolutePath()));
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
		List<String> oldData = getFile(oldFile);

		String[] oldArray = oldData.toArray(new String[0]);
		setFile(newFile, oldArray);
	}

	public static void deleteFile(File file)
	{
		file.delete();
	}
}
