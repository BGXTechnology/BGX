package com.bgx.client.net;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.jboss.security.auth.callback.UsernamePasswordHandler;

public class ClientLogin{
    private String userName;
    private String password;
    private static ClientLogin cl;
    private LoginContext lc;
    private ClientLogin(String userName, String password){
        this.password = password;
        this.userName = userName;
    }
    public LoginContext login(){
        try{
            CallbackHandler cal;
            cal = new UsernamePasswordHandler(userName, password.toCharArray());
            lc = new LoginContext("client-login", cal);
            lc.login();
        }
        catch (LoginException e){
            e.printStackTrace();
        }
        return lc;
    }
    public void logout(){
        try{
            lc.logout();
        }catch (LoginException e){
            e.printStackTrace();
        }
    }
    public static ClientLogin getLoginInstance(){
        try{
            if(cl == null){
                throw new Exception("Login before please");
            }
        }catch (Exception ex){
        }
        return cl;
    }
    public static ClientLogin getLoginInstance(String userName, String password){
        if(cl == null){
            cl = new ClientLogin(userName, password);
        }else{
            // Do not warry, but i am have not create setUserName an setPassword
            // methods is static:)
            cl.setUserName(userName);
            cl.setPassword(password);
        }
        return cl;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setUserName(String userName){
        this.userName = userName;
    }
}
