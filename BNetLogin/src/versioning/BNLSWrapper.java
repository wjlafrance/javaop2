package versioning;

import java.io.IOException;
import java.util.LinkedList;

import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import constants.ErrorLevelConstants;
import exceptions.InvalidCDKey;
import exceptions.InvalidVersion;


/**
 * @author Joe[x86]
 * Converted from iago's RCRSWrapper on 02/07/07
 */
public class BNLSWrapper
{

	private final Game g;
	private final PublicExposedFunctions out;

	private BNLSClient bnls;

	private static String bnlsServer;
	private static final LinkedList servers = new LinkedList();

	private static Object lock = new Object();

	public BNLSWrapper(String game, PublicExposedFunctions out) throws InvalidVersion
	{
		synchronized(lock)
		{
			this.out = out;
			g = new Game(game);

			setServer();
		}
	}

	public synchronized static void initialize(StaticExposedFunctions staticFuncs, String botName)
	{
		String[] splitServers = staticFuncs.getGlobalSettingDefault(botName,
				"BNLS Server", "jbls.org, pyro.no-ip.biz").split(",");
		for(int i = 0; i < splitServers.length; i++)
		{
			splitServers[i] = splitServers[i].trim();
			if(splitServers[i].length() == 0)
				continue;
			
			servers.add(splitServers[i]);
		}
	}

	// Pulls out the next BNLS server
	private synchronized void setServer()
	{
		if(servers.size() == 0)
		{
			System.err.println("Unable to find usable BNLS server");
			bnls = null;
			return;
		}

		bnlsServer = (String) servers.getFirst();
		out.systemMessage(ErrorLevelConstants.INFO, "Trying BNLS server: " + bnlsServer);

		bnls = new BNLSClient(bnlsServer, g.getName(), out);
	}

	public synchronized int getVersionByte()
	{
		try
		{
			if(bnls != null)
				return bnls.getVersionByte();
		}
		catch(IOException e)
		{
			out.systemMessage(ErrorLevelConstants.WARNING, "BNLS server '" + bnlsServer + "' returned an error -- this means the server might not be running.");
			out.systemMessage(ErrorLevelConstants.WARNING, "The error is: " + e);
			out.systemMessage(ErrorLevelConstants.INFO, "Attempting to use next BNLS server.");

			servers.remove(0);
			setServer();

			if(bnls != null)
				getVersionByte();
			else
				out.systemMessage(ErrorLevelConstants.WARNING, "Out of BNLS servers, defaulting to local hashing.");
		}

		return g.getVersionByte();
	}

	public synchronized CheckRevisionResults getVersionCheck(String filename, byte[] formula, long filetime) throws InvalidVersion
	{
		try
		{
			if(bnls != null)
				return bnls.getVersionCheck(filename, formula, filetime);
		}
		catch(IOException e)
		{
			out.systemMessage(ErrorLevelConstants.WARNING, "(BNLS not responding: " + e + ")");

			out.systemMessage(ErrorLevelConstants.INFO, "Attempting to use next BNLS server.");

			servers.remove(0);
			setServer();

			if(bnls != null)
				getVersionByte();
			else
				out.systemMessage(ErrorLevelConstants.WARNING, "Out of BNLS servers, defaulting to local hashing.");
		}
		
		return new CheckRevisionResults(g.getVersionHash(), g.checkRevision(new String(formula), filename), g.getExeInfo().getBytes());
		// FIXME: Ew++ at that.
	}

	public synchronized int getGameCode()
	{
		return g.getGameCode();
	}
	
	public String getGame()
	{
		return g.getGame();
	}

	public synchronized byte[] getCDKey(int clientToken, int serverToken, String cdkey1, String cdkey2) throws InvalidCDKey
	{
		return g.getKeyBuffer(cdkey1, cdkey2, clientToken, serverToken).getBytes();
	}

	public synchronized String getGameInfo(String game)
	{
		try
		{
			if(game == null)
				return g.toString();
			return new Game(game).toString();
		}
		catch(Exception e)
		{
			return "Error: unknown game";
		}
	}
}
