package com.javaop.BNetLogin;


import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.exceptions.*;
import com.javaop.util.BnetPacket;
import com.javaop.util.Buffer;

import com.javaop.constants.ErrorLevelConstants;
import com.javaop.constants.PacketConstants;
import com.javaop.BNetLogin.password.BrokenSHA1;
import com.javaop.BNetLogin.password.DoubleHash;
import com.javaop.BNetLogin.packets.*;

/*
 * Created on Dec 9, 2004 By iago
 */

/**
 * @author iago
 * 
 */
public class Login {
    
    private String prefSection;
    private SrpLogin srpLogin;
    
    public Login(String prefSection, SrpLogin srp)
    {
        this.prefSection = prefSection;
        this.srpLogin = srp;
    }
    
    /**
     * Returns a single byte array, 0x01. This is the initial packet sned on login
     */
    public Buffer getProtocolByte()
    {
        return new Buffer(new byte[] { 0x01 });
    }
    
    
    /****************************
     * Legacy (non-nls) functions
     */

    public BnetPacket checkCreateAccount(PublicExposedFunctions out, BnetPacket createAccount) throws LoginException,
            PluginException
    {
        int status = createAccount.removeDWord();

        switch(status)
        {
        case 0: // Account created
            switch((Integer)out.getLocalVariable("loginType"))
            {
                case 0:
                    return SidLogonResponse2.getOutgoing(out);
                case 1:
                case 2:
                    return srpLogin.getSidAuthAccountLogon(out);
            }
        case 2:
            throw new LoginException("[BNET] Name contained invalid characters");
        case 3:
            throw new LoginException("[BNET] Name contained a banned word");
        case 4:
            throw new LoginException("[BNET] Account already exists");
        case 6:
            throw new LoginException("[BNET] Name did not contain enough alphanumeric characters");
        default:
            throw new LoginException("[BNET] Unknown error creating account: " + status);

        }
    }

    public BnetPacket getChangePassword(PublicExposedFunctions out) throws PluginException
    {
        String username = out.getLocalSetting(prefSection, "username");
        String password = out.getLocalSetting(prefSection, "password").toLowerCase();
        int loginType = (Integer)out.getLocalVariable("loginType");
        int clientToken = (Integer)out.getLocalVariable("clientToken");
        int serverToken = (Integer)out.getLocalVariable("serverToken");
        
        if(loginType == 0) {
            BnetPacket changePassword = new BnetPacket(PacketConstants.SID_CHANGEPASSWORD);

            changePassword.add(clientToken);
            changePassword.add(serverToken);

            int[] oldPassword = DoubleHash.doubleHash(password, clientToken, serverToken);
            for(int i = 0; i < 5; i++)
                changePassword.add(oldPassword[i]);

            int[] newPassword = BrokenSHA1.calcHashBuffer(out.getLocalSetting(prefSection, "password change")
                    .toLowerCase().getBytes());
            for(int i = 0; i < 5; i++)
                changePassword.add(newPassword[i]);

            changePassword.addNTString(username);

            return changePassword;
        } else if(loginType == 1 || loginType == 2) {
            srpLogin.getSidAuthAccountChange(out);
        }

        throw new LoginException("[BNET] Unable to login in with type " + loginType);
    }

    public BnetPacket checkPasswordChange(PublicExposedFunctions out, BnetPacket passwordChangePacket)
            throws LoginException, PluginException
    {
        int status = passwordChangePacket.removeDWord();

        if(status == 0)
            throw new LoginException("[BNET] Password change failed");

        out.systemMessage(ErrorLevelConstants.DEBUG, "[BNET] Password change successful");
        // Switch the new password to the current password
        out.putLocalSetting(prefSection, "password", out.getLocalSetting(prefSection, "password change"));
        out.putLocalSetting(prefSection, "password change", "");

        switch((Integer)out.getLocalVariable("loginType"))
        {
            case 0:
                return SidLogonResponse2.getOutgoing(out);
            case 1:
            case 2:
                return srpLogin.getSidAuthAccountLogon(out);
            default:
                throw new LoginException("[BNET] Unable to login in with type " +
                        (Integer)out.getLocalVariable("loginType"));
        }
    }

    /*******************
     * Generic functions
     */

    public BnetPacket getEnterChat(PublicExposedFunctions out) {
        BnetPacket enterChat = new BnetPacket(PacketConstants.SID_ENTERCHAT);
        enterChat.addNTString(out.getLocalSettingDefault(prefSection, "username", "not.iago.x86"));
        enterChat.addNTString("");

        return enterChat;
    }

    public BnetPacket getJoinHomeChannel(PublicExposedFunctions out) {
        BnetPacket enterChannel = new BnetPacket();
        enterChannel.setCode(PacketConstants.SID_JOINCHANNEL);
        enterChannel.addDWord(0x02);
        enterChannel.addNTString(out.getLocalSettingDefault(prefSection, "home channel", "op x86"));

        return enterChannel;
    }

}
