package com.javaop.BNetLogin.versioning;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Assume;
import org.junit.Test;

/** DRTL / DSHR game data. Values: bnet-dev/docs/product-status.md (verbyte 0x2A, no key) and docs/checkrevision.md. */
public class DiabloGameTest {

	@Test public void gameNamesNormalize() throws Exception {
		for (String name : new String[] { "DRTL", "drtl", "Diablo", "diablo", "LTRD", "Diablo Retail" }) {
			assertEquals(name, "DRTL", new Game(name).getName());
		}
		for (String name : new String[] { "DSHR", "dshr", "Diablo Shareware", "diablo shareware", "RHSD" }) {
			assertEquals(name, "DSHR", new Game(name).getName());
		}
	}

	@Test public void gameCodesAreTheProductDwords() throws Exception {
		assertEquals(('D' << 24) | ('R' << 16) | ('T' << 8) | 'L', new Game("DRTL").getGameCode());
		assertEquals(('D' << 24) | ('S' << 16) | ('H' << 8) | 'R', new Game("DSHR").getGameCode());
	}

	@Test public void bothAreInTheGameList() {
		assertTrue(Game.getGames().contains("Diablo"));
		assertTrue(Game.getGames().contains("Diablo Shareware"));
	}

	@Test public void versionByteIs2A() {
		GameData data = new GameData();
		assertEquals(0x2A, data.getVersionByte("DRTL"));
		assertEquals(0x2A, data.getVersionByte("DSHR"));
	}

	@Test public void noCdKeys() {
		GameData data = new GameData();
		assertEquals(0, data.numberOfKeys("DRTL"));
		assertEquals(0, data.numberOfKeys("DSHR"));
		assertEquals(1, data.numberOfKeys("STAR"));
		assertEquals(2, data.numberOfKeys("D2XP"));
	}

	@Test public void keyBlockIsEmpty() throws Exception {
		// DWORD key count 0, DWORD spawn 0, nothing else
		assertEquals(8, new Game("DRTL").getKeyBuffer("", "", 1, 2).getBytes().length);
		assertEquals(8, new Game("DSHR").getKeyBuffer(null, null, 1, 2).getBytes().length);
	}

	@Test public void hashFileSetsMatchTheReference() {
		GameData data = new GameData();
		String[] drtl = data.getFiles("DRTL");
		String[] dshr = data.getFiles("DSHR");
		assertTrue(drtl[0], drtl[0].endsWith("DRTL/Diablo.exe"));
		assertTrue(drtl[1], drtl[1].toLowerCase().endsWith("drtl/storm.dll"));
		assertTrue(drtl[2], drtl[2].toLowerCase().endsWith("drtl/battle.snp"));
		assertTrue(drtl[3], drtl[3].endsWith("DRTL/DRTL.bin"));
		assertTrue(dshr[0], dshr[0].endsWith("DSHR/Diablo_s.exe"));
		assertTrue(dshr[3], dshr[3].endsWith("DSHR/DSHR.bin"));
	}

	@Test public void diabloPolicy() {
		assertTrue(GameData.isDiablo("DRTL"));
		assertTrue(GameData.isDiablo("DSHR"));
		assertFalse(GameData.isDiablo("D2DV"));
		assertTrue(GameData.usesLegacyLogonPacket("DRTL"));
		assertFalse(GameData.usesLegacyLogonPacket("W2BN"));
		assertTrue(GameData.isChatMenuRestricted("DSHR"));
		assertFalse(GameData.isChatMenuRestricted("STAR"));
		assertEquals("Diablo", GameData.firstJoinChannel("DRTL"));
		assertEquals("Diablo Shareware", GameData.firstJoinChannel("DSHR"));
	}

	/** DOC (VL 154030): the documented exe info for the 1.09 Diablo.exe is size 757760. */
	@Test public void hashfileSizesAndExeInfoName() throws Exception {
		Path root = null;
		for (Path dir = Paths.get("").toAbsolutePath(); dir != null && root == null; dir = dir.getParent()) {
			if (Files.isDirectory(dir.resolve("Hashfiles"))) {
				root = dir.resolve("Hashfiles");
			}
		}
		Assume.assumeTrue(root != null);
		Path exe = root.resolve("DRTL/Diablo.exe");
		Assume.assumeTrue(Files.isRegularFile(exe));
		assertEquals(757760, Files.size(exe));
		String info = Game.formatExeInfo(exe.getFileName().toString(), 0, Files.size(exe));
		assertTrue(info, info.startsWith("Diablo.exe ") && info.endsWith(" 757760"));
	}
}
