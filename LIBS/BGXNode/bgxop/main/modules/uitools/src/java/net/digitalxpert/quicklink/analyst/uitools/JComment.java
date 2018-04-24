package net.bgx.bgxnetwork.bgxop.uitools;

import javax.swing.*;
import java.awt.*;

/**
 * Class JComment
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class JComment extends JLabel {
  public JComment(String text, Icon icon, int horizontalAlignment) {
    super(text, icon, horizontalAlignment);
    checkFont();
  }

  public JComment(String text, int horizontalAlignment) {
    super(text, horizontalAlignment);
    checkFont();
  }

  public JComment(String text) {
    super(text);
    checkFont();
  }

  public JComment(Icon image, int horizontalAlignment) {
    super(image, horizontalAlignment);
    checkFont();
  }

  public JComment(Icon image) {
    super(image);
    checkFont();
  }

  public JComment() {
    checkFont();
  }

  private void checkFont() {
    Font f = UIManager.getFont("Comment.font");
    if (f!=null) setFont(f);
    Color c = UIManager.getColor("Comment.background");
    if (c!=null) setBackground(c);
    c = UIManager.getColor("Comment.foreground");
    if (c!=null) setForeground(c);
  }
}
