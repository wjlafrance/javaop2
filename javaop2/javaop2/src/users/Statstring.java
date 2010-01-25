package users;

/**
 * Parses statstrings. Information from:
 * http://www.geocities.com/wizardguy@sbcglobal.net/statparse.txt
 * 
 * @author joe[x86]
 */
public class Statstring
{
    private String[] tokens;

    public Statstring(String statstring)
    {
        if (statstring.length() >= 4)
        {
            this.tokens = statstring.split(" ", -1);

            /* System.out.print("Raw: " + statstring + "; ");
            for (int i = 0; i < tokens.length; i++)
            {
                System.out.print(i + ": " + tokens[i] + "; ");
            }
            System.out.println(); */
        }
    }

    /**
     * @return Backwards client ID
     */
    public String getClient()
    {
        return tokens[0].substring(0, 4);
    }

    /**
     * @return WarCraft III: Clan name.
     */
    public String getClan()
    {
        if (tokens.length < 4)
            return "";

        if (getClient().equals("3RAW") || getClient().equals("PX3W"))
            return new StringBuffer(tokens[3]).reverse().toString();

        return "";
    }

    /**
     * @return StarCraft / WarCraft II: Wins
     */
    public int getWins()
    {
        if (tokens.length < 2)
            return 0;

        if (getClient().equals("RATS") || getClient().equals("PXES") || getClient().equals("RTSJ")
                || getClient().equals("NB2W"))
            return Integer.parseInt(tokens[1]);
        else
            return 0;
    }
}
