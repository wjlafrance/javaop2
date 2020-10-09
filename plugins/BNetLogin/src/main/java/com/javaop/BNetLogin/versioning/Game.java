/*
 * Game.java
 *
 * Created on March 9, 2004, 9:03 AM
 */

package com.javaop.BNetLogin.versioning;

import java.io.IOException;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.javaop.bot.BotCoreStatic;
import com.javaop.util.Buffer;
import com.javaop.util.PadString;
import com.javaop.util.RelativeFile;

import com.javaop.BNetLogin.cdkey.Decode;

import com.javaop.exceptions.LoginException;

public class Game {

	private static GameData gameData = new GameData();

	private String game;

	public Game(String game) throws LoginException {
		this.game = BotCoreStatic.getInstance().normalizeGameName(game);
	}

	public int getVersionByte() {
		return gameData.getVersionByte(game);
	}

	public int getVersionHash() {
		return gameData.getVersionHash(game);
	}

	public int getGameCode() {
		return (game.charAt(0) << 24) |
			   (game.charAt(1) << 16) |
			   (game.charAt(2) << 8) |
			   (game.charAt(3) << 0);
	}

	public String getName() {
		return game;
	}

	public String getExeInfo() {
		// return "starcraft.exe 03/28/03 04:21:56 1064960";

		RelativeFile f = new RelativeFile(gameData.getFiles(game)[0]);

		// Set up a calendar to point at the last modified time
		Calendar c = Calendar.getInstance();
		c.setTime(new Date(f.lastModified()));

		StringBuilder exeInfo = new StringBuilder();

		// Write to the exeInfo buffer
		exeInfo.append(f.getName()).append(" ");

		// date
		exeInfo.append(PadString.padNumber(c.get(Calendar.MONTH), 2)).append("/");
		exeInfo.append(PadString.padNumber(c.get(Calendar.DAY_OF_MONTH), 2)).append("/");
		exeInfo.append(PadString.padNumber((c.get(Calendar.YEAR) % 100), 2)).append(" ");

		// time
		exeInfo.append(PadString.padNumber(c.get(Calendar.HOUR_OF_DAY), 2)).append(":");
		exeInfo.append(PadString.padNumber(c.get(Calendar.MINUTE), 2)).append(":");
		exeInfo.append(PadString.padNumber(c.get(Calendar.SECOND), 2)).append(" ");

		// size
		exeInfo.append(f.length());

		return exeInfo.toString();
	}

	public int doCheckRevision(byte[] formula, String mpqFile) throws LoginException, IOException {
		return CheckRevision.doCheckRevision(mpqFile, gameData.getFiles(game), formula);
	}

	/**
	 * Gets a Vector of all possible games.
	 *
	 * @return A Vector of all possible games.
	 */
	public static List<String> getGames() {
		return Arrays.asList(
			"Diablo",
			"Starcraft",
			"Brood War",
			"Warcraft II",
			"Diablo II",
			"Diablo II: LoD",
			"Warcraft III",
			"Warcraft III: TFT"
		);
	}

	/**
	 * Gets the number of keys, using spawn, and key hash for the cdkey.
	 */
	public Buffer getKeyBuffer(String cdkey1, String cdkey2, int clientToken, int serverToken) {
		Buffer ret = new Buffer();

		int numberOfKeys = gameData.numberOfKeys(game);
		ret.addDWord(numberOfKeys);
		ret.addDWord(0);
		if (numberOfKeys >= 1) {
			ret.add(getKeyBlock(cdkey1, clientToken, serverToken));
		}
		if (numberOfKeys >= 2) {
			ret.add(getKeyBlock(cdkey2, clientToken, serverToken));
		}
		return ret;
	}

	private Buffer getKeyBlock(String cdkey, int clientToken, int serverToken) {
		Decode key = Decode.getDecoder(cdkey);
		int[] hashedKey = key.getKeyHash(clientToken, serverToken);

		Buffer ret = new Buffer();
		ret.addDWord(cdkey.length());       // (DWORD) Key Length
		ret.addDWord(key.getProduct());     // (DWORD) Product
		ret.addDWord(key.getVal1());        // (DWORD) Value 1
		ret.addDWord(0);                    // (DWORD) Uknown
		for (int i = 0; i < 5; i++) {
			ret.addDWord(hashedKey[i]);     // (DWORD[5]) Hashed key data
		}

		return ret;
	}

	@Override public String toString() {
		return String.format("%s (%s, Version Hash = 0x%X, Version Byte = 0x%X)", game, getExeInfo(),
				gameData.getVersionHash(game), gameData.getVersionByte(game));
	}
}
