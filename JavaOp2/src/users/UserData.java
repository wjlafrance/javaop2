/*
 * Users.java
 *
 * Created on March 18, 2004, 12:25 PM
 */

package users;

import util.User;


/**
 * This class stores a single user, including their icon and lag and the time they joined the channel.
 * It implements User, which is a public class and can be safely given to other functions.
 */
class UserData implements User
{
	private static final long serialVersionUID = 1L;
	
    final private int ping;
    final private String name;
    final private long joinTime;
    
    final private String prettyStatstring;
    final private String rawStatstring;
    
    private int flags;
    
    public UserData (String name, int ping, int flags, String stats)
    {
        this.name = name;
        this.ping = ping;
        this.flags = flags;
        this.joinTime = System.currentTimeMillis();

        rawStatstring = stats;
        prettyStatstring = stats;
//        
//        if(stats.substring(0, 4).equalsIgnoreCase("3raw"))
//            statstring = War3Statstring.getWar3(stats);
//        else
    }

    public String getName () 
    {
        return name;
    }

    public int getPing () 
    {
        return ping;
    }
    
    public int getFlags()
    {
        return flags;
    }
    
    public String getPrettyStatstring()
    {
        return toString();
    }
    
    public String getRawStatstring()
    {
        return rawStatstring;
    }

    public void setFlags(int flags)
    {
        this.flags = flags;
    }
    
    public String toString () 
    {
        if(prettyStatstring != null)
            return name + "(" + ping + "ms, " + prettyStatstring + ")";
        return name + "(" + ping + "ms)";
    }
    

    public boolean equals(Object o)
    {
        if(o instanceof UserData && ((UserData)o).getName().equalsIgnoreCase(getName()))
            return true;
        else if(o instanceof String && ((String)o).equalsIgnoreCase(getName()))
            return true;
        else
            return false;
        
    }    
    
    public long getJoinTime()
    {
        return joinTime;
    }
}

