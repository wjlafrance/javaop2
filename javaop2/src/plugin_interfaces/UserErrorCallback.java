/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_interfaces;

/**
 * These could be handy for logging or such, or figuring out what is confusing
 * people. Not terribly useful for functionality.
 * 
 * @author iago
 * 
 */
public interface UserErrorCallback extends AbstractCallback
{
    /**
     * This occurs when any access exception is thrown. Either the user doesn't
     * have the flags to use the command, or they are doing something else which
     * is making the command throw an AccessException.
     */
    public void illegalCommandUsed(String user, String userFlags, String requiredFlags,
            String command, Object data);

    /**
     * This occurs when a user uses a command that doesn't exist. This could be
     * helpful in tracking down non-intuitive names.
     */
    public void nonExistantCommandUsed(String user, String command, Object data);

    /**
     * This is used when somebody tries to use a command improperly. Again, not
     * useful for much else besides tracking down non-intuitive commands.
     */
    public void commandUsedImproperly(String user, String command, String syntaxUsed,
            String errorMessage, Object data);
}
