/*
 * Created on May 14, 2005 By iago
 */
package util.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;


public class JTextFieldNumeric extends JTextField implements KeyListener
{
    private static final long serialVersionUID = 1L;

    public JTextFieldNumeric(String text)
    {
        super(text);

        this.addKeyListener(this);
    }

    public void keyTyped(KeyEvent e)
    {
        char code = e.getKeyChar();

        if (code < '0' || code > '9')
            e.consume();
    }

    public void keyPressed(KeyEvent e)
    {
    }

    public void keyReleased(KeyEvent e)
    {

    }
}
