package com.javaop.SwingGui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Point;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

import com.javaop.exceptions.CommandUsedIllegally;
import com.javaop.exceptions.CommandUsedImproperly;
import com.javaop.exceptions.LoginException;
import com.javaop.exceptions.PluginException;

import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.ErrorCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GuiCallback;
import com.javaop.plugin_interfaces.SystemMessageCallback;
import com.javaop.plugin_interfaces.OutgoingTextCallback;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.LoudnessConstants;
import com.javaop.constants.PriorityConstants;

import com.javaop.users.Statstring;
import com.javaop.util.BnetEvent;
import com.javaop.util.BnetPacket;
import com.javaop.util.ColorConstants;
import com.javaop.util.PadString;
import com.javaop.util.Timestamp;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;

import com.javaop.SwingGui.util.GameIcons;
import com.javaop.SwingGui.gui.ChannelList;
import com.javaop.SwingGui.gui.ColorTextArea;
import com.javaop.SwingGui.gui.TextFieldHistory;


/**
 * Panel - Each panel is a subclass of JInternalFrame, so it can be inserted
 * into a JDesktop - Has to be informed of events/information in the channel
 * [Implement EventCallback?] - Has to be informed of SystemMessage's [Implement
 * GuiCallback] - Has to be informed of updates to user menus [GuiCallback] -
 * Keeps a list of all user menus - Needs access to PublicExposedFunctions for
 * sending outgoing text / requests - Allow access to its PublicExposedFunctions
 * for the frame / menu - TODO Can process profile requests -- NO! Do this via
 * PROFILE but DON'T FORGOT
 */

public class JavaOpPanel extends JInternalFrame implements FocusListener,
		InternalFrameListener, MouseListener, ActionListener, GuiCallback,
		EventCallback, OutgoingTextCallback, SystemMessageCallback,
		ErrorCallback, CommandCallback
{
    private static final long            serialVersionUID = 1L;

    private final PublicExposedFunctions 	out;
    final private ColorTextArea          	chatWindow;
    final private ChannelList            	channelList;
    final private TextFieldHistory       	input;
    final private JButton                	send;
    final private JLabel                 	channelName;
    final private JavaOpBotMenu          	menu;
    final private JavaOpUserMenu         	userMenu;
    private String                 			name             = "Swing Gui";
    private String                       	channel          = "<Not Logged In>";
    private String                       	lastWhisperTo    = null;
    private String                       	lastWhisperFrom  = null;

    public JavaOpPanel(PublicExposedFunctions out, FocusTraversalPolicy policy)
    {
        super(out.getName(), true, true, true, true);
        this.out = out;

        // Create the bot's menu bar
        this.setJMenuBar(menu = new JavaOpBotMenu(out, this));

        // Create the user menu
        userMenu = new JavaOpUserMenu(out);

        // Set the bot's icon (why not?)
        this.setFrameIcon(GameIcons.getIcon(out.getLocalSetting(
        		"Battle.net Login Plugin", "Game")));

        // Set up the settings
        this.getContentPane().setLayout(new BorderLayout());

        // We want to know when we get focus
        this.addInternalFrameListener(this);

        // This lets us control-tab to switch bots
        this.setFocusTraversalPolicy(policy);

        // This will select the proper textbox when we get focus
        this.addFocusListener(this);

        // Make sure it doesn't try to dispose itself
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Create our input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(input = new TextFieldHistory(500), BorderLayout.CENTER);
        inputPanel.add(send = new JButton("Send"), BorderLayout.EAST);

        // Create our channel panel
        JScrollPane scroller = new JScrollPane(channelList = new ChannelList(
                out.getLocalSettingDefault(name, "Ops on top", "true")
                .equalsIgnoreCase("true")));
        JPanel channelPanel = new JPanel();
        channelPanel.setLayout(new BorderLayout());
        channelPanel.add(channelName = new JLabel("Channel", JLabel.CENTER),
        		BorderLayout.NORTH);
        channelPanel.add(scroller, BorderLayout.EAST);
        
        // Set up the ColorTextArea
        int maxChars = Integer.parseInt(out.getLocalSettingDefault(name,
        		"chat max length", "100000"));
        int cutTo = Integer.parseInt(out.getLocalSettingDefault(name,
        		"chat reset length", "80000"));
        boolean holdAtBottom = out.getLocalSettingDefault(name,
        		"chat max length", "false").equalsIgnoreCase("true");
        String fontName = out.getLocalSettingDefault(name, "font", "Serif");
        int fontSize = Integer.parseInt(out.getLocalSettingDefault(name,
        		"fontsize", "15"));
        chatWindow = new ColorTextArea(Color.BLACK, Color.WHITE, maxChars,
        		cutTo, holdAtBottom);
        chatWindow.setFont(new Font(fontName, Font.PLAIN, fontSize));
        chatWindow.setMargin(new Insets(5, 5, 5, 5));

        // Add our objects to the main form
        this.getContentPane().add(new JScrollPane(chatWindow),
        		BorderLayout.CENTER);
        this.getContentPane().add(channelPanel, BorderLayout.EAST);
        this.getContentPane().add(inputPanel, BorderLayout.SOUTH);

        // Set the panel opaque, then set some colors
        this.setOpaque(true);
        this.setBackground(Color.BLACK);

        channelName.setOpaque(true);
        channelName.setBackground(Color.BLACK);
        channelName.setForeground(Color.WHITE);
        channelName.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        channelList.setOpaque(true);
        channelList.setBackground(Color.BLACK);
        channelList.setSelectionBackground(Color.BLACK);
        channelList.setSelectionForeground(Color.LIGHT_GRAY);
        channelList.setRowSelectionAllowed(true);
        channelList.addMouseListener(this);

        chatWindow.setEditable(false);

        this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        input.setBackground(Color.BLACK);
        input.setForeground(Color.WHITE);
        input.setSelectionColor(Color.LIGHT_GRAY);
        input.addActionListener(this);
        input.setCaretColor(Color.WHITE);

        send.setBackground(Color.WHITE);
        send.setForeground(Color.BLACK);
        send.addActionListener(this);

        scroller.setPreferredSize(new Dimension(200, 1000));
        scroller.getViewport().setBackground(Color.BLACK);
    }

    public void registerCallbacks(PluginCallbackRegister register) {
        register.registerEventPlugin(this, null);
        register.registerOutgoingTextPlugin(this, null);
        register.registerErrorPlugin(this, null);

        register.registerGuiPlugin(this, null);

        register.registerSystemMessagePlugin(this, PACKET, EMERGENCY, null);

        register.registerCommandPlugin(this, "reply", 1, false, "U",
        		"<message>", "Whispers a message back to the last person who "
        		+ "sent a message", null);
        register.registerCommandPlugin(this, "rewhisper", 1, false, "U",
        		"<message>", "Whispers a message to the last person a message "
        		+ "was whispered to", null);

        out.addAlias("reply", "r");
        out.addAlias("reply", "re");
        out.addAlias("rewhisper", "rw");

    }

    public void addUser(String user, String client, String clan, int ping,
    		int flags)
    {
        channelList.addUser(user, client, clan, ping, flags);
        channelName.setText(channel + " (" + channelList.getRowCount() + ")");
    }

    public void removeUser(String user) {
        channelList.removeUser(user);
        channelName.setText(channel + " (" + channelList.getRowCount() + ")");
    }

    public void addText(String text) {
        chatWindow.addText(text);
        channelName.setText(channel + " (" + channelList.getRowCount() + ")");
    }

    public void joinChannel(String channel) {
        this.channel = channel;
        channelList.clear();
        channelName.setText(channel + " (" + channelList.getRowCount() + ")");
    }

    public void clear() {
        chatWindow.setText("");
        chatWindow.addText(ColorConstants.getColor("info")
        		+ "Chat window cleared\n");
    }

    private boolean doCommand(String str) throws IOException, PluginException {
        if (str.length() > 2 && str.charAt(0) == '/') {
            String[] commandArg = str.substring(1).split(" ", 2);

            return out.raiseCommand(null, commandArg[0], commandArg.length
            		== 2 ? commandArg[1] : "", LoudnessConstants.SILENT, false);
        }

        return false;
    }

    public void selectInput() {
        input.requestFocus();
        input.selectAll();
    }

    public String getBotName() {
        return out.getName();
    }

    public void select() {
        try {
            this.setSelected(true);
        } catch (PropertyVetoException exc) {
        }
        input.requestFocus();
    }

    public void actionPerformed(ActionEvent e) {
        if (input.getText().length() == 0)
            return;

        try {
            if (out != null) {
                if (doCommand(input.getText()) == false)
                    out.sendTextPriority(input.getText(), PriorityConstants.PRIORITY_VERY_HIGH + 1);
            }
            chatWindow.setSelectionStart(chatWindow.getText().length());
            chatWindow.setSelectionEnd(chatWindow.getText().length());

            input.setText("");
        } catch (Exception exception) {
            input.selectAll();
            exception.printStackTrace();
        }

        input.requestFocus();
    }

    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        String name = (String) channelList.getValueAt(channelList
        		.rowAtPoint(new Point(x, y)), 1);

        if (name == null)
            return;

        if (e.getButton() == MouseEvent.BUTTON3
                || ((e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) > 0))
        {
            userMenu.getMenu(name).show(channelList, x, y);
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void menuItemAdded(String name, String whichMenu, int index,
    		char mnemonic, KeyStroke hotkey, Icon icon,
    		ActionListener callback, Object data)
    {
        menu.addItem(name, whichMenu, index, mnemonic, hotkey, icon, callback);
    }

    public void menuItemRemoved(String name, String whichMenu, Object data) {
        menu.removeItem(name, whichMenu);
    }

    public void menuSeparatorAdded(String whichMenu, Object data) {
        menu.addSeparator(whichMenu);
    }

    public void menuAdded(String name, int index, char mnemonic, Icon icon,
            ActionListener callback, Object data)
    {
        menu.addMenu(name, index, mnemonic, icon, callback);
    }

    public void menuRemoved(String name, Object data) {
        menu.removeMenu(name);
    }

    public void userMenuAdded(String name, int index, Icon icon,
    		ActionListener callback, Object data)
    {
        userMenu.addItem(name, index, icon, callback);
    }

    public void userMenuRemoved(String name, Object data) {
        userMenu.removeItem(name);
    }

    public void userMenuSeparatorAdded(Object data) {
        userMenu.addSeparator();
    }

    private String colorMessage(String message) {
        if (out.getLocalSettingDefault(name, "Colored names", "true")
        		.equalsIgnoreCase("true"))
        {
            Random r = new Random(message.hashCode());
            String green = PadString.padHex(r.nextInt(115) + 70, 2);
            String blue = PadString.padHex(r.nextInt(115) + 70, 2);
            String red = PadString.padHex(r.nextInt(115) + 70, 2);

            String color = red + green + blue;
            message = ColorConstants.COLOR + color + message;
        }

        return message;
    }

    public void talk(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        if ((flags & USER_CHANNELOP) > 0) {
            display(ColorConstants.getColor("Op talk brackets") + "<"
                    + ColorConstants.getColor("Op talk name")
                    + colorMessage(user)
                    + ColorConstants.getColor("Op talk brackets") + "> "
                    + ColorConstants.getColor("Op talk text") + statstring);
        } else if ((flags & USER_BLIZZREP) > 0) {
            display(ColorConstants.getColor("Blizzard talk brackets") + "<"
                    + ColorConstants.getColor("Blizzard talk name")
                    + colorMessage(user)
                    + ColorConstants.getColor("Blizzard talk brackets") + "> "
                    + ColorConstants.getColor("Blizzard talk text")
                    + statstring);
        } else {
            display(ColorConstants.getColor("Talk brackets") + "<"
                    + ColorConstants.getColor("Talk name") + colorMessage(user)
                    + ColorConstants.getColor("Talk brackets") + "> "
                    + ColorConstants.getColor("Talk text") + statstring);
        }
    }

    public void emote(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        display(ColorConstants.getColor("Emote brackets") + "<"
                + ColorConstants.getColor("Emote name") + colorMessage(user)
                + " " + ColorConstants.getColor("Emote message") + statstring
                + ColorConstants.getColor("Emote brackets") + ">");
    }

    public void whisperFrom(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        this.lastWhisperFrom = user;

        display(ColorConstants.getColor("Whisper from brackets") + "<"
                + ColorConstants.getColor("Whisper from name") + "From: "
                + colorMessage(user)
                + ColorConstants.getColor("Whisper from brackets") + "> "
                + ColorConstants.getColor("Whisper from message") + statstring);
    }

    public void whisperTo(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        this.lastWhisperTo = user;

        display(ColorConstants.getColor("Whisper to brackets") + "<"
                + ColorConstants.getColor("Whisper to name") + "To: "
                + colorMessage(user)
                + ColorConstants.getColor("Whisper to brackets") + "> "
                + ColorConstants.getColor("Whisper to message") + statstring);
    }

    public void userShow(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        if (out.getLocalSettingDefault(name, "Show join/leave", "true")
        		.equalsIgnoreCase("true"))
        {
            display(ColorConstants.getColor("User show name")
            		+ colorMessage(user)
            		+ ColorConstants.getColor("User show message")
                    + " is in the channel "
                    + ColorConstants.getColor("User show info")
                    + "(ping: " + ping + ", flags: " + flags + ")");
        }

        Statstring ss = new Statstring(statstring);
        addUser(user, ss.getClient(), ss.getClan(), ping, flags);
    }

    public void userJoin(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        if (out.getLocalSettingDefault(name, "Show join/leave", "true")
        		.equalsIgnoreCase("true"))
        {
            display(ColorConstants.getColor("User join name")
            		+ colorMessage(user)
                    + ColorConstants.getColor("User join message")
                    + " has joined the channel "
                    + ColorConstants.getColor("User join info")
                    + "(ping: " + ping + ", flags: "
                    + flags + ")");
        }

        Statstring ss = new Statstring(statstring);
        addUser(user, ss.getClient(), ss.getClan(), ping, flags);
    }

    public void userLeave(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        if (out.getLocalSettingDefault(name, "Show join/leave", "true")
        		.equalsIgnoreCase("true"))
        {
            display(ColorConstants.getColor("User leave name")
            		+ colorMessage(user)
                    + ColorConstants.getColor("User leave message")
                    + " has left the channel "
                    + ColorConstants.getColor("User leave info")
                    + "(ping: " + ping + ", flags: "
                    + flags + ")");
        }
        removeUser(user);
    }

    public void userFlags(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        if (out.getLocalSettingDefault(name, "Show status updates", "false")
        		.equalsIgnoreCase("true"))
        {
            display(ColorConstants.getColor("User update name")
            		+ colorMessage(user)
                    + ColorConstants.getColor("User update message")
                    + " has had a status update "
                    + ColorConstants.getColor("User update info")
                    + "(ping: " + ping + ", flags: "
                    + flags + ")");
        }

        Statstring ss = new Statstring(statstring);
        addUser(user, ss.getClient(), ss.getClan(), ping, flags);
    }

    public void error(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        display(ColorConstants.getColor("Error") + statstring);
    }

    public void info(String user, String statstring, int ping, int flags) 
    		throws PluginException
    {

    	// filter out away messages
        if (out.getLocalSettingDefault(getName(), "Show away", "false")
        		.equalsIgnoreCase("false"))
        {
            // [21:29:46.534] You are now marked as being away.
            if (statstring.equalsIgnoreCase("You are now marked as being "
            		+ "away."))
                return;
            // [21:29:49.091] You are still marked as being away.
            if (statstring.equalsIgnoreCase("You are still marked as being "
            		+ "away."))
                return;
            // [21:29:50.299] You are no longer marked as away.
            if (statstring.equalsIgnoreCase("You are no longer marked as "
            		+ "away."))
                return;
        }

        display(ColorConstants.getColor("Info") + statstring);

    }

    public void broadcast(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        display(ColorConstants.getColor("Broadcast") + statstring);
    }

    public void channel(String user, String statstring, int ping, int flags)
    		throws PluginException
    {
        display(ColorConstants.getColor("Channel text") + "Joining channel: "
                + ColorConstants.getColor("Channel name") + statstring);
        joinChannel(statstring);
    }

    private void display(String message) {
        addText(ColorConstants.getColor("Timestamp") + Timestamp.getTimestamp()
        		+ message + "\n");
    }

    public String queuingText(String text, Object data) {
        return text;
    }

    public void queuedText(String text, Object data) {
    }

    public String nextInLine(String text, Object data) {
        return text;
    }

    public long getDelay(String text, Object data) {
        return 0;
    }

    public boolean sendingText(String text, Object data) {
        return true;
    }

    public void sentText(String text, Object data) {
        if (text.length() == 0)
            return;

        boolean showCommands = out.getLocalSettingDefault(getName(), 
        		"Show \"/commands\"", "false").equalsIgnoreCase("true");
        boolean isCommand = (text.charAt(0) == '/');

        if (showCommands == true || isCommand == false) {
            display(ColorConstants.getColor("Me talk brackets") + "<"
                    + ColorConstants.getColor("Me talk name")
                    + colorMessage((String) out.getLocalVariable("username"))
                    + ColorConstants.getColor("Me talk brackets") + "> "
                    + ColorConstants.getColor("Me talk text") + text);
        }

    }
    
    private int getMinimumMessageLevel() {
    	String loudness = out.getStaticExposedFunctionsHandle()
				.getGlobalSettingDefault(getName(), "loudness", "debug");

		if(loudness.equalsIgnoreCase("PACKET"))    
			return ErrorLevelConstants.PACKET;
		if(loudness.equalsIgnoreCase("DEBUG"))     
			return ErrorLevelConstants.DEBUG;
		if(loudness.equalsIgnoreCase("INFO"))      
			return ErrorLevelConstants.INFO;
		if(loudness.equalsIgnoreCase("NOTICE"))    
			return ErrorLevelConstants.NOTICE;
		if(loudness.equalsIgnoreCase("WARNING"))   
			return ErrorLevelConstants.WARNING;
		if(loudness.equalsIgnoreCase("ERROR"))     
			return ErrorLevelConstants.ERROR;
		if(loudness.equalsIgnoreCase("CRITICAL"))  
			return ErrorLevelConstants.CRITICAL;
		if(loudness.equalsIgnoreCase("ALERT"))     
			return ErrorLevelConstants.ALERT;
		if(loudness.equalsIgnoreCase("EMERGENCY")) 
			return ErrorLevelConstants.EMERGENCY;
		return 1;
    	
    }

    public void systemMessage(int level, String message, Object data) {
    	int minMessage = getMinimumMessageLevel();
    	
    	if(minMessage > level)
    		return;
    	
        switch (level) {
            case DEBUG:
                display(ColorConstants.getColor("Error 1 Debug")
                    	+ errorLevelConstants[level] + ": " + message);
                break;
            case INFO:
                display(ColorConstants.getColor("Error 2 Info")
                    	+ errorLevelConstants[level] + ": " + message);
                break;
            case ErrorLevelConstants.NOTICE:
                display(ColorConstants.getColor("Error 3 Notice")
                    	+ errorLevelConstants[level] + ": " + message);
                break;
            case ErrorLevelConstants.WARNING:
                display(ColorConstants.getColor("Error 4 Warning")
                    	+ errorLevelConstants[level] + ": " + message);
                break;
            case ErrorLevelConstants.ERROR:
                display(ColorConstants.getColor("Error 5 Error")
                		+ errorLevelConstants[level] + ": " + message);
                break;
            case CRITICAL:
                display(ColorConstants.getColor("Error 6 Critical")
                        + errorLevelConstants[level] + ": " + message);
                break;
            case ALERT:
                display(ColorConstants.getColor("Error 7 Alert")
                		+ errorLevelConstants[level] + ": " + message);
                break;
            case EMERGENCY:
                display(ColorConstants.getColor("Error 8 Emergency")
                        + errorLevelConstants[level] + ": " + message);
                break;
        }

    }

    public void showMessage(String message, Object data) {
        display(message);
    }

    public void unknownPacketReceived(BnetPacket packet, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error unknown packet")
        		+ "Unknown packet received:");
        display(ColorConstants.getColor("Error unknown packet") + packet);
    }

    public void unknownEventReceived(BnetEvent event, Object data)
    {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error unknown event")
        		+ "Unknown event received:");
        display(ColorConstants.getColor("Error unknown event") + event);
    }

    /* Exception displaying */
    
    public void loginException(LoginException e, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error") + "Login Exception:");
        displayError(e);
    }

    public void ioException(IOException e, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error") + "IO Exception:");
        displayError(e);
    }

    public void unknownException(Exception e, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error") + "Exception:");
        displayError(e);
    }

    public void error(Error e, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        display(ColorConstants.getColor("Error") + "Error:");
        displayError(e);
    }

    public void pluginException(PluginException e, Object data) {
    	if (getMinimumMessageLevel() > ErrorLevelConstants.ERROR)
    		return;
        displayError(e);
    }

    /**
     * Displays a stack trace for an exception
     * @param t Exception
     */
    public void displayError(Throwable t) {
        StackTraceElement[] stack = t.getStackTrace();

        display(ColorConstants.getColor("Error") + t);
        for (int i = 0; i < stack.length; i++)
            display(ColorConstants.getColor("Error") + stack[i]);
    }

    public void commandExecuted(String user, String command, String[] args,
    		int loudness, Object data) throws PluginException, IOException,
    		CommandUsedIllegally, CommandUsedImproperly
    {
    	if (command.equalsIgnoreCase("say")) {
    		if (args.length == 0) {
    			throw new CommandUsedImproperly("What do you want to say?",
    					user, command);
    		}
    	} else if (command.equalsIgnoreCase("reply")) {
            if (lastWhisperFrom == null)
                out.sendTextUser(user, "Error: no last incoming whisper",
                		loudness);
            else
                out.sendText("/w " + lastWhisperFrom + " " + args[0]);
        }
        else if (command.equalsIgnoreCase("rewhisper"))
        {
            if (lastWhisperTo == null)
                out.sendTextUser(user, "Error: no last outgoing whisper",
                		loudness);
            else
                out.sendText("/w " + lastWhisperTo + " " + args[0]);
        }
    }

    public void internalFrameOpened(InternalFrameEvent arg0) {
        selectInput();
    }

    public void internalFrameClosing(InternalFrameEvent arg0) {
        try {
            out.getStaticExposedFunctionsHandle().botStop(out.getName());
        } catch (IOException e) {
        }
    }

    public void internalFrameClosed(InternalFrameEvent arg0)
    {
    }

    public void internalFrameIconified(InternalFrameEvent arg0)
    {
    }

    public void internalFrameDeiconified(InternalFrameEvent arg0)
    {
    }

    public void internalFrameActivated(InternalFrameEvent arg0) {
        selectInput();
    }

    public void internalFrameDeactivated(InternalFrameEvent arg0) {
    }

    public void focusGained(FocusEvent arg0) {
        this.selectInput();
    }

    public void focusLost(FocusEvent arg0) {
    }
}
