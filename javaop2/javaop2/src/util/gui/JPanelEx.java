/*
 * Created on Mar 20, 2005 By iago
 */

package com.javaop.util.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;


/** This is a special Gui class I wrote for GridBag stuff */
public class JPanelEx extends JPanel implements Scrollable
{
    private static final long   serialVersionUID = 1L;
    private final GridBagLayout layout;

    public JPanelEx(GridBagLayout layout)
    {
        super();

        this.layout = layout;
        this.setLayout(layout);
    }

    public void add(JComponent c, GridBagConstraints con)
    {
        layout.setConstraints(c, con);
        this.add(c);
    }

    // ---------------

    public Dimension getPreferredScrollableViewportSize()
    {
        return this.getPreferredSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        if (orientation == SwingConstants.VERTICAL)
            return visibleRect.height - 10;

        return visibleRect.width - 10;
    }

    public boolean getScrollableTracksViewportHeight()
    {
        return false;
    }

    public boolean getScrollableTracksViewportWidth()
    {
        return true;
    }

    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
        if (orientation == SwingConstants.VERTICAL)
        {
            int unit = visibleRect.height / 10;
            return (unit == 0 ? 1 : (unit > 20 ? 20 : unit));
        }

        int unit = visibleRect.width / 10;
        return (unit == 0 ? 1 : (unit > 20 ? 20 : unit));
    }
}