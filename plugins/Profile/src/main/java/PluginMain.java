package com.javaop.Profile;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.plugin_interfaces.PacketCallback;
import com.javaop.util.BnetPacket;
import com.javaop.util.FileTime;
import com.javaop.util.TimeReader;


/*
 * Created on Apr 21, 2005 By iago
 */

public class PluginMain extends GenericPluginInterface implements PacketCallback, CommandCallback,
		EventCallback
{
	private int                    cookie   = getName().hashCode();
	private Hashtable              requests = new Hashtable();

	private PublicExposedFunctions out;

	public void load(StaticExposedFunctions staticFuncs)
	{
	}

	public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
	{
		this.out = out;

		register.registerIncomingPacketPlugin(this, SID_READUSERDATA, null);
		register.registerIncomingPacketPlugin(this, SID_ENTERCHAT, null);
		register.registerIncomingPacketPlugin(this, SID_PROFILE, null);

		register.registerCommandPlugin(this, "profile", 2, false,
			"L", "<user> [keys]",
			"This will get and display the requested profile keys " +
			"(comma-separated list).  If no keys are specified, it " +
			"uses the local setting 'default keys'",
			null);
		register.registerCommandPlugin(this, "setprofile", 2, false,
			"N", "<key> <value>",
			"This will set the requested key to the requested value. " +
			"Keys are typically profile\\sex, profile\\location, and " +
			"profile\\description.  If you don't understand this, you " + "" +
			"can use the SwingGui to set profile data.  Be careful -- " +
			"the wrong key can get you ipbanned.",
			null);

		register.registerCommandPlugin(this, "w3profile", 1, false,
			"L", "<user>",
			"This will request the profile information for a War3 account",
			null);

		register.registerEventPlugin(this, null);

		out.addAlias("setprofile profile\\sex", "setsex");
		out.addAlias("setprofile profile\\age", "setage");
		out.addAlias("setprofile profile\\location", "setlocation");
		out.addAlias("setprofile profile\\description", "setdescription");
	}

	public void deactivate(PluginCallbackRegister register)
	{
	}

	public String getName()
	{
		return "Profile";
	}

	public String getVersion()
	{
		return "2.1.3";
	}

	public String getAuthorName()
	{
		return "iago";
	}

	public String getAuthorWebsite()
	{
		return "www.javaop.com";
	}

	public String getAuthorEmail()
	{
		return "iago@valhallalegends.com";
	}

	public String getShortDescription()
	{
		return "Profile plugin";
	}

	public String getLongDescription()
	{
		return "This plugin allows the user to change/view their profile on Battle.net.  SwingGUI can show the profile graphically, but this is textbased.";
	}

	public Properties getDefaultSettingValues()
	{
		Properties p = new Properties();
		p.setProperty("Default keys", "profile\\sex, profile\\age, " +
			"profile\\location, profile\\description");
		p.setProperty("Own keys", "profile\\sex, profile\\age, " +
			"profile\\location, profile\\description, system\\account " +
			"created, system\\last logon, system\\last logoff, " +
			"system\\time logged");
		p.setProperty("Get own profile", "true");
		p.setProperty("Get all profiles", "false");

		return p;
	}

	public Properties getSettingsDescription()
	{
		Properties p = new Properties();
		p.setProperty("Default keys", "The keys that are requested " +
			"if no profile keys are specified");
		p.setProperty("Own keys", "The keys that are requested for " +
			"yourself at login, if that's enabled");
		p.setProperty("Get own profile", "If enabled, this retrieves and " +
			"displays your own profile when you log in");
		p.setProperty("Get all profiles", "If enabled, this retrieves " +
			"the profile for every user who enters the channel.  " +
			"Not really recommended, because floodbots might eat you, but eh?");

		return p;
	}

	public JComponent getComponent(String settingName, String value)
	{
		if (settingName.equalsIgnoreCase("Get own profile"))
			return new JCheckBox("", value.equalsIgnoreCase("true"));
		if (settingName.equalsIgnoreCase("Get all profiles"))
			return new JCheckBox("", value.equalsIgnoreCase("true"));

		return null;
	}

	public Properties getGlobalDefaultSettingValues()
	{
		Properties p = new Properties();
		return p;
	}

	public Properties getGlobalSettingsDescription()
	{
		Properties p = new Properties();
		return p;
	}

	public JComponent getGlobalComponent(String settingName, String value)
	{
		return null;
	}

	public BnetPacket processingPacket(BnetPacket buf, Object data) throws PluginException
	{
		return buf;
	}

	public void processedPacket(BnetPacket buf, Object data) throws IOException, PluginException
	{
		if (buf.getCode() == SID_READUSERDATA)
		{
			// (DWORD) Number of accounts
			buf.removeDWord();
			// (DWORD) Number of keys
			buf.removeDWord();
			// (DWORD) Request ID
			int thisCookie = buf.removeDWord();

			ProfileRequest thisRequest = (ProfileRequest) requests.remove(thisCookie + "");

			// Check if the request belongs to us
			if (thisRequest == null)
				return;

			// If it does, display its info
			String[] keys = thisRequest.getKeys();
			String requester = thisRequest.getRequester();
			String user = thisRequest.getUser();
			int loudness = thisRequest.getLoudness();

			// (STRING[]) Requested Key Values
			for (int i = 0; i < keys.length; i++)
			{
				String thisStr = buf.removeNTString();

				if (keys[i].equalsIgnoreCase("system\\account created")
						|| keys[i].equalsIgnoreCase("system\\last logon")
						|| keys[i].equalsIgnoreCase("system\\last logoff"))
					thisStr = new Date(FileTime.fileTimeToMillis(thisStr.replaceAll(" .*", ""),
																 thisStr.replaceAll(".* ", ""))).toString();
				else if (keys[i].equalsIgnoreCase("system\\time logged"))
					thisStr = TimeReader.timeToString(Long.parseLong(thisStr) * 1000);

				out.sendTextUser(requester, "\\" + user + "\\" + keys[i] + " = " + thisStr,
								 loudness);

			}
		}
		else if (buf.getCode() == SID_PROFILE)
		{
			int thisCookie = buf.removeDWord();
			byte success = buf.removeByte();

			if (success != 0)
				return;

			ProfileRequest thisRequest = (ProfileRequest) requests.remove(thisCookie + "");
			if (thisRequest == null)
				return;

			String requester = thisRequest.getRequester();
			String user = thisRequest.getUser();
			int loudness = thisRequest.getLoudness();

			String description = buf.removeNTString();
			String location = buf.removeNTString();
			String clan = getClanTag(buf.removeString(4));

			out.sendTextUser(requester, "\\" + user + "\\location = " + location, loudness);
			out.sendTextUser(requester, "\\" + user + "\\description = " + description, loudness);
			out.sendTextUser(requester, "\\" + user + "\\clan name = " + clan, loudness);
		}
		else if (buf.getCode() == SID_ENTERCHAT)
		{
			if (out.getLocalSettingDefault(getName(), "Get own profile", "true").equalsIgnoreCase(
																								  "true"))
				out.sendPacket(getRequest(
										  null,
										  (String) out.getLocalVariable("username"),
										  out.getLocalSettingDefault(
																	 getName(),
																	 "Own keys",
																	 "profile\\sex, profile\\age, profile\\location, profile\\description, system\\account created, system\\account expires, system\\last logon, system\\last logoff, system\\time logged").split(
																																																																  "\\s*,\\s*"),
										  SILENT));
		}
	}

	private BnetPacket getRequest(String requester, String user, String[] keys, int loudness)
	{
		ProfileRequest thisRequest = new ProfileRequest(requester, user, keys, loudness);
		requests.put(cookie + "", thisRequest);

		BnetPacket request = new BnetPacket(SID_READUSERDATA);
		// (DWORD) Number of Accounts
		request.addDWord(1);
		// (DWORD) Number of Keys
		request.addDWord(keys.length);
		// (DWORD) Request ID
		request.addDWord(cookie);
		// (STRING[]) Requested Accounts
		request.addNTString(user.replaceAll("\\#.*", ""));
		// (STRING[]) Requested Keys
		for (int i = 0; i < keys.length; i++)
			request.addNTString(keys[i]);

		cookie++;

		return request;
	}

	public void commandExecuted(String user, String command, String[] args, int loudness,
			Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
	{
		if (command.equalsIgnoreCase("profile"))
		{
			if (args.length == 0)
				throw new CommandUsedImproperlyException("Profile requires at least 1 key", user, command);

			String[] keys;

			if (args.length == 2)
				keys = args[1].split("\\s*,\\s*");
			else
				keys = out.getLocalSettingDefault(getName(), "Default keys",
												  "profile\\sex, profile\\age, profile\\location, profile\\description").split(
																															   "\\s*,\\s*");

			out.sendPacket(getRequest(user, args[0], keys, loudness));
		}
		else if (command.equalsIgnoreCase("setprofile"))
		{
			if (args.length == 0)
				throw new CommandUsedImproperlyException("setprofile requires a key", user, command);
			BnetPacket set = new BnetPacket(SID_WRITEUSERDATA);
			// (DWORD) Number of accounts
			set.addDWord(1);
			// (DWORD) Number of keys
			set.addDWord(1);
			// (STRING[]) Accounts to update
			set.addNTString((String) out.getLocalVariable("username"));
			// (STRING[]) Keys to update
			set.addNTString(args[0]);
			// (STRING[]) New values
			set.addNTString(args.length == 2 ? args[1] : "");

			out.sendPacket(set);
			out.sendTextUser(user, out.getLocalVariable("username") + "\\" + args[0] + " => "
					+ (args.length == 2 ? args[1] : ""), loudness);
		}
		else if (command.equalsIgnoreCase("w3profile"))
		{
			if (args.length == 0)
				throw new CommandUsedImproperlyException("w3profile requires a username", user, command);

			requests.put(cookie + "", new ProfileRequest(user, args[0], null, loudness));
			BnetPacket profile = new BnetPacket(SID_PROFILE);
			profile.add(cookie);
			profile.addNTString(args[0]);

			out.sendPacket(profile);

			cookie++;

		}
		else
		{
			out.sendTextUser(user,
							 "Error in Profile plugin: unknown command.  Please report to iago.",
							 QUIET);
		}
	}

	public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
		if (out.getLocalSettingDefault(getName(), "Get all profiles", "false").equalsIgnoreCase(
																								"true"))
			out.sendPacket(getRequest(
									  null,
									  user,
									  out.getLocalSettingDefault(getName(), "Default keys",
																 "profile\\sex, profile\\age, profile\\location, profile\\description").split(
																																			  "\\s*,\\s*"),
									  SILENT));
	}

	private String getClanTag(String clan)
	{
		StringBuffer ret = new StringBuffer();

		for (int i = clan.length() - 1; i >= 0; i--)
			if (clan.charAt(i) > 0)
				ret.append(clan.charAt(i));

		return ret.toString();
	}

	public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}

	public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException
	{
	}
}

class ProfileRequest
{
	final private String   requester;
	final private String   user;
	final private String[] keys;
	final private int      loudness;

	public ProfileRequest(String requester, String user, String[] keys, int loudness)
	{
		this.requester = requester;
		this.user = user;
		this.keys = keys;
		this.loudness = loudness;
	}

	public String getRequester()
	{
		return requester;
	}

	public String getUser()
	{
		return user;
	}

	public String[] getKeys()
	{
		return keys;
	}

	public int getLoudness()
	{
		return loudness;
	}
}
