package com.javaop.users;

/**
 * Parses statstrings. Information from:
 * http://www.geocities.com/wizardguy@sbcglobal.net/statparse.txt
 *
 * @author joe[x86]
 */
public class Statstring
{
	private final String[] tokens;

	public Statstring(String statstring) {
		if (statstring.length() >= 4) {
			this.tokens = statstring.split(" ", -1);
		} else {
			this.tokens = null;
		}
	}

	/**
	 * @return Backwards client ID
	 */
	public String getClient()
	{
		if (tokens == null) {
			return "TAHC";
		}

		return tokens[0].substring(0, 4);
	}

	/**
	 * @return WarCraft III: Clan name.
	 */
	public String getClan() {
		if (tokens == null || tokens.length < 4) {
			return "";
		}

		if (clientStatstringContainsClan(getClient())) {
			return new StringBuffer(tokens[3]).reverse().toString();
		}

		return "";
	}

	/**
	 * @return StarCraft / WarCraft II: Wins
	 */
	public int getWins() {
		if (tokens == null || tokens.length < 2) {
			return 0;
		}

		if (clientStatstringContainsWinCount(getClient())) {
			return Integer.parseInt(tokens[1]);
		} else {
			return 0;
		}
	}

	private static boolean clientStatstringContainsClan(final String client) {
		return "3RAW".equals(client) || "PX3W".equals(client);
	}

	private static boolean clientStatstringContainsWinCount(final String client) {
		return "RATS".equals(client) || "PXES".equals(client)
				|| "RTSJ".equals(client) || "NB2W".equals(client);

	}
}
