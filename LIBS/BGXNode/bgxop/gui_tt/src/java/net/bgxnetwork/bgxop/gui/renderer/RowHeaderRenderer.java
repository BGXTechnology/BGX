package net.bgx.bgxnetwork.bgxop.gui.renderer;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * User: A.Borisenko
 * Date: 26.12.2006
 * Time: 17:35:28
 */
public class RowHeaderRenderer extends JLabel implements ListCellRenderer {
    public RowHeaderRenderer(JTable table) {
        JTableHeader header = table.getTableHeader();
        setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(CENTER);
        setForeground(header.getForeground());
        setBackground(header.getBackground());
        setFont(header.getFont());
    }

  public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    setText((value == null) ? "" : value.toString());
    return this;
  }
}
