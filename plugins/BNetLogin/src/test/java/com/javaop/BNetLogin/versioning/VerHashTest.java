package com.javaop.BNetLogin.versioning;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * EXE version DWORD (SID_AUTH_CHECK) from the PE version resource of the Hashfiles/ executables.
 *
 * Expected values: REF = Bnet.Hashing.CheckRevision.GetExeVersion (bnet-dev/bnet-core), which is itself ported from
 * JBLS util/PE. DRTL 0x01000901 is also DOC (VL 154030, accepted by Battle.net; docs/checkrevision.md) and W2BN
 * 0x02000200 is the DOC W2BN 2.02 version. Skips when Hashfiles/ is not found; -Dhashfiles.dir overrides the search.
 */
public class VerHashTest {

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

	private static int version(String relative) {
		Path root = hashfilesDir();
		Assume.assumeTrue("Hashfiles/ not found", root != null);
		Path exe = root.resolve(relative);
		Assume.assumeTrue("Missing " + exe, Files.isRegularFile(exe));
		return VerHash.getVersion(exe.toString());
	}

	private static void assertVersion(String relative, int expected) {
		assertEquals(relative, String.format("%08X", expected), String.format("%08X", version(relative)));
	}

	@Test public void diabloRetail() { assertVersion("DRTL/Diablo.exe", 0x01000901); }
	@Test public void diabloShareware() { assertVersion("DSHR/Diablo_s.exe", 0x01000901); }
	@Test public void warcraft2() { assertVersion("W2BN/Warcraft II BNE.exe", 0x02000200); }
	@Test public void starcraft() { assertVersion("STAR/Starcraft.exe", 0x01100101); }
	@Test public void starcraftShareware() { assertVersion("SSHR/Starcraft_s.exe", 0x01010001); }
	@Test public void starcraftKorean() { assertVersion("JSTR/StarcraftJ.exe", 0x01000000); }
	@Test public void diablo2() { assertVersion("D2DV/game.exe", 0x01000D00); }
	@Test public void diablo2Expansion() { assertVersion("D2XP/game.exe", 0x01000D00); }
	@Test public void warcraft3() { assertVersion("WAR3/war3.exe", 0x011804F3); }

	/** Existing behavior: an unreadable file gives 0 (the IOException is printed, not thrown). */
	@Test public void missingFileGivesZero() {
		assertEquals(0, VerHash.getVersion("/nonexistent/definitely-not-here.exe"));
	}
}
