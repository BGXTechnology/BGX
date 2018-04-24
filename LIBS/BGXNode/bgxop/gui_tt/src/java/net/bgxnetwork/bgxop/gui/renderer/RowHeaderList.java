package net.bgx.bgxnetwork.bgxop.gui.renderer;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: A.Borisenko
 * Date: 26.12.2006
 * Time: 17:40:13
 * To change this template use File | Settings | File Templates.
 */
public class RowHeaderList extends AbstractListModel{
    private int _cnt = 0;
    public RowHeaderList(int rowCnt) {
        _cnt = rowCnt;
    }

    public int getSize() {
        return _cnt;
    }

    public Object getElementAt(int index) {
        return ""+(index+1);  
    }
}
