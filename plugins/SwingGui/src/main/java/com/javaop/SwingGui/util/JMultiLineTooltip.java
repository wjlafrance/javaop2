package com.javaop.SwingGui.util;

// JMultiLineTooltip.java
import javax.swing.*;
import javax.swing.plaf.*;

import java.awt.*;
import javax.swing.plaf.basic.BasicToolTipUI;


/**
 * This code creates a multilined tooltip.
 *
 * @author Zafir Anjum
 */

public class JMultiLineTooltip extends JToolTip
{
	private static final long serialVersionUID = 1L;

	String                    tipText;

	JComponent                component;

	public JMultiLineTooltip()
	{
		updateUI();
	}

	public void updateUI()
	{
		setUI(MultiLineToolTipUI.createUI(this));
	}

	public void setColumns(int columns)
	{
		this.columns = columns;
		this.fixedwidth = 0;
	}

	public int getColumns()
	{
		return columns;
	}

	public void setFixedWidth(int width)
	{
		this.fixedwidth = width;
		this.columns = 0;
	}

	public int getFixedWidth()
	{
		return fixedwidth;
	}

	protected int columns    = 0;

	protected int fixedwidth = 0;
}

class MultiLineToolTipUI extends BasicToolTipUI
{
	private static MultiLineToolTipUI sharedInstance = new MultiLineToolTipUI();
	private static JTextArea          textArea;

	protected CellRendererPane        rendererPane;

	public static ComponentUI createUI(JComponent c)
	{
		return sharedInstance;
	}

	public MultiLineToolTipUI()
	{
		super();
	}

	public void installUI(JComponent c)
	{
		super.installUI(c);
		rendererPane = new CellRendererPane();
		c.add(rendererPane);
	}

	public void uninstallUI(JComponent c)
	{
		super.uninstallUI(c);

		c.remove(rendererPane);
		rendererPane = null;
	}

	public void paint(Graphics g, JComponent c)
	{
		Dimension size = c.getSize();

		textArea.setBackground(Color.GRAY);
		textArea.setForeground(Color.WHITE);
		textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));

		rendererPane.setBackground(Color.BLACK);
		rendererPane.setForeground(Color.BLACK);

		rendererPane.paintComponent(g, textArea, c, 0, 0, size.width, size.height, true);
	}

	public Dimension getPreferredSize(JComponent c)
	{
		String tipText = ((JToolTip) c).getTipText();
		if (tipText == null)
			return new Dimension(0, 0);
		textArea = new JTextArea(tipText);
		rendererPane.removeAll();
		rendererPane.add(textArea);
		textArea.setWrapStyleWord(true);
		int width = ((JMultiLineTooltip) c).getFixedWidth();
		int columns = ((JMultiLineTooltip) c).getColumns();

		if (columns > 0)
		{
			textArea.setColumns(columns);
			textArea.setSize(0, 0);
			textArea.setLineWrap(true);
			textArea.setSize(textArea.getPreferredSize());
		}
		else if (width > 0)
		{
			textArea.setLineWrap(true);
			Dimension d = textArea.getPreferredSize();
			d.width = width;
			d.height++;
			textArea.setSize(d);
		}
		else
			textArea.setLineWrap(false);

		Dimension dim = textArea.getPreferredSize();

		dim.height += 6;
		dim.width += 6;
		return dim;
	}

	public Dimension getMinimumSize(JComponent c)
	{
		return getPreferredSize(c);
	}

	public Dimension getMaximumSize(JComponent c)
	{
		return getPreferredSize(c);
	}
}