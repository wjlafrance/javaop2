/*
 * Created on Apr 18, 2005 By iago
 */
package settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import callback_interfaces.StaticExposedFunctions;


public class PluginPanel extends JPanel
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public PluginPanel(String plugin, StaticExposedFunctions funcs)
    {
        // Set up the components
        JTextField author = new JTextField(funcs.pluginGetAuthor(plugin) + "<"
                + funcs.pluginGetEmail(plugin) + ">");
        JTextField website = new JTextField(funcs.pluginGetWebsite(plugin));
        JTextArea description = new JTextArea(funcs.pluginGetLongDescription(plugin), 3, 25);

        author.setEditable(false);
        website.setEditable(false);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);

        JPanel top = new JPanel();
        JPanel bottom = new JPanel();

        this.setLayout(new BorderLayout());
        this.add(top, BorderLayout.NORTH);
        this.add(bottom, BorderLayout.CENTER);

        top.setLayout(new GridLayout(-1, 2));
        JLabel name = new JLabel(plugin);
        name.setForeground(Color.BLUE);
        top.add(name);
        top.add(new JLabel());
        top.add(new JLabel("Author"));
        top.add(author);
        top.add(new JLabel("Website"));
        top.add(website);

        bottom.setLayout(new BorderLayout());
        bottom.add(new JLabel("Description"), BorderLayout.NORTH);
        bottom.add(new JScrollPane(description), BorderLayout.CENTER);

    }
}
