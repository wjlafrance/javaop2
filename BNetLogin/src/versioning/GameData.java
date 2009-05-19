package versioning;

import util.PersistantMap;
import util.RelativeFile;

/*
 * Created on Mar 2, 2005 By iago
 */

public class GameData
{

	private PersistantMap games;

	public GameData()
	{
		games = new PersistantMap(
				new RelativeFile("_GameData.txt"),
				"These are the important informations for games to log in with -- values are stored in hex.");
		this.initialize();
	}

	public int getVersionByte(String game)
	{
		return Integer.parseInt(games.getNoWrite(game, "Version byte", "0"), 16);
	}

	public int getVersionHash(String game)
	{
		int calc = VerHash.getVersion(games.getNoWrite(game, "File1", null));
		if (calc == 0) // File1 is absent
			calc = Integer.parseInt(games.getNoWrite(game, "Version hash", "0"), 16);
		return calc;
	}

	public String[] getFiles(String game)
	{
		String[] files = new String[3];
		files[0] = games.getNoWrite(game, "File1", null);
		files[1] = games.getNoWrite(game, "File2", null);
		files[2] = games.getNoWrite(game, "File3", null);

		return files;
	}

	public boolean hasTwoKeys(String game)
	{
		if(game.equals("STAR")) return false;
		else if(game.equals("SEXP")) return false;
		else if(game.equals("D2DV")) return false;
		else if(game.equals("D2XP")) return true;
		else if(game.equals("W2BN")) return false;
		else if(game.equals("WAR3")) return false;
		else if(game.equals("W3XP")) return true;
		else return false; // wtf?
	}


	private void initialize()
	{
		String prefix = System.getProperty("user.home") + "/.hashes/";

		games.getWrite("STAR", "Version byte", "D3");
		games.getWrite("SEXP", "Version byte", "D3");
		games.getWrite("D2DV", "Version byte", "0C");
		games.getWrite("D2XP", "Version byte", "0C");
		games.getWrite("W2BN", "Version byte", "4F");
		games.getWrite("WAR3", "Version byte", "17");
		games.getWrite("W3XP", "Version byte", "17");

		games.getWrite("STAR", "File1", prefix + "STAR/starcraft.exe");
		games.getWrite("STAR", "File2", prefix + "STAR/storm.dll");
		games.getWrite("STAR", "File3", prefix + "STAR/battle.snp");
		games.getWrite("STAR", "File4", prefix + "STAR/STAR.bin");
		games.getWrite("SEXP", "File1", prefix + "STAR/starcraft.exe");
		games.getWrite("SEXP", "File2", prefix + "STAR/storm.dll");
		games.getWrite("SEXP", "File3", prefix + "STAR/battle.snp");
		games.getWrite("SEXP", "File4", prefix + "STAR/STAR.bin");
		games.getWrite("D2DV", "File1", prefix + "D2DV/Game.exe");
		games.getWrite("D2DV", "File2", prefix + "D2DV/Bnclient.dll");
		games.getWrite("D2DV", "File3", prefix + "D2DV/D2Client.dll");
		games.getWrite("D2XP", "File1", prefix + "D2XP/Game.exe");
		games.getWrite("D2XP", "File2", prefix + "D2XP/Bnclient.dll");
		games.getWrite("D2XP", "File3", prefix + "D2XP/D2Client.dll");
		games.getWrite("W2BN", "File1", prefix + "W2BN/Warcraft II BNE.exe");
		games.getWrite("W2BN", "File2", prefix + "W2BN/storm.dll");
		games.getWrite("W2BN", "File3", prefix + "W2BN/battle.snp");
		games.getWrite("W2BN", "File4", prefix + "W2BN/W2BN.bin");
		games.getWrite("WAR3", "File1", prefix + "WAR3/war3.exe");
		games.getWrite("WAR3", "File2", prefix + "WAR3/Storm.dll");
		games.getWrite("WAR3", "File3", prefix + "WAR3/game.dll");
		games.getWrite("W3XP", "File1", prefix + "WAR3/war3.exe");
		games.getWrite("W3XP", "File2", prefix + "WAR3/Storm.dll");
		games.getWrite("W3XP", "File3", prefix + "WAR3/game.dll");

		// Just in case they don't have the file handy
		games.getWrite("STAR", "Version hash", "01010303");
		games.getWrite("SEXP", "Version hash", "01010303");
		games.getWrite("D2DV", "Version hash", "01000c00");
		games.getWrite("D2XP", "Version hash", "01000c00");
		games.getWrite("W2BN", "Version hash", "01010001");
		games.getWrite("WAR3", "Version hash", "01001027");
		games.getWrite("W3XP", "Version hash", "01001027");

		/*
		games.getWrite("STAR", "Has2Keys", "false");
		games.getWrite("SEXP", "Has2Keys", "false");
		games.getWrite("D2DV", "Has2Keys", "false");
		games.getWrite("D2XP", "Has2Keys", "true");
		games.getWrite("W2BN", "Has2Keys", "false");
		games.getWrite("WAR3", "Has2Keys", "false");
		games.getWrite("W3XP", "Has2Keys", "true");
		*/

	}
}
