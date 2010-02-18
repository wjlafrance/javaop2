package com.javaop.BNetLogin.packets;

import com.javaop.util.BnetPacket;
import com.javaop.constants.PacketConstants;
import com.javaop.exceptions.*;
import com.javaop.callback_interfaces.PublicExposedFunctions;
import com.javaop.constants.ErrorLevelConstants;

import com.javaop.BNetLogin.password.SRP;

/*
 * Created on Feb 17, 2010 by wjlafrance
 */

/**
 * Handles all the srp-related login
 */
public class SrpLogin {
    
    private SRP srp;
    private SRP accountChangeSrp;
    
    private byte[] salt;
    private byte[] B;
    
    private String prefSection = "Battle.net Login Plugin";
    
    
    // -------------------------------------------------------------------------
    // 0x52 SID_AUTH_CREATEACCOUNT
    // -------------------------------------------------------------------------
    
    public void checkSidAuthCreateAccount(PublicExposedFunctions out,
            BnetPacket buf) throws PluginException, LoginException
    {
        int status = buf.removeDWord();
        
        switch(status) {
            case 0x00:
            case 0x06:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name is in use");
            case 0x07:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name is not long enough");
            case 0x08:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name contains bad characters");
            case 0x09:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name contains bad words");
            case 0x0A:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name needs more alphanumeric characters");
            case 0x0B:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name cannot have adjacent puncuation");
            case 0x0C:
                throw new LoginException("[BNET] NLS Create Account failed: "
                        + "Name has too much puncuation");
            default:
                throw new LoginException("[BNET] NLS Create Account failed "
                        + "with an unknown error: " + status);
        }
    }
    
    // -------------------------------------------------------------------------
    // 0x53 SID_AUTH_ACCOUNTLOGON
    // -------------------------------------------------------------------------
    
    public BnetPacket getSidAuthAccountLogon(PublicExposedFunctions pubFuncs)
            throws LoginException
    {
        String username = pubFuncs.getLocalSetting("Battle.net Login Plugin",
                "username");
        String password = pubFuncs.getLocalSetting("Battle.net Login Plugin",
                "password").toLowerCase();
        int clientToken = (Integer)pubFuncs.getLocalVariable("clientToken");
        int serverToken = (Integer)pubFuncs.getLocalVariable("serverToken");
        
        if(username == null || username.isEmpty())
            throw new LoginException("[BNET] Cannot login because username is null.");
        if(password == null || password.isEmpty())
            throw new LoginException("[BNET] Cannot login because password is null.");
        if(clientToken == 0)
            throw new LoginException("[BNET] Cannot login because client token isn't set. Wtf?");
        if(serverToken == 0)
            throw new LoginException("[BNET] Cannot login because server token isn't set. Wtf?");
        
        // Construct and store SRP
        if (srp == null)
            srp = new SRP(username, password);
        
        // Construct packet
        BnetPacket accountLogon = new BnetPacket(PacketConstants.SID_AUTH_ACCOUNTLOGON);
        accountLogon.add(srp.get_A());
        accountLogon.addNTString(username);
        
        return accountLogon;
    }
    
    // -------------------------------------------------------------------------
    // 0x54 SID_AUTH_ACCOUNTLOGONPROOF
    // -------------------------------------------------------------------------
    
    public BnetPacket getSidAuthAccountLogonProof(PublicExposedFunctions out,
            BnetPacket authCheckPacket) throws PluginException, LoginException
    {
        int status = authCheckPacket.removeDWord();
        
        if(srp == null)
            throw new RuntimeException(
                    "[BNET] SID_AUTH_ACCOUNTLOGIN was received without SRP "
                    + "being set up -- this shouldn't happen.");
        
        switch(status) {
            case 0:
                salt = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);
                B = authCheckPacket.removeBytes(SRP.BIGINT_SIZE);
                
                BnetPacket accountLogonProof = new BnetPacket(PacketConstants
                        .SID_AUTH_ACCOUNTLOGONPROOF);
                accountLogonProof.add(srp.getM1(salt, B));
                
                return (accountLogonProof);
                
            case 1:
                out.systemMessage(ErrorLevelConstants.INFO, "Account does not "
                        + "exist. Attempting to create.");
                
                BnetPacket accountCreate = new BnetPacket(PacketConstants
                        .SID_AUTH_ACCOUNTCREATE);
                
                // The salt is just a random value
                salt = srp.get_A();
                
                // 0x52 - SID_AUTH_ACCOUNTCREATE -
                // (DWORD[8]) s
                // (DWORD[8]) v
                // (STRING) Username
                accountCreate.add(salt);
                accountCreate.add(srp.get_v(salt).toByteArray());
                accountCreate.addNTString(out.getLocalSetting(prefSection,
                        "username"));
                
                return accountCreate;
                
            case 5:
                throw new LoginException("[BNET] Account needs to be upgraded. "
                        + "Please log in with the client.");
                
            default:
                throw new LoginException("[BNET] Login failed with unknown "
                        + "error code: " + status);
        }
    }
    
    public void checkSidAuthAccountLogonProof(PublicExposedFunctions out,
            BnetPacket buf) throws PluginException, LoginException
    {
        int status = buf.removeDWord();
        
        switch(status) {
            case 0:
            case 0xe:
            
                byte[] serverProof = buf.removeBytes(SRP.SHA_DIGESTSIZE);
                byte[] M2 = srp.getM2(salt, B);
                
                for(int i = 0; i < serverProof.length; i++) {
                    if(serverProof[i] != M2[i])
                        throw new LoginException("[BNET] Server failed to provide "
                                + "proof that it knows your password!");
                }
                
                break;
                
            case 2:
                throw new LoginException("[BNET] Login failed: incorrect "
                        + "password.");
            default:
                throw new LoginException("[BNET] Login failed with unknown "
                        + "error: " + status);
        }
    }


    // -------------------------------------------------------------------------
    // 0x55 SID_AUTH_ACCOUNTCHANGE
    // -------------------------------------------------------------------------

    public BnetPacket getSidAuthAccountChange(PublicExposedFunctions out) throws
            LoginException
    {
        
        String username = out.getLocalSetting("Battle.net Login Plugin",
                "username");
        String password = out.getLocalSetting("Battle.net Login Plugin",
                "password").toLowerCase();
        if(username == null || username.isEmpty())
            throw new LoginException("[BNET] Cannot login because username is null.");
        if(password == null || password.isEmpty())
            throw new LoginException("[BNET] Cannot login because password is null.");
        
        BnetPacket changePassword = new BnetPacket(PacketConstants
                .SID_AUTH_ACCOUNTCHANGE);
        srp = new SRP(username, password);
        out.putLocalVariable("srp", srp);
        
        changePassword.add(srp.get_A());
        changePassword.addNTString(out.getLocalSetting(prefSection, "username"));

        return changePassword;
    }
    
    
    // -------------------------------------------------------------------------
    // 0x56 SID_AUTH_ACCOUNTCHANGEPROOF
    // -------------------------------------------------------------------------
    
    public BnetPacket getSidAuthAccountChangeProof(PublicExposedFunctions out,
            BnetPacket buf) throws LoginException
    {
        int status = buf.removeDWord();
        
        if(status == 1)
            throw new LoginException("[BNET] Account doesn't exist -- create "
                    + "before trying to change password");
        else if(status == 5)
            throw new LoginException("[BNET] Account needs to be upgraded");
        else if(status != 0)
            throw new LoginException("[BNET] Unknown NLS Change Password error "
                    + "code: " + status);
        
        accountChangeSrp = new SRP(out.getLocalSetting(prefSection, "username"),
                out.getLocalSetting(prefSection, "password change"));

        BnetPacket proof = new BnetPacket(PacketConstants
                .SID_AUTH_ACCOUNTCHANGEPROOF);

        salt = buf.removeBytes(SRP.BIGINT_SIZE);
        B = buf.removeBytes(SRP.BIGINT_SIZE);

        proof.add(srp.getM1(salt, B));
        proof.add(salt);
        proof.add(accountChangeSrp.get_v(salt).toByteArray());

        return proof;
    }
    
    public void checkSidAuthAccountChangseProof(PublicExposedFunctions pubFuncs,
            BnetPacket buf) throws LoginException, PluginException
    {
        int status = buf.removeDWord();
        
        if(status == 2)
            throw new LoginException("[BNET] Account change failed: invalid "
                    + "old password");
        else if(status != 0)
            throw new LoginException("[BNET] Account change failed: unknown "
                    + "error: " + status);
        
        byte[] recvM2 = buf.removeBytes(SRP.SHA_DIGESTSIZE);
        byte[] realM2 = srp.getM2(salt, B);
        
        for(int i = 0; i < realM2.length; i++)
            if(recvM2[i] != realM2[i])
                throw new PluginException("[BNET] Server failed to provide "
                        + "proof that it knows your password!");
        
        pubFuncs.systemMessage(ErrorLevelConstants.DEBUG, "[BNET] Password "
                + "successfully changed!");
        
        // Switch the new password to the current password
        pubFuncs.putLocalSetting(prefSection, "password",
                pubFuncs.getLocalSetting(prefSection, "password change"));
        pubFuncs.putLocalSetting(prefSection, "password change", "");
        
        srp = null;
        accountChangeSrp = null;
    }


}
