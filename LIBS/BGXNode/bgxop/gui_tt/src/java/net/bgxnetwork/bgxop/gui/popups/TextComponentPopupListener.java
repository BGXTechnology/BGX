package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.Component;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

public class TextComponentPopupListener extends PopupMenuListener{
    public TextComponentPopupListener(JPopupMenu popup){
        super(popup);
    }
    public void mousePressed(MouseEvent e){
        eventHandler(e);
    }
    public void mouseReleased(MouseEvent e){
        eventHandler(e);
    }
    private void eventHandler(MouseEvent e){
        ((Component) e.getSource()).requestFocusInWindow();
        maybeShowPopup(e);
    }
}
