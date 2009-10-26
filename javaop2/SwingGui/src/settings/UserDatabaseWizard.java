/*
 * Created on Apr 9, 2005 By iago
 */
package settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import callback_interfaces.PublicExposedFunctions;

import util.gui.Gui;


public class UserDatabaseWizard extends JFrame implements ActionListener, ListSelectionListener
{
    /**
	 * 
	 */
    private static final long            serialVersionUID = 1L;
    final private String                 allFlags;
    final private PublicExposedFunctions out;

    final private CheckPanel             checkPanel;
    final private DefaultListModel       model            = new DefaultListModel();
    final private JList                  list             = new JList(model);

    final private JButton                addButton        = new JButton("Add...");
    final private JButton                removeButton     = new JButton("Remove");

    public UserDatabaseWizard(PublicExposedFunctions out)
    {
        this(out, null);
    }

    public UserDatabaseWizard(PublicExposedFunctions out, String user)
    {
        super("User database for " + out.getName());

        this.out = out;

        String[] users = out.dbGetAllUsers();

        allFlags = out.getLocalSettingDefault("user management", "all flags",
                                              "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        for (int i = users.length - 1; i >= 0; i--)
            model.addElement(users[i]);

        JPanel form = new JPanel();
        form.setLayout(new BorderLayout(5, 5));
        form.setBorder(BorderFactory.createLineBorder(form.getBackground(), 5));

        this.getContentPane().add(form);

        JScrollPane scroller = new JScrollPane(list);
        scroller.setPreferredSize(new Dimension(150, 0));

        JLabel l = new JLabel("All changes take effect instantly, so be careful!");

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(1, 2));
        southPanel.add(addButton);
        southPanel.add(removeButton);

        form.add(l, BorderLayout.NORTH);
        form.add(scroller, BorderLayout.WEST);
        form.add(southPanel, BorderLayout.SOUTH);
        form.add(checkPanel = new CheckPanel(allFlags, null), BorderLayout.CENTER);

        addButton.addActionListener(this);
        removeButton.addActionListener(this);

        checkPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        scroller.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        if (user != null)
        {
            list.setSelectedValue(user, true);
            if (list.getSelectedValue() == null)
            {
                model.addElement(user);
                list.setSelectedValue(user, true);
            }
            valueChanged(null);
        }

        list.addListSelectionListener(this);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack();
        Gui.center(this);
        this.setVisible(true);
    }

    public void valueChanged(ListSelectionEvent e)
    {
        String selected = (String) list.getSelectedValue();

        if (selected == null)
        {
            checkPanel.clear();
        }
        else
        {
            checkPanel.set(out.dbGetRawFlags(selected));
            checkPanel.setActiveUser(selected);
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == addButton)
        {
            String user = JOptionPane.showInputDialog(null, "Name or pattern for the user?");
            if (user != null)
                model.addElement(user);
        }
        else if (e.getSource() == removeButton)
        {
            String user = (String) list.getSelectedValue();
            int index = list.getSelectedIndex();
            if (index >= 0)
            {
                model.remove(index);
                if (index < model.size())
                    list.setSelectedIndex(index);
            }

            out.dbDeleteUser(user);
        }
    }

    class CheckPanel extends JPanel implements ChangeListener
    {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1L;
        final private JCheckBox[] checkBoxes;
        final private int         COLS             = 3;
        final private String      allFlags;

        private String            user             = null;

        public CheckPanel(String allFlags, String user)
        {
            this.allFlags = allFlags;
            this.user = user;

            checkBoxes = new JCheckBox[allFlags.length()];

            this.setLayout(new GridLayout(0, COLS));

            int oneColSize = allFlags.length() / COLS + 1;

            for (int i = 0; i < oneColSize; i++)
            {
                for (int j = 0; j < COLS; j++)
                {
                    int thisLocation = i + (j * oneColSize);

                    if (thisLocation >= checkBoxes.length)
                    {
                        this.add(new JLabel());
                    }
                    else
                    {
                        checkBoxes[thisLocation] = new JCheckBox("" + allFlags.charAt(thisLocation));
                        this.add(checkBoxes[thisLocation]);
                        checkBoxes[thisLocation].addChangeListener(this);
                    }
                }
            }
        }

        public void setActiveUser(String user)
        {
            this.user = user;
        }

        private void setCheckbox(JCheckBox box, boolean state)
        {
            box.removeChangeListener(this);
            box.setSelected(state);
            box.addChangeListener(this);
        }

        private void clear()
        {

            for (int i = 0; i < checkBoxes.length; i++)
                setCheckbox(checkBoxes[i], false);
        }

        private void set(String flags)
        {
            clear();

            for (int i = 0; i < flags.length(); i++)
            {
                int flagIndex = allFlags.indexOf(flags.charAt(i));
                if (flagIndex < 0 || flagIndex >= checkBoxes.length)
                    continue;

                if (flagIndex >= 0 && flagIndex < checkBoxes.length)
                    setCheckbox(checkBoxes[flagIndex], true);
            }
        }

        public void stateChanged(ChangeEvent e)
        {
            if (user != null)
            {
                String flags = "";
                for (int i = 0; i < checkBoxes.length; i++)
                    if (checkBoxes[i].isSelected())
                        flags += allFlags.charAt(i);

                out.dbDeleteUser(user);
                out.dbAddFlags(user, flags);
            }
        }
    }
}
