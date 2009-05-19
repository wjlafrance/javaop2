/*
 * Created on Dec 12, 2004
 * By iago
 */

/**
 * @author iago
 *
 */
public class Output
{
    public final static String PLAIN 				= ((char)27) + "[0;00m";
    
    public final static String BLACK 				= ((char)27) + "[0;30m";
    public final static String RED 					= ((char)27) + "[0;31m";
    public final static String GREEN 				= ((char)27) + "[0;32m";
    public final static String YELLOW 				= ((char)27) + "[0;33m";
    public final static String BLUE 				= ((char)27) + "[0;34m";
    public final static String MAGENTA 				= ((char)27) + "[0;35m";
    public final static String CYAN 				= ((char)27) + "[0;36m";
    public final static String WHITE				= ((char)27) + "[0;37m";

    public final static String BRIGHT_BLACK 		= ((char)27) + "[1;30m";
    public final static String BRIGHT_RED 			= ((char)27) + "[1;31m";
    public final static String BRIGHT_GREEN 		= ((char)27) + "[1;32m";
    public final static String BRIGHT_YELLOW 		= ((char)27) + "[1;33m";
    public final static String BRIGHT_BLUE 			= ((char)27) + "[1;34m";
    public final static String BRIGHT_MAGENTA 		= ((char)27) + "[1;35m";
    public final static String BRIGHT_CYAN 			= ((char)27) + "[1;36m";
    public final static String BRIGHT_WHITE			= ((char)27) + "[1;37m";

    public final static String DARK_BLACK 			= ((char)27) + "[2;30m";
    public final static String DARK_RED 			= ((char)27) + "[2;31m";
    public final static String DARK_GREEN 			= ((char)27) + "[2;32m";
    public final static String DARK_YELLOW 			= ((char)27) + "[2;33m";
    public final static String DARK_BLUE 			= ((char)27) + "[2;34m";
    public final static String DARK_MAGENTA 		= ((char)27) + "[2;35m";
    public final static String DARK_CYAN 			= ((char)27) + "[2;36m";
    public final static String DARK_WHITE			= ((char)27) + "[2;37m";
    
    public static void output(String name, String str, boolean useColors)
    {
        String toDisplay = (YELLOW + name + ": " + BRIGHT_WHITE + Timestamp.getTimestamp() + PLAIN + str + PLAIN);
        
        if(useColors == false)
            toDisplay = toDisplay.replaceAll("\\" + ((char)27) + ".*?m", "");
        
        System.out.println(toDisplay);
    }
}
