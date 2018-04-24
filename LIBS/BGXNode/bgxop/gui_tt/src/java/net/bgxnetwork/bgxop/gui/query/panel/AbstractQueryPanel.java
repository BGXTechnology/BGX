package net.bgx.bgxnetwork.bgxop.gui.query.panel;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.border.EtchedBorder;
import net.bgx.bgxnetwork.bgxop.gui.SelectableFormattedField;
import net.bgx.bgxnetwork.bgxop.gui.query.IReadinessListener;
import net.bgx.bgxnetwork.bgxop.uitools.ui.MultiRowToolTip;
import net.bgx.bgxnetwork.exception.query.ErrorList;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.transfer.query.QueryType;


/**
 * User: A.Borisenko Date: 08.02.2007
 */
public abstract class AbstractQueryPanel extends JPanel implements IReadinessListener{
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
    protected ResourceBundle trb = PropertyResourceBundle.getBundle("transfer");
    protected boolean _ready = false;
    protected ArrayList<IReadinessListener> _listeners = new ArrayList<IReadinessListener>();
    protected QueryType queryType = null;
    protected JTextField _tfName;
    protected JTextArea _taDescription;
    protected JCheckBox _chbIsLimit, _chbIsActual;
    protected SelectableFormattedField _tfLimit;
    protected JScrollPane _descriptionScroll;
    private Long queryId = null;
    public AbstractQueryPanel(){
        initCommonBlock();
    }
    private void initCommonBlock(){
        // Name of Query
        _tfName = new JTextField();
        _tfName.setPreferredSize(new Dimension(120, 22));
        _tfName.setMinimumSize(new Dimension(120, 22));
        // Description of Query
        _taDescription = new JTextArea();
        _taDescription.setLineWrap(true);
        _taDescription.setWrapStyleWord(true);
        // Scroll of description
        _descriptionScroll = new JScrollPane(_taDescription);
        _descriptionScroll.setBorder(new EtchedBorder(EtchedBorder.RAISED));
        _descriptionScroll.setMinimumSize(new Dimension(100, 75));
        _descriptionScroll.setPreferredSize(new Dimension(200, 75));
        // Limit Field
        _tfLimit = new SelectableFormattedField(NumberFormat.getIntegerInstance());
        _tfLimit.setValue(200);
        _tfLimit.setEnabled(false);
        _tfLimit.setPreferredSize(new Dimension(50, 22));
        _tfLimit.setMinimumSize(new Dimension(50, 22));
        // Check Box of Limit
        _chbIsLimit = new JCheckBox(rb.getString("QueryPanel.fetchLimitLabel"));
        // Check Box of Actual
        _chbIsActual = new JCheckBox(rb.getString("QueryPanel.isActual"));
    }
    public boolean validateQueryName(Query query) {
        return query.getName() != null && query.getName().length() != 0;
    }

    protected abstract String produceQueryName(Query query);

    public String getQueryName(){
        if(_tfName.getText() == null || _tfName.getText().length() == 0){
            return queryType.getName();
        }
        return _tfName.getText();
    }
    public Query getCommonDataFromPanel(){
        Query query = new Query();
//        if(_chbIsActual.isSelected())
//            query.addParameter(QueryParameterType.IsActual, "1");
//        else
//            query.addParameter(QueryParameterType.IsActual, "0");
//        if(_chbIsLimit.isSelected())
//            query.addParameter(QueryParameterType.MaxObjects, _tfLimit.getValue().toString());
//        query.setName(_tfName.getText());
//        query.setDescription(_taDescription.getText());
        return query;
    }
    protected int parseIntParameter(Query q, QueryParameterType type) throws QueryBusinesException{
        String s = getStringParameter(q, type);
        if(s == null)
            return 0;
        try{
            return Integer.parseInt(s);
        }catch (NumberFormatException e){
            String message = "Cannot parse " + type + " as integer.";
            throw new QueryBusinesException(ErrorList.BUSINES_CONVERSION_TYPE_EXCEPTION, new Object[] { message });
        }
    }
    protected String getStringParameter(Query q, QueryParameterType type) throws QueryBusinesException{
        Object o = q.getParameter(type);
        if(o == null)
            return null;
        if(!(o instanceof String)){
            String message = "ParameterTypeEntity " + type + " is expected of java.lang.String type instead of " + o.getClass().getName();
            throw new QueryBusinesException(ErrorList.BUSINES_TYPE_MISMATCH, new Object[] { message });
        }
        return (String) o;
    }
    protected java.util.List<String> getArrayParameter(Query q, QueryParameterType type) throws QueryBusinesException{
        Object o = q.getParameter(type);
        try{
            return (java.util.List<String>) o;
        }catch (ClassCastException e){
            String message = "ParameterTypeEntity " + type + " is expected of java.util.List<String> type instead of "
                    + o.getClass().getName();
            throw new QueryBusinesException(ErrorList.BUSINES_TYPE_MISMATCH, new Object[] { message });
        }
    }
    public void addReadinessListener(IReadinessListener l){
        _listeners.add(l);
    }
    public void setReady(boolean ready){
        _ready = ready;
    }
    protected void fireReadiness(boolean ready){
        if(this._ready != ready){
            this._ready = ready;
            for(IReadinessListener l : _listeners)
                l.setReady(ready);
        }
    }
    public Long getQueryId(){
        return queryId;
    }
    public void setQueryId(Long queryId){
        this.queryId = queryId;
    }
    public class MultiRowToolTipTextField extends JTextField{
        public MultiRowToolTipTextField(int columns){
            super(columns);
        }
        public MultiRowToolTipTextField(){
            super();
        }
        public JToolTip createToolTip(){
            return new MultiRowToolTip();
        }
    }
}
