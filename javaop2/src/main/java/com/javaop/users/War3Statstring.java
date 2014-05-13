/*
 * War3Statstring.java
 *
 * Created on January 20, 2005, 4:36 PM
 */

package com.javaop.users;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author lobo
 */
public class War3Statstring {

	private static final Map<String, String> raceNames = new HashMap<>();
	private static final Map<String, String> iconNames = new HashMap<>();

	static {
		raceNames.put("R", "Random");
		raceNames.put("O", "Orc");
		raceNames.put("N", "Night Elf");
		raceNames.put("U", "Unknown");
		raceNames.put("H", "Human");

		iconNames.put("R0", "Unknown");
		iconNames.put("R1", "Peon");
		iconNames.put("R2", "Green Dragon Whelp");
		iconNames.put("R3", "Blue Dragon");
		iconNames.put("R4", "Red Dragon");
		iconNames.put("R5", "Deathwing");

		iconNames.put("O0", "Unknown");
		iconNames.put("O1", "Peon");
		iconNames.put("O2", "Grunt");
		iconNames.put("O3", "Tauren");
		iconNames.put("O4", "Far Seer");
		iconNames.put("O5", "Thrall");

		iconNames.put("U0", "Unknown");
		iconNames.put("U1", "Peon");
		iconNames.put("U2", "Ghoul");
		iconNames.put("U3", "Abomination");
		iconNames.put("U4", "Lich");
		iconNames.put("U5", "Tichondrius");

		iconNames.put("H0", "Unknown");
		iconNames.put("H1", "Peon");
		iconNames.put("H2", "Footman");
		iconNames.put("H3", "Knight");
		iconNames.put("H4", "Archmage");
		iconNames.put("H5", "Medivh");
	}

	/**
	 * Gets icon name from rank initial and icon index
	 */
	private static String getIconName(String race, int i) {
		return Optional.of(iconNames.get(race + i)).orElse("Unknown");
	}

	public static String getWar3(String message) {
		String icon = "Unknown";
		String race = "Unknown";
		String clan = "none";
		int level = 0;

		String[] tokens = message.split(" ");

		if (tokens.length >= 1) {
			race = raceNames.get(tokens[1].substring(2, 1));
			icon = getIconName(tokens[1].substring(2, 1), Integer.parseInt(tokens[1].substring(0, 1)));
		}
		if (tokens.length >= 2) {
			level = Integer.parseInt(tokens[2]);
		}
		if (tokens.length >= 3) {
			clan = new StringBuffer(tokens[3]).reverse().toString();
		}

		icon = getIconName(race, Integer.parseInt(icon));
		race = raceNames.get(race);

		switch (tokens.length) {
			case 0:
				return "WarCraft III (no statstring)";
			case 1:
				return String.format("WarCraft III (best race: %s, %s icon)", race, icon);
			case 2:
				return String.format("WarCraft III (level %d, best race: %s, %s icon)", level, race, icon);
			case 3:
				return String.format("WarCraft III (level %d, best race: %s, %s icon, in Clan %s)", level, race, icon, clan);
			default:
				return "WarCraft III (malformed statstring)";
		}
	}
}