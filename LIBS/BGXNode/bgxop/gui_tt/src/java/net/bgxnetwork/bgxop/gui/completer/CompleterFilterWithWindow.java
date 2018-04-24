package net.bgx.bgxnetwork.bgxop.gui.completer;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;

public class CompleterFilterWithWindow extends AbstractCompleterFilter{
    protected JWindow window;
    protected JList list;
    protected JScrollPane scrollPane;
    protected CompleterTextField textField;
    protected Object[] objectList;
    protected TextFieldKeyListener textFieldKeyListener;
    protected ListMouseMotionListener mouseMotionListener;
    protected ListMouseListener mouseListener;
    protected FilterWindowListener filterWindowListener;
    protected FilterListModel filter;
    protected boolean isAdjusting = false;
    public static int MAX_VISIBLE_ROWS = 8;
    public CompleterFilterWithWindow(Object[] completerObjs, CompleterTextField textField){
        objectList = completerObjs;
        this.textField = textField;
        init();
    }
    private void init(){
        filterWindowListener = new FilterWindowListener();
        filter = new FilterListModel(objectList);
        // textFieldKeyListener = new TextFieldKeyListener();
        // textField.addKeyListener(textFieldKeyListener);
        EscapeAction escape = new EscapeAction();
        textField.registerKeyboardAction(escape, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    public boolean isFilterWindowVisible(){
        return ((window != null) && (window.isVisible()));
    }
    public void setFilterWindowVisible(boolean visible){
        if(visible){
            initWindow();
            list.setModel(filter);
            window.setVisible(true);
            textField.requestFocus();
            textField.addFocusListener(filterWindowListener);
        }else{
            if(window == null)
                return;
            window.setVisible(false);
            window.removeFocusListener(filterWindowListener);
            Window ancestor = SwingUtilities.getWindowAncestor(textField);
            ancestor.removeMouseListener(filterWindowListener);
            textField.removeFocusListener(filterWindowListener);
            textField.removeAncestorListener(filterWindowListener);
            list.removeMouseListener(mouseListener);
            list.removeMouseMotionListener(mouseMotionListener);
            mouseMotionListener = null;
            mouseListener = null;
            window.dispose();
            window = null;
            list = null;
        }
    }
    private void initWindow(){
        Window ancestor = SwingUtilities.getWindowAncestor(textField);
        window = new JWindow(ancestor);
        window.addWindowFocusListener(filterWindowListener);
        textField.addAncestorListener(filterWindowListener);
        ancestor.addMouseListener(filterWindowListener);
        mouseListener = new ListMouseListener();
        list = new JList(filter);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFocusable(false);
        list.setPrototypeCellValue("Prototype");
        list.addMouseListener(mouseListener);
        mouseMotionListener = new ListMouseMotionListener();
        list.addMouseMotionListener(mouseMotionListener);
        scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setFocusable(false);
        scrollPane.getVerticalScrollBar().setFocusable(false);
        setWindowHeight();
        window.setLocation(textField.getLocationOnScreen().x, textField.getLocationOnScreen().y + textField.getHeight());
        window.getContentPane().add(scrollPane);
    }
    private void setWindowHeight(){
        int height = list.getFixedCellHeight() * Math.min(MAX_VISIBLE_ROWS, filter.getSize());
        height += list.getInsets().top + list.getInsets().bottom;
        height += scrollPane.getInsets().top + scrollPane.getInsets().bottom;
        window.setSize(textField.getWidth(), height);
        scrollPane.setSize(textField.getWidth(), height);
    }
    public void setCompleterMatches(Object[] objectsToMatch){
        if(isFilterWindowVisible())
            setFilterWindowVisible(false);
        objectList = objectsToMatch;
        firstSelectedIndex = -1;
        filter.setCompleterMatches(objectsToMatch);
    }
    public void insertString(FilterBypass filterBypass, int offset, String string, AttributeSet attributeSet) throws BadLocationException{
        setFilterWindowVisible(false);
        super.insertString(filterBypass, offset, string, attributeSet);
    }
    public void remove(FilterBypass filterBypass, int offset, int length) throws BadLocationException{
        setFilterWindowVisible(false);
        super.remove(filterBypass, offset, length);
        oldPreText = preText;
        preText = textField.getText();
        if(filter.setFilter(preText)){
            oldPreText = preText;
        }
//        else{
//            textField.setText(oldPreText);
//        }
        if(filter.getSize() > 0 && !isFilterWindowVisible() && textField.getText().length() != 0)
            setFilterWindowVisible(true);
    }
    public void replace(FilterBypass filterBypass, int offset, int length, String string, AttributeSet attributeSet)
            throws BadLocationException{
        if(isAdjusting){
            filterBypass.replace(offset, length, string, attributeSet);
            return;
        }
        oldPreText = preText;
        super.replace(filterBypass, offset, length, string, attributeSet);
        if(getLeadingSelectedIndex() == -1){
            if(isFilterWindowVisible()){
                setFilterWindowVisible(false);
            }
//            textField.setText(oldPreText);
            return;
        }
        filter.setFilter(preText);
        if(!textField.isUpdateData()){
            if(!isFilterWindowVisible() && textField.isInFocus())
                setFilterWindowVisible(true);
            else
                setWindowHeight();
            list.setSelectedValue(textField.getText(), true);
        }else{
            textField.setIsUpdateData(false);
        }
    }
    public int getCompleterListSize(){
        return objectList.length;
    }
    public Object getCompleterObjectAt(int i){
        return objectList[i];
    }
    public JTextField getTextField(){
        return textField;
    }
    // inner classes
    class EscapeAction extends AbstractAction{
        public void actionPerformed(ActionEvent e){
            if(isFilterWindowVisible())
                setFilterWindowVisible(false);
        }
    }
    private class FilterWindowListener extends MouseAdapter implements AncestorListener, FocusListener, WindowFocusListener{
        public void ancestorMoved(AncestorEvent event){
            setFilterWindowVisible(false);
        }
        public void ancestorAdded(AncestorEvent event){
            setFilterWindowVisible(false);
        }
        public void ancestorRemoved(AncestorEvent event){
            setFilterWindowVisible(false);
        }
        public void focusLost(FocusEvent e){
            if(e.getOppositeComponent() != window)
                setFilterWindowVisible(false);
        }
        public void focusGained(FocusEvent e){
        }
        public void windowLostFocus(WindowEvent e){
            // Window w = e.getOppositeWindow();
            // if(w.getFocusOwner() != textField)
            setFilterWindowVisible(false);
        }
        public void windowGainedFocus(WindowEvent e){
        }
        public void mousePressed(MouseEvent e){
            setFilterWindowVisible(false);
        }
    }
    private class TextFieldKeyListener extends KeyAdapter{
        public void keyPressed(KeyEvent e){
            if(!((e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_LEFT)
                    || ((e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) && (isFilterWindowVisible()))
                    || ((e.getKeyCode() == KeyEvent.VK_PAGE_UP) && (isFilterWindowVisible())) || (e.getKeyCode() == KeyEvent.VK_ENTER)))
                return;
            if((e.getKeyCode() == KeyEvent.VK_RIGHT) && !isFilterWindowVisible()){
                preText = textField.getText();
                filter.setFilter(preText);
                if(filter.getSize() > 0)
                    setFilterWindowVisible(true);
                else
                    return;
            }
            if(e.getKeyCode() == KeyEvent.VK_ENTER){
                if(isFilterWindowVisible())
                    setFilterWindowVisible(false);
                textField.setCaretPosition(textField.getText().length());
                return;
            }
            int index = -1;
            if(e.getKeyCode() == KeyEvent.VK_RIGHT)
                index = Math.min(list.getSelectedIndex() + 1, list.getModel().getSize() - 1);
            else if(e.getKeyCode() == KeyEvent.VK_LEFT)
                index = Math.max(list.getSelectedIndex() - 1, 0);
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_UP)
                index = Math.max(list.getSelectedIndex() - MAX_VISIBLE_ROWS, 0);
            else if(e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                index = Math.min(list.getSelectedIndex() + MAX_VISIBLE_ROWS, list.getModel().getSize() - 1);
            if(index == -1)
                return;
            list.setSelectedIndex(index);
            list.scrollRectToVisible(list.getCellBounds(index, index));
        }
    }
    private class ListMouseListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e){
            isAdjusting = true;
            textField.setText(list.getSelectedValue().toString());
            isAdjusting = false;
            textField.select(preText.length(), textField.getText().length());
            setFilterWindowVisible(false);
        }
    }
    private class ListMouseMotionListener extends MouseMotionAdapter{
        public void mouseMoved(MouseEvent e){
            list.setSelectedIndex(list.locationToIndex(e.getPoint()));
        }
    }
}
