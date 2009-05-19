package gui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/*
 * Created on Feb 17, 2005
 * By iago
 */

/**
 * @author iago
 *
 */
public class LoadWebsite extends Thread implements ActionListener, HyperlinkListener 
{
    private final String site;
    private JFrame window;
    private JEditorPane editor;
    
    public LoadWebsite(String site)
    {
        this.site = site;
        this.setName("Load-Website");
        this.start();
    }
    
    public void run()
    {
        try
        {
            window = new JFrame(site);
            
            JPanel form = new JPanel();
            window.getContentPane().add(form);
            
            form.setLayout(new BorderLayout());
            
            JButton close = new JButton("Close");
            close.addActionListener(this);
            
            editor = new JEditorPane(site);
            editor.setEditable(false);
            editor.addHyperlinkListener(this);
            
            form.add(new JScrollPane(editor), BorderLayout.CENTER);
            form.add(close, BorderLayout.SOUTH);
            
            
            window.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            window.setSize(450, 450);
            window.setVisible(true);
        }
        catch(IOException exc)
        {
            JOptionPane.showMessageDialog(window, "Error loading site: " + exc);
        }
        
        
    }
    
    public void actionPerformed(ActionEvent e)
    {
        window.dispose();
    }

    public void hyperlinkUpdate(HyperlinkEvent e)
    {
        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
        {
            try
            {
                editor.setPage(e.getURL());
            }
            catch(IOException exc)
            {
                JOptionPane.showMessageDialog(window, "Error loading site: " + exc);
            }
        }
    }

}
