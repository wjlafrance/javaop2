package versioning;

/**
 * This is a small class to hold the version hash, checksum, and EXE
 * statstring so that they can all be returned from one CheckRevision call.
 * Consider it to be like a struct.
 * 
 * @author wjlafrance
 */
public class CheckRevisionResults {
	public int verhash;
	public int checksum;
	public byte[] statstring;

	public CheckRevisionResults(int verhash, int checksum, byte[] statstring) {
		this.checksum = checksum;
		this.verhash = verhash;
		this.statstring = statstring;
	}
}
