package com.javaop.BNetLogin.versioning;

import com.javaop.util.PersistantMap;
import com.javaop.util.RelativeFile;

/*
 * Created on Mar 2, 2005 By iago
 */

public class GameData {

	/**
	 * The PersistantMap holding game data
	 */
	private PersistantMap games;

	public GameData() {
		games = new PersistantMap(new RelativeFile("_GameData.txt"),
				"These are the important informations for games to log in with "
				+ "-- values are stored in hex.");
		this.initialize();
	}

	/**
	 * Gets the version byte for <b>game</b>
	 */
	public int getVersionByte(String game) {
		return Integer.parseInt(games.getNoWrite(game, "Version byte", "0"), 16);
	}

	/**
	 * Gets the version hash for <b>game</b>
	 */
	public int getVersionHash(String game) {
		int calc = VerHash.getVersion(games.getNoWrite(game, "File1", null));
		if (calc == 0) { // File1 is absent
			calc = Integer.parseInt(games.getNoWrite(game, "Version hash", "0"), 16);
		}
		return calc;
	}

	/**
	 * Gets the hash file directory
	 */
	public String getHashFileDirectory() {
		return games.getWrite("[default]", "hash directory", System.getProperty("user.home") + "/.hashes/");
	}

	/**
	 * Gets a String array of files for doing checkrevision
	 */
	public String[] getFiles(String game) {
		String prefix = getHashFileDirectory();
		String[] files = new String[4];
		files[0] = prefix + games.getNoWrite(game, "File1", null);
		files[1] = prefix + games.getNoWrite(game, "File2", null);
		files[2] = prefix + games.getNoWrite(game, "File3", null);
		files[3] = prefix + games.getNoWrite(game, "File4", null);

		return files;
	}

	/**
	 * Gets an int indicating the number of keys needed to log in
	 */

	public int numberOfKeys(String game) {
		if (game.equals("D2XP") || game.equals("W3XP")) {
			return 2;
		} else if (isDiablo(game)) {
			return 0; // Diablo sends no CD key on Battle.net
		} else {
			return 1;
		}
	}

	/** Diablo retail (DRTL) and shareware (DSHR). */
	public static boolean isDiablo(String game) {
		return "DRTL".equals(game) || "DSHR".equals(game);
	}

	/**
	 * Diablo logs on with SID_LOGONRESPONSE (0x29, Broken-SHA1 double hash) after the 0x50/0x51 version check; everything
	 * else with loginType 0 uses SID_LOGONRESPONSE2 (0x3A).
	 */
	public static boolean usesLegacyLogonPacket(String game) {
		return isDiablo(game);
	}

	/** Battle.net restricts Diablo chat to the SID_GETCHANNELLIST menu: no forced join of an arbitrary home channel. */
	public static boolean isChatMenuRestricted(String game) {
		return isDiablo(game);
	}

	/** The product channel the real Diablo client joins first (menu channel names on useast, 2026-10-04). */
	public static String firstJoinChannel(String game) {
		return "DSHR".equals(game) ? "Diablo Shareware" : "Diablo";
	}

	/**
	 * Gets a boolean indicating if a certain game has a server signature
	 */
	public boolean hasServerSignature(String game) {
		if (game.equals("WAR3") || game.equals("W3XP")) {
			return true;
		} else {
			return false;
		}
	}

	private void initialize() {
		games.getWrite("STAR", "Version byte", "D3");
		games.getWrite("SEXP", "Version byte", "D3");
		games.getWrite("D2DV", "Version byte", "0C");
		games.getWrite("D2XP", "Version byte", "0C");
		games.getWrite("W2BN", "Version byte", "4F");
		games.getWrite("DRTL", "Version byte", "2A");
		games.getWrite("DSHR", "Version byte", "2A");
		games.getWrite("WAR3", "Version byte", "17");
		games.getWrite("W3XP", "Version byte", "17");

		String hashDir = getHashFileDirectory();

		games.getWrite("STAR", "File1", hashDir + "STAR/starcraft.exe");
		games.getWrite("STAR", "File2", hashDir + "STAR/storm.dll");
		games.getWrite("STAR", "File3", hashDir + "STAR/battle.snp");
		games.getWrite("STAR", "File4", hashDir + "STAR/STAR.bin");
		games.getWrite("SEXP", "File1", hashDir + "STAR/starcraft.exe");
		games.getWrite("SEXP", "File2", hashDir + "STAR/storm.dll");
		games.getWrite("SEXP", "File3", hashDir + "STAR/battle.snp");
		games.getWrite("SEXP", "File4", hashDir + "STAR/STAR.bin");
		games.getWrite("D2DV", "File1", hashDir + "D2DV/Game.exe");
		games.getWrite("D2DV", "File2", hashDir + "D2DV/Bnclient.dll");
		games.getWrite("D2DV", "File3", hashDir + "D2DV/D2Client.dll");
		games.getWrite("D2DV", "File4", hashDir + "D2DV/D2DV.bin");
		games.getWrite("D2XP", "File1", hashDir + "D2XP/Game.exe");
		games.getWrite("D2XP", "File2", hashDir + "D2XP/Bnclient.dll");
		games.getWrite("D2XP", "File3", hashDir + "D2XP/D2Client.dll");
		games.getWrite("D2XP", "File4", hashDir + "D2XP/D2XP.bin");
		games.getWrite("W2BN", "File1", hashDir + "W2BN/Warcraft II BNE.exe");
		games.getWrite("W2BN", "File2", hashDir + "W2BN/storm.dll");
		games.getWrite("W2BN", "File3", hashDir + "W2BN/battle.snp");
		games.getWrite("W2BN", "File4", hashDir + "W2BN/W2BN.bin");
		games.getWrite("DRTL", "File1", hashDir + "DRTL/Diablo.exe");
		games.getWrite("DRTL", "File2", hashDir + "DRTL/storm.dll");
		games.getWrite("DRTL", "File3", hashDir + "DRTL/battle.snp");
		games.getWrite("DRTL", "File4", hashDir + "DRTL/DRTL.bin");
		games.getWrite("DSHR", "File1", hashDir + "DSHR/Diablo_s.exe");
		games.getWrite("DSHR", "File2", hashDir + "DSHR/storm.dll");
		games.getWrite("DSHR", "File3", hashDir + "DSHR/battle.snp");
		games.getWrite("DSHR", "File4", hashDir + "DSHR/DSHR.bin");
		games.getWrite("WAR3", "File1", hashDir + "WAR3/war3.exe");
		games.getWrite("WAR3", "File2", hashDir + "WAR3/Storm.dll");
		games.getWrite("WAR3", "File3", hashDir + "WAR3/game.dll");
		games.getWrite("WAR3", "File4", hashDir + "WAR3/WAR3.bin");
		games.getWrite("W3XP", "File1", hashDir + "WAR3/war3.exe");
		games.getWrite("W3XP", "File2", hashDir + "WAR3/Storm.dll");
		games.getWrite("W3XP", "File3", hashDir + "WAR3/game.dll");
		games.getWrite("W3XP", "File4", hashDir + "WAR3/WAR3.bin");

		games.getWrite("STAR", "Version hash", "01010303");
		games.getWrite("SEXP", "Version hash", "01010303");
		games.getWrite("D2DV", "Version hash", "01000c00");
		games.getWrite("D2XP", "Version hash", "01000c00");
		games.getWrite("W2BN", "Version hash", "01010001");
		games.getWrite("DRTL", "Version hash", "01000901");
		games.getWrite("DSHR", "Version hash", "01000901");
		games.getWrite("WAR3", "Version hash", "01001027");
		games.getWrite("W3XP", "Version hash", "01001027");
	}

}
