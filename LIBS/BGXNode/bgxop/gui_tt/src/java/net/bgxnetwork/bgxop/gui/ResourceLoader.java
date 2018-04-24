package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.bgxop.uitools.UITools;

import javax.swing.*;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class ResourceLoader
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ResourceLoader {
  private static ResourceLoader instance = null;

  public static ResourceLoader getInstance() {
    if (instance==null) instance = new ResourceLoader();
    return instance;
  }

  private String dir;
  private HashMap<String, Icon> loadedIcons = new HashMap<String, Icon>();

  private ResourceLoader() {
    dir = System.getProperty("resources.dir");
    if (dir==null) dir="resources/";
  }

  public Icon getIcon(String name) {
    if (loadedIcons.containsKey(name))
      return loadedIcons.get(name);
    Icon image = UITools.loadIcon(name, dir);
    if (image!=null)
      loadedIcons.put(name, image);
    return image;
  }

  public Icon getIconByResource(ResourceBundle rb, String resourceName) {
    return getIcon(rb.getString(resourceName));
  }

}
