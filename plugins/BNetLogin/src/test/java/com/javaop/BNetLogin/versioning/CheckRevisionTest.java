package com.javaop.BNetLogin.versioning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.javaop.exceptions.LoginException;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static org.junit.Assert.*;

/**
 * Tests for the old-style (formula) CheckRevision.
 *
 * Expected values do not come from this code. They come from two independent sources:
 *
 * <ul>
 * <li>REF: the managed reference implementation Bnet.Hashing.CheckRevision.ComputeFormulaPaddedChecksum
 * (bnet-dev/bnet-core), run over the files in Hashfiles/. The files of each game are passed in the order
 * exe, storm.dll (or Bnclient.dll), battle.snp (or D2Client.dll), as in GameData.</li>
 * <li>DOC: published captures from bnet-dev/docs/checkrevision.md, section "Observed values". These are
 * accepted-by-Battle.net values with their exact inputs (VL 157871 and VL 154030).</li>
 * </ul>
 *
 * The Java implementation always pads files up to a multiple of 1024 bytes (0xFF, 0xFE, ...). That is the
 * "Formula-Padded" scheme; the older truncating scheme (used by IX86verN.mpq before 2006-08-22) is not
 * implemented, so the DOC truncating values (e.g. 0x867B88F3 for VL 154030) are deliberately not asserted.
 *
 * Tests that need Hashfiles/ skip (Assume) when the directory is not found. Set -Dhashfiles.dir=... to point at
 * it, otherwise it is searched for upward from the working directory (the module dir under Maven).
 */
public class CheckRevisionTest {

	@Rule public TemporaryFolder tmp = new TemporaryFolder();

	// ----- Formulas, one per MPQ number 0-7. Mostly real captured strings (see docs/checkrevision.md) -----

	/** MPQ 0..7, canonical shape (A=A.S B=B.C C=C.A A=A.B), so these take the fast path. */
	private static final String[] FORMULAS = {
		"A=611366670 B=373021351 C=264564859 4 A=A^S B=B-C C=C^A A=A^B",       // DRTL, VL 154030
		"C=403829810 A=2286501934 B=786621025 4 A=A^S B=B-C C=C-A A=A-B",      // DRTL, VL 157871
		"A=1258175249 B=4084170028 C=1032689061 4 A=A+S B=B-C C=C+A A=A+B",    // D2XP, VL 170121
		"A=154406729 B=202803944 C=1068357779 4 A=A-S B=B-C C=C^A A=A^B",      // D2DV, VL 147573
		"A=317938143 B=686992172 C=88810782 4 A=A^S B=B+C C=C-A A=A+B",        // SEXP, VL 157004
		"A=287582341 B=874700259 C=299380864 4 A=A^S B=B^C C=C-A A=A^B",       // SSHR, VL 19388
		"A=1262984606 B=3951383673 C=2230464239 4 A=A+S B=B-C C=C^A A=A-B",    // D2 gist example
		"A=716191481 B=922307312 C=1257253552 4 A=A+S B=B^C C=C-A A=A^B",      // W2BN, VL 121428
	};

	private static final String DOC_FORMULA_VER_IX86_1 = FORMULAS[1];
	private static final String DOC_FORMULA_IX86VER_0 = FORMULAS[0];

	/** Game file sets, in hashing order. */
	private static final String[][] GAMES = {
		{ "DRTL", "Diablo.exe", "Storm.dll", "Battle.snp" },
		{ "DSHR", "Diablo_s.exe", "Storm.dll", "Battle.snp" },
		{ "STAR", "Starcraft.exe", "Storm.dll", "Battle.snp" },
		{ "SSHR", "Starcraft_s.exe", "Storm.dll", "Battle.snp" },
		{ "JSTR", "StarcraftJ.exe", "Storm.dll", "Battle.snp" },
		{ "W2BN", "Warcraft II BNE.exe", "Storm.dll", "Battle.snp" },
		{ "D2DV", "game.exe", "Bnclient.dll", "D2Client.dll" },
		{ "D2XP", "game.exe", "Bnclient.dll", "D2Client.dll" },
		{ "WAR3", "war3.exe", "Storm.dll", "Game.dll" },
	};

	/** REF: checksums of FORMULAS[n] for MPQ number n = 0..7, over each game's three files. Same order as GAMES. */
	private static final int[][] EXPECTED = {
		{ 0x2F540D46, 0x8DE87784, 0xD1BEB7F3, 0x1BC2E82D, 0x0369A56F, 0x762D9824, 0x4998AB0A, 0xAE91AB5D }, // DRTL
		{ 0x23DF268D, 0x4935B341, 0x6B1CC053, 0x4C8F8852, 0xBD5FA272, 0xD69B3C86, 0xD5228AA9, 0x2C9651B7 }, // DSHR
		{ 0xDAAE8B63, 0x106FAC88, 0xA26A11F3, 0xE0B111E8, 0x05D520BB, 0xDD17C595, 0x85AA27F7, 0x26A91128 }, // STAR
		{ 0xE59F7F5D, 0x1283BF6B, 0x8FF3B2C0, 0x0680E425, 0xDF7B716F, 0x72C658B4, 0xE95C1FD5, 0x5DD3A63A }, // SSHR
		{ 0x1D3F5212, 0xB8BFA1ED, 0x157C5AA3, 0x2DBEFFB6, 0xB78C11CB, 0x8C19D480, 0xCF2C08B6, 0xD9E9CD02 }, // JSTR
		{ 0x5E0FC403, 0xDFAE1786, 0xBEC88D31, 0x8CDA0588, 0x2EC81583, 0x33AC65D5, 0xCDE40169, 0x4AA094BC }, // W2BN
		{ 0xB89959E0, 0xC17B4073, 0x28E2684C, 0xA380782E, 0xD7BC372F, 0x47283D43, 0x71037A94, 0x21ACC87D }, // D2DV
		{ 0xD5AA63C5, 0x8CD76947, 0x6295A9AA, 0x24AE3978, 0x8B12BB2C, 0x0000CA73, 0x9D89086D, 0x9D6492F0 }, // D2XP
		{ 0x57E88BE8, 0x34558977, 0xF7C902B7, 0xC0853EA3, 0x683C2554, 0x5E8F5879, 0xE7B6A776, 0x4E23D466 }, // WAR3
	};

	// ----- Locating Hashfiles/ -----

	private static Path hashfilesDir() {
		String prop = System.getProperty("hashfiles.dir");
		if (prop != null) {
			Path p = Paths.get(prop);
			return Files.isDirectory(p) ? p : null;
		}
		for (Path dir = Paths.get("").toAbsolutePath(); dir != null; dir = dir.getParent()) {
			Path candidate = dir.resolve("Hashfiles");
			if (Files.isDirectory(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	/** Absolute paths of a game's files (all of them if count is 3, plus the .bin if 4), or skips the test. */
	private static String[] gameFiles(String[] game, boolean withBin) {
		Path root = hashfilesDir();
		Assume.assumeTrue("Hashfiles/ not found", root != null);
		int n = game.length - 1;
		String[] files = new String[withBin ? n + 1 : n];
		for (int i = 0; i < n; i++) {
			files[i] = root.resolve(game[0]).resolve(game[i + 1]).toString();
		}
		if (withBin) {
			files[n] = root.resolve(game[0]).resolve(game[0] + ".bin").toString();
		}
		for (String f : files) {
			Assume.assumeTrue("Missing " + f, new File(f).isFile());
		}
		return files;
	}

	private static String[] gameFiles(String name) {
		for (String[] game : GAMES) {
			if (game[0].equals(name)) {
				return gameFiles(game, false);
			}
		}
		throw new IllegalArgumentException(name);
	}

	private static byte[] bytes(String s) {
		return s.getBytes(StandardCharsets.US_ASCII);
	}

	private static void assertChecksum(String message, int expected, int actual) {
		assertEquals(message, String.format("%08X", expected), String.format("%08X", actual));
	}

	// ----- DOC vectors -----

	/** DOC: VL 157871, DRTL 1.09 retail, ver-IX86-1.mpq, accepted 2006-09-13: 0x8DE87784. */
	@Test public void documentedDrtlVerIx86_1() throws Exception {
		String[] files = gameFiles("DRTL");
		assertChecksum("checkRevisionOld", 0x8DE87784, CheckRevision.checkRevisionOld(1, files, DOC_FORMULA_VER_IX86_1));
		assertChecksum("doCheckRevision", 0x8DE87784,
				CheckRevision.doCheckRevision("ver-IX86-1.mpq", files, bytes(DOC_FORMULA_VER_IX86_1)));
	}

	/**
	 * DOC: VL 154030, DRTL 1.09, IX86ver0.mpq. The server accepted the truncating value 0x867B88F3, which this
	 * implementation cannot produce; the padded value for the same input is documented as 0x2F540D46.
	 */
	@Test public void documentedDrtlIx86ver0PaddedValue() throws Exception {
		int checksum = CheckRevision.doCheckRevision("IX86ver0.mpq", gameFiles("DRTL"), bytes(DOC_FORMULA_IX86VER_0));
		assertChecksum("padded", 0x2F540D46, checksum);
		assertNotEquals("truncating value is not implemented", 0x867B88F3, checksum);
	}

	// ----- REF vectors over Hashfiles -----

	/** Every game x every MPQ number 0-7, through the fast path. */
	@Test public void oldStyleAllMpqNumbersAllGames() throws Exception {
		for (int g = 0; g < GAMES.length; g++) {
			String[] files = gameFiles(GAMES[g], false);
			for (int n = 0; n < 8; n++) {
				assertChecksum(GAMES[g][0] + " mpq " + n, EXPECTED[g][n],
						CheckRevision.checkRevisionOld(n, files, FORMULAS[n]));
			}
		}
	}

	/** checkRevisionOld (fast path for canonical formulas) and checkRevisionOldSlow (generic parser) must agree. */
	@Test public void fastAndSlowPathsAgreeOnAllGames() throws Exception {
		for (int g = 0; g < GAMES.length; g++) {
			String[] files = gameFiles(GAMES[g], false);
			for (int n = 0; n < 8; n++) {
				assertChecksum(GAMES[g][0] + " mpq " + n, EXPECTED[g][n],
						CheckRevision.checkRevisionOldSlow(n, files, FORMULAS[n]));
			}
		}
	}

	/** Operand order and unusual shapes force the slow path inside checkRevisionOld. REF values. */
	@Test public void nonCanonicalFormulasUseSlowPath() throws Exception {
		String[] formulas = {
			// last op is B^A instead of A^B: same value as the canonical DRTL mpq 0 formula
			"A=611366670 B=373021351 C=264564859 4 A=A^S B=B-C C=C^A A=B^A",
			// different destinations and operands in every step
			"A=3845581634 B=880823580 C=1363937103 4 B=B^S C=C-B A=A+C A=A-B",
			// multiplication, with B=B*C in the second step
			"A=2046065788 B=2088504843 C=235633141 4 A=A+S B=B*C C=C+A A=B^A",
		};
		int[] mpq = { 0, 3, 2 };
		int[][] expected = {
			{ 0x2F540D46, 0x8CBFD435, 0xDE87EDED }, // DRTL
			{ 0xB89959E0, 0x742934EC, 0x923D1E8E }, // D2DV
			{ 0x57E88BE8, 0xEEE91690, 0xCACDF3C0 }, // WAR3
		};
		String[] names = { "DRTL", "D2DV", "WAR3" };
		for (int g = 0; g < names.length; g++) {
			String[] files = gameFiles(names[g]);
			for (int i = 0; i < formulas.length; i++) {
				String label = names[g] + " formula " + i;
				assertChecksum(label + " old", expected[g][i], CheckRevision.checkRevisionOld(mpq[i], files, formulas[i]));
				assertChecksum(label + " slow", expected[g][i], CheckRevision.checkRevisionOldSlow(mpq[i], files, formulas[i]));
			}
		}
	}

	/** Multiplication on the fast path (A=A+S B=B*C C=C+A A=A^B). REF values. */
	@Test public void multiplicationFormulaFastPath() throws Exception {
		String formula = "A=2046065788 B=2088504843 C=235633141 4 A=A+S B=B*C C=C+A A=A^B";
		assertChecksum("DRTL", 0xDE87EDED, CheckRevision.checkRevisionOld(2, gameFiles("DRTL"), formula));
		assertChecksum("D2DV", 0x923D1E8E, CheckRevision.checkRevisionOld(2, gameFiles("D2DV"), formula));
		assertChecksum("WAR3", 0xCACDF3C0, CheckRevision.checkRevisionOld(2, gameFiles("WAR3"), formula));
	}

	/** GameData passes four files (exe, storm.dll, battle.snp and the .bin screen dump). REF value. */
	@Test public void fourFilesIncludingBin() throws Exception {
		String[] files = gameFiles(GAMES[0], true);
		assertEquals(4, files.length);
		assertChecksum("DRTL + DRTL.bin", 0xB7AA4670, CheckRevision.checkRevisionOld(1, files, FORMULAS[1]));
	}

	// ----- Synthetic files (no Hashfiles needed). REF values -----

	private String writeFile(String name, byte[] content) throws IOException {
		File f = tmp.newFile(name);
		Files.write(f.toPath(), content);
		return f.getPath();
	}

	/** 10 bytes: padded to 1024 with 0xFF, 0xFE, ... */
	@Test public void shortFileIsPaddedDescending() throws Exception {
		byte[] data = new byte[10];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i * 7 + 1);
		}
		String[] files = { writeFile("tiny.bin", data) };
		assertChecksum("old", 0x5B251817, CheckRevision.checkRevisionOld(4, files, FORMULAS[4]));
		assertChecksum("slow", 0x5B251817, CheckRevision.checkRevisionOldSlow(4, files, FORMULAS[4]));
	}

	/** An exact multiple of 1024 is not padded. */
	@Test public void exactMultipleOf1024IsNotPadded() throws Exception {
		byte[] data = new byte[2048];
		for (int i = 0; i < data.length; i++) {
			data[i] = (byte) (i * 13);
		}
		String[] files = { writeFile("k2.bin", data) };
		assertChecksum("old", 0x7D76FBAD, CheckRevision.checkRevisionOld(5, files, FORMULAS[5]));
		assertChecksum("slow", 0x7D76FBAD, CheckRevision.checkRevisionOldSlow(5, files, FORMULAS[5]));
	}

	// ----- doCheckRevision name dispatch -----

	@Test public void mpqNamesAreCaseInsensitiveAndAllPlatformsUseTheSameOldStyleHash() throws Exception {
		String[] files = gameFiles("DRTL");
		byte[] formula = bytes(FORMULAS[3]);
		int expected = EXPECTED[0][3];
		String[] names = {
			"IX86ver3.mpq", "ix86ver3.mpq", "IX86VER3.MPQ", "ver-IX86-3.mpq", "VER-ix86-3.MPQ",
			"PMACver3.mpq", "ver-PMAC-3.mpq", "XMACver3.mpq", "ver-XMAC-3.mpq",
		};
		for (String name : names) {
			assertChecksum(name, expected, CheckRevision.doCheckRevision(name, files, formula));
		}
	}

	@Test public void everyMpqNumberDispatchesToItsOwnSeed() throws Exception {
		String[] files = gameFiles("DRTL");
		for (int n = 0; n < 8; n++) {
			assertChecksum("ver-IX86-" + n, EXPECTED[0][n],
					CheckRevision.doCheckRevision("ver-IX86-" + n + ".mpq", files, bytes(FORMULAS[n])));
			assertChecksum("IX86ver" + n, EXPECTED[0][n],
					CheckRevision.doCheckRevision("IX86ver" + n + ".mpq", files, bytes(FORMULAS[n])));
		}
	}

	// ----- Failure cases (existing behavior) -----

	@Test(expected = LoginException.class) public void unknownMpqNameIsRejected() throws Exception {
		CheckRevision.doCheckRevision("nonsense.mpq", new String[0], bytes(FORMULAS[0]));
	}

	@Test(expected = LoginException.class) public void mpqNumberEightIsRejected() throws Exception {
		CheckRevision.doCheckRevision("ver-IX86-8.mpq", new String[0], bytes(FORMULAS[0]));
	}

	@Test(expected = LoginException.class) public void missingMpqExtensionIsRejected() throws Exception {
		CheckRevision.doCheckRevision("ver-IX86-1", new String[0], bytes(FORMULAS[0]));
	}

	/** Lockdown is not implemented: recognized names fail with a "not supported" LoginException. */
	@Test public void lockdownIsNotSupported() throws Exception {
		// 00-19 are accepted by the name pattern. lockdown-IX86-07.mpq is the DRTL vector in the docs (VL 162945),
		// but the Lockdown DLL from the MPQ is not in the repo, so there is no way to compute it here.
		for (String name : new String[] { "lockdown-IX86-07.mpq", "lockdown-PMAC-04.mpq", "lockdown-XMAC-13.mpq" }) {
			try {
				CheckRevision.doCheckRevision(name, new String[0], new byte[] { 1, 2, 3 });
				fail(name);
			} catch (LoginException e) {
				assertTrue(name + ": " + e.getMessage(), e.getMessage().contains("Lockdown"));
			}
		}
		try {
			CheckRevision.checkRevisionLockdown(7, new String[0], new byte[0]);
			fail();
		} catch (LoginException e) {
			assertTrue(e.getMessage().contains("not supported"));
		}
	}

	/** A lockdown number above 19 does not match the lockdown pattern at all. */
	@Test public void lockdownNumberTwentyIsUnknownName() throws Exception {
		try {
			CheckRevision.doCheckRevision("lockdown-IX86-20.mpq", new String[0], new byte[0]);
			fail();
		} catch (LoginException e) {
			assertTrue(e.getMessage(), e.getMessage().contains("doesn't match"));
		}
	}

	@Test public void missingFileThrowsFileNotFound() throws Exception {
		String[] files = { new File(tmp.getRoot(), "does-not-exist.exe").getPath() };
		try {
			CheckRevision.checkRevisionOld(0, files, FORMULAS[0]);
			fail();
		} catch (FileNotFoundException expected) {
			// ok
		}
		try {
			CheckRevision.checkRevisionOldSlow(0, files, FORMULAS[0]);
			fail();
		} catch (FileNotFoundException expected) {
			// ok
		}
	}

	/** Anything other than exactly 8 space-separated tokens is "malformed" and returns -1. */
	@Test public void malformedFormulaTokenCountReturnsMinusOne() throws Exception {
		String[] files = new String[0];
		String[] bad = {
			"",
			"A=1 B=2 C=3",
			"A=611366670 B=373021351 C=264564859 4 A=A^S B=B-C C=C^A",           // 7 tokens
			"A=611366670 B=373021351 C=264564859 4 A=A^S B=B-C C=C^A A=A^B X=1", // 9 tokens
		};
		for (String formula : bad) {
			assertEquals("old: '" + formula + "'", -1, CheckRevision.checkRevisionOld(0, files, formula));
			assertEquals("slow: '" + formula + "'", -1, CheckRevision.checkRevisionOldSlow(0, files, formula));
		}
	}

	/**
	 * Surprising existing behavior: if any of A, B, C is missing or 0 the fast path returns 0 without reading
	 * any file (so the missing-file case is not even noticed). The slow path has no such check and computes.
	 */
	@Test public void zeroOrMissingSeedReturnsZeroOnFastPath() throws Exception {
		String[] files = { new File(tmp.getRoot(), "does-not-exist.exe").getPath() };
		assertEquals(0, CheckRevision.checkRevisionOld(0, files, "A=0 B=373021351 C=264564859 4 A=A^S B=B-C C=C^A A=A^B"));
		assertEquals(0, CheckRevision.checkRevisionOld(0, files, "A=611366670 B=373021351 X=264564859 4 A=A^S B=B-C C=C^A A=A^B"));
	}

	/** Surprising existing behavior: a non-numeric seed is a NumberFormatException, not a malformed (-1) result. */
	@Test(expected = NumberFormatException.class) public void nonNumericSeedThrows() throws Exception {
		CheckRevision.checkRevisionOld(0, new String[0], "A=abc B=373021351 C=264564859 4 A=A^S B=B-C C=C^A A=A^B");
	}

	/** Surprising existing behavior: MPQ numbers outside 0-7 are not checked by checkRevisionOld itself. */
	@Test(expected = ArrayIndexOutOfBoundsException.class) public void directCallWithMpqNumberEightFails() throws Exception {
		CheckRevision.checkRevisionOld(8, new String[0], FORMULAS[0]);
	}

	/** No files: the result is just C after the seed XOR (A ^= hashcode, C untouched). */
	@Test public void noFilesReturnsInitialC() throws Exception {
		assertEquals(264564859, CheckRevision.checkRevisionOld(0, new String[0], FORMULAS[0]));
		assertEquals(264564859, CheckRevision.checkRevisionOldSlow(0, new String[0], FORMULAS[0]));
	}
}
