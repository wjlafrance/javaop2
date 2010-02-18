/*
 * Created on Jan 17, 2005 By iago
 */
package com.javaop.SwingGui.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


/**
 * @author iago
 * 
 */
public class TextFieldHistory extends JTextField implements KeyListener
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    final private String[]    history;
    final private int         size;

    int                       position         = 0;

    public TextFieldHistory(int historySize)
    {
        super();

        this.size = historySize;
        history = new String[size];

        this.addKeyListener(this);
    }

    public TextFieldHistory(int historySize, String text)
    {
        super(text);

        this.size = historySize;
        history = new String[size];

        this.addKeyListener(this);
    }

    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP)
        {
            history[position] = this.getText();
            position--;
            position = (position + size) % size;
            super.setText(history[position]);
            highlight();
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN)
        {
            history[position] = this.getText();
            position++;
            position = position % size;
            super.setText(history[position]);
            highlight();
        }
    }

    public void keyReleased(KeyEvent e)
    {
    }

    private void highlight()
    {
        this.selectAll();
    }

    public void keyTyped(KeyEvent e)
    {

    }

    public void setText(String str)
    {
        history[position] = getText();
        position++;
        position = position % size;
        super.setText(str);
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new GridLayout(2, 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 300);
        frame.setVisible(true);

        JButton button = new JButton("*click*");

        final TextFieldHistory test = new TextFieldHistory(500);

        frame.getContentPane().add(test);
        frame.getContentPane().add(button);
        frame.pack();

        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                test.setText("");
            }
        });
    }

}
