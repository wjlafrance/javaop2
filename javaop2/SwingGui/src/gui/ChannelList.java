/*
 * Created on Jan 17, 2005 By iago
 */
package com.javaop.SwingGui.gui;

// import gui.Model.Row;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;

import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.javaop.constants.Flags;

import com.javaop.SwingGui.util.GameIcons;
import com.javaop.SwingGui.util.JMultiLineTooltip;


/**
 * @author iago
 * 
 */
public class ChannelList extends JTable
{

    private static final long serialVersionUID = 1L;
    private Model             tableModel;

    /**
     * Create a new instance of table list. Set it up specially to use tooltips,
     * which means we have to do the renderer ourselves and some other nasty
     * stuff.
     */
    public ChannelList(boolean opsOnTop)
    {
        super();

        tableModel = new Model(opsOnTop);
        this.setModel(tableModel);

        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);

        this.setShowGrid(false);
        this.setTableHeader(null);
        this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        this.setBackground(Color.BLACK);
        this.setForeground(Color.WHITE);

        TableColumnModel t = this.getColumnModel();
        t.getColumn(0).setPreferredWidth(24);
        t.getColumn(1).setPreferredWidth(120);
        t.getColumn(2).setPreferredWidth(24);
        t.getColumn(3).setPreferredWidth(48);
    }

    public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
    {
        Component c = super.prepareRenderer(renderer, row, column);

        if (c != null && c instanceof JComponent)
            ((JComponent) c).setToolTipText(getTooltip(row, column) + "");
        return c;
    }

    public JToolTip createToolTip()
    {
        return new JMultiLineTooltip();
    }

    /**
     * This is a wrapper for when a user changes.
     */
    public void addUser(String name, String client, String clan, int ping, int flags)
    {
        tableModel.addUser(name, client, clan, ping, flags);
        repaint();
    }

    /**
     * This is a wrapper for getting the tooltip text
     */
    public String getTooltip(int row, int column)
    {
        return tableModel.getTooltip(row, column);
    }

    /**
     * This is a wrapper for when a user leaves the channel.
     */
    public void removeUser(String name)
    {
        tableModel.removeUser(name);
    }

    /**
     * This clears out the entire list.
     */
    public void clear()
    {
        tableModel.clear();
        repaint();
    }

    public int length()
    {
        return tableModel.getRowCount();
    }

}

class Model extends AbstractTableModel
{
    private static final long serialVersionUID = 1L;
    /** A vector of the users in the table. */
    private Vector            users;
    private boolean           opsOnTop;

    /**
     * Create a new instance of the TableModel. Basically, initialize the vector
     * of users.
     */
    public Model(boolean opsOnTop)
    {
        users = new Vector();
        this.opsOnTop = opsOnTop;
    }

    /**
     * Attempts to remove the user with the specified name from the list of
     * users.
     */
    public Row removeUser(String name)
    {
        int oldRowIndex = users.indexOf(new Row(name, null, null, 0, 0));
        if (oldRowIndex < 0)
            return null;

        Row oldRow = (Row) users.get(oldRowIndex);
        users.remove(oldRow);

        fireTableDataChanged();

        return oldRow;
    }

    /** Find the user */
    public Row findUser(String name)
    {
        int index = users.indexOf(new Row(name, null, null, 0, 0));

        if (index < 0)
            return null;

        return (Row) users.get(index);
    }

    /**
     * A wrapper around setUser().
     */
    public void addUser(String name, String client, String clan, int ping, int flags)
    {
        Row row = findUser(name);

        if (row == null)
        {
            row = new Row(name, client, clan, ping, flags);

            if (opsOnTop && (flags & Flags.USER_CHANNELOP) > 0 || (flags & Flags.USER_BLIZZREP) > 0
                    || (flags & Flags.USER_ADMIN) > 0)
                users.add(0, row);
            else
                users.add(row);
        }
        else
        {
            if (name.length() > 0)
                row.setName(name);

            if (client.length() > 0)
                row.setClient(client);

            row.setPing(ping);
            row.setFlags(flags);

            if (opsOnTop && (flags & Flags.USER_CHANNELOP) > 0 || (flags & Flags.USER_BLIZZREP) > 0
                    || (flags & Flags.USER_ADMIN) > 0)
            {
                users.remove(row);
                users.add(0, row);
            }
        }

        this.fireTableDataChanged();
    }

    public int getRowCount()
    {
        return users.size();
    }

    /** In the forseeable future, we only have 4 columns */
    public int getColumnCount()
    {
        return 4;
    }

    /** Returns the object at the requested row and column. */
    public Object getValueAt(int row, int column)
    {
        if (row < 0 || row >= users.size())
            return null;

        Row rowData = (Row) users.get(row);
        switch (column)
        {
            case 0:
                return rowData.getGameIcon();
            case 1:
                return rowData.getName();
            case 2:
                return rowData.getPingIcon();
            case 3:
                return rowData.getClan();
            default:
                return "Error";
        }
    }

    /** Returns the user object for the requested row and column */
    public String getTooltip(int row, int column)
    {
        if (row < 0 || row > users.size())
            return null;

        Row rowData = (Row) users.get(row);
        switch (column)
        {
            case 0:
                return "Client: " + rowData.getClient();
            case 1:
                return "Username: " + rowData.getName();
            case 2:
                return "Ping: " + rowData.getPing() + "ms";
            case 3:
                return "Clan: " + rowData.getClan();
            default:
                return "Error";
        }
    }

    /** Returns the class of the selected column. */
    public Class getColumnClass(int i)
    {
        switch (i)
        {
            case 0:
                return ImageIcon.class; // Client
            case 1:
                return String.class; // Username
            case 2:
                return ImageIcon.class; // Ping
            case 3:
                return String.class; // Clan
            default:
                return null;
        }
    }

    /**
     * Clears the user list
     */
    public void clear()
    {
        users.clear();
        fireTableDataChanged();
    }

    private class Row
    {
        private String name;
        private String client;
        private String clan;
        private int    ping;
        private int    flags;

        public Row(String name, String client, String clan, int ping, int flags)
        {
            this.name = name;
            this.client = client;
            this.clan = clan;
            this.ping = ping;
            this.flags = flags;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public void setClient(String client)
        {
            this.client = client;
        }

        public String getClient()
        {
            return client;
        }

        public void setClan(String clan)
        {
            this.clan = clan;
        }

        public String getClan()
        {
            return clan;
        }

        public void setPing(int ping)
        {
            this.ping = ping;
        }

        public int getPing()
        {
            return ping;
        }

        public void setFlags(int flags)
        {
            this.flags = flags;
        }

        @SuppressWarnings("unused")
		public int getFlags()
        {
            return flags;
        }

        public ImageIcon getGameIcon()
        {
            if (client == null || client.isEmpty())
                return null;
            else if ((flags & Flags.USER_SQUELCHED) > 0)
                return GameIcons.getIcon("SQUELCHED");
            else if ((flags & Flags.USER_ADMIN) > 0)
                return GameIcons.getIcon("SYSOP");
            else if ((flags & Flags.USER_BLIZZREP) > 0)
                return GameIcons.getIcon("BLIZZARD");
            else if ((flags & Flags.USER_SPEAKER) > 0)
                return GameIcons.getIcon("SPEAKER");
            else if ((flags & Flags.USER_CHANNELOP) > 0)
                return GameIcons.getIcon("OPS");
            else
                return GameIcons.getIcon(client);
        }

        public ImageIcon getPingIcon()
        {
            if ((flags & Flags.USER_NOUDP) > 0)
                return GameIcons.getIcon("DISCONNECTED");

            else if (ping == 0)
                return null;
            else if (ping < 200)
                return GameIcons.getIcon("1g");
            else if (ping < 300)
                return GameIcons.getIcon("2g");
            else if (ping < 400)
                return GameIcons.getIcon("3y");
            else if (ping < 500)
                return GameIcons.getIcon("4y");
            else if (ping < 600)
                return GameIcons.getIcon("5r");

            return GameIcons.getIcon("6r");
        }

        public boolean equals(Object o)
        {
            if ((o instanceof Row) && ((Row) o).getName().equalsIgnoreCase(getName()))
                return true;
            if ((o instanceof String) && ((String) o).equalsIgnoreCase(getName()))
                return true;

            return false;
        }

        public String toString()
        {
            return name;
        }

    }
}
