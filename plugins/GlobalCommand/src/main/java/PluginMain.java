package com.javaop.GlobalCommand;

import java.util.List;
import java.util.Properties;

import javax.swing.JComponent;
import java.io.IOException;
import java.util.stream.Collectors;

import com.javaop.callback_interfaces.PluginCallbackRegister;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.callback_interfaces.StaticExposedFunctions;
import com.javaop.exceptions.CommandUsedIllegallyException;
import com.javaop.exceptions.CommandUsedImproperlyException;
import com.javaop.plugin_interfaces.CommandCallback;
import com.javaop.plugin_interfaces.GenericPluginInterface;
import com.javaop.exceptions.PluginException;


public class PluginMain extends GenericPluginInterface implements CommandCallback
{

	private PublicExposedFunctions        out;

	private static StaticExposedFunctions sef;

	public void load(StaticExposedFunctions staticFuncs)
	{

		sef = staticFuncs;
	}

	public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
	{
		this.out = out;

		register.registerCommandPlugin(this, "gconnect", 1, false, "U", "[bot name]",
									   "Connects a bot based on its name.", null);
		register.registerCommandPlugin(this, "botlist", 0, false, "U", "",
									   "Returns a list of all running bots.", null);
	}

	public void deactivate(PluginCallbackRegister register)
	{

	}

	public String getName()
	{
		return "GlobalCommand";
	}

	public String getVersion()
	{
		return "2.1.3";
	}

	public String getAuthorName()
	{
		return "Ryan Marcus";
	}

	public String getAuthorWebsite()
	{
		return "N/A";
	}

	public String getAuthorEmail()
	{
		return "ryan@marcusfamily.info";
	}

	public String getLongDescription()
	{
		return "This plugin allows you to disconnect and connect bots other then the one currently connected.";
	}

	public Properties getDefaultSettingValues()
	{
		Properties p = new Properties();
		return p;
	}

	public Properties getSettingsDescription()
	{
		Properties p = new Properties();
		return p;
	}

	public JComponent getComponent(String settingName, String value)
	{
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

	public void commandExecuted(String user, String command, String[] args, int loudness,
			Object data) throws PluginException, IOException, CommandUsedIllegallyException, CommandUsedImproperlyException
	{
		if (command.equalsIgnoreCase("gconnect"))
		{

			sef.botGet(args[0]).connect();
			out.sendText("Connecting bot.");

		}

		if (command.equalsIgnoreCase("botlist"))
		{
			List<String> msg = sef.botGetActiveNames();
			String fmsg = msg.stream().collect(Collectors.joining(","));
			out.sendText(fmsg);
		}

	}

}
