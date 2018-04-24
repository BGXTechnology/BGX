package net.bgx.bgxnetwork.bgxop.gui.completer;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

abstract public class AbstractCompleterFilter extends DocumentFilter{
    protected String preText;
    protected String oldPreText = "";
    protected int firstSelectedIndex = -1;
    abstract public int getCompleterListSize();
    abstract public Object getCompleterObjectAt(int i);
    abstract public JTextField getTextField();
    public void replace(FilterBypass filterBypass, int offset, int length, String string, AttributeSet attributeSet)
            throws BadLocationException{
        super.replace(filterBypass, offset, length, string, attributeSet);
        Document doc = filterBypass.getDocument();
        preText = doc.getText(0, doc.getLength());
        firstSelectedIndex = -1;
        for(int i = 0; i < getCompleterListSize(); i++){
            String objString = getCompleterObjectAt(i).toString();
            if(objString.equalsIgnoreCase(preText)){
                firstSelectedIndex = i;
                break;
            }
            if(objString.length() <= preText.length())
                continue;
            String objStringStart = objString.substring(0, preText.length());
            if(objStringStart.equalsIgnoreCase(preText)){
                String objStringEnd = objString.substring(preText.length());
                filterBypass.insertString(preText.length(), objStringEnd, attributeSet);
                getTextField().select(preText.length(), doc.getLength());
                firstSelectedIndex = i;
                break;
            }
        }
    }
    public void insertString(FilterBypass filterBypass, int offset, String string, AttributeSet attributeSet) throws BadLocationException{
        super.insertString(filterBypass, offset, string, attributeSet);
    }
    public void remove(FilterBypass filterBypass, int offset, int length) throws BadLocationException{
        super.remove(filterBypass, offset, length);
    }
    public int getLeadingSelectedIndex(){
        return firstSelectedIndex;
    }
}
