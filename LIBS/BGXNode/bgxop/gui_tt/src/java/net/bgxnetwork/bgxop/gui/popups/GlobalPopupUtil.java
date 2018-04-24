package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.Component;
import java.awt.Container;

import javax.swing.JMenuItem;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import net.bgx.bgxnetwork.bgxop.gui.NodeListTable;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.CardDialog;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TimeDiagram;

public class GlobalPopupUtil{
    private static PopupMenuListener textComponentListener;
    private static PopupMenuListener tableListener;
    private GlobalPopupUtil(){
    }
    public static void initListeners(Component c){
        initAllListeners(c);
        changeGlobalMouseListener(c);
    }
    private static void initAllListeners(Component c){
        TextComponentPopupMenu cpMenu = new TextComponentPopupMenu();
        if ( c instanceof CardDialog ) {
            JMenuItem menuItem = new CopyCardDataMenuItem((CardDialog)c);
            cpMenu.addSeparator();
            cpMenu.add(menuItem);
        }
        textComponentListener = new TextComponentPopupListener(cpMenu);
        TablePopupMenu tableMenu = new TablePopupMenu();
        tableListener = new TablePopupListener(tableMenu);
    }
    private static void changeGlobalMouseListener(Component c){
        if(c instanceof TimeDiagram) {
            return;
        }
        if(c instanceof JTextComponent){
            c.addMouseListener(textComponentListener);
        }
        if(c instanceof JTable){
            if(!(c instanceof NodeListTable)){
                c.addMouseListener(tableListener);
            }
        }
        if(c instanceof Container){
            Container cc = (Container) c;
            int l = cc.getComponentCount();
            for(int i = 0; i < l; i++){
                Component child = cc.getComponent(i);
                changeGlobalMouseListener(child);
            }
        }
    }
}
