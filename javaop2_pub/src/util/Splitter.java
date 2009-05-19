/*
 * Created on Jan 16, 2005
 * By iago
 */
package util;

import java.util.Vector;

/**
 * @author iago
 *
 */
public class Splitter
{
    private static final int maxLength = 100;
    
    public static Vector split(String str, boolean moreTag)
    {
        Vector ret = new Vector();
        
        String[] allWords = str.split(" ");
        // padLength is the amount of extra space on each line
        int padLength = 1 + ((moreTag && allWords.length < maxLength)  ? 6 : 0); 
        
        // Sanity check -- make sure that no single word will break this
        for(int i = 0; i < allWords.length; i++)
        {
            if(allWords[i].length() + padLength > maxLength)
            {
                ret.add(str);
                return ret;
            }
        }
                 
        
        int i = 0;
        
        while(i < allWords.length)
        {
            String currentLine = "";

                while(i < allWords.length && (currentLine.length() + allWords[i].length() + padLength) < maxLength)
                {
                    currentLine = currentLine + allWords[i] + " ";
                    i++;
                }

                ret.add(currentLine + ((moreTag && i < allWords.length) ? "<more>" : ""));
                
        }
            
        return ret;
    }
}
