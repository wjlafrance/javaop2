/*
 * Created on Aug 27, 2004 By iago
 */
package com.javaop.util;

import java.io.Serializable;


/**
 * @author iago
 *
 */
public interface User extends Serializable
{
	public String getName();

	public int getPing();

	public int getFlags();

	public String getPrettyStatstring();

	public String getRawStatstring();

	public long getJoinTime();

	public String toString();
}
