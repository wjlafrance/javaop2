package com.javaop.BnetEventProcess;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JComponent;

import javax.swing.JCheckBox;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.RawEventCallback;
import com.javaop.util.BnetEvent;
import com.javaop.util.gui.JTextFieldNumeric;


/*
 * Created on Jan 14, 2005 By iago
 */

/**
 * This is a very simple event processor. All it does is pass the events
 * straight to the appropriate handlers in the PublicExposedFunctions.
 * Eventually I'm going to write an anti-flood bot version of this which will
 * queue up and send later.
 * 
 * @author iago
 * 
 */
public class PluginMain extends GenericPluginInterface implements RawEventCallback {
    private PublicExposedFunctions out;

    public void load(StaticExposedFunctions staticFuncs) {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register) {
        this.out = out;

        register.registerRawEventPlugin(this, EID_SHOWUSER, null);
        register.registerRawEventPlugin(this, EID_JOIN, null);
        register.registerRawEventPlugin(this, EID_LEAVE, null);
        register.registerRawEventPlugin(this, EID_WHISPER, null);
        register.registerRawEventPlugin(this, EID_TALK, null);
        register.registerRawEventPlugin(this, EID_BROADCAST, null);
        register.registerRawEventPlugin(this, EID_CHANNEL, null);
        register.registerRawEventPlugin(this, EID_USERFLAGS, null);
        register.registerRawEventPlugin(this, EID_WHISPERSENT, null);
        register.registerRawEventPlugin(this, EID_CHANNELFULL, null);
        register.registerRawEventPlugin(this, EID_CHANNELDOESNOTEXIST, null);
        register.registerRawEventPlugin(this, EID_INFO, null);
        register.registerRawEventPlugin(this, EID_ERROR, null);
        register.registerRawEventPlugin(this, EID_EMOTE, null);
    }

    public void deactivate(PluginCallbackRegister register) {
    }

    public String getName() {
        return "BnetEventProcess";
    }

    public String getVersion() {
        return "2.1.3";
    }

    public String getAuthorName() {
        return "iago";
    }

    public String getAuthorWebsite() {
        return "www.javaop.com";
    }

    public String getAuthorEmail() {
        return "iago@valhallalegends.com";
    }

    public String getShortDescription() {
        return "A simple event parser.";
    }

    public String getLongDescription() {
        return "A very simple event parser which just translates the incoming events (EID_JOIN, for"
        	+ "instance) into the appropriate events that can be understood by the display plugins. "
        	+ "The reason this is separate is so that a filter can be put in the middle that will hide "
        	+ "floodbots and such. If you're using a different event processor, DON'T use this one too. "
        	+ "Otherwise, you'll end up seeing events happening twice.";
    }

    public Properties getDefaultSettingValues() {
        Properties p = new Properties();
        p.setProperty("Ignore floodbots", "true");
        p.setProperty("Time to ignore", "400");
        return p;
    }

    public Properties getSettingsDescription() {
        Properties p = new Properties();
        p.setProperty("Ignore floodbots",
        		"If a user joins and leaves really fast, events from him won't be processed");
        p.setProperty("Time to ignore",
        		"The time, in milliseconds, before the user's events are processed");
        return p;
    }

    public JComponent getComponent(String settingName, String value) {
        if (settingName.equalsIgnoreCase("Ignore floodbots")) {
            return new JCheckBox("", value.equalsIgnoreCase("true") ? true : false);
        } else if (settingName.equalsIgnoreCase("Time to ignore")) {
            return new JTextFieldNumeric(value);
        }

        return null;
    }

    public Properties getGlobalDefaultSettingValues() {
        Properties p = new Properties();
        return p;
    }

    public Properties getGlobalSettingsDescription() {
        Properties p = new Properties();
        return p;
    }

    public JComponent getGlobalComponent(String settingName, String value) {
        return null;
    }

    public BnetEvent eventOccurring(BnetEvent event, Object data) throws IOException, PluginException {
        return event;
    }

    private Hashtable<String, Vector<BnetEvent>> 	queuedMessages
    		= new Hashtable<String, Vector<BnetEvent>>();
    private Hashtable<String, Callback> 			timers
    		= new Hashtable<String, Callback>();
    private Timer     timer          = new Timer();

    public void eventOccurred(BnetEvent event, Object data) throws IOException, PluginException {
        boolean ignore = out.getLocalSettingDefault(getName(), "Ignore floodbots",
        		"true").equalsIgnoreCase("true");

        if (ignore == false) {
            processEvent(event);
        } else {
            int time = Integer.parseInt(out.getLocalSettingDefault(getName(), "Time to ignore", "200"));

            synchronized (this) {
                int code = event.getCode();

                if (code == EID_JOIN) {

                    Vector<BnetEvent> v = new Vector<BnetEvent>();
                    v.add(event);
                    queuedMessages.put(event.getUsername(), v);

                    Callback callback = new Callback(event.getUsername());
                    timers.put(event.getUsername(), callback);
                    timer.schedule(callback, time);
                } else if (code == EID_LEAVE) {
                    if (cancelCallback(event.getUsername()) == false)
                        processEvent(event);
                } else {
                    Vector<BnetEvent> events = (Vector<BnetEvent>) queuedMessages.get(event.getUsername());
                    if (events == null)
                        processEvent(event);
                    else
                        events.add(event);
                }
            }
        }
    }

    private boolean cancelCallback(String username) {
        queuedMessages.remove(username);
        Callback callback = (Callback) timers.remove(username);
        if (callback != null)
            callback.cancel();
        return callback != null;
    }

    private class Callback extends TimerTask {
        private final String username;

        public Callback(String username) {
            this.username = username;
        }

        public void run() {
            synchronized (this) {
                try {
                    Vector<BnetEvent> messages = (Vector<BnetEvent>) queuedMessages.get(username);
                    if (messages == null)
                        return;

                    Enumeration<BnetEvent> e = messages.elements();

                    while (e.hasMoreElements())
                        processEvent((BnetEvent) e.nextElement());

                    cancelCallback(username);

                } catch (Exception e) {
                    out.systemMessage(WARNING, "Error processing event: " + e);
                }
            }
        }
    }

    private void processEvent(BnetEvent event) throws IOException, PluginException {
        String username = event.getUsername();
        String message = event.getMessage();
        int ping = event.getPing();
        int flags = event.getFlags();

        switch (event.getCode()) {
            case EID_TALK:
                out.talk(username, message, ping, flags);
                break;
            case EID_EMOTE:
                out.emote(username, message, ping, flags);
                break;
            case EID_WHISPER:
                out.whisperFrom(username, message, ping, flags);
                break;
            case EID_WHISPERSENT:
                out.whisperTo(username, message, ping, flags);
                break;
            case EID_SHOWUSER:
                out.userShow(username, message, ping, flags);
                break;
            case EID_JOIN:
                out.userJoin(username, message, ping, flags);
                break;
            case EID_LEAVE:
                out.userLeave(username, message, ping, flags);
                break;
            case EID_USERFLAGS:
                out.userFlags(username, message, ping, flags);
                break;
            case EID_ERROR:
            case EID_CHANNELDOESNOTEXIST:
            case EID_CHANNELFULL:
                out.error(username, message, ping, flags);
                break;
            case EID_INFO:
                out.info(username, message, ping, flags);
                break;
            case EID_BROADCAST:
                out.broadcast(username, message, ping, flags);
                break;
            case EID_CHANNEL:
                out.channel(username, message, ping, flags);
                break;
        }
    }

}
