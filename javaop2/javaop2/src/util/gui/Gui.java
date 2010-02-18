/*
 * Gui.java Written by Ron Created 1/17/2005
 */
package com.javaop.util.gui;

import javax.swing.JComponent;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import javax.swing.text.JTextComponent;


/** These are helper gui functions, written to do annoying things in Gui's. */
public class Gui
{
    public static String getTextFromComponent(JComponent c)
    {
        if (c instanceof JTextComponent)
            return ((JTextComponent) c).getText();
        else if (c instanceof AbstractButton)
            return ((AbstractButton) c).isSelected() ? "true" : "false";
        else if (c instanceof JComboBox)
            return ((JComboBox) c).getSelectedItem() + "";
        else if (c instanceof JLabel)
            return ((JLabel) c).getText();
        else
            throw new Error("Unrecognized component: " + c);
    }

    public static void setTextFromComponent(JComponent c, String text)
    {
        if (c instanceof JTextComponent)
            ((JTextComponent) c).setText(text);
        else if (c instanceof AbstractButton)
            ((AbstractButton) c).setSelected(text.equalsIgnoreCase("true") ? true : false);
        else if (c instanceof JComboBox)
            ((JComboBox) c).setSelectedItem(text);
        else
            throw new Error("Unrecognized component: " + c);
    }

    public static void center(Window w)
    {
        Toolkit tool = Toolkit.getDefaultToolkit();
        int width = (int) tool.getScreenSize().getWidth() / 2;
        int height = (int) tool.getScreenSize().getHeight() / 2;
        w.setLocation(width - (w.getWidth() / 2), height - (w.getHeight() / 2));
    }

}
