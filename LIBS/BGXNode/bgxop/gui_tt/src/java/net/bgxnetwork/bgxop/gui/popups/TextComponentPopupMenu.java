package net.bgx.bgxnetwork.bgxop.gui.popups;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

public class TextComponentPopupMenu extends JPopupMenu{
    private JTextComponent textComp;
    private ResourceBundle rb = null;
    private Action copyA;
    private Action pasteA;
    private Action selectAllA;
    private Action cutA;
    private Action deleteA;
    private JMenuItem copyItem;
    private JMenuItem pasteItem;
    private JMenuItem selectAllItem;
    private JMenuItem cutItem;
    private JMenuItem deleteItem;
    private String labelCopy;
    private String labelPaste;
    private String labelCut;
    private String labelSelectAll;
    private String labelDelete;
    public TextComponentPopupMenu(){
        initResourceBundle();
        copyA = new CopyAction();
        pasteA = new PasteAction();
        selectAllA = new SelectAllAction();
        cutA = new CutAction();
        deleteA = new DeleteAction();
        copyItem = new JMenuItem(copyA);
        pasteItem = new JMenuItem(pasteA);
        selectAllItem = new JMenuItem(selectAllA);
        cutItem = new JMenuItem(cutA);
        deleteItem = new JMenuItem(deleteA);
        this.add(copyItem);
        this.add(pasteItem);
        this.add(selectAllItem);
        this.add(cutItem);
        this.add(deleteItem);
    }
    private void initResourceBundle(){
        if(rb == null){
            rb = PropertyResourceBundle.getBundle("gui");
            labelCopy = rb.getString("PopupMenu.items.copy");
            labelPaste = rb.getString("PopupMenu.items.paste");
            labelCut = rb.getString("PopupMenu.items.cut");
            labelSelectAll = rb.getString("PopupMenu.items.selectAll");
            labelDelete = rb.getString("PopupMenu.items.delete");
        }
    }
    public void show(Component invoker, int x, int y){
        super.show(invoker, x, y);
        textComp = (JTextComponent) invoker;
        setModifiable(textComp);
    }
    public void setModifiable(JTextComponent component){
        if(textComp.isEnabled()){
            boolean canModify = textComp.isEditable();
            boolean emptyText = (textComp.getText().equals("") || textComp.getText() == null);
            boolean selectIsNotEmpty = (textComp.getSelectedText() != null && textComp.getSelectedText().length() > 0);
            pasteItem.setEnabled(canModify);
            copyItem.setEnabled(selectIsNotEmpty);
            selectAllItem.setEnabled(!emptyText);
            cutItem.setEnabled(canModify && selectIsNotEmpty);
            deleteItem.setEnabled(canModify && selectIsNotEmpty);
        }else{
            copyItem.setEnabled(false);
            pasteItem.setEnabled(false);
            selectAllItem.setEnabled(false);
            cutItem.setEnabled(false);
            deleteItem.setEnabled(false);
        }
    }
    public class CopyAction extends AbstractAction{
        public CopyAction(){
            super(labelCopy);
            putValue(SHORT_DESCRIPTION, labelCopy);
        }
        public void actionPerformed(ActionEvent e){
            textComp.copy();
        }
    }
    public class PasteAction extends AbstractAction{
        public PasteAction(){
            super(labelPaste);
            putValue(SHORT_DESCRIPTION, labelPaste);
        }
        public void actionPerformed(ActionEvent e){
            textComp.paste();
        }
    }
    public class SelectAllAction extends AbstractAction{
        public SelectAllAction(){
            super(labelSelectAll);
            putValue(SHORT_DESCRIPTION, labelSelectAll);
        }
        public void actionPerformed(ActionEvent e){
            SwingUtilities.invokeLater(new Runnable(){
                public void run(){
                    textComp.selectAll();
                }
            });
        }
    }
    public class CutAction extends AbstractAction{
        public CutAction(){
            super(labelCut);
            putValue(SHORT_DESCRIPTION, labelCut);
        }
        public void actionPerformed(ActionEvent e){
            textComp.cut();
        }
    }
    public class DeleteAction extends AbstractAction{
        public DeleteAction(){
            super(labelDelete);
            putValue(SHORT_DESCRIPTION, labelDelete);
        }
        public void actionPerformed(ActionEvent e){
            int start = textComp.getSelectionStart();
            int end = textComp.getSelectionEnd();
            String str = textComp.getText();
            textComp.setText(str.substring(0, start) + str.substring(end));
        }
    }
}
