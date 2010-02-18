/*
 * Created on Jan 18, 2005 By iago
 */
package com.javaop.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.javaop.util.gui.Gui;


/**
 * @author iago
 * 
 */
public class ColorConstants
{
    public static final char          COLOR = (char) 0x8000;

    // public static final String RED = COLOR + "FF0000";
    // public static final String GREEN = COLOR + "00FF00";
    // public static final String BLUE = COLOR + "0000FF";
    // public static final String LIGHTBLUE = COLOR + "00CCFF";
    // public static final String MAJENTA = COLOR + "FF0080";
    // public static final String PURPLE = COLOR + "FF00FF";
    // public static final String CYAN = COLOR + "00FFFF";
    // public static final String YELLOW = COLOR + "FFFF00";
    // public static final String GRAY = COLOR + "808080";
    // public static final String DARKGRAY = COLOR + "404040";
    // public static final String LIGHTGRAY = COLOR + "C0C0C0";
    // public static final String ORANGE = COLOR + "FF8000";
    // public static final String BLACK = COLOR + "000000";
    // public static final String WHITE = COLOR + "FFFFFF";
    // public static final String AQUAMARINE = COLOR + "7FFFD0";
    // public static final String BEIGE = COLOR + "F5F5E0";
    // public static final String CHARTREUSE = COLOR + "7FFF00";
    // public static final String DARKCYAN = COLOR + "008D8D";
    // public static final String DARKBLUE = COLOR + "00008D";
    // public static final String DARKRED = COLOR + "8D0000";
    // public static final String DARKGREEN = COLOR + "009000";
    // public static final String GOLD = COLOR + "FFd700";
    // public static final String GREENYELLOW = COLOR + "ADFF2F";
    // public static final String HOTPINK = COLOR + "FF69B4";
    // public static final String PLUM = COLOR + "DDA0DD";
    // public static final String ROYALBLUE = COLOR + "4169E1";

    public static final PersistantMap colors;

    static
    {
        colors = new PersistantMap(new RelativeFile("_Colors.txt"),
                "These are the color constants that display on the SwingGUI.  Feel free to modify/play with them.");

        colors.getWrite(null, "Timestamp", "FFFFFF");

        colors.getWrite(null, "Me talk name", "00FFFF");
        colors.getWrite(null, "Me talk text", "FFFFFF");
        colors.getWrite(null, "Me talk brackets", "00FFFF");

        colors.getWrite(null, "Blizzard talk name", "00CCFF");
        colors.getWrite(null, "Blizzard talk text", "00CCFF");
        colors.getWrite(null, "Blizzard talk brackets", "FFFFFF");

        colors.getWrite(null, "Op talk name", "FFFFFF");
        colors.getWrite(null, "Op talk text", "FFFFFF");
        colors.getWrite(null, "Op talk brackets", "FFFFFF");

        colors.getWrite(null, "Talk name", "FFFF00");
        colors.getWrite(null, "Talk text", "FFFFFF");
        colors.getWrite(null, "Talk brackets", "FFFF00");

        colors.getWrite(null, "Emote name", "FFFF00");
        colors.getWrite(null, "Emote message", "FFFF00");
        colors.getWrite(null, "Emote brackets", "FFFF00");

        colors.getWrite(null, "Whisper from name", "C0C0C0");
        colors.getWrite(null, "Whisper from message", "C0C0C0");
        colors.getWrite(null, "Whisper from brackets", "C0C0C0");

        colors.getWrite(null, "Whisper to name", "C0C0C0");
        colors.getWrite(null, "Whisper to message", "C0C0C0");
        colors.getWrite(null, "Whisper to brackets", "C0C0C0");

        colors.getWrite(null, "User show name", "00FF00");
        colors.getWrite(null, "User show message", "00FF00");
        colors.getWrite(null, "User show info", "00FF00");

        colors.getWrite(null, "User join name", "00FF00");
        colors.getWrite(null, "User join message", "00FF00");
        colors.getWrite(null, "User join info", "00FF00");

        colors.getWrite(null, "User leave name", "00FF00");
        colors.getWrite(null, "User leave message", "00FF00");
        colors.getWrite(null, "User leave info", "00FF00");

        colors.getWrite(null, "User update name", "00FF00");
        colors.getWrite(null, "User update message", "00FF00");
        colors.getWrite(null, "User update info", "00FF00");

        colors.getWrite(null, "Error", "FF0000");
        colors.getWrite(null, "Error 1 Debug", "009000");
        colors.getWrite(null, "Error 2 Info", "FFD700");
        colors.getWrite(null, "Error 3 Notice", "FF8000");
        colors.getWrite(null, "Error 4 Warning", "FF8000");
        colors.getWrite(null, "Error 5 Error", "FF0000");
        colors.getWrite(null, "Error 6 Critical", "FF0000");
        colors.getWrite(null, "Error 7 Alert", "FF0000");
        colors.getWrite(null, "Error 8 Emergency", "FF0000");
        colors.getWrite(null, "Error unknown event", "0000FF");
        colors.getWrite(null, "Error unknown packet", "0000FF");

        colors.getWrite(null, "Info", "FFFF00");
        colors.getWrite(null, "Broadcast", "FF8000");
        colors.getWrite(null, "Message box", "7FFFD0");

        colors.getWrite(null, "Channel name", "FFFFFF");
        colors.getWrite(null, "Channel text", "00FF00");

        colors.getWrite(null, "Silent message", "00CCFF");
        colors.getWrite(null, "Clan message", "ADFF2F");
        colors.getWrite(null, "Clan emphasize", "FFFF00");
    }

    public static String getColor(String name)
    {
        return COLOR + colors.getNoWrite(null, name, "FF00FF");
    }

    public static String removeColors(String s)
    {
        return (s.replaceAll(COLOR + "......", ""));
    }

    public static void showColorEditor()
    {
        new ColorConstants().new ColorEditor().setVisible(true);
    }

    private class ColorEditor extends JFrame implements ListSelectionListener, WindowListener
    {
        private static final long   serialVersionUID = 1L;

        private final JList         list;
        private final JColorChooser chooser          = new JColorChooser();
        private String              currentKey       = null;

        public ColorEditor()
        {
            super("Editing colors");

            String[] colorArray = Uniq.uniq(colors.getSection(null).keys());
            list = new JList(colorArray);
            list.addListSelectionListener(this);
            list.setSelectedIndex(0);
            valueChanged(null);

            this.getContentPane().setLayout(new BorderLayout());
            this.getContentPane().add(new JScrollPane(list), BorderLayout.WEST);
            this.getContentPane().add(chooser, BorderLayout.CENTER);
            this.addWindowListener(this);
            this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            this.setSize(570, 330);
            Gui.center(this);
        }

        public void trySave()
        {
            if (currentKey != null)
            {
                String original = colors.getNoWrite(null, currentKey, "");
                Color newColor = chooser.getColor();

                String newString = PadString.padHex(newColor.getRed(), 2)
                        + PadString.padHex(newColor.getGreen(), 2)
                        + PadString.padHex(newColor.getBlue(), 2);

                if (original.equalsIgnoreCase(newString) == false)
                {
                    if (JOptionPane.showConfirmDialog(this, "Save new color?", "Save?",
                                                      JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                    {
                        colors.set(null, currentKey, newString);
                    }
                }
            }
        }

        public void valueChanged(ListSelectionEvent e)
        {
            trySave();

            currentKey = (String) list.getSelectedValue();
            if (currentKey == null)
                return;

            String color = colors.getNoWrite(null, currentKey, "");
            chooser.setColor(Color.decode("#" + color));
        }

        public void windowOpened(WindowEvent e)
        {
        }

        public void windowClosing(WindowEvent e)
        {
            trySave();
            this.dispose();
        }

        public void windowClosed(WindowEvent e)
        {
        }

        public void windowIconified(WindowEvent e)
        {
        }

        public void windowDeiconified(WindowEvent e)
        {
        }

        public void windowActivated(WindowEvent e)
        {
        }

        public void windowDeactivated(WindowEvent e)
        {
        }
    }
}
