package plugin_interfaces;
import java.io.IOException;

import exceptions.PluginException;

/*
 * Created on Dec 1, 2004
 * By iago
 */

/**
 * @author iago
 * These are the processed events, after they've been through anti-flood protection and any filtering and the user has 
 * been physically added to the channel and everything.  This is the best place to actually display and use the events,
 * but the Raw event handler is the better place to go if you need real control. 
 */
public interface EventCallback extends AbstractCallback
{
    public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    
    public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    
    public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException;
    public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException;
}
