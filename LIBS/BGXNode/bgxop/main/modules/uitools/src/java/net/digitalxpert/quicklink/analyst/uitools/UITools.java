package net.bgx.bgxnetwork.bgxop.uitools;

import javax.swing.*;
import java.lang.management.MemoryUsage;
import java.lang.management.ManagementFactory;
import java.awt.*;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Class UITools
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public abstract class UITools {
  private static final Logger log = Logger.getLogger(UITools.class.getName());

  public static String getStackTrace(Throwable e) {
    StackTraceElement[] trace = e.getStackTrace();
    String s = e.toString();
    for (int i = 0; i < trace.length; i++)
      s = s + " at\n   " + trace[i].toString();
    s += getCause(e);
    return s;
  }

  protected static String getCause(Throwable e) {
    if (e.getCause() != null)
      return "\n caused by: " + e.getCause().toString() + getCause(e.getCause());
    else
      return "";
  }

  public static String getMemoryInfo() {
    MemoryUsage usage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
    return usage.getInit() + " : " + usage.getCommitted() + " : " + usage.getMax() + " used: " + usage.getUsed();
  }

  public static void centrateWindow(Window w) {
    Rectangle r = w.getGraphicsConfiguration().getBounds();
    Dimension d = w.getPreferredSize();
    w.setLocation(r.x + r.width / 2 - d.width / 2 - 10, r.y + r.height / 2 - d.height / 2 - 10);
  }

  public static Icon loadIcon(String name, String dir) {
    if (dir == null)
      dir = System.getProperty("resources.dir");
    if (dir == null)
      dir = "resources/";
    try {
      ClassLoader classLoader = UITools.class.getClassLoader();
      URL url = classLoader.getResource(dir + name);
      if (url == null) {
        log.warning("Cannot load icon '" + name + "'.");
        return null;
      }
      return new ImageIcon(url);
    } catch (Exception e) {
      log.warning(e.toString());
      return null;
    }
  }

  public static URL getUrlForResource(String name){
    String dir = System.getProperty("resources.dir");
    if (dir == null) dir = "resources/";
    try {
      ClassLoader classLoader = UITools.class.getClassLoader();
      URL url = classLoader.getResource(dir + name);
      if (url == null) {
        log.warning("Cannot get URL for '" + name + "'.");
        return null;
      }
      return url;
    }
    catch (Exception e) {
      log.warning(e.toString());
      return null;
    }
  }
}
