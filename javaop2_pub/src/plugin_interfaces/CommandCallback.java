/*
 * Created on Dec 1, 2004
 * By iago
 */
package plugin_interfaces;

import java.io.IOException;

import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

/**
 * @author iago
 *
 */
public interface CommandCallback extends AbstractCallback
{
    /** This will be called with the name of the command and the arguments.
     * It will only get called if the user has the required flags to use it. */
    public void commandExecuted(String user, String command, String []args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly;
}
