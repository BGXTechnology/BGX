package net.bgx.bgxnetwork.bgxop.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ramek
 * Date: 30.06.2006
 * Time: 14:10:34
 * To change this template use File | Settings | File Templates.
 */
public class FormatScrollPane extends JScrollPane {
  public FormatScrollPane(FormatTable view, int vsbPolicy, int hsbPolicy) {
    super(view, vsbPolicy, hsbPolicy);
  }

  public FormatScrollPane(FormatTable view) {
    super(view);
  }

  public FormatScrollPane(int vsbPolicy, int hsbPolicy) {
    super(vsbPolicy, hsbPolicy);
  }

  public FormatScrollPane() {
  }

  public void fitChildTable() {
    fitChildTable(getViewport().getSize().width);
  }

  public void fitChildTable(int width) {
    if (getViewport().getView() instanceof FormatTable)
      ((FormatTable)getViewport().getView()).fitToWidth(width);
  }

  public void setBounds(int x, int y, int width, int height) {
    super.setBounds(x, y, width, height);    //To change body of overridden methods use File | Settings | File Templates.
    fitChildTable(width);
  }

  public void setBounds(Rectangle r) {
    super.setBounds(r);    //To change body of overridden methods use File | Settings | File Templates.
    fitChildTable(r.width);
  }

}
