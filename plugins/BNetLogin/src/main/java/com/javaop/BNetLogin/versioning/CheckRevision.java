/*
 * CheckRevision.java
 *
 * Created on March 10, 2004, 9:05 AM
 */
package com.javaop.BNetLogin.versioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.LinkedList;
import java.util.StringTokenizer;

import com.javaop.exceptions.LoginException;

/**
 * This takes care of the CheckRevision() for the main game files of any
 * program. This is done to prevent tampering and to make sure the version is
 * correct. This class is generally slow because it has to read through the
 * entire files. The majority of the time is spent in i/o, but I've tried to
 * optimize this as much as possible.
 *
 * @author iago, wjlafance
 */
public class CheckRevision {

	/** Hashcodes for each MPQ file, for old-style CheckRevisions */
	private static final int hashcodes[] = { 0xE7F4CB62, 0xF6A14FFC, 0xAA5504AF, 0x871FCDC2, 0x11BF6A18, 0xC57292E6, 0x7927D27E, 0x2FEC8733 };

	/**
	 * This is the main entry point for doing CheckRevision. This sorts out
	 * what kind of CheckRevision we're doing and then calls another function.
	 * @param mpqName MPQ name specified in SID_AUTH_INFO response
	 * @param files Files to run CheckRevision on
	 * @param formula Version check formula specified in SID_AUTH_INFO response
	 * @return
	 */
	public static int doCheckRevision(String mpqName, String[] files, byte[] formula) throws LoginException, IOException {

		String mpq = mpqName.toLowerCase();

		// Windows (IX86)
		if (mpq.matches("ix86ver[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(7, 8));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("ver-ix86-[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(9, 10));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("lockdown-ix86-[0-1][0-9].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(14, 16));
			return checkRevisionLockdown(mpqNum, files, formula);
		}

		// Power Macintosh
		if (mpq.matches("pmacver[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(7, 8));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("ver-pmac-[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(9, 10));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("lockdown-pmac-[0-1][0-9].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(14, 16));
			return checkRevisionLockdown(mpqNum, files, formula);
		}

		// Mac OS X
		if (mpq.matches("xmacver[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(7, 8));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("ver-xmac-[0-7].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(9, 10));
			return checkRevisionOld(mpqNum, files, new String(formula));
		} else if (mpq.matches("lockdown-xmac-[0-1][0-9].mpq")) {
			int mpqNum = Integer.parseInt(mpq.substring(14, 16));
			return checkRevisionLockdown(mpqNum, files, formula);
		}

		throw new LoginException("MPQ filename (" + mpqName + ") doesn't match " + "any known pattern.");
	}

	/**
	 * Performs an old-style CheckRevision.
	 *
	 * @param mpqNumber MPQ number from server's SID_AUTH_INFO
	 * @param files Array of files to be hashed
	 * @param formula Version check formula from server's SID_AUTH_INFO
	 *
	 * @throws FileNotFoundException If the datafiles aren't found
	 * @throws IOException If there is an error reading from one of the datafiles
	 *
	 * @return The 32-bit CheckRevision hash.
	 */
	public static int checkRevisionOld(int mpqNumber, String []files, String formula) throws FileNotFoundException, IOException, LoginException {
		StringTokenizer tok = new StringTokenizer(formula, " ");
		if (tok.countTokens() != 8) {
			return -1; // malformed formula
		}

		long a = 0, b = 0, c = 0;

		for (int x = 0; x<3; x++) {
			String seed = tok.nextToken();
			if (seed.toLowerCase().startsWith("a=")) {
				a = Long.parseLong(seed.substring(2));
			}
			if (seed.toLowerCase().startsWith("b=")) {
				b = Long.parseLong(seed.substring(2));
			}
			if (seed.toLowerCase().startsWith("c=")) {
				c = Long.parseLong(seed.substring(2));
			}
		}

		tok.nextToken();

		if (a == 0 || b == 0 || c == 0) {
			return 0;
		}
		String formulaChunk;

		formulaChunk = tok.nextToken();
		if (!formulaChunk.matches("A=A.S")) { return checkRevisionOldSlow(mpqNumber, files, formula); }
		char op1 = formulaChunk.charAt(3);

		formulaChunk = tok.nextToken();
		if (!formulaChunk.matches("B=B.C")) { return checkRevisionOldSlow(mpqNumber, files, formula); }
		char op2 = formulaChunk.charAt(3);

		formulaChunk = tok.nextToken();
		if (!formulaChunk.matches("C=C.A")) { return checkRevisionOldSlow(mpqNumber, files, formula); }
		char op3 = formulaChunk.charAt(3);

		formulaChunk = tok.nextToken();
		if (!formulaChunk.matches("A=A.B")) { return checkRevisionOldSlow(mpqNumber, files, formula); }
		char op4 = formulaChunk.charAt(3);

		// Now we actually do the hashing for each file
		// Start by hashing A by the hashcode
		a ^= hashcodes[mpqNumber];

		for (String file : files) {
			File currentFile = new File(file);
			for (Integer s : readFile(currentFile)) {
				switch (op1) {
					case '^': a ^= s; break;
					case '+': a += s; break;
					case '-': a -= s; break;
					case '*': a *= s; break;
					case '/': a /= s; break;
				}
				switch (op2) {
					case '^': b ^= c; break;
					case '+': b += c; break;
					case '-': b -= c; break;
					case '*': b *= c; break;
					case '/': b /= c; break;
				}
				switch (op3) {
					case '^': c ^= a; break;
					case '+': c += a; break;
					case '-': c -= a; break;
					case '*': c *= a; break;
					case '/': c /= a; break;
				}
				switch (op4) {
					case '^': a ^= b; break;
					case '+': a += b; break;
					case '-': a -= b; break;
					case '*': a *= b; break;
					case '/': a /= b; break;
				}
			}
		}

		return (int) c;
	}

	public static int checkRevisionOldSlow(int mpqNumber, String []files, String formula) throws FileNotFoundException, IOException, LoginException {

		// First, parse the versionString to name=value pairs and put them
		// in the appropriate place
		long[] values = new long[4];
		int[] opValueDest = new int[4];
		int[] opValueSrc1 = new int[4];
		char[] operation = new char[4];
		int[] opValueSrc2 = new int[4];

		// Break this apart at the spaces
		StringTokenizer s = new StringTokenizer(formula, " ");
		if (s.countTokens() != 8) {
			return -1; // malformed formula
		}
		int currentFormula = 0;
		while (s.hasMoreTokens()) {
			String thisToken = s.nextToken();
			// As long as there is an '=' in the string
			if (thisToken.indexOf('=') > 0) {
				// Break it apart at the '='
				StringTokenizer nameValue = new StringTokenizer(thisToken, "=");
				if (nameValue.countTokens() != 2) {
					return 0;
				}

				int variable = getNum(nameValue.nextToken().charAt(0));

				String value = nameValue.nextToken();

				// If it starts with a number, assign that number to the appropriate variable
				if (Character.isDigit(value.charAt(0))) {
					values[variable] = Long.parseLong(value);
				} else {
					opValueDest[currentFormula] = variable;

					opValueSrc1[currentFormula] = getNum(value.charAt(0));
					operation[currentFormula] = value.charAt(1);
					opValueSrc2[currentFormula] = getNum(value.charAt(2));

					currentFormula++;
				}
			}
		}

		// Now we actually do the hashing for each file
		// Start by hashing A by the hashcode
		values[0] ^= hashcodes[mpqNumber];

		for (String file : files) {
			File currentFile = new File(file);

			for (Integer word : readFile(currentFile)) {
				values[3] = word;

				for (int k = 0; k < currentFormula; k++) {
					switch (operation[k]) {
						case '^': values[opValueDest[k]] = values[opValueSrc1[k]] ^ values[opValueSrc2[k]]; break;
						case '+': values[opValueDest[k]] = values[opValueSrc1[k]] + values[opValueSrc2[k]]; break;
						case '-': values[opValueDest[k]] = values[opValueSrc1[k]] - values[opValueSrc2[k]]; break;
						case '*': values[opValueDest[k]] = values[opValueSrc1[k]] * values[opValueSrc2[k]]; break;
						case '/': values[opValueDest[k]] = values[opValueSrc1[k]] / values[opValueSrc2[k]]; break;
					}
				}
			}
		}
		return (int) values[2];
	}


	/**
	 * Performs an lockdown CheckRevision.
	 *
	 * @param mpqNumber MPQ number from server's SID_AUTH_INFO
	 * @param files Array of files to be hashed
	 * @param formula Version check formula from server's SID_AUTH_INFO
	 *
	 * @throws FileNotFoundException If the datafiles aren't found
	 * @throws IOException If there is an error reading from one of the datafiles
	 *
	 * @return The 32-bit CheckRevision hash.
	 */
	public static int checkRevisionLockdown(int mpqNumber, String[] files, byte[] formula) throws LoginException, IOException {
		throw new LoginException("Lockdown CheckRevision not supported.");
	}

	/**
	 * Reads a file and returns a List of Integers.
	 */
	private static List<Integer> readFile(File file) throws IOException {
		int length = (int) file.length();
		byte []bytes = new byte[(length % 1024) == 0 ? length : (length / 1024 * 1024) + 1024];

		try (InputStream in = new FileInputStream(file)) {
			in.read(bytes);
		}

		int value = 0xFF;
		for (int i = (int) file.length(); i < bytes.length; i++) {
			bytes[i] = (byte) value--;
		}

		LinkedList<Integer> words = new LinkedList<Integer>();
		for (int i = 0; i < bytes.length; i += 4) {
			int j = ((bytes[i + 0] << 0) & 0x000000ff)
					| ((bytes[i + 1] << 8) & 0x0000ff00)
					| ((bytes[i + 2] << 16) & 0x00ff0000)
					| ((bytes[i + 3] << 24) & 0xff000000);
			words.add(j);
		}

		return words;
	}

	/**
	 * Utility method for old slow CheckRevision
	 */
	private static int getNum(char c) {
		c = Character.toUpperCase(c);
		if (c == 'S') {
			return 3;
		}

		return c - 'A';
	}

}