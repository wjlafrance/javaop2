package com.javaop.BNetLogin.versioning;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

public class GameExeInfoTest
{
	private static long at(int year, int month1Based, int day, int hour, int minute, int second)
	{
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month1Based - 1, day, hour, minute, second);
		return c.getTimeInMillis();
	}

	/** The documented DRTL exe info (VL forum): April must print as 04, not 03. */
	@Test
	public void documentedDiabloString()
	{
		assertEquals("Diablo.exe 04/11/01 16:53:18 757760", Game.formatExeInfo("Diablo.exe", at(2001, 4, 11, 16, 53, 18), 757760));
	}

	@Test
	public void januaryIsOne()
	{
		assertEquals("a.exe 01/05/03 04:21:56 10", Game.formatExeInfo("a.exe", at(2003, 1, 5, 4, 21, 56), 10));
	}

	@Test
	public void decemberIsTwelve()
	{
		assertEquals("a.exe 12/31/99 23:59:59 1", Game.formatExeInfo("a.exe", at(1999, 12, 31, 23, 59, 59), 1));
	}
}
