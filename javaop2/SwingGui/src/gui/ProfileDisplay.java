/*
 * Created on Apr 19, 2005 By iago
 */
package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import constants.PacketConstants;

import util.BnetPacket;
import util.gui.Gui;

import callback_interfaces.PublicExposedFunctions;


public class ProfileDisplay extends JFrame implements WindowListener
{
    /**
	 * 
	 */
    private static final long            serialVersionUID = 1L;

    private final PublicExposedFunctions out;

    private final String                 name;
    private final String                 sex;
    private final String                 age;
    private final String                 location;
    private final String                 description;

    private final JTextField             nameField;
    private final JTextField             sexField;
    private final JTextField             ageField;
    private final JTextField             locationField;
    private final JTextArea              descriptionField;

    public ProfileDisplay(PublicExposedFunctions out, String name, String sex, String age,
            String location, String description)
    {
        super("Profile for " + name);

        this.out = out;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.location = location;
        this.description = description;

        // Set up the panels
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(5, 5));

        // Set up the widgets
        nameField = new JTextField(name);
        sexField = new JTextField(sex);
        ageField = new JTextField(age);
        locationField = new JTextField(location);
        descriptionField = new JTextArea(description);

        nameField.setEditable(false);
        if (((String) out.getLocalVariable("username")).equalsIgnoreCase(name) == false)
        {
            sexField.setEditable(false);
            ageField.setEditable(false);
            locationField.setEditable(false);
            descriptionField.setEditable(false);
        }

        // Set up the panels
        ValuePanel namePanel = new ValuePanel("Name", nameField, BorderLayout.WEST);
        ValuePanel sexPanel = new ValuePanel("Sex", sexField, BorderLayout.WEST);
        ValuePanel agePanel = new ValuePanel("Age", ageField, BorderLayout.WEST);
        ValuePanel locationPanel = new ValuePanel("Location", locationField, BorderLayout.NORTH);
        ValuePanel descriptionPanel = new ValuePanel("Description", new JScrollPane(
                descriptionField), BorderLayout.NORTH);

        JPanel topTop = new JPanel();
        topTop.setLayout(new GridLayout(1, 3, 5, 5));
        topTop.add(namePanel);
        topTop.add(sexPanel);
        topTop.add(agePanel);
        topPanel.add(topTop, BorderLayout.NORTH);

        topPanel.add(locationPanel, BorderLayout.SOUTH);
        mainPanel.add(descriptionPanel, BorderLayout.CENTER);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        this.getContentPane().add(mainPanel);
        this.setSize(330, 210);
        this.addWindowListener(this);
        Gui.center(this);
        this.setVisible(true);
    }

    private class ValuePanel extends JPanel
    {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;
        private final JComponent  component;

        public ValuePanel(String name, JComponent component)
        {
            this(name, component, BorderLayout.WEST);
        }

        public ValuePanel(String name, JComponent component, String nameLocation)
        {
            this.component = component;

            this.setLayout(new BorderLayout());
            this.add(new JLabel(name), nameLocation);
            this.add(component, BorderLayout.CENTER);
        }

        public JComponent getComponent()
        {
            return component;
        }
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        // Check if it's us, first
        if (((String) out.getLocalVariable("username")).equalsIgnoreCase(name))
        {
            // Check if anything's changed
            if (sex.equals(sexField.getText()) == false || age.equals(ageField.getText()) == false
                    || location.equals(locationField.getText()) == false
                    || description.equals(descriptionField.getText()) == false)
            {
                if (JOptionPane.showConfirmDialog(this, "Save new profile?", "Save?",
                                                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                {
                    BnetPacket write = new BnetPacket(PacketConstants.SID_WRITEUSERDATA);
                    // (DWORD) Number of accounts
                    write.addDWord(1);

                    // (DWORD) Number of keys
                    write.addDWord(4);

                    // (STRING[]) Accounts to update
                    write.addNTString(name);

                    // (STRING[]) Keys to update
                    write.addNTString("profile\\sex");
                    write.addNTString("profile\\age");
                    write.addNTString("profile\\location");
                    write.addNTString("profile\\description");

                    // (STRING[]) New values
                    write.addNTString(sexField.getText());
                    write.addNTString(ageField.getText());
                    write.addNTString(locationField.getText());
                    write.addNTString(descriptionField.getText());

                    try
                    {
                        out.sendPacket(write);
                    }
                    catch (IOException err)
                    {
                        JOptionPane.showMessageDialog(this, "Error writing user data: " + err);
                    }
                }
            }
        }
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
