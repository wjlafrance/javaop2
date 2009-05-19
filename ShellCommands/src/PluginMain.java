import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.JComponent;

import plugin_interfaces.CommandCallback;
import plugin_interfaces.GenericPluginInterface;
import callback_interfaces.PluginCallbackRegister;
import callback_interfaces.PublicExposedFunctions;
import callback_interfaces.StaticExposedFunctions;
import exceptions.CommandUsedIllegally;
import exceptions.CommandUsedImproperly;
import exceptions.PluginException;

public class PluginMain extends GenericPluginInterface implements CommandCallback
{
    private PublicExposedFunctions out;
    private int thread = 0;
    private final Hashtable threads = new Hashtable();
    
    public void load(StaticExposedFunctions staticFuncs)
    {
    }

    public void activate(PublicExposedFunctions out, PluginCallbackRegister register)
    {
        this.out = out;
        
        register.registerCommandPlugin(this, "runquiet", 0, false, "U", "<command> <parameters>", "Runs the specified local command without returning any output.  I recommend using this with aliases", null);
        register.registerCommandPlugin(this, "run", 0, false, "U", "<command> <parameters>", "Runs the specified local command and displays the output.  I don't recommend using this for anything that returns more than a couple lines", null);
        register.registerCommandPlugin(this, "runstop", 0, false, "U", "<command num>", "Stops the specified local command from running", null);
    }

    public void deactivate(PluginCallbackRegister register)
    {
    }

    public String getName()
    {
        return "Shell";
    }

    public String getVersion()
    {
        return "1.0";
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
        return "Allows users to run commands from Battle.net";
    }

    public String getLongDescription()
    {
        return "Provides functions for running commands on the local system from Battle.net.  This can be used for things like changing " + 
            "songs on an mp3 player.  I recommend using aliases to save your brain.";
    }

    public Properties getDefaultSettingValues()
    {
        return new Properties();
    }

    public Properties getSettingsDescription()
    {
        return new Properties();
    }

    public JComponent getComponent(String settingName, String value)
    {
        return null;
    }

    public Properties getGlobalDefaultSettingValues()
    {
        return new Properties();
    }

    public Properties getGlobalSettingsDescription()
    {
        return new Properties();
    }

    public JComponent getGlobalComponent(String settingName, String value)
    {
        return null;
    }

    public void commandExecuted(String user, String command, String[] args, int loudness, Object data) throws PluginException, IOException, CommandUsedIllegally, CommandUsedImproperly
    {
        if(command.equalsIgnoreCase("run") || command.equalsIgnoreCase("runquiet"))
        {
            if(args.length == 0)
                throw new CommandUsedImproperly("run requires the name of the program to run", user, command);
            
            thread++;
            
            Thread t = new Run(thread, user, loudness, args, command.equalsIgnoreCase("run"));
            threads.put(thread + "", t);
            t.start();
        }
        else if(command.equalsIgnoreCase("runstop"))
        {
            if(args.length == 0)
                throw new CommandUsedImproperly("runstop requires the number of the process", user, command);
            
            Run r = (Run) threads.get(args[0]);
            
            if(r == null)
            {
                out.sendTextUser(user, "Couldn't find process #" + args[0], loudness);
            }
            else
            {
                r.kill();
                out.sendTextUser(user, "Process #" + args[0] + " killed.", loudness);
            }
        }
    }

    public class Run extends Thread
    {
        private final int num;
        private final String user;
        private final int loudness;
        private final String []cmd;
        private final boolean sendResults;
        
        private boolean kill = false;
        
        public Run(int num, String user, int loudness, String []cmd, boolean sendResults)
        {
            this.num = num;
            this.user = user;
            this.loudness = loudness;
            this.cmd = cmd;
            this.sendResults = sendResults;
            
            this.setName("Shell-Commands-" + num);
        }
        
        public void kill()
        {
            kill = true;
        }
        
        public void run()
        {
            try
            {
                Runtime r = Runtime.getRuntime();
                Process p = r.exec(cmd);
                
                
                BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while((line = in.readLine()) != null)
                {
                    if(kill)
                    {
                        p.destroy();
                        return;
                    }
                    if(sendResults)
                        out.sendTextUser(user, "#" + num + ": " + line, loudness);
                }
            }
            catch(IOException e)
            {
                try
                {
                    out.sendTextUser(user, "Error executing command: " + e, loudness);
                }
                catch(IOException e2)
                {
                }
            }
        }
    }
}
