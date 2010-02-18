/*
 * Created on Dec 2, 2004 By iago
 */
package com.javaop.plugin_containers;

import com.javaop.plugin_interfaces.CommandCallback;


/**
 * @author iago
 * 
 */
public class CommandPlugin extends AbstractPlugin
{
    private String  name;
    private int     args;
    private boolean requiresOps;
    private String  requiredFlags;
    private String  usage;
    private String  help;

    public CommandPlugin(CommandCallback callback, String name, int args, boolean requiresOps,
            String requiredFlags, String usage, String help, Object data)
    {
        super(callback, data);
        this.name = name;
        this.args = args;
        this.requiresOps = requiresOps;
        this.requiredFlags = requiredFlags;
        this.usage = usage;
        this.help = help;
    }

    public String getName()
    {
        return name;
    }

    public int getArgs()
    {
        return args;
    }

    public boolean getRequiresOps()
    {
        return requiresOps;
    }

    public String getRequiredFlags()
    {
        return requiredFlags;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getHelp()
    {
        return help;
    }
}
