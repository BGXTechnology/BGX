package net.bgx.bgxnetwork.bgxop.gui.calendar;
import javax.swing.*;
import net.bgx.bgxnetwork.bgxop.gui.SelectableFormattedField;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.Date;
import java.util.Calendar;

/**
 * Class CalendarDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class CalendarDialog extends JDialog implements ActionListener{
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
    private JCalendar calendarComponent;
    private JButton choose, cancel;
    private Calendar calendar = null;
    private SelectableFormattedField currentField = null;
    public CalendarDialog(Dialog owner, Point position){
        super(owner, true);
        init(position);
    }
    public CalendarDialog(Frame owner, Point position){
        super(owner, true);
        init(position);
    }
    public CalendarDialog(Frame owner,Point position, SelectableFormattedField currentField){
    	super(owner, true);
        this.currentField = currentField;
        init(position);
    }
    public CalendarDialog(Point position, SelectableFormattedField currentField){
        super();
        this.currentField = currentField;
        init(position);
    }
    private void init(Point position){
        setTitle(rb.getString("CalendarDialog.title"));
       Date d=null;
        if(currentField!=null){
        	d=(Date)currentField.getValue();
        	
        }else{
        	d=new Date();
        }
        calendarComponent = new JCalendar(d);
        calendarComponent.setOpaque(true);
        calendarComponent.getDayChooser().setOpaque(false);
        getContentPane().setLayout(new GridBagLayout());
        getContentPane().add(
                calendarComponent,
                new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
                        0));
        choose = new JButton(rb.getString("CalendarDialog.apply"));
        choose.addActionListener(this);
        cancel = new JButton(rb.getString("CalendarDialog.cancel"));
        cancel.addActionListener(this);
        getContentPane()
                .add(
                        choose,
                        new GridBagConstraints(0, 1, 1, 1, 0.5, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5,
                                5), 0, 0));
        getContentPane()
                .add(
                        cancel,
                        new GridBagConstraints(1, 1, 1, 1, 0.5, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 5,
                                5), 0, 0));
        pack();
        if(position != null){
            setLocation(position);
        }else{
            setLocationRelativeTo(null);
        }
        setVisible(true);
    }
    public Calendar getCalendar(){
        return calendar;
    }
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == choose){
            calendar = calendarComponent.getCalendar();
            if(currentField != null){
                currentField.setValue(calendar.getTime());
            }
        }
        processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
