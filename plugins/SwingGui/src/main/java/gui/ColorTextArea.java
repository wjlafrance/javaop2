/*
 * Created on Jan 17, 2005 By iago
 */

package com.javaop.SwingGui.gui;

import java.awt.Color;

import javax.swing.JTextPane;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleContext;
import javax.swing.text.StyleConstants;

import com.javaop.util.ColorConstants;


/**
 * @author iago
 *
 */
public class ColorTextArea extends JTextPane
{
	private static final long  serialVersionUID = 1L;

	private final Color        foreground;

	private final int          maxChars;
	private final int          cutTo;
	private final boolean      holdAtBottom;

	private int                styleNum         = 0;

	private final StringBuffer fullText         = new StringBuffer();

	public ColorTextArea(int maxChars, int cutTo, boolean holdAtBottom)
	{
		this(Color.BLACK, Color.WHITE, maxChars, cutTo, holdAtBottom);
	}

	public ColorTextArea(Color background, Color foreground, int maxChars, int cutTo,
			boolean holdAtBottom)
	{
		super();

		this.foreground = foreground;
		this.maxChars = maxChars;
		this.cutTo = cutTo;
		this.holdAtBottom = holdAtBottom;

		this.setBackground(background);
		this.setForeground(foreground);
		this.setSelectionColor(Color.LIGHT_GRAY);
	}

	public void addText(String str)
	{
		addText(str, true);
	}

	private synchronized void addText(String str, boolean append)
	{
		// Make sure the string has a default color
		str = ColorConstants.getColor("Timestamp") + str;
		if (append == true)
			fullText.append(str);

		int length = this.getText().length();
		boolean goToEnd = holdAtBottom || ((this.getSelectionStart() + 100) > length);

		if (append == false)
			this.setText("");

		StyledDocument document = getStyledDocument();
		Style styles = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		String style = "style" + (++styleNum);

		try
		{
			StyleConstants.setForeground(document.addStyle(style, styles), foreground);

			String[] colorStrings = str.split("\\" + ColorConstants.COLOR);

			for (int i = 0; i < colorStrings.length; i++)
			{
				if (colorStrings[i].length() < 6)
					continue;

				int red = Integer.parseInt("" + colorStrings[i].charAt(0)
						+ colorStrings[i].charAt(1), 16);
				int green = Integer.parseInt("" + colorStrings[i].charAt(2)
						+ colorStrings[i].charAt(3), 16);
				int blue = Integer.parseInt("" + colorStrings[i].charAt(4)
						+ colorStrings[i].charAt(5), 16);

				StyleConstants.setForeground(document.addStyle(style, styles), new Color(red,
						green, blue));
				document.insertString(document.getLength(), colorStrings[i].substring(6),
									  document.getStyle(style));
			}
			chop();

			document.removeStyle(style);

			if (goToEnd)
				this.setSelectionStart(this.getText().length());

		}
		catch (Exception e)
		{
			e.printStackTrace();
			// Ignore bad messages
			try
			{
				document.insertString(document.getLength(), "\n", document.getStyle(style));
			}
			catch (BadLocationException ignored)
			{
			}
		}
	}

	private void chop()
	{
		if (maxChars <= 0)
			return;
		if (cutTo <= 0)
			return;

		try
		{
			if (fullText.length() > maxChars)
			{
				fullText.delete(0, fullText.length() - cutTo);
				this.addText(fullText.toString(), false);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}