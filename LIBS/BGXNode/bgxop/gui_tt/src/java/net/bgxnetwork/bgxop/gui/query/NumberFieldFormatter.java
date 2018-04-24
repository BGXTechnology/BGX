package net.bgx.bgxnetwork.bgxop.gui.query;
import java.text.ParseException;
import java.util.regex.Pattern;
import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatter;

public class NumberFieldFormatter extends DefaultFormatter{
    private Pattern pattern = Pattern.compile("\\d{0,100}");
    private JFormattedTextField tf = null;
    public NumberFieldFormatter(){
        setOverwriteMode(false);
    }
    public void install(JFormattedTextField ftf){
        super.install(ftf);
        tf = ftf;
    }
    public Object stringToValue(String string) throws ParseException{
        string = string.trim();
        if(!pattern.matcher(string).matches()){
            Object value = tf.getValue();
            tf.setValue(value);
            return value;
        }
        if(tf != null)
            tf.setValue(string);
        return string;
    }
    public String valueToString(Object value) throws ParseException{
        if(value == null)
            return "";
        return value.toString();
    }
}
