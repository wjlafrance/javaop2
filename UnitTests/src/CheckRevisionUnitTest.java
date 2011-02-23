package com.javaop.UnitTests;
import junit.framework.*;

import com.javaop.BNetLogin.versioning.CheckRevision;
import com.javaop.BNetLogin.versioning.CheckRevisionResults;

import java.lang.reflect.Method;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CheckRevisionUnitTest {
	
	private static Class[] doCheckRevisionReturnType =
			new Class[] { String.class, String[].class, byte[].class };

  	@BeforeClass
  	public static void initialSetUp() {
  	}

	@AfterClass
	public static void tearDown() {
	}
	
	@Test
	public void classExists() {
		Assert.assertNotNull(CheckRevision.class);
	}

	@Test
	public void doCheckRevisionMethodExists() throws NoSuchMethodException {
		Method localMethod = CheckRevision.class.getMethod("doCheckRevision", doCheckRevisionReturnType);
		Assert.assertNotNull(localMethod);
	}

	@Test
	public void doCheckRevisionCorrectReturnType() throws NoSuchMethodException {
		Method localMethod = CheckRevision.class.getMethod("doCheckRevision", doCheckRevisionReturnType);
		Assert.assertEquals(int.class, localMethod.getReturnType());
	}
}