/*
 * Created on Apr 8, 2005 By iago
 */
package util;

import javax.swing.JOptionPane;


public class ErrorMessage
{
    private static boolean useGui = false;

    public static void error(Throwable t, boolean fatal)
    {
        if (useGui)
        {
            try
            {
                JOptionPane.showMessageDialog(null, t, "Error: see console for full report",
                                              JOptionPane.OK_OPTION);
            }
            catch (Error e)
            {
                System.err.println(t);
            }
        }
        else
        {
            System.err.println(t);
        }

        t.printStackTrace();

        if (fatal)
            System.exit(1);
    }

    public static void error(String message, boolean fatal)
    {
        if (useGui)
        {
            try
            {
                JOptionPane.showMessageDialog(null, message);
            }
            catch (Exception e)
            {
                System.err.println(message);
            }
        }
        else
        {
            System.err.println(message);
        }

        if (fatal)
            System.exit(1);
    }

    public static void setUseGui(boolean useGui)
    {
        ErrorMessage.useGui = useGui;
    }
}
