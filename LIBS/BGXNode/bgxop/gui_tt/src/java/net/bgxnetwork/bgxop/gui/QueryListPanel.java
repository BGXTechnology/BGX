package net.bgx.bgxnetwork.bgxop.gui;
import java.awt.event.MouseEvent;
import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class QueryListPanel
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class QueryListPanel extends JTabbedPane implements ChangeListener{
	private QueryListController controller;
	public QueryListPanel(QueryListController controller){
		super(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		this.controller = controller;
		addChangeListener(this);
		JPopupMenu menu = new JPopupMenu();
		menu.add(new JMenuItem(controller.closeA));
		menu.add(new JMenuItem(controller.saveLayoutA));
		menu.addSeparator();
		ButtonGroup bg = new ButtonGroup();
		for(QueryListController.DoLayoutAction ac : controller.getLayoutActions()){
			JRadioButtonMenuItem item = new JRadioButtonMenuItem(ac);
			ac.addParent(item);
			bg.add(item);
			menu.add(item);
		}
		addMouseListener(new TabPopupListener(menu));
	}
	public QueryListController getController(){
		return controller;
	}
	public void stateChanged(ChangeEvent e){
		controller.tabSelected();
	}
	class TabPopupListener extends PopupMenuListener{
		public TabPopupListener(JPopupMenu popup){
			super(popup);
		}
		protected void maybeShowPopup(MouseEvent e){
			int index = QueryListPanel.this.getUI().tabForCoordinate(QueryListPanel.this, e.getX(), e.getY());
			if(index < 0)
				return;
			super.maybeShowPopup(e);
		}
	}
}
