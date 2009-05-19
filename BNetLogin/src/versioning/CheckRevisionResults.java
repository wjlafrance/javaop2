package versioning;

public class CheckRevisionResults
{

	private int m_verhash;
	private int m_checksum;
	private byte[] m_statstring;

	public CheckRevisionResults(int verhash, int checksum, byte[] statstring)
	{
		m_checksum = checksum;
		m_verhash = verhash;
		m_statstring = statstring;
	}

	public int getChecksum()
	{
		return m_checksum;
	}

	public int getVerhash()
	{
		return m_verhash;
	}

	public byte[] getStatstring()
	{
		return m_statstring;
	}
}
