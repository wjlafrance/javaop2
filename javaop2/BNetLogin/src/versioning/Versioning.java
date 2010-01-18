/*
 * Created on Sep 17, 2009
 * 
 * By joe
 */
package versioning;

import util.Buffer;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import constants.ErrorLevelConstants;
import exceptions.LoginException;


/**
 * @author wjlafrance
 *
 * This is the entry point for all things versioning. This is analogous to the
 * old BNLSWrapper.
 */
public class Versioning {
	
	private static boolean bnlsFailed = false;

	/**
	 * Attempts to use BNLS to get the version byte. If BNLS is unavailable for
	 * whatever reason, use the locally stored value.
	 * @return
	 */
	public static int VersionByte(String game, PublicExposedFunctions pubFuncs) throws LoginException {
		StaticExposedFunctions staticFuncs = pubFuncs.getStaticExposedFunctionsHandle();
		
		Game g = new Game(game);
		if(bnlsFailed || !Bnls.IsEnabled(staticFuncs)) {
			pubFuncs.systemMessage(ErrorLevelConstants.INFO, "Using local version byte for " + g.getName());
			return g.getVersionByte();
		} else {
			try {
				pubFuncs.systemMessage(ErrorLevelConstants.INFO, "[BNLS] Retreiving version byte for " + g.getName());
				return Bnls.VersionByte(staticFuncs, g);
			} catch(Exception ex) {
				bnlsFailed = true;
				pubFuncs.systemMessage(ErrorLevelConstants.WARNING,
						"Caught exception when attempting to get verbyte from BNLS.");
				pubFuncs.systemMessage(ErrorLevelConstants.WARNING,
						"Stack trace available on console. Recursing.");
				ex.printStackTrace();
				return VersionByte(game, pubFuncs); // recurse
			}
		}
	}

	/**
	 * This will attempt to use BNLS to calculate a version check. If BNLS is
	 * unavailable for whatever reason, we'll resort to doing it locally.
	 * @param filename MPQ name (S: SID_AUTH_INFO)
	 * @param formula Version check formula (S: SID_AUTH_INFO)
	 * @param filetime MPQ filetime (S: SID_AUTH_INFO)
	 * @return
	 * @throws InvalidVersion
	 */
	public static synchronized CheckRevisionResults CheckRevision(String game,
			PublicExposedFunctions pubFuncs, String filename,
			byte[] formula, long filetime) throws LoginException
	{
		StaticExposedFunctions staticFuncs = pubFuncs.getStaticExposedFunctionsHandle();
		Game g = new Game(game);
		if(bnlsFailed || !Bnls.IsEnabled(staticFuncs)) {
			pubFuncs.systemMessage(ErrorLevelConstants.INFO, "Running local version check for " + g.getName());
			return new CheckRevisionResults(
					g.getVersionHash(),
					g.doCheckRevision(formula, filename),
					g.getExeInfo().getBytes()
				);
		} else {
			try {
				pubFuncs.systemMessage(ErrorLevelConstants.INFO, "[BNLS] Running version check for " + g.getName());
				return Bnls.CheckRevision(g, staticFuncs, filename, filetime, formula);
			} catch(Exception ex) {
				bnlsFailed = true;
				pubFuncs.systemMessage(ErrorLevelConstants.WARNING,
						"Caught exception when attempting to perform check revision with BNLS.");
				pubFuncs.systemMessage(ErrorLevelConstants.WARNING,
						"Stack trace available on console. Recursing.");
				ex.printStackTrace();
				return CheckRevision(game, pubFuncs, filename, formula,
						filetime); // recurse
			}
		}
	}

	/**
	 * CD-Key Block for SID_AUTH_CHECK
	 * @return byte[] CD Key block
	 * @throws InvalidCDKey
	 */
	public static Buffer CDKeyBlock(String game, int clientToken, int serverToken,
			String cdkey1, String cdkey2) throws LoginException {
		return new Game(game).getKeyBuffer(cdkey1, cdkey2, clientToken, serverToken);
	}
}
