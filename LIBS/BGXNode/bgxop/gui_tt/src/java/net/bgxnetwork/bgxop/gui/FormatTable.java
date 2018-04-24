package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.bgxop.gui.renderer.RowHeaderList;
import net.bgx.bgxnetwork.bgxop.gui.renderer.RowHeaderRenderer;

import javax.swing.*;
import javax.swing.table.TableColumn;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * Class FormatTable
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class FormatTable extends JTable {
  private JList _rowHeader = null;
  protected ResourceBundle rb = ResourceBundle.getBundle("gui");

  public FormatTable() {
    setAutoResizeMode(AUTO_RESIZE_OFF);
    getTableHeader().setReorderingAllowed(false);
    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    setShowGrid(false);
    getTableHeader().addMouseListener(new TableHeaderListener());
    getTableHeader().setDefaultRenderer(new SelectableTableCellRenderer());
  }

  public FormatTable(IFormatTableModel model) {
    this();
    setModel(model);
  }

  public void setModel(TableModel dataModel) {
    super.setModel(dataModel);
    if (getFormatModel() != null) {
      setRowHeight(getFormatModel().getRowHeight());
      for (int i=0; i<getFormatModel().getColumnCount(); i++) {
        if (getFormatModel().getColumnMinWidth(i)!=0) {
          getColumnModel().getColumn(i).setMinWidth(getFormatModel().getColumnMinWidth(i));
          getColumnModel().getColumn(i).setPreferredWidth(getFormatModel().getColumnMinWidth(i));
        }
        if (getFormatModel().getColumnMaxWidth(i)!=0)
          getColumnModel().getColumn(i).setMaxWidth(getFormatModel().getColumnMaxWidth(i));
      }
    }
  }

  public IFormatTableModel getFormatModel() {
    if (super.getModel() instanceof IFormatTableModel)
      return (IFormatTableModel) super.getModel();
    else return null;
  }

  public void fitToWidth(int width) {
    if (getFormatModel()==null) return;
    int restSize = getColumnModel().getTotalColumnWidth();
    int diff = width-restSize;
    if (diff <= 0) return;
    int col = getFormatModel().getDefaultResizableColumn();
    if (col >= getColumnCount()) return;
    if (col >= 0) {
      TableColumn resCol = getColumnModel().getColumn(col);
      resCol.setPreferredWidth(resCol.getPreferredWidth()+diff);
    } else {
      ArrayList<TableColumn> resizables = new ArrayList<TableColumn>();
      for (int i=0; i<getFormatModel().getColumnCount(); i++)
        if (getFormatModel().getColumnMaxWidth(i)==0) resizables.add(getColumnModel().getColumn(i));
      diff = diff / resizables.size();
      for (TableColumn tc : resizables)
        tc.setPreferredWidth(tc.getPreferredWidth()+diff);
    }
  }


  private class TableHeaderListener extends MouseAdapter {
    private Icon asc, desc;

    public TableHeaderListener() {
      asc = ResourceLoader.getInstance().getIconByResource(rb, "FormatTable.sort.asc.img");
      desc = ResourceLoader.getInstance().getIconByResource(rb, "FormatTable.sort.desc.img");
    }

    public void mousePressed(MouseEvent e) {
      int col = FormatTable.this.getTableHeader().columnAtPoint(e.getPoint());
      SelectableTableCellRenderer c =
          (SelectableTableCellRenderer) FormatTable.this.getTableHeader().getDefaultRenderer();
      c.select(col);
      FormatTable.this.getTableHeader().repaint();
    }

    public void mouseReleased(MouseEvent e) {
      SelectableTableCellRenderer c =
          (SelectableTableCellRenderer) FormatTable.this.getTableHeader().getDefaultRenderer();
      c.deselect();
      FormatTable.this.getTableHeader().repaint();
    }

    public void mouseClicked(MouseEvent e) {
      if (e.getClickCount()== 1 && e.getButton()==MouseEvent.BUTTON1) {
        int col = FormatTable.this.getTableHeader().columnAtPoint(e.getPoint());
        if (col<0) return;
        int res = FormatTable.this.getFormatModel().sortByColumn(col);
        SelectableTableCellRenderer c =
            (SelectableTableCellRenderer) FormatTable.this.getTableHeader().getDefaultRenderer();
        if (res<0) c.setIcon(col, desc);
        else if (res>0) c.setIcon(col, asc);
        FormatTable.this.getTableHeader().repaint();
      }
    }
  }

  protected static class SelectableTableCellRenderer extends DefaultTableCellRenderer {
    private Color bgColor = Color.blue;
    private int selected = -1;
    private int iconed = -1;
    private Icon icon;

    public SelectableTableCellRenderer() {
      Color c = UIManager.getColor("TableHeader.selected.background");
      if (c!=null) bgColor = c;
    }

    public void select(int col) {
      selected = col;
    }

    public void deselect() {
      selected = -1;
    }

    public void setIcon(int column, Icon icon) {
      iconed = column;
      this.icon = icon;
    }

    public void removeIcon() {
      iconed = -1;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                       boolean isSelected, boolean hasFocus, int row, int column) {
      if (table != null) {
        JTableHeader header = table.getTableHeader();
        if (header != null) {
          setForeground(header.getForeground());
          setBackground(header.getBackground());
          setFont(header.getFont());
        }
      }

      setText((value == null) ? "" : value.toString());
      setBorder(UIManager.getBorder("TableHeader.cellBorder"));
      setHorizontalAlignment(CENTER);

      if (column==selected) setBackground(bgColor);
      if (column==iconed) setIcon(icon);
      else setIcon(null);

      return this;
    }
  }

  public void setRowHeader(){
        RowHeaderList rhl = new RowHeaderList(dataModel.getRowCount());
        JList rowHeader = new JList(rhl);
        String size = ""+rhl.getSize();
        int width = 10+size.length()*7;
        if (width < 25) width = 25;
        rowHeader.setFixedCellWidth(width);
        rowHeader.setFixedCellHeight(this.getRowHeight());
        rowHeader.setCellRenderer(new RowHeaderRenderer((JTable)this));
        _rowHeader = rowHeader;
  }

  public JList getRowHeader(){
      return _rowHeader;
  }
}
