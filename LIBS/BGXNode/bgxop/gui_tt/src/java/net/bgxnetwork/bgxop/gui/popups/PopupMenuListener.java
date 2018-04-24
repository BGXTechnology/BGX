package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
public class PopupMenuListener extends MouseAdapter {
	private JPopupMenu popup;
	public PopupMenuListener(JPopupMenu popup) {
		this.popup = popup;
	}
	protected void maybeShowPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
