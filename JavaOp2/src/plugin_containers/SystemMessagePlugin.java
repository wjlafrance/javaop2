/*
 * Created on Dec 2, 2004
 * By iago
 */
package plugin_containers;

import plugin_interfaces.SystemMessageCallback;

/**
 * @author iago
 *
 */
public class SystemMessagePlugin extends AbstractPlugin
{
    private int minLevel;
    private int maxLevel;
    public SystemMessagePlugin(SystemMessageCallback callback, int minLevel, int maxLevel, Object data)
    {
        super(callback, data);
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }
    
    public int getMinLevel()
    {
        return minLevel;
    }
    
    public int getMaxLevel()
    {
        return maxLevel;
    }
}