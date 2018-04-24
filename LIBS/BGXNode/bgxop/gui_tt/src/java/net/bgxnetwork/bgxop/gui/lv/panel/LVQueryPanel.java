/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.panel;

import net.bgx.bgxnetwork.bgxop.wizard.WizardQueryPanel;
import net.bgx.bgxnetwork.bgxop.wizard.Wizard;
import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotEnoughParameters;
import net.bgx.bgxnetwork.bgxop.gui.query.dictionary.Loader;
import net.bgx.bgxnetwork.bgxop.gui.query.QueryHelper;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.QuestListModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.transfer.data.Request;
import net.bgx.bgxnetwork.transfer.data.Quest;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.util.ArrayList;

/**
 * User: A.Borisenko
 * Date: 01.06.2007
 * Time: 10:48:47
 */
public class LVQueryPanel extends JPanel implements WizardQueryPanel {
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_query");
    protected ResourceBundle imageResource = PropertyResourceBundle.getBundle("gui");
    JEditorPane requestDescription;
    JList selectingQueryList = new JList(new QuestListModel());
    JList selectedQueryList = new JList(new QuestListModel());
    JEditorPane descQueryArea;
    JTextField visualizationTfName;
    JComboBox requestObjects;
    JButton bAddQuest, bAddAllQuests, bRemoveQuest, bRemoveAllQuests;

    public LVQueryPanel() {
        initPanel();
        if (requestObjects.getModel().getSize() >0 )
            requestObjects.setSelectedIndex(0);

    }

    protected String produceQueryName(Query query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getDataFromPanel() throws WizardPanelNotEnoughParameters {
        //todo добавиь это ограничение на само поле
        if ((visualizationTfName.getText()).length() > 63){
            MessageDialogs.info(this,rb.getString("LVPanel.grName.tolong.msg"), rb.getString("LVPanel.grName.tolong.title"));
            visualizationTfName.setText(visualizationTfName.getText().substring(0, 64));
        }

        if (!checkRequiredParameters())
            throw new WizardPanelNotEnoughParameters();

        Query query = new Query();
        String grName = visualizationTfName.getText();
        query.setName(rb.getString("Visualization.default.name"));

        Request request = (Request)requestObjects.getSelectedItem();
        ArrayList<Quest> quests = new ArrayList<Quest>();

        int capacity = selectedQueryList.getModel().getSize();
        for (int i= 0; i<capacity; i++){
            Quest q = (Quest)selectedQueryList.getModel().getElementAt(i);
            quests.add(q);
        }

        query.setQueryType(QueryHelper.getInstance().getQueryType(QueryType.VISUALIZATION.intValue()));
        query.addParameter(QueryParameterType.REQUEST_ID, ""+request.getId());

        ArrayList<String> questIds = new ArrayList<String>();
        for (int i=0; i<((DefaultListModel)selectedQueryList.getModel()).getSize(); i++){
            Quest q = (Quest)((QuestListModel)selectedQueryList.getModel()).getElementAt(i);
            questIds.add(""+q.getId());
        }
        query.addParameter(QueryParameterType.QUEST_ID, questIds);
        query.addParameter(QueryParameterType.REQUEST_NAME, request.getName());
        query.addParameter(QueryParameterType.QUEST_NAME, grName);
        return query;
    }

    public void setDataToPanel(Object obj) throws QueryBusinesException {
        Query query = (Query)obj;
        ArrayList<String> queryIdList = (ArrayList<String>) query.getParameter(QueryParameterType.QUEST_ID);
        String strRequestId = (String)query.getParameter(QueryParameterType.REQUEST_ID);
        Long requestId = Long.parseLong(strRequestId);

        String strQuestName = (String)query.getParameter(QueryParameterType.QUEST_NAME);
        visualizationTfName.setText(query.getName());

        for (int i = 0 ; i <requestObjects.getModel().getSize(); i++){
            Request request = (Request)requestObjects.getModel().getElementAt(i);
            if (request.getId().equals(requestId)){
                requestObjects.getModel().setSelectedItem(request);
                break;
            }
        }
        requestObjects.setEnabled(false);

        QuestListModel model = (QuestListModel)selectingQueryList.getModel();
        ArrayList<Quest> quests = new ArrayList<Quest>();

        for (int i = 0 ; i < model.getSize(); i++){
            Quest q = (Quest)model.getElementAt(i);
            if (queryIdList.contains(""+q.getId())){
                if (!quests.contains(q))
                    quests.add(q);
            }
        }
            for(Quest q : quests){
                if (q != null){
                    ((DefaultListModel) selectedQueryList.getModel()).removeElement(q);
                    ((DefaultListModel) selectedQueryList.getModel()).addElement(q);
                }
            }
    }

    public void handleException(Exception e, Wizard w) {
        //
    }

    public void initPanel() {
        initButtons();
        this.setPreferredSize(new Dimension(620, 350));
        setLayout(new BorderLayout());
        this.add(getRequestBlock(), BorderLayout.NORTH);
        this.add(getQuestionBlock(), BorderLayout.CENTER);
    }

    private JPanel getRequestBlock() {
        JPanel requestDataPanel = new JPanel(new GridBagLayout());
        requestDataPanel.setOpaque(false);

        JPanel requestNamePanel = new JPanel(new GridBagLayout());
        requestNamePanel.setOpaque(false);

        JLabel requestLabel = new JLabel(rb.getString("Label.request.name"));
        requestNamePanel.add(requestLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(5, 0, 5, 5), 0, 0));
        requestObjects = new JComboBox();
        for(Request request : Loader.getInstance().getRequestsList()){
            requestObjects.addItem(request);
        }

        requestObjects.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                Request request = (Request)cb.getSelectedItem();
                Long rId = request.getId();
                DefaultListModel qlm = (DefaultListModel)selectingQueryList.getModel();
                qlm.removeAllElements();
                for (Quest q : Loader.getInstance().getQueryListByRequest(rId))
                    qlm.addElement(q);
                //selectingQueryList.repaint();
                ((DefaultListModel)selectedQueryList.getModel()).removeAllElements();

                requestDescription.setText(buildHTML(request));
                requestDescription.setCaretPosition(0);
            }

            private String buildHTML(Request request){
                return Util.getParametersAsHtml(request);
            }
        });

        requestObjects.setPreferredSize(new Dimension(300, 22));
        requestNamePanel.add(requestObjects, new GridBagConstraints(1, 0, GridBagConstraints.REMAINDER, 1, 5.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));

        requestDataPanel.add(requestNamePanel, new GridBagConstraints(0, 0, GridBagConstraints.RELATIVE, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 5), 0, 0));

        JLabel descriptionLabel = new JLabel(rb.getString("Label.request.desc.name"));
        requestDataPanel.add(descriptionLabel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        requestDescription = new JEditorPane("text/html", "");
        requestDescription.setEditable(false);

        JScrollPane scrollDesc = new JScrollPane(requestDescription);
        scrollDesc.setPreferredSize(new Dimension(300, 70));
        requestDescription.setCaretPosition(0);
        requestDataPanel.add(scrollDesc, new GridBagConstraints(0, 2, 1, 4, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        return requestDataPanel;
    }

    private JPanel getQuestionBlock() {
        JPanel questionSelectionPanel = new JPanel(new GridBagLayout());
        questionSelectionPanel.setOpaque(false);

        JLabel forSelectionLabel = new JLabel(rb.getString("Label.question.selectingQuestion"));
        JLabel selectedLabel = new JLabel(rb.getString("Label.question.selectedQuestion"));
        questionSelectionPanel.add(forSelectionLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));
        questionSelectionPanel.add(selectedLabel, new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 5, 0, 5), 0, 0));

        selectedQueryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollSelectingList = new JScrollPane(selectingQueryList);
        selectingQueryList.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent e) {
                Quest quest = (Quest)((JList)e.getSource()).getSelectedValue();
                if (quest != null)
                    descQueryArea.setText(buildHTML(quest));
                else descQueryArea.setText("");
                descQueryArea.setCaretPosition(0);
            }

            private String buildHTML(Quest quest){
                return Util.getParametersAsHtml(quest);
            }
        });
        questionSelectionPanel.add(scrollSelectingList, new GridBagConstraints(0, 1, 1, 1, 4.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false);
        buttonPanel.add(bAddQuest, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        buttonPanel.add(bAddAllQuests, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        buttonPanel.add(bRemoveQuest, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));
        buttonPanel.add(bRemoveAllQuests, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.NONE,
                new Insets(0, 0, 5, 5), 0, 0));

        questionSelectionPanel.add(buttonPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5), 0, 0));

        selectedQueryList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollSelectedList = new JScrollPane(selectedQueryList);
        questionSelectionPanel.add(scrollSelectedList, new GridBagConstraints(2, 1, 1, 1, 3.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));


        JLabel questionOverviewLabel = new JLabel(rb.getString("Label.question.overview"));
        JPanel questionOverviewPanel = new JPanel(new BorderLayout());
        questionOverviewPanel.setOpaque(false);
        questionOverviewPanel.setPreferredSize(new Dimension(200, 100));
        questionOverviewPanel.add(questionOverviewLabel, BorderLayout.NORTH);

        descQueryArea = new JEditorPane("text/html", "");
        descQueryArea.setEditable(false);
        descQueryArea.setPreferredSize(new Dimension(200, 100));

        JScrollPane scrollDescPane = new JScrollPane(descQueryArea);
        questionOverviewPanel.add(scrollDescPane, BorderLayout.CENTER);
        descQueryArea.setCaretPosition(0);
        questionSelectionPanel.add(questionOverviewPanel, new GridBagConstraints(0, 2, 2, 2, 1.0, 2.0, GridBagConstraints.LINE_START, GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5), 0, 0));

        JPanel visNamePanel = new JPanel(new BorderLayout());
        visNamePanel.setOpaque(false);
        JLabel visNameLabel = new JLabel(rb.getString("Label.question.visualName"));
        visualizationTfName = new JTextField();
        visNamePanel.add(visNameLabel, BorderLayout.NORTH);
        visNamePanel.add(visualizationTfName, BorderLayout.CENTER);
        questionSelectionPanel.add(visNamePanel, new GridBagConstraints(2, 2, 1, 1, 3.0, 0.0, GridBagConstraints.LINE_START, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 0, 0));
        return questionSelectionPanel;
    }

    private void initButtons(){
        Action addQuestAction = new AddQuestAction();
        bAddQuest = new JButton(addQuestAction);
        bAddQuest.setIcon(ResourceLoader.getInstance().getIconByResource(imageResource, "Button.addQuest.img"));
        bAddQuest.setText("");

        Action addQllQuestsAction = new AddAllQuestsAction();
        bAddAllQuests = new JButton(addQllQuestsAction);
        bAddAllQuests.setIcon(ResourceLoader.getInstance().getIconByResource(imageResource, "Button.addAllQuests.img"));
        bAddAllQuests.setText("");

        Action removeQuestAction = new RemoveQuestAction();
        bRemoveQuest = new JButton(removeQuestAction);
        bRemoveQuest.setIcon(ResourceLoader.getInstance().getIconByResource(imageResource, "Button.removeQuest.img"));
        bRemoveQuest.setText("");

        Action removeAllQuestsAction = new RemoveAllQuestsAction();
        bRemoveAllQuests = new JButton(removeAllQuestsAction);
        bRemoveAllQuests.setIcon(ResourceLoader.getInstance().getIconByResource(imageResource, "Button.removeAllQuests.img"));
        bRemoveAllQuests.setText("");

        setButtonSize(bAddQuest, 40,22);
        setButtonSize(bAddAllQuests, 40,22);
        setButtonSize(bRemoveQuest, 40,22);
        setButtonSize(bRemoveAllQuests, 40,22);
    }

    private void setButtonSize(JButton b, int w, int h){
        b.setPreferredSize(new Dimension(w, h));
        b.setMinimumSize(new Dimension(w, h));
        b.setMaximumSize(new Dimension(w, h));
        b.setSize(new Dimension(w, h));
    }

    private class AddQuestAction extends AbstractAction {
        public AddQuestAction() {
            super(rb.getString("Button.quest.AddOne"));
            putValue(SHORT_DESCRIPTION, rb.getString("Button.quest.AddOne"));
        }

        public void actionPerformed(ActionEvent e) {
            Object[] qs = (Object[]) selectingQueryList.getSelectedValues();
            if (qs != null) {
                for(Object o : qs){
                    Quest q = (Quest)o;
                    if (q != null){
                        ((DefaultListModel) selectedQueryList.getModel()).removeElement(q);
                        ((DefaultListModel) selectedQueryList.getModel()).addElement(q);
                    }
                }
            }
        }
    }

    private class AddAllQuestsAction extends AbstractAction {
        public AddAllQuestsAction() {
            super(rb.getString("Button.quest.AddAll"));
            putValue(SHORT_DESCRIPTION, rb.getString("Button.quest.AddAll"));
        }

        public void actionPerformed(ActionEvent e) {
            QuestListModel model = (QuestListModel)selectingQueryList.getModel();
            ArrayList<Quest> quests = new ArrayList<Quest>();

            for (int i = 0 ; i < model.getSize(); i++){
                Quest q = (Quest)model.getElementAt(i);
                quests.add(q);
            }
            for(Quest q : quests){
                if (q != null){
                    ((DefaultListModel) selectedQueryList.getModel()).removeElement(q);
                    ((DefaultListModel) selectedQueryList.getModel()).addElement(q);
                }
            }
        }
    }

    private class RemoveQuestAction extends AbstractAction {
        public RemoveQuestAction() {
            super(rb.getString("Button.quest.DelOne"));
            putValue(SHORT_DESCRIPTION, rb.getString("Button.quest.DelOne"));
        }

        public void actionPerformed(ActionEvent e) {
            Object[] qs = (Object[]) selectedQueryList.getSelectedValues();
            if (qs != null) {
                for (Object o : qs) {
                    Quest q = (Quest)o;
                    if (q != null)
                        ((DefaultListModel) selectedQueryList.getModel()).removeElement(q);
                }
            }
        }
    }

    private class RemoveAllQuestsAction extends AbstractAction {
        public RemoveAllQuestsAction() {
            super(rb.getString("Button.quest.DelAll"));
            putValue(SHORT_DESCRIPTION, rb.getString("Button.quest.DelAll"));
        }

        public void actionPerformed(ActionEvent e) {
            QuestListModel model = (QuestListModel)selectedQueryList.getModel();
            model.removeAllElements();
        }
    }

    private boolean checkRequiredParameters(){
        if (visualizationTfName.getText().length() == 0)
            return false;
        if (selectedQueryList.getModel().getSize() == 0)
            return false;
        return true;
    }

}
