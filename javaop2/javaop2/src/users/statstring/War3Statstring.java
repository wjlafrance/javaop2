/*
 * War3Statstring.java
 * 
 * Created on January 20, 2005, 4:36 PM
 */

package users.statstring;

/**
 * 
 * @author lobo
 */
public class War3Statstring
{
    private static String getRank(String rank, int i) throws java.lang.ArrayIndexOutOfBoundsException
    {
        String rankResult;
        if (rank.equals("R"))
        {
            String[] ranks =
            { "Unknown", "Peon", "Green Dragon Whelp", "Blue Dragon", "Red Dragon", "Deathwing" };
            rankResult = ranks[i];
        }
        else if (rank.equals("O"))
        {
            String[] ranks =
            { "Unknown", "Peon", "Grunt", "Tauren", "Far Seer", "Thrall" };
            rankResult = ranks[i];
        }
        else if (rank.equals("U"))
        {
            String[] ranks =
            { "Unknown", "Peon", "Ghoul", "Abomination", "Lich", "Tichondrius" };
            rankResult = ranks[i];
        }
        else if (rank.equals("H"))
        {
            String[] ranks =
            { "Unknown", "Peon", "Footman", "Knight", "Archmage", "Medivh" };
            rankResult = ranks[i];
        }
        else if (rank.equals("N"))
        {
            String[] ranks =
            {
                    "Unknown", "Peon", "Archer", "Druid of the Claw", "Priestess of the Moon",
                    "Furion Stormrage" };
            rankResult = ranks[i];
        }
        else
        {
            String[] ranks =
            { "Unknown", "Peon", "Unknown", "Unknown" };
            rankResult = ranks[i];
        }
        return rankResult;
    }

    public static String getWar3(String message)
    {
        String[] war3Stats;
        String icon;
        String race;
        StringBuffer clan;
        war3Stats = message.split(" ");
        System.out.println("DEBUGGING LOBO:" + war3Stats[0] + " " + war3Stats[1] + " "
                + war3Stats[2]);
        if (war3Stats.length > 1)
        {
            race = war3Stats[1];
            race = race.substring(2, 1);
            icon = war3Stats[1];
            icon = icon.substring(0, 1);
        }
        else
        {
            race = "";
            icon = "";
        }
        int iconIndex = Integer.parseInt(icon);
        System.out.println("DEBUGGING LOBO: " + iconIndex);
        System.out.println("DEBUGGING LOBOL " + war3Stats.length);
        if (race.equals("R"))
        {
            icon = getRank(race, iconIndex);
            race = "Random";
        }
        else if (race.equals("O"))
        {
            icon = getRank(race, iconIndex);
            race = "Orc";
        }
        else if (race.equals("N"))
        {
            icon = getRank(race, iconIndex);
            race = "Night Elf";
        }
        else if (race.equals("U"))
        {
            icon = getRank(race, iconIndex);
            race = "Undead";
        }
        else if (race.equals("H"))
        {
            icon = getRank(race, iconIndex);
            race = "Human";
        }
        if (war3Stats.length == 2)
        {
            clan = new StringBuffer(war3Stats[3]).reverse();
            return "Stats: Warcraft III(level " + war3Stats[2] + ", Best Race " + race + ", Icon"
                    + icon + ", Clan " + clan.toString() + ")";
        }
        else if (war3Stats.length == 1)
        {
            return "Stats: Warcraft III(level " + war3Stats[2] + ", Best Race " + race + ", Icon"
                    + icon + ")";
        }
        else if (war3Stats.length == 0)
        {
            return "Stats: Warcraft III(None)";
        }
        else
        {
            return "Unknown statstring: " + message;
        }
    }
}