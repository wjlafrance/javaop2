package plugin_interfaces;
import java.io.IOException;

import util.BNetPacket;
import exceptions.PluginException;

/*
 * Created on Dec 1, 2004
 * By iago
 */

/**
 * @author iago
 *
 */
public interface PacketCallback extends AbstractCallback
{
    public final boolean ABORT = false;
    public final boolean CONTINUE = true;
    
    /** This is called when a packet is about to be sent or received.  It can be changed/dropped here. 
     * If null is returned, the packet is dropped.  If the packet is changed, it stays changed. */
    public BNetPacket processingPacket(BNetPacket buf, Object data) throws IOException, PluginException;
    
    /** This is called when a packet has completed being send , and it can no longer be modified/dropped. */
    public void processedPacket(BNetPacket buf, Object data) throws IOException, PluginException;
}
