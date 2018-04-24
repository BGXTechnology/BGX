package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import javax.swing.filechooser.FileFilter;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.io.File;

/**
 * Class ExtensibleFileFilter
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ExtensibleFileFilter extends FileFilter {
  ArrayList exts = new ArrayList();
  String descr = "";

  public ExtensibleFileFilter() {
  }

  public ExtensibleFileFilter(String description) {
    descr = description;
  }

  public boolean accept(File f) {
    if (f.isDirectory()) return true;
    String ext, n = f.getName();
    int idx = n.lastIndexOf('.');
    if (idx>=0 && idx<n.length()-1) {
      ext = n.substring(idx+1);
      for (int i=0; i<exts.size(); i++)
        if (ext.equalsIgnoreCase((String)exts.get(i))) return true;
    }
    return false;
  }

  public String getDescription() {
    return descr;
  }

  public void setDescription(String descr) {
    this.descr = descr;
  }

  public void addExtension(String str) {
    exts.add(str);
  }

  public String formatFileName(String fileName) {
    StringTokenizer st = new StringTokenizer(fileName, System.getProperty("file.separator"));
    String tok = "";
    while (st.hasMoreTokens()) tok = st.nextToken();
    int idx = tok.lastIndexOf('.');
    if (idx<0 && exts.size()>0) return fileName+"."+exts.get(0);
    else return fileName;
  }
}
