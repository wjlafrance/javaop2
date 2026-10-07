package com.javaop.BNetLogin.versioning;

/**
 * This is a small class to hold the version hash, checksum, and EXE
 * statstring so that they can all be returned from one CheckRevision call.
 * Consider it to be like a struct.
 *
 * @author wjlafrance
 */
public class CheckRevisionResults {
	public final int verhash;
	public final int checksum;
	public final byte[] statstring;

	public CheckRevisionResults(int verhash, int checksum, byte[] statstring) {
		this.verhash = verhash;
		this.checksum = checksum;
		this.statstring = statstring;
	}

	public int getVerhash() {
		return this.verhash;
	}

	public int getChecksum() {
		return this.checksum;
	}

	public byte[] getStatstring() {
		return this.statstring;
	}
}
