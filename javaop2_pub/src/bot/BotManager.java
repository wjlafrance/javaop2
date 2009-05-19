/*
 * Created on Jan 20, 2005
 * By iago
 */
package bot;

import java.io.IOException;
import java.util.Hashtable;

import util.Uniq;
import exceptions.PluginException;

/** This manages bots.  It creates, destroys, and lists them.
 * @author iago
 *
 */
public class BotManager
{
    private static final Hashtable activeBots = new Hashtable();
    
    public static void startBot(String name) throws IOException, PluginException
    {
        if(activeBots.get(name) == null)
            activeBots.put(name, new BotCore(name));
        else
            System.err.println("Attempting to load an already active bot!");
    }
    
    public static void stopBot(String name) throws IllegalArgumentException
    {
        
        BotCore bot = (BotCore) activeBots.get(name);
        
        activeBots.remove(name);
        
        if(bot != null)
            bot.stop();
        
//        if(activeBots.size() == 0)
//            System.exit(0);
    }
    
    public static String []getAllBots()
    {
        return JavaOpFileStuff.getAllBots();
    }
    
    public static String []getActiveBots()
    {
//        Enumeration e = activeBots.keys();
//        Vector v = new Vector();
//        while(e.hasMoreElements())
//            v.add(e.nextElement());
//           
        return Uniq.uniq(activeBots.keys());
    }
    
    public static BotCore getBot(String name)
    {
        return (BotCore) activeBots.get(name);
    }
}
