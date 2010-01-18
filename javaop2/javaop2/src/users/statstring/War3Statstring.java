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
	
	/**
	 * Gets the "best race" name from the initial
	 */
	private static String getRaceNameFromInitial(String initial) {
        if (initial.equals("R")) {
            return "Random";
        } else if (initial.equals("O")) {
            return "Orc";
        } else if (initial.equals("N")) {
            return "Night Elf";
        } else if (initial.equals("U")) {
            return "Undead";
        } else if (initial.equals("H")) {
            return "Human";
        } else {
        	return "Unknown";
        }
	}
	
	/**
	 * Gets icon name from rank initial and icon index
	 */
    private static String getIconName(String rank, int i) {
    	if(i > 5)
    		return "Unknown";
    	
        if (rank.equals("R")) {				// Race: Random
            String[] ranks = {
            		"Unknown",
            		"Peon",
            		"Green Dragon Whelp",
            		"Blue Dragon",
            		"Red Dragon",
            		"Deathwing"
            };
            return ranks[i];
        } else if (rank.equals("O")) {		// Race: Orc
            String[] ranks = {
            		"Unknown",
            		"Peon",
            		"Grunt",
            		"Tauren",
            		"Far Seer",
            		"Thrall"
            };
            return ranks[i];
        } else if (rank.equals("U")) {		// Race: Undead
            String[] ranks = {
            		"Unknown",
            		"Peon",
            		"Ghoul",
            		"Abomination",
            		"Lich",
            		"Tichondrius"
            };
            return ranks[i];
        } else if (rank.equals("H")) {		// Race: Human
            String[] ranks = {
            		"Unknown",
            		"Peon",
            		"Footman",
            		"Knight",
            		"Archmage",
            		"Medivh"
            };
            return ranks[i];
        } else if (rank.equals("N")) {		// Race: Night Elf
            String[] ranks = {
                    "Unknown",
                    "Peon",
                    "Archer",
                    "Druid of the Claw",
                    "Priestess of the Moon",
                    "Furion Stormrage"
            };
            return ranks[i];
        } else {							// Unknown race, wtf?
            String[] ranks = {
            		"Unknown",
            		"Peon",
            		"Unknown",
            		"Unknown",
            		"Unknown",
            		"Unknown"
            };
            return ranks[i];
        }
    }

    public static String getWar3(String message) {
        String icon;
        String race;
        String[] tokens = message.split(" ");
        
        if (tokens.length > 1) {
            race = tokens[1].substring(2, 1);
            icon = tokens[1].substring(0, 1);
        } else {
            race = "";
            icon = "";
        }
        
        icon = getIconName(race, Integer.parseInt(icon));
        race = getRaceNameFromInitial(race);
        
        if (tokens.length == 2) {
            StringBuffer clan = new StringBuffer(tokens[3]).reverse();
            return "Stats: Warcraft III (level " + tokens[2] + ", Best Race: " + race + ", Icon:"
                    + icon + ", Clan: " + clan.toString() + ")";
        } else if (tokens.length == 1) {
            return "Stats: Warcraft III (level " + tokens[2] + ", Best Race: " + race + ", Icon:"
                    + icon + ")";
        } else if (tokens.length == 0) {
            return "Stats: Warcraft III (no statstring)";
        } else {
            return "Unknown statstring format: " + message;
        }
    }
}