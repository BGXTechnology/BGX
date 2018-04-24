package net.bgx.bgxnetwork.bgxop.gui.lv.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.bgx.bgxnetwork.bgxop.gui.SelectableFormattedField;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.object.FieldObjectContainer;
import net.bgx.bgxnetwork.bgxop.gui.calendar.CalendarDialog;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.transfer.query.Query;

public class FilterPanel extends JDialog {
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private ResourceBundle rb_gui_query = PropertyResourceBundle.getBundle("gui_query");
    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private DialogAction da = new DialogAction();
    private LVObject objectProperty;
    private LVObject linkProperty;
    private JButton applyButton = new JButton("apply");
    private JButton cancelButton = new JButton("cancel");
    private JButton clearButton = new JButton("clear");
    private MainFrame owner;
    private HashMap<Long, Object> textFieldContainer = new HashMap<Long, Object>();
    private HashMap<Long, ArrayList<Object>> dateFieldContainer = new HashMap<Long, ArrayList<Object>>();
    private Query query;

    public FilterPanel(MainFrame owner, Query query, LVObject objectProperty, LVObject objectLink) {
        super(owner);
        this.owner = owner;
        this.objectProperty = objectProperty;
        this.linkProperty = objectLink;
        this.query = query;
        initDialog();
    }

    private void initDialog() {
        String title = rb.getString("FilterPanel.title");
        setTitle(title);
        getContentPane().add(getObjectPanel(), BorderLayout.PAGE_START);
        getContentPane().add(getLinkPanel(), BorderLayout.CENTER);
        getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
        GlobalPopupUtil.initListeners(this);
        pack();
        setResizable(false);
        setLocationRelativeTo(getOwner());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private JComponent getLinkPanel() {
        JPanel linkPanel = new JPanel();

        String title = rb.getString("FilterPanel.link.title");
        linkPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER));

        linkPanel.setLayout(new GridBagLayout());
        int y = 0;
        for (FieldObject fo : linkProperty.getFields()) {
            if (fo == null) continue;
            JLabel label = new JLabel(fo.getCaption());
            linkPanel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));
            switch (fo.getDataTypeCode()) {
                case 2: //string
                    JTextField textField = new JTextField(20);
                    textFieldContainer.put(fo.getCode(), textField);
                    linkPanel.add(textField, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                            new Insets(5, 5, 5, 5), 0, 0));
                    break;
                case 12: //number
                    JTextField numberTextField = new JTextField(20);
                    textFieldContainer.put(fo.getCode(), numberTextField);
                    linkPanel.add(numberTextField, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                            new Insets(5, 5, 5, 5), 0, 0));
                    break;
                case 91: //date
                    JPanel startPanel = new JPanel();

                    startPanel.setOpaque(false);
                    SelectableFormattedField dateStartTF = new SelectableFormattedField(df);
                    dateStartTF.setEditable(false);
                    dateStartTF.setPreferredSize(new Dimension(70, 22));

                    JButton clearButton = new JButton(ResourceLoader.getInstance().getIconByResource(rb_gui_query, "FLSearchQueryPanel.clearDate.img"));
                    clearButton.setPreferredSize(new Dimension(20, 20));
                    ClearActionListener clearListener = new ClearActionListener(dateStartTF);
                    clearButton.addActionListener(clearListener);

                    JButton calendarButton = new JButton(ResourceLoader.getInstance().getIconByResource(rb_gui_query, "FLSearchQueryPanel.chooseDate.img"));
                    calendarButton.setPreferredSize(new Dimension(20, 20));
                    CalendarActionListener calendarActionListener = new CalendarActionListener(dateStartTF);
                    calendarButton.addActionListener(calendarActionListener);

                    startPanel.add(new JLabel(rb.getString("FilterPanel.date.1")));
                    startPanel.add(dateStartTF);
                    startPanel.add(calendarButton);
                    startPanel.add(clearButton);

                    JPanel finishPanel = new JPanel();

                    finishPanel.setOpaque(false);
                    SelectableFormattedField dateFinishTF = new SelectableFormattedField(df);
                    dateFinishTF.setEditable(false);
                    dateFinishTF.setPreferredSize(new Dimension(70, 22));

                    JButton clearFinishButton = new JButton(ResourceLoader.getInstance().getIconByResource(rb_gui_query, "FLSearchQueryPanel.clearDate.img"));
                    clearFinishButton.setPreferredSize(new Dimension(20, 20));
                    ClearActionListener clearFinishListener = new ClearActionListener(dateFinishTF);
                    clearFinishButton.addActionListener(clearFinishListener);

                    JButton calendarFinishButton = new JButton(ResourceLoader.getInstance().getIconByResource(rb_gui_query, "FLSearchQueryPanel.chooseDate.img"));
                    calendarFinishButton.setPreferredSize(new Dimension(20, 20));
                    CalendarActionListener calendarFinishActionListener = new CalendarActionListener(dateFinishTF);
                    calendarFinishButton.addActionListener(calendarFinishActionListener);

                    finishPanel.add(new JLabel(rb.getString("FilterPanel.date.2")));
                    finishPanel.add(dateFinishTF);
                    finishPanel.add(calendarFinishButton);
                    finishPanel.add(clearFinishButton);

                    linkPanel.add(startPanel, new GridBagConstraints(1, y, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                            new Insets(5, 5, 5, 5), 0, 0));
                    linkPanel.add(finishPanel, new GridBagConstraints(2, y, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                            new Insets(5, 5, 5, 5), 0, 0));

                    ArrayList<Object> dateFields = new ArrayList<Object>();
                    dateFields.add(dateStartTF);
                    dateFields.add(dateFinishTF);
                    dateFieldContainer.put(fo.getCode(), dateFields);
                    break;
            }
            y++;
        }
        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(linkPanel);
        return scroll;
    }

    private JComponent getObjectPanel() {
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setOpaque(false);

        String title = rb.getString("FilterPanel.object.title");
        topPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER));

        int y = 0;
        for (FieldObject fieldObject : objectProperty.getFields()) {
            if (fieldObject == null) continue;
            JLabel label = new JLabel(fieldObject.getCaption());
            JTextField textField = new JTextField(20);
            textFieldContainer.put(fieldObject.getCode(), textField);
            topPanel.add(label, new GridBagConstraints(0, y, 1, 1, 0.0, 0.0, GridBagConstraints.PAGE_START, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));
            topPanel.add(textField, new GridBagConstraints(1, y, 2, 1, 2.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));
            y++;
        }

        return topPanel;
    }

    private Component getButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel underButtonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        applyButton.setText(rb.getString("HiddenObjectDialog.applyButton"));
        cancelButton.setText(rb.getString("HiddenObjectDialog.cancelButton"));
        clearButton.setText(rb.getString("HiddenObjectDialog.clearButton"));
        applyButton.addActionListener(da);
        cancelButton.addActionListener(da);
        clearButton.addActionListener(da);

        underButtonPanel.add(applyButton);
        underButtonPanel.add(clearButton);
        underButtonPanel.add(cancelButton);
        buttonPanel.add(underButtonPanel, BorderLayout.EAST);
        return buttonPanel;
    }

    private class DialogAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source == applyButton) {
                if (setReturnResult())
                    dispose();
            } else if (source == cancelButton) {
                dispose();
            } else if (source == clearButton) {
                clearFields();
            }
        }
    }

    public void setFilterParameters(FieldObjectContainer fieldObjectContainer) {
        for (FieldObject fieldObject : fieldObjectContainer.getObjectProperties()) {
            if (fieldObject == null) continue;
            JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
            if (tf == null) continue;
            tf.setText((String) FieldObject.convertToString(fieldObject.getValue(), fieldObject.getDataTypeCode()));
        }

        for (FieldObject fieldObject : fieldObjectContainer.getLinkProperties()) {
            if (fieldObject == null) continue;
            switch (fieldObject.getDataTypeCode()) {
                case 2:
                case 12:
                    JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
                    if (tf == null) continue;
                    tf.setText((String) FieldObject.convertToString(fieldObject.getValue(), fieldObject.getDataTypeCode()));
                    break;
                case 91:
                    ArrayList<Object> dates = dateFieldContainer.get(fieldObject.getCode());
                    if (dates == null) continue;
                    SelectableFormattedField startDate = (SelectableFormattedField) dates.get(0);
                    SelectableFormattedField finishDate = (SelectableFormattedField) dates.get(1);

                    ArrayList<Object> dateInterval = (ArrayList<Object>) fieldObject.getValue();
                    startDate.setText((String) dateInterval.get(0));
                    finishDate.setText((String) dateInterval.get(1));
                    break;
            }
        }
    }

    private void clearFields() {
        for (FieldObject fieldObject : objectProperty.getFields()) {
            if (fieldObject == null) continue;

            JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
            if (tf == null) continue;
            tf.setText("");
        }
        for (FieldObject fieldObject : linkProperty.getFields()) {
            if (fieldObject == null) continue;
            switch (fieldObject.getDataTypeCode()) {
                case 2:
                case 12:
                    JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
                    if (tf == null) continue;
                    tf.setText("");
                    break;
                case 91:
                    ArrayList<Object> dates = dateFieldContainer.get(fieldObject.getCode());
                    if (dates == null) continue;
                    SelectableFormattedField startDate = (SelectableFormattedField) dates.get(0);
                    SelectableFormattedField finishDate = (SelectableFormattedField) dates.get(1);
                    startDate.setText(null);
                    finishDate.setText(null);
                    startDate.setValue(null);
                    finishDate.setValue(null);
                    break;
            }
        }
    }

    private boolean setReturnResult() {
        for (FieldObject fieldObject : objectProperty.getFields()) {
            if (fieldObject == null) continue;

            JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
            if (tf == null) continue;
            fieldObject.setValue(tf.getText());
        }

        for (FieldObject fieldObject : linkProperty.getFields()) {
            if (fieldObject == null) continue;
            switch (fieldObject.getDataTypeCode()) {
                case 2:
                case 12:
                    JTextField tf = (JTextField) textFieldContainer.get(fieldObject.getCode());
                    if (tf == null) continue;
                    fieldObject.setValue(tf.getText());
                    break;
                case 91:
                    ArrayList<Object> dates = dateFieldContainer.get(fieldObject.getCode());
                    if (dates == null) continue;
                    SelectableFormattedField startDate = (SelectableFormattedField) dates.get(0);
                    SelectableFormattedField finishDate = (SelectableFormattedField) dates.get(1);
                    if (startDate.getText() != null && startDate.getText().length() > 0 &&
                            finishDate.getText() != null && finishDate.getText().length() > 0 &&
                            FieldObject.convertValueToDate(startDate.getText()).getTime() > FieldObject.convertValueToDate(finishDate.getText()).getTime()) {
                        MessageDialogs.warning(this, rb.getString("TimedDiagramParameterPanel.error.wrongIntervalDate"),
                                rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                        return false;
                    }

                    ArrayList<Object> dateInterval = new ArrayList<Object>();
                    dateInterval.add(startDate.getText());
                    dateInterval.add(finishDate.getText());
                    fieldObject.setValue(dateInterval);
                    break;
            }
        }

        FieldObjectContainer fieldObjectContainer = new FieldObjectContainer();
        fieldObjectContainer.setObjectProperties(objectProperty.getFields());
        fieldObjectContainer.setLinkProperties(linkProperty.getFields());
        owner.applyFilter(query, fieldObjectContainer);
        return true;
    }

    public class ClearActionListener implements ActionListener {
        private SelectableFormattedField textField;

        public ClearActionListener(SelectableFormattedField field) {
            textField = field;
        }

        public void actionPerformed(ActionEvent e) {
            textField.setText(null);
            textField.setValue(null);
        }
    }

    public class CalendarActionListener implements ActionListener {
        private SelectableFormattedField textField;

        public CalendarActionListener(SelectableFormattedField field) {
            textField = field;
        }

        public void actionPerformed(ActionEvent e) {
            CalendarDialog dialog = new CalendarDialog(owner, ((Component) e.getSource()).getLocationOnScreen(), textField);
        }
    }

    public void dispose() {
        QueryPanel queryPanel = owner.getQueryListController().getQueryPanel(query);
        if (queryPanel != null) {
            queryPanel.setFilterButtonSelected(queryPanel.getController().isFilterIsSet());
            queryPanel.removeDialog(this);
        }
        super.dispose();
    }
}
