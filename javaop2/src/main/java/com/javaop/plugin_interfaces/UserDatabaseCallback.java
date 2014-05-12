/*
 * Created on Dec 4, 2004 By iago
 */
package com.javaop.plugin_interfaces;

/**
 * These are callbacks for the user database. The flags and such that the user
 * has.
 *
 * @author iago
 *
 */
public interface UserDatabaseCallback extends AbstractCallback
{
	/** A user who wasn't in the database before was added */
	public void userAdded(String username, String flags, Object data);

	/** A user who was already in the database was given new flags */
	public void userChanged(String username, String oldFlags, String newFlags, Object data);

	/** A user who was in the database before was removed */
	public void userRemoved(String username, String oldFlags, Object data);
}
