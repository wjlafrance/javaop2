/*
 * Created on Dec 6, 2004
 * By iago
 */
package bot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import pluginmanagers.PluginRegistration;
import util.BNetEvent;
import util.BNetPacket;
import util.TimeoutSocket;
import callback_interfaces.PublicExposedFunctions;
import constants.ErrorLevelConstants;
import constants.PacketConstants;
import exceptions.InvalidCDKey;
import exceptions.InvalidPassword;
import exceptions.PluginException;

/** This runs as a separate thread.  It is basically a receive loop.  It receives messages
 * from the socket, and calls back to the receivedPacket callback.  All it stores is a 
 * handle to the Callbacks class, and the input stream that it's reading from.
 *  
 * @author iago
 *
 */
public class PacketThread extends Thread
{
    final private PluginRegistration callbacks;
    final private PublicExposedFunctions out;

    protected Socket s = null;  
    protected OutputStream output = null;
    protected InputStream input;
    
    private boolean stop = false;
    
    public PacketThread(PluginRegistration callbacks, PublicExposedFunctions out)
    {
        this.callbacks = callbacks;
        this.out = out;
        
        this.setName("Packet-thread-" + out.getName());
    }
    
    public void stopThread()
    {
        synchronized(this)
        {
            stop = true;
            
            try { input.close(); } catch (Exception e) { }
            try { output.close(); } catch (Exception e) { }
            try { s.close(); } catch (Exception e) { }
        }
    }
    
    public void run()
    {
        // Connect
        
        try
        {
            String server = out.getLocalSettingDefault(null, "server", "uswest.battle.net");
            int port = Integer.parseInt(out.getLocalSettingDefault(null, "port", "6112"));
            
            if(callbacks.connecting(server, port) == false)
                return;
            
            out.lock();
            
            out.systemMessage(ErrorLevelConstants.INFO, "Resolving ip for server: " + server);
            
            // Get a list of addresses
            InetAddress []addresses = InetAddress.getAllByName(server);
            if(stop)
                return;
            
            out.systemMessage(ErrorLevelConstants.INFO, "Resolves to " + addresses.length + " different addresses");
            
            int address = (int)(Math.random() * addresses.length);
            
            out.systemMessage(ErrorLevelConstants.INFO, "Choosing address " + address + " [" + addresses[address] + "]");
            out.systemMessage(ErrorLevelConstants.INFO, "Attempting to connect");
            
            s = TimeoutSocket.getSocket(addresses[address].getHostAddress(), port, Integer.parseInt(BotCoreStatic.getInstance().getGlobalSettingDefault(null, "timeout", "5000")));
            
            if(stop)
                return;
            
            callbacks.systemMessage(ErrorLevelConstants.INFO, "Connected to " + s.getRemoteSocketAddress());
            
            out.putLocalVariable("address", addresses[address].getAddress());
            
            output = s.getOutputStream();
            input = s.getInputStream();
            
            callbacks.connected(server, port);
        }
        catch(IOException e)
        {
            if(stop)
                return;
            out.systemMessage(ErrorLevelConstants.ERROR, "Connect failed: " + e);
            disconnected();
            return;
        }
        catch(PluginException e)
        {
            if(stop)
                return;
            out.systemMessage(ErrorLevelConstants.ERROR, "Connect failed: " + e);
            disconnected();
            return;
        }
        
        if(stop)
            return;
        
        try
        {
            out.systemMessage(ErrorLevelConstants.INFO, "Beginning to receive packets");
            // Loop and receive packets
            while(true)
            {
                // This try loop will catch exceptions that are thrown from plugins.  If an IOException occurs,
                // it assumes that it's lost the Battle.net connection and calls the disconnect() function and
                // kills this thread
                try
                {
                    int FF = input.read();
                    byte code = (byte) input.read();
                    int len1 = (input.read() & 0x000000FF);
                    int len2 = (input.read() & 0x000000FF) << 8;
    
                    if(FF == -1)
                        throw new IOException("Connection lost");
                        
                    if(FF != 0x000000FF)
                        throw new IOException("Packet didn't start with 0xFF (it started with 0x" + Integer.toHexString(FF) + ") -- Battle.net broke or something.");
                    
                    int length = len1 | len2;
                    
                    byte []packet = new byte[length - 4];
    
                    for(int i = 0; i < packet.length; i++)
                        packet[i] = (byte)input.read();
                    
                    BNetPacket buf = new BNetPacket(code);
                    buf.add(packet);
                    
                    buf = callbacks.processingIncomingPacket(buf);
                    if(stop)
                        return;

                    if(buf == null)
                        continue;
                    
                    if(code == PacketConstants.SID_CHATEVENT)
                    {
                        BNetEvent event = new BNetEvent(buf);
                        event = callbacks.eventOccurring(event);
                        
                        if(event == null)
                            continue;
                        
                        callbacks.eventOccurred(new BNetEvent(event));
                    }
                    else
                    {
    	                callbacks.processedIncomingPacket(buf);
                    }
    	                
                    if(stop)
                        return;

                }
                catch(InvalidPassword e)
                {
                    throw e;
                }
                catch(InvalidCDKey e)
                {
                    throw e;
                }
                catch(PluginException e)
                {
                    if(stop)
                        return;

                    callbacks.pluginException(e);
                }
                catch(IOException e)
                {
                    throw e;
                }
                catch(Exception e)
                {
                    if(stop)
                        return;

                    
                    callbacks.unknownException(e);
                }
            } // while(true)
        }
        catch(InvalidPassword e)
        {
            if(stop)
                return;
            
            callbacks.badPassword(e);
        }
        catch(InvalidCDKey e)
        {
            if(stop)
                return;
            
            callbacks.badCDKey(e);
        }
        catch(IOException e)
        {
            if(stop)
                return;
            
            callbacks.ioexception(e);
        }
        
        disconnected();
    }
    
    private void disconnected() 
    {
        try { input.close(); } catch (Exception e) { }
        try { output.close(); } catch (Exception e) { }
        try { s.close(); } catch (Exception e) { }
        
        callbacks.disconnected();
        stop = true;
    }
    
    public void send(byte []data) throws IOException
    {
        if(output != null)
        {            
            output.write(data);
            output.flush();
        }
    }
    
    public void setTcpNoDelay(boolean delay) throws IOException
    {
        s.setTcpNoDelay(delay);
    }
}
