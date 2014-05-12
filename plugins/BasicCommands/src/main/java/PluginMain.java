package com.javaop.BasicCommands;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import com.javaop.constants.LoudnessConstants;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.exceptions.PluginException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.EventCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.util.BnetPacket;
import com.javaop.util.User;

import com.javaop.util.TimeReader;


/*
 * Created on Jan 27, 2005 By iago
 */

/**
 * @author iago
 *
 */
public class PluginMain extends GenericPluginInterface implements CommandCallback, EventCallback
{
	private PublicExposedFunctions out;
	private static long loaded;
	private String lastWhisper = "<n/a>";

	@Override public void load(StaticExposedFunctions staticFuncs) {
		loaded = System.currentTimeMillis();
	}

	@Override public void activate(PublicExposedFunctions out, PluginCallbackRegister register) {
		this.out = out;

		register.registerCommandPlugin(this, "join", 1, false, "J", "<channel>",
				"Joins the specified channel", null);
		register.registerCommandPlugin(this, "rejoin", 0, false, "J", "",
				"Rejoins the current channel", null);
		register.registerCommandPlugin(this, "uptime", 0, false, "ALN", "",
				"Gives the time the bot's been online for", null);
		register.registerCommandPlugin(this, "ping", 1, false, "AN", "<user>",
				"Pings the specified user", null);
		register.registerCommandPlugin(this, "where", 0, false, "ALN", "",
				"Tells the user where the bot is", null);
		register.registerCommandPlugin(this, "say", 1, false, "T", "<text>",
				"Says the specified text out loud", null);
		register.registerCommandPlugin(this, "lastwhisper", 0, false, "N", "",
				"Shows the last user to give a command", null);

		register.registerEventPlugin(this, null);
	}

	@Override public void deactivate(PluginCallbackRegister register) {
	}

	@Override public String getName() {
		return "BasicCommands";
	}

	@Override public String getVersion() {
		return "2.1.3";
	}

	@Override public String getAuthorName() {
		return "iago";
	}

	@Override public String getAuthorWebsite() {
		return "www.javaop.com";
	}

	@Override public String getAuthorEmail() {
		return "iago@valhallalegends.com";
	}

	public String getShortDescription() {
		return "Basic commands";
	}

	@Override public String getLongDescription() {
		return "A collection of basic commands that most bots have.";
	}

	@Override public Properties getDefaultSettingValues() {
		Properties p = new Properties();
		p.setProperty("Instant joins", "true");
		return p;
	}

	@Override public Properties getSettingsDescription() {
		Properties p = new Properties();
		p.setProperty("Instant joins", "If this is set, joins will happen instantly without waiting for other events to occur.");
		return p;
	}

	@Override public JComponent getComponent(String settingName, String value) {
		return new JCheckBox("", value.equalsIgnoreCase("true"));
	}

	@Override public Properties getGlobalDefaultSettingValues() {
		return new Properties();
	}

	@Override public Properties getGlobalSettingsDescription() {
		return new Properties();
	}

	@Override public JComponent getGlobalComponent(String settingName, String value) {
		return null;
	}

	public boolean isRequiredPlugin() {
		return true;
	}

	@Override public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws
			PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
	{
		String commandLowerCase = command.toLowerCase();

		switch (command.toLowerCase()) {
			case "join":
				if (args.length == 0) {
					throw new CommandUsedImproperlyException("join requires a parameter", user, command);
				}

				if (out.getLocalSettingDefault(getName(), "Instant joins", "true").equalsIgnoreCase("true")) {
					BnetPacket join = new BnetPacket(SID_JOINCHANNEL);
					join.addDWord(0x02);
					join.addNTString(args[0]);
					out.sendPacket(join);
				} else {
					out.sendText("/join " + args[0]);
				}
				break;

			case "rejoin":
				if (out.getLocalSettingDefault(getName(), "Instant joins", "true").equalsIgnoreCase("true")) {
					BnetPacket leave = new BnetPacket(SID_LEAVECHAT);
					out.sendPacket(leave);
					BnetPacket join = new BnetPacket(SID_JOINCHANNEL);
					join.addDWord(0x02);
					join.addNTString(out.channelGetName());
					out.sendPacket(join);
				} else {
					String channel = out.channelGetName();
					out.sendText("/join some random JavaOp-channel!");
					out.sendText("/join " + channel);
				}
				break;

			case "uptime":
				out.sendTextUserPriority(user, "This bot has been up for "
								+ TimeReader.timeToString(System.currentTimeMillis() - loaded), loudness,
						PRIORITY_LOW);
				break;

			case "ping":
				if (args.length == 0) {
					args = new String[]{user};
				}

				Optional<User> u = out.channelGetUser(args[0]);
				if (u.isPresent()) {
					out.sendTextUserPriority(user, args[0] + "'s ping is " + u.get().getPing() + "ms", loudness, PRIORITY_LOW);
				} else {
					if (user != null) {
						out.sendTextPriority("/w " + args[0] + " [ping]" + user, PRIORITY_LOW);
					} else {
						out.sendText("/w " + args[0] + " [ping]" + "<local>");
					}
				}
				break;

			case "where":
				out.sendTextUser(user, "I am in channel: " + out.channelGetName(), PRIORITY_LOW);
				break;

			case "say":
				if (args.length == 0 || args[0].length() < 1) {
					throw new CommandUsedImproperlyException("Say requires a parameter", user, command);
				}

				if (args[0].charAt(0) == '/' && out.dbHasAny(user, "N", true) == false) {
					args[0] = " " + args[0];
				}

				if (out.dbHasAny(user, "M", true)) {
					out.sendTextPriority(args[0], PRIORITY_VERY_HIGH + 1);
				} else {
					out.sendTextPriority(args[0], PRIORITY_LOW);
				}
				break;

			case "lastwhisper":
				out.sendTextUserPriority(user, "Last whisper was: " + lastWhisper, QUIET, PRIORITY_LOW);
				break;

			default:
				out.sendTextUser(user, "There is an error in BasicCommands -- missing a command -- please inform iago", loudness);
		}
	}

	@Override public void talk(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void emote(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void whisperFrom(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		this.lastWhisper = "<" + user + "> " + statstring;
	}

	@Override public void whisperTo(String user, String statstring, int ping, int flags) throws IOException, PluginException {
		if (statstring.matches("\\[ping\\].*")) {
			String username = statstring.replaceAll("\\[ping\\]", "");

			if (user.equalsIgnoreCase("<local>")) {
				out.sendTextUserPriority(username, user + "'s ping is " + ping + "ms", LoudnessConstants.SILENT, PRIORITY_LOW);
			} else {
				out.sendTextUserPriority(username, user + "'s ping is " + ping + "ms", LoudnessConstants.QUIET, PRIORITY_LOW);
			}
		}
	}

	@Override public void userShow(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void userJoin(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void userLeave(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void userFlags(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void error(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void info(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void broadcast(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

	@Override public void channel(String user, String statstring, int ping, int flags) throws IOException, PluginException {
	}

}
