/*
 * CheckRevision.java
 * 
 * Created on March 10, 2004, 9:05 AM
 */
package versioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import java.util.StringTokenizer;

import util.RelativeFile;

import exceptions.LoginException;

/**
 * This takes care of the CheckRevision() for the main game files of any
 * program. This is done to prevent tampering and to make sure the version is
 * correct. This class is generally slow because it has to read through the
 * entire files. The majority of the time is spent in i/o, but I've tried to
 * optimize this as much as possible.
 * 
 * @author iago, joe
 */
public class CheckRevision
{
	
	/**
	 * This is the main entry point for doing CheckRevision. This sorts out
	 * what kind of CheckRevision we're doing and then calls another function.
	 * @param mpqName MPQ name specified in SID_AUTH_INFO response
	 * @param files Files to run CheckRevision on
	 * @param formula Version check formula specified in SID_AUTH_INFO response
	 * @return
	 */
	public static int doCheckRevision(String mpqName, String[] files, byte[] formula)
		throws LoginException
	{
		
		if(mpqName.toLowerCase().matches("ix86ver[0-7].mpq"))
		{
			// IX86ver0.mpq
			String[] ix86Files = new String[] { files[0], files[1], files[2] };
			return CheckRevision_IX86Ver(Integer.parseInt(mpqName.substring(7, 8)),
				ix86Files, new String(formula));
		}
		if(mpqName.toLowerCase().matches("ver-ix86-[0-7].mpq"))
		{
			// ver-IX86-0.mpq
			String[] ix86Files = new String[] { files[0], files[1], files[2] };
			return CheckRevision_IX86Ver(Integer.parseInt(mpqName.substring(9, 10)),
				ix86Files, new String(formula));
		}
		throw new LoginException("Unable to locally hash for MPQ file " + mpqName);
	
	}


	/**
	 * Performs a IX86-Ver style CheckRevision.
	 * 
	 * @param mpqNumber MPQ number specified in SID_AUTH_INFO response
	 * @param files The array of files we're checking. Generally the main game
	 * files, like Starcraft.exe, Storm.dll, and Battle.snp.
	 * @param formula Version check formula from SID_AUTH_INFO response
	 * @throws FileNotFoundException If the datafiles aren't found.
	 * @throws IOException If there is an error reading from one of the datafiles.
	 * @return The 32-bit CheckRevision hash.
	 */
	private static int CheckRevision_IX86Ver(int mpqNumber, String[] files,
		String formula) throws LoginException
	{

		/** These are the hashcodes for the various .mpq files. */
		int hashcodes[] =
		{
			0xE7F4CB62, 0xF6A14FFC, 0xAA5504AF, 0x871FCDC2,
			0x11BF6A18, 0xC57292E6, 0x7927D27E, 0x2FEC8733
		};
		
		// First, parse the versionString to name=value pairs and put them
		// in the appropriate place
		long[] values = new long[4];

		int[] opValueDest = new int[4];
		int[] opValueSrc1 = new int[4];
		char[] operation = new char[4];
		int[] opValueSrc2 = new int[4];

		// Break this apart at the spaces
		StringTokenizer s = new StringTokenizer(formula, " ");
		int currentFormula = 0;
		while(s.hasMoreTokens())
		{
			String thisToken = s.nextToken();
			// As long as there is an '=' in the string
			if(thisToken.indexOf('=') > 0)
			{
				// Break it apart at the '='
				StringTokenizer nameValue = new StringTokenizer(thisToken, "=");
				if(nameValue.countTokens() != 2)
					return 0;

				int variable = getNum(nameValue.nextToken().charAt(0));

				String value = nameValue.nextToken();

				// If it starts with a number, assign that number to the appropriate variable
				if(Character.isDigit(value.charAt(0)))
				{
					values[variable] = Long.parseLong(value);
				}
				else
				{
					opValueDest[currentFormula] = variable;

					opValueSrc1[currentFormula] = getNum(value.charAt(0));
					operation[currentFormula] = value.charAt(1);
					opValueSrc2[currentFormula] = getNum(value.charAt(2));

					currentFormula++;
				}
			}
		}

		// Now we actually do the hashing for each file
		// Start by hashing A by the hashcode
		values[0] ^= hashcodes[mpqNumber];

		for(int i = 0; i < 3; i++)
		{
			File currentFile = new File(files[i]);
			int roundedSize = (int) ((currentFile.length() / 1024) * 1024);

			MappedByteBuffer fileData;
			
			try 
			{
				fileData = new FileInputStream(currentFile).getChannel()
					.map(FileChannel.MapMode.READ_ONLY, 0, roundedSize);
				fileData.order(ByteOrder.LITTLE_ENDIAN);
			}
			catch(Exception ex)
			{
				String error = "You are missing files necessary to connect.\n" +
					"Please ensure that you have the latest version of the " +
					"appropriate files:\n";
				for(int j = 0; j < files.length; j++)
				{
					RelativeFile thisFile = new RelativeFile(files[j]);
					thisFile.getParentFile().mkdirs();
					error += thisFile.getAbsolutePath() + "\n";
				}
				error += "\nInternal Exception: " + ex.toString() + "\n";

				throw new LoginException(error);
			}
			
			for(int j = 0; j < roundedSize; j += 4)
			{
				values[3] = fileData.getInt(j);

				for(int k = 0; k < currentFormula; k++)
				{
					switch(operation[k])
					{
						case '+':
							values[opValueDest[k]] = values[opValueSrc1[k]] +
								values[opValueSrc2[k]];
							break;
	
						case '-':
							values[opValueDest[k]] = values[opValueSrc1[k]] -
								values[opValueSrc2[k]];
							break;
	
						case '^':
							values[opValueDest[k]] = values[opValueSrc1[k]] ^
								values[opValueSrc2[k]];
					}
				}
			}
		}

		return (int)values[2];
	}

	/**
	 * Converts the parameter to which number in the array it is, based on A=0, B=1, C=2, S=3.
	 * 
	 * @param c
	 *            The character letter.
	 * @return The array number this is found at.
	 */
	private static int getNum(char c)
	{
		c = Character.toUpperCase(c);
		if(c == 'S')
			return 3;

		return c - 'A';
	}
}