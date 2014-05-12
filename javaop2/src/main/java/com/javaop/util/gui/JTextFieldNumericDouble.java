/*
 * Created on May 14, 2005 By iago
 */
package com.javaop.util.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;


public class JTextFieldNumericDouble extends JTextField implements KeyListener
{
	private static final long serialVersionUID = 1L;

	public JTextFieldNumericDouble(String text)
	{
		super(text);

		this.addKeyListener(this);
	}

	public void keyTyped(KeyEvent e)
	{
		char code = e.getKeyChar();

		if ((code < '0' || code > '9') && code != '.')
			e.consume();
	}

	public void keyPressed(KeyEvent e)
	{
	}

	public void keyReleased(KeyEvent e)
	{

	}
}
