import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;


/*
 * Created on Apr 12, 2005 By iago
 */

public class Define
{
    /** Get the list of definitions for the word */
    public static String[] define(String word) throws IOException
    {
        word = word.replaceAll(" ", "+");

        Vector<String> ret = new Vector<String>();
        String text = getPage(word);

        boolean stop = false;
        while (stop == false)
        {
            int ddIndex = text.indexOf("<DD>");
            int liIndex = text.indexOf("<LI>");

            if (liIndex >= 0 && (ddIndex >= 0 ? liIndex < ddIndex : true))
            {
                text = text.substring(liIndex + 4);
                String def = text.replaceAll("<\\/LI>.*", "");
                ret.add(def.replaceAll("<.*?>", "").trim());
                text = text.substring(text.indexOf("</LI>") + 5);
            }
            else if (ddIndex >= 0 && (liIndex >= 0 ? ddIndex < liIndex : true))
            {
                text = text.substring(ddIndex + 4);
                String def = text.replaceAll("<\\/DD>.*", "");
                ret.add(def.replaceAll("<.*?>", "").trim());
                text = text.substring(text.indexOf("</DD>") + 5);
            }
            else
            {
                stop = true;
            }
        }

        return (String[]) ret.toArray(new String[ret.size()]);
    }

    private static String getPage(String word) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) new URL("http", "dictionary.reference.com",
                80, "/search?q=" + word).openConnection();

        conn.connect();

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK)
        {
            conn.disconnect();
            throw new IOException("Unable to find the result: Server returned error "
                    + conn.getResponseCode());
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        StringBuffer fullTextBuf = new StringBuffer();
        String line;
        while ((line = in.readLine()) != null)
        {
            fullTextBuf.append(line);
        }

        conn.disconnect();

        return fullTextBuf.toString();
    }
    //    
    // public static void main(String []args) throws IOException
    // {
    // while(true)
    // {
    // System.out.print("Please enter a word to define --> ");
    // String word = new BufferedReader(new
    // InputStreamReader(System.in)).readLine();
    // String []defs = define(word);
    //            
    // System.out.println(defs.length + " definitions found");
    // for(int i = 0; i < defs.length; i++)
    // System.out.println((i + 1) + ": " + defs[i]);
    // }
    // }
}
