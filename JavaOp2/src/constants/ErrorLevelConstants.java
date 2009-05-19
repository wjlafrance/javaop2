/*
 * Created on Dec 2, 2004
 * By iago
 */
package constants;

/** A list of constants that I stole from Linux.
 * 
 * @author iago
 *
 */
public interface ErrorLevelConstants
{
    public final int DEBUG = 0;     /* Debug-level messages */
    public final int INFO = 1;      /* Informational */
    public final int NOTICE = 2;    /* Normal but significant conditions */
    public final int WARNING = 3;   /* Warning conditions */
    public final int ERROR = 4;     /* Error conditions */
    public final int CRITICAL = 5;  /* Critical conditions */
    public final int ALERT = 6;     /* Action must be taken immediately */
    public final int EMERGENCY = 7; /* System is unusable */
    
    public final String[] errorLevelConstants = new String[] {
            "DEBUG",
            "INFO",
            "NOTICE",
            "WARNING",
            "ERROR",
            "CRITICAL",
            "ALERT",
            "EMERGENCY"
    };
                                                      
}
