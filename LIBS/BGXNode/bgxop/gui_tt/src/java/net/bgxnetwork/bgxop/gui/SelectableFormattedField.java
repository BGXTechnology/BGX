package net.bgx.bgxnetwork.bgxop.gui;
import java.awt.event.FocusEvent;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JToolTip;
import javax.swing.text.DefaultFormatter;
import net.bgx.bgxnetwork.bgxop.uitools.ui.MultiRowToolTip;

public class SelectableFormattedField extends JFormattedTextField{
    boolean inFocus;
    public SelectableFormattedField(NumberFormat format){
        super(format);
    }
    public SelectableFormattedField(DefaultFormatter formatter){
        super(formatter);
    }
    public SelectableFormattedField(SimpleDateFormat df){
        super(df);
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
            if(getSelectedText() == null){
                inFocus = false;
            }else{
                inFocus = true;
            }
            // try{
            // AbstractFormatter formatter = getFormatter();
            // if(formatter != null){
            // formatter.stringToValue(this.getText());
            // }
            // }catch (ParseException e1){
            // }
        }
    }
    public JToolTip createToolTip(){
        return new MultiRowToolTip();
    }
}
