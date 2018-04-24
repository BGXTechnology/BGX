package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import javax.swing.*;
import javax.imageio.ImageIO;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class ScreenshotWriter
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ScreenshotWriter {
  private String currentDir = "";
  private JFrame parent;
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");

  public ScreenshotWriter(JFrame parent) {
    this.parent = parent;
  }

  public ScreenshotWriter(JFrame parent, String currentDir) {
    this(parent);
    this.currentDir = currentDir;
  }

  public boolean saveScreenshot(BufferedImage image, String queryName) throws IOException {
    Color c = UIManager.getColor("Panel.background");
    UIManager.put("Panel.background", UIManager.getColor("default.gradient"));
    JFileChooser chooser = new JFileChooser();
    if (currentDir != null)
      chooser.setCurrentDirectory(new File(currentDir));
    if (queryName != null && ! queryName.equals("")) {
        File pictureFile = new File(queryName+rb.getString("ScreenshotWriter.filename.graph")
                +"."+rb.getString("ScreenshotWriter.filename.default.extension"));
        chooser.setSelectedFile(pictureFile);
    }
    GlobalPopupUtil.initListeners(chooser);
    ExtensibleFileFilter ffilter = new ExtensibleFileFilter(rb.getString("ScreenshotWriter.filterName")+" (*.jpg,*.png,*.bmp)");
    ffilter.addExtension("jpg");
    ffilter.addExtension("png");
    ffilter.addExtension("bmp");
    chooser.setFileFilter(ffilter);
    int returnVal = chooser.showDialog(parent, rb.getString("ScreenshotWriter.acceptButton"));
    UIManager.put("Panel.background", c);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      currentDir = chooser.getCurrentDirectory().getPath();
      File f = chooser.getSelectedFile();
      f = new File(ffilter.formatFileName(f.getAbsolutePath()));
      int idx = f.getName().lastIndexOf('.');
      String ext = f.getName().substring(idx+1);
      if (ext.equalsIgnoreCase("png")) ext = "png";
      else if (ext.equalsIgnoreCase("bmp")) ext = "bmp";
      else ext = "jpg";
      ImageIO.write(image, ext, f);
      return true;
    }
    return false;
  }

  public String getCurrentDir() {
    return currentDir;
  }

}
