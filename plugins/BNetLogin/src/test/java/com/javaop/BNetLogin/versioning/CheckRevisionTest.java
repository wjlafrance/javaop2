package com.javaop.BNetLogin.versioning;

import org.junit.Test;

import static org.junit.Assert.*;

public class CheckRevisionTest {

	private static final String[] d2files = new String[] {
		"/Users/lafrance/dev/javaop2/Hashfiles/D2DV/Bnclient.dll",
		"/Users/lafrance/dev/javaop2/Hashfiles/D2DV/D2Client.dll",
		"/Users/lafrance/dev/javaop2/Hashfiles/D2DV/game.exe"
	};

	public @Test void testDoCheckRevision() throws Exception {
		assertEquals(-729125288, CheckRevision.doCheckRevision("ver-ix86-7.mpq", d2files, "A=3845581634 B=880823580 C=1363937103 4 A=A-S B=B*C C=C+A A=A^B".getBytes()));
		assertEquals(-729125288, CheckRevision.checkRevisionOld(7, d2files, "A=3845581634 B=880823580 C=1363937103 4 A=A-S B=B*C C=C+A A=A^B"));
		assertEquals(-729125288, CheckRevision.checkRevisionOldSlow(7, d2files, "A=3845581634 B=880823580 C=1363937103 4 A=A-S B=B*C C=C+A A=A^B"));
	}
}