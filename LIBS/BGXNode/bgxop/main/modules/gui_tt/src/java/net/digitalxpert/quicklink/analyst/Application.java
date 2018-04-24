package net.bgx.bgxnetwork.bgxop;

import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.uitools.*;
import net.bgx.bgxnetwork.transfer.types.LoginInfo;

import javax.swing.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.MissingResourceException;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.*;

/**
 * Class Application
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class Application implements ILoginListener {
  private static Logger log = Logger.getLogger(Application.class.getName());

  private static LoginDialog loginDialog = null;

  public static void main(String[] args) {
    //read config
    String filename = "auth.conf";
    ClassLoader classLoader = Application.class.getClassLoader();

    InputStream is = classLoader.getResourceAsStream(filename);
    if (is==null) {
      log.warning("Authenication configuration file '"+filename+"' not found.");
    } else {
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      FileOutputStream fo = null;
      try {
        fo = new FileOutputStream(new File(System.getProperty("user.home") + "/" + filename));
        int count = 0;
        while ((count = br.read()) != -1) {
          fo.write(count);
        }
      } catch (Exception e) {
        log.severe(UITools.getStackTrace(e));
      } finally{
        if (br!=null) try { br.close(); } catch (IOException e) {}
        if (fo!=null) try { fo.close(); } catch (IOException e) {}
      }
    }
    String toAuth = System.getProperty("user.home") + "/" + "auth.conf";
    System.setProperty("java.security.auth.login.config", toAuth);

    String configName = "application.properties";
    if (args.length>0) configName = args[0];
    Properties props = new Properties();
    InputStream config = null;
    config = classLoader.getResourceAsStream(configName);
    if (config==null) {
      log.warning("Application configuration file '"+configName+"' not found.");
    } else {
      try {
        props.load(config);
        for (Object o : props.keySet())
          System.setProperty((String) o, props.getProperty((String)o));
      } catch (Exception e) {
        log.warning(e.toString());
        log.warning("Cannot load application configuration file.");
      } finally {
        if (config!=null) try { config.close(); } catch (IOException e) {}
      }
    }

    StyleInstaller.install();

    Application instance = new Application();
    ImageIcon appIcon = (ImageIcon) ResourceLoader.getInstance().
        getIconByResource(instance.rb, "icon");
    Image image = null;
    if (appIcon!=null) image = appIcon.getImage();
    loginDialog = new LoginDialog(instance, image);
  }

  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  public void doLogin(LoginInfo li) {
    if (li!=null) {
      if (true) {
        try {
          loginDialog.close();
//          MessageDialogs.info(loginDialog,
//              rb.getString("login.login.msg"),
//              rb.getString("login.login.title"),
//              ResourceLoader.getInstance().getIconByResource(rb, "login.login.img"));
          new MainFrame();
          //ActorInfo.getInstance();
        } catch (MissingResourceException e) {
          log.severe(e.toString()+"\nExiting...");
          System.exit(1);
        }
      } else {
        MessageDialogs.generalError(loginDialog, null,
            rb.getString("login.error.insufficientPrivilegesMsg"),
            rb.getString("login.error.title"));
        return;
      }
    }
  }

}
