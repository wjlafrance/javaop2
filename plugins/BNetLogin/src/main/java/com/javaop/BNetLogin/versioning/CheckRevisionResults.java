package com.javaop.BNetLogin.versioning;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This is a small class to hold the version hash, checksum, and EXE
 * statstring so that they can all be returned from one CheckRevision call.
 * Consider it to be like a struct.
 *
 * @author wjlafrance
 */
public @RequiredArgsConstructor @Getter class CheckRevisionResults {

	public final int verhash;
	public final int checksum;
	public final byte[] statstring;

}
