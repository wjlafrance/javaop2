/*
 * Created on Aug 20, 2005 By iago
 */

package plugin_interfaces;

import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.KeyStroke;


public interface GuiCallback extends AbstractCallback
{
    public final int ANY_INDEX = -1;

    /**
     * Called when a plugin requests to add a menu item to the main menu for the
     * current bot. name - the name of the menu item. whichMenu - the name of
     * the menu to add it under (it will be created it if doesn't exist). index
     * - the index to add the item at. -1 = any index. mnemonic - the mnemonic
     * (the letter that is underlined for shortcuts) hotkey - the hotkey. Use
     * null for none. icon - the icon. Use null for none. callback - the
     * callback for when it is clicked. Use null for none.
     */
    public void menuItemAdded(String name, String whichMenu, int index, char mnemonic,
            KeyStroke hotkey, Icon icon, ActionListener callback, Object data);

    /**
     * Called when a plugin requests to remove a menu item from the main menu
     * for the current bot.
     */
    public void menuItemRemoved(String name, String whichMenu, Object data);

    /**
     * Called when a plugin requests to add a separator. These will always be
     * placed at the bottom, and can't be remove. If you don't like that, too
     * bad.
     */
    public void menuSeparatorAdded(String whichMenu, Object data);

    /**
     * Called when a plugin requests to add a new menu (at the top). This
     * function isn't necessary, since menus will automatically be created, but
     * this allows for more fine-grained control such as location, icon, and
     * mnemonic. name - the displayed name of the menu index - the index to add
     * the menu at (-1 = any index) mnemonic - the mnemonic (the letter that is
     * underlined for shortcuts) icon - the icon. Use null for none. callback -
     * if you want a callback when it's clicked. Use null for none.
     */
    public void menuAdded(String name, int index, char mnemonic, Icon icon,
            ActionListener callback, Object data);

    /**
     * Remove a menu (and, obviously, all the items under it)
     */
    public void menuRemoved(String name, Object data);

    /**
     * Called when a plugin requests to add a menu item for users (ie,
     * right-click on a user, and there's the menu)
     */
    public void userMenuAdded(String name, int index, Icon icon, ActionListener callback,
            Object data);

    /** Called when a plugin requests to remove a men item for users */
    public void userMenuRemoved(String name, Object data);

    /** Called when a plugin requests to add a user menu separator */
    public void userMenuSeparatorAdded(Object data);

    // public void menuItemAdded(String name, String whichMenu, int index, char
    // mnemonic, KeyStroke hotkey, Icon icon, ActionListener callback, Object
    // data);
    // public void menuItemRemoved(String name, String whichMenu, Object data);
    // public void menuSeparatorAdded(String whichMenu, Object data);
    // public void menuAdded(String name, int index, char mnemonic, Icon icon,
    // ActionListener callback, Object data);
    // public void menuRemoved(String name, Object data);
    // public void userMenuAdded(String name, int index, Icon icon,
    // ActionListener callback, Object data);
    // public void userMenuRemoved(String name, Object data);
    // public void userMenuSeparatorAdded(Object data);
}
