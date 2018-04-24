package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TablePopupListener extends PopupMenuListener{
    public TablePopupListener(JPopupMenu popup){
        super(popup);
    }
    public void mousePressed(MouseEvent e){
        eventHandler(e);
    }
    public void mouseReleased(MouseEvent e){
        eventHandler(e);
    }
    private void eventHandler(MouseEvent e){
        JTable table = (JTable) e.getSource();
        if(e.getButton() == MouseEvent.BUTTON3 && table.getSelectedColumn() == -1){
            int col = table.columnAtPoint(e.getPoint());
            int row = table.rowAtPoint(e.getPoint());
            table.changeSelection(row, col, true, false);
        }
        maybeShowPopup(e);
    }
}
