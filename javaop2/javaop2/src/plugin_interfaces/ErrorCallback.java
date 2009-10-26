/*
 * Created on Dec 1, 2004 By iago
 */
package plugin_interfaces;

import java.io.IOException;

import util.BNetEvent;
import util.BNetPacket;

import exceptions.InvalidCDKey;
import exceptions.InvalidPassword;
import exceptions.PluginException;


/**
 * These are error callbacks that are called when an error occurs. These aren't
 * allowed to throw an exception because that would likely cause infinite
 * recursion. If any of them DO throw an error or exception, it will be caught
 * and displayed to stdout rather than calling one of these.
 * 
 * @author iago
 * 
 */
public interface ErrorCallback extends AbstractCallback
{
    /** This is called if there is a connection problem. */
    public void ioexception(IOException e, Object data);

    /** This is called if an exception makes it to the top level. */
    public void unknownException(Exception e, Object data);

    /**
     * This is called if there is an "error". These should never be handled,
     * they're always something horrible.
     */
    public void error(Error e, Object data);

    /**
     * If an exception is throw from a plugin, besides an IOException (which
     * forces a reconnect), this gets called.
     */
    public void pluginException(PluginException e, Object data);

    /** If the cdkey is invalid, this is called */
    public void badCDKey(InvalidCDKey e, Object data);

    /** If the password is invalid, this is called */
    public void badPassword(InvalidPassword e, Object data);

    /** This is called if an unhandled packet is received. */
    public void unknownPacketReceived(BNetPacket packet, Object data);

    /** This is called if an unhandled event is received. */
    public void unknownEventReceived(BNetEvent event, Object data);
}
