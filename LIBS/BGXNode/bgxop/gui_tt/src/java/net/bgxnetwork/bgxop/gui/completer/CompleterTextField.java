package net.bgx.bgxnetwork.bgxop.gui.completer;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


import javax.swing.JTextField;
import javax.swing.text.PlainDocument;


public class CompleterTextField extends JTextField implements KeyListener{

    private CompleterFilterWithWindow filter;
    private boolean inFocus;
    
    private boolean isUpdateData;
    private static final int LIMIT=4;
    public CompleterTextField(Object[] completeMatches){
        super();
        init(completeMatches);
    }
    private void init(Object[] suggestions){
       
        addKeyListener(this);
        PlainDocument document = new PlainDocument();
        filter = new CompleterFilterWithWindow(suggestions, this);
        document.setDocumentFilter(filter);
        setDocument(document);
    }
    public void setSuggestions(Object[] suggestions){
        if(filter == null){
            filter = new CompleterFilterWithWindow(suggestions, this);
        }
        filter.setCompleterMatches(suggestions);
    }
    

    public void setUpdateText(String text){
        isUpdateData = true;
        super.setText(text);
    }
    protected void processFocusEvent(FocusEvent e){
        super.processFocusEvent(e);
        if(e.getID() == FocusEvent.FOCUS_GAINED){
            if(inFocus){
                selectAll();
            }else{
                inFocus = true;
            }
        }
        if(e.getID() == FocusEvent.FOCUS_LOST){
            inFocus = false;
            String text = getText();
            
            validateInput();
            if(text.indexOf('%') == -1 && text.length() != 0){
                text = text + '%';
                setText(text);
            }
        }
    }
    public boolean isInFocus(){
        return inFocus;
    }
    public boolean isUpdateData(){
        return isUpdateData;
    }
    public void setIsUpdateData(boolean isUpdateData){
        this.isUpdateData = isUpdateData;
    }
    public void keyReleased(KeyEvent e){
        validateInput();
    }
    public void keyTyped(KeyEvent e){
    }
    public void keyPressed(KeyEvent e){
    }
    
    public void validateInput(){
        String text = getText();
        if(text != null && !text.equals("")){
            boolean ok = false;
            if(text.length() > LIMIT){
                if(text.indexOf("%") != LIMIT){
                    setText(text.substring(0, LIMIT));
                }else{
                    if(text.length() != LIMIT+1){
                        setText(text.substring(0, LIMIT+1));
                    }
                }
            }else{
                char lastCharacter = text.charAt(text.length() - 1);
                for(int i = 0; i <= 9; i++){
                    if(String.valueOf(lastCharacter).equals(Integer.toString(i))){
                        ok = true;
                        break;
                    }else if(lastCharacter == '%' && text.indexOf(lastCharacter) == LIMIT){

                        ok = true;
                        break;
                    }
                    else if (lastCharacter == '%'){
                        ok = true;
                        break;
                    }
                }
                if(!ok){
                    setText(text.substring(0, text.length() - 1));
                }
            }
        }
    }
}
