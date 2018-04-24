package net.bgx.bgxnetwork.bgxop.gui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.bgx.bgxnetwork.bgxop.engine.AlgorithmsHolder;
import net.bgx.bgxnetwork.bgxop.gui.background.*;
import net.bgx.bgxnetwork.bgxop.gui.background.lv.CreateQueryThread;
import net.bgx.bgxnetwork.bgxop.gui.query.QueryHelper;
import net.bgx.bgxnetwork.bgxop.gui.lv.panel.LVQueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.AttributeManager;
import net.bgx.bgxnetwork.bgxop.gui.lv.request.RequestPanel;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.services.QueryServiceDelegator;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.wizard.WizardCommand;
import net.bgx.bgxnetwork.bgxop.wizard.WizardQueryPanel;
import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotEnoughParameters;
import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryStatus;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;

/**
 * Class ProfilePanelController
 *
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class ProfilePanelController implements WizardCommand {
    private static Logger log = Logger.getLogger(ProfilePanelController.class.getName());
    private static final long defaultRefreshInterval = 30000;
    private static final long defaultPollingInterval = 1000;
    private List<QueryType> queryTypes = null;
    private List<Query> data = new ArrayList<Query>();
    private ProfilePanel view;
    private MainFrame owner;
    private RefreshThread refresher;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    protected ResourceBundle rb_gui_query = PropertyResourceBundle.getBundle("gui_query");
    private HashMap<Long, JDialog> dialogs = new HashMap<Long, JDialog>();

    public ProfilePanelController(MainFrame owner) {
        this.owner = owner;
        long refL = defaultRefreshInterval;
        long polL = defaultPollingInterval;
        try {
            String s = System.getProperty("profile.refreshInterval");
            if (s != null) {
                double d = Double.parseDouble(s);
                refL = (long) (d * 1000);
            }
        } catch (Exception e) {
        }
        try {
            String s = System.getProperty("profile.pollingInterval");
            if (s != null) {
                double d = Double.parseDouble(s);
                polL = (long) (d * 1000);
            }
        } catch (Exception e) {
        }
        refresher = new RefreshThread(this, polL, refL, owner.getStatusBar());
        refresher.start();
    }

    protected void setView(ProfilePanel view) {
        this.view = view;
    }

    public ProfilePanel getView() {
        return view;
    }

    public RefreshThread getRefresher() {
        return refresher;
    }

    public QueryType getQueryTypeById(Long id) {
        for (QueryType qt : queryTypes) {
            if (qt.getId() == id)
                return qt;
        }
        return null;
    }

    protected void readQueryTypes() {
        try {
            queryTypes = QueryServiceDelegator.getInstance().getQueryTypeList();
        } catch (Exception e) {
            MessageDialogs.generalError(owner, e);
            data = new ArrayList<Query>();
        }
    }

    public void refresh() {
        refresher.forceRefresh();
    }

    public void refresh(List<QueryType> queryTypes, List<Query> queries) {
        this.queryTypes = queryTypes;
        this.data = queries;
        if (view != null) {
            view.loadData(data);
            view.repaint();
        }
    }

    public boolean containtsName(Query q) {
        for (Query q1 : data) {
            if (q1.getId() == q.getId())
                continue;
            if (q1.getName().equals(q.getName()))
                return true;
        }
        return false;
    }

    public void createQueryPanel() {
//        System.out.println("createQueryPanel started");
        JDialog createQueryPanel = new JDialog(owner);
//        System.out.println("createQueryPanel with title "+rb_gui_query.getString("Dialog.CreateQuery.name"));
        createQueryPanel.setTitle(rb_gui_query.getString("Dialog.CreateQuery.name"));
        createQueryPanel.setResizable(false);
        createQueryPanel.setModal(false);
        createQueryPanel.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        createQueryPanel.getContentPane().setLayout(new BorderLayout());
        createQueryPanel.setPreferredSize(new Dimension(680, 450));
        JPanel lvPanel = new LVQueryPanel();
        GlobalPopupUtil.initListeners(lvPanel);

//        System.out.println("LVQueryPanel created");
        JPanel bottomPanel = new JPanel();
        JButton createQueryButton = new JButton(new createQueryAction((WizardQueryPanel) lvPanel, createQueryPanel));
        bottomPanel.add(createQueryButton);

        JButton exitButton = new JButton(new closeDialogAction(createQueryPanel));
        bottomPanel.add(exitButton);

        createQueryPanel.getContentPane().add(lvPanel, BorderLayout.CENTER);
        createQueryPanel.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

//        System.out.println("LVQueryPanel show panel");
        createQueryPanel.pack();
        createQueryPanel.setLocationRelativeTo(null);
        createQueryPanel.setVisible(true);
    }

    private class createQueryAction extends AbstractAction {
        WizardQueryPanel queryPanel;
        JDialog dlg;

        public createQueryAction(WizardQueryPanel panel, JDialog ownerDlg) {
            super(rb_gui_query.getString("Button.createVisualization"));
            queryPanel = panel;
            dlg = ownerDlg;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Object objQuery = queryPanel.getDataFromPanel();
                CreateQueryThread createQueryThread = new CreateQueryThread(owner.getProfilePanel().getController(), objQuery);
                createQueryThread.start();
                //finishAction(objQuery);
                dlg.dispose();
            }
            catch (WizardPanelNotEnoughParameters nep) {
                MessageDialogs.generalError((Frame) null, null, rb_gui_query.getString("warning.msg.requiredParameter.isAbsent"), rb_gui_query.getString("warning.title.requiredParameter.isAbsent"));
            }

        }
    }

    private class editQueryAction extends AbstractAction {
        WizardQueryPanel queryPanel;
        JDialog dlg;
        Query oldQuery;

        public editQueryAction(WizardQueryPanel panel, JDialog ownerDlg, Query initQuery) {
            super(rb_gui_query.getString("Button.editGroup"));
            queryPanel = panel;
            dlg = ownerDlg;
            oldQuery = initQuery;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Object objQuery = queryPanel.getDataFromPanel();
                Query newQuery = (Query) objQuery;
                ArrayList<String> newQueryIdList = (ArrayList<String>) newQuery.getParameter(QueryParameterType.QUEST_ID);
                ArrayList<String> oldQueryIdList = (ArrayList<String>) oldQuery.getParameter(QueryParameterType.QUEST_ID);
                String questName = (String) newQuery.getParameter(QueryParameterType.QUEST_NAME);

                RequestPanel requestPanel = owner.getRequestTabbedPane().getRequestListController().getPanelByQuestId(oldQuery.getId());

                if (oldQueryIdList.size() == newQueryIdList.size() && oldQueryIdList.containsAll(newQueryIdList)) {
                    String messageText = rb.getString("Editgroup.question");
                    boolean res = MessageDialogs.confirm(owner, messageText, rb.getString("confirm.title"));
                    if (!res) return;
                    oldQuery.setName(questName);
                    QueryServiceDelegator.getInstance().updateName(oldQuery);
                    if (requestPanel != null) {
                        JTabbedPane tabbedPane = owner.getRequestTabbedPane();
                        Component[] components = tabbedPane.getComponents();
                        int i = -1;
                        for (Component component : components){

                            if (component instanceof RequestPanel){
                                i++;
                                if (requestPanel.equals((RequestPanel)component))
                                    break;
                            }
                        }
                        DefaultMutableTreeNode dmtn = owner.getProfilePanel().controller.findNodeById(oldQuery.getId());
                        Query zayvka = (Query)(((DefaultMutableTreeNode)dmtn.getParent()).getUserObject());
                        if (i >= 0)
                            owner.getRequestTabbedPane().setTitleAt(i, zayvka.getName()+" - "+oldQuery.getName());
                    }
                    DefaultMutableTreeNode dmtn = findNodeById(oldQuery.getId());
                    dmtn.setUserObject(oldQuery);
                    ((DefaultTreeModel) view.contents.getModel()).nodeChanged(dmtn);
                }
                else {
                    String messageText = rb.getString("Editgroup.question");
                    boolean res = MessageDialogs.confirm(owner, messageText, rb.getString("confirm.title"));
                    if (!res) return;

                    DefaultMutableTreeNode dmtn = findNodeById(oldQuery.getId());

                    for (int i = 0; i < dmtn.getChildCount(); i++) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) dmtn.getChildAt(i);
                        Query childQuery = (Query) child.getUserObject();
                        AlgorithmsHolder.getInstance().removeGraphCustoms(childQuery.getId());
                        if (requestPanel!= null) requestPanel.getQueryListPanel().getController().removeQuery(childQuery);
                    }
                    owner.getRequestTabbedPane().remove(requestPanel);
                    owner.getRequestTabbedPane().getRequestListController().removeRequestPanel(oldQuery.getId());

                    //remove old
                    if (!removeQuery(oldQuery, true)){
                        String oldName = questName + " "+ rb.getString("copy.text");
                        String newName = getChildNodeName((DefaultMutableTreeNode)dmtn.getParent(), oldName);
                        ((Query)objQuery).addParameter(QueryParameterType.QUEST_NAME, newName);
                    }
                    //create new
                    CreateQueryThread createQueryThread = new CreateQueryThread(owner.getProfilePanel().getController(), objQuery);
                    createQueryThread.start();
                }
                dlg.dispose();
            }
            catch (WizardPanelNotEnoughParameters nep) {
                MessageDialogs.generalError((Frame) null, null, rb_gui_query.getString("warning.msg.requiredParameter.isAbsent"), rb_gui_query.getString("warning.title.requiredParameter.isAbsent"));
            }
            catch (Exception e1) {
                MessageDialogs.generalError((Frame) null, e1, rb.getString("Action.exception.name"), rb.getString("Action.exception.title"));
            }

        }
    }

    private class closeDialogAction extends AbstractAction {
        JDialog dialog;

        public closeDialogAction(JDialog dlg) {
            super(rb_gui_query.getString("Button.cancel"));
            dialog = dlg;
        }

        public void actionPerformed(ActionEvent e) {
            dialog.dispose();
        }
    }

    public void updateQuery(Query q) {
        JDialog createQueryPanel = new JDialog(owner);
        createQueryPanel.setTitle(rb_gui_query.getString("Dialog.UpdateQuery.name"));
        createQueryPanel.setResizable(false);
        createQueryPanel.setModal(false);
        createQueryPanel.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        createQueryPanel.getContentPane().setLayout(new BorderLayout());
        createQueryPanel.setPreferredSize(new Dimension(680, 450));
        JPanel lvPanel = new LVQueryPanel();
        GlobalPopupUtil.initListeners(lvPanel);

        JPanel bottomPanel = new JPanel();
        JButton createQueryButton = new JButton(new editQueryAction((WizardQueryPanel) lvPanel, createQueryPanel, q));
        bottomPanel.add(createQueryButton);

        JButton exitButton = new JButton(new closeDialogAction(createQueryPanel));
        bottomPanel.add(exitButton);

        createQueryPanel.getContentPane().add(lvPanel, BorderLayout.CENTER);
        createQueryPanel.getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        try {
            ((WizardQueryPanel) lvPanel).setDataToPanel(q);
            createQueryPanel.pack();
            createQueryPanel.setLocationRelativeTo(null);
            createQueryPanel.setVisible(true);
        }
        catch (Exception e) {
            createQueryPanel.dispose();
            MessageDialogs.generalError((Frame) null, e, rb.getString("Action.exception.name"), rb.getString("Action.exception.title"));
        }
    }

    public boolean openQuery(Query q) {
        AttributeManager.getInstance().setOwner(owner);

        if ((q.getQueryType().getId() == QueryType.VISUALIZATION)) {
            // check query status
            if (q.getQueryStatus() == QueryStatus.Ready || q.getQueryStatus() == QueryStatus.Limited) {
                // wheather query data are also open
                //QueryPanel exists = owner.getQueryListPanel().getController().getQueryPanel(q.getName());
                QueryPanel exists = owner.getQueryListController().getQueryPanel(q);

                if (exists != null) {
                    Long parentId = q.getParent();
                    RequestPanel requestPanel = owner.getRequestTabbedPane().getRequestListController().getPanelByQuestId(parentId);
                    owner.getRequestTabbedPane().setSelectedComponent(requestPanel);
                    requestPanel.getQueryListPanel().setSelectedComponent(exists);
                    return false;
                }
                owner.getQueryListController().openQuery(q);
            }
        }
        return true;
    }

    public boolean removeQuery(Query q, boolean fromUpdate) {
        String firstPart = rb.getString("ProfilePanelController.confirm.removeDefaultObject.part1");
        QueryType qt = q.getQueryType();
        String messageText = "";
        if (qt.getId() == QueryType.VISUALIZATION) {
            firstPart = rb.getString("ProfilePanelController.confirm.removeQuery.part1");
            messageText = firstPart + " '" + q.getName()+"'?";
            //+ "' " + rb.getString("ProfilePanelController.confirm.removeQuery.part2");
        } else if (qt.getId() == QueryType.REQUEST) {
            firstPart = rb.getString("ProfilePanelController.confirm.removeRequest.part1");
            messageText = firstPart + " '" + q.getName()+"'?";
            //+ "' " + rb.getString("ProfilePanelController.confirm.removeQuery.part2");
        } else if (qt.getId() == QueryType.QUEST) {
            if (fromUpdate){
                firstPart = rb.getString("ProfilePanelController.confirm.removeUpdateQuest.part1")+"?";
                messageText = firstPart;
            }
            else{
                firstPart = rb.getString("ProfilePanelController.confirm.removeQuest.part1");
                messageText = firstPart  + " '" + q.getName()+"'?";
            }
        }

        boolean res = MessageDialogs.confirm(owner, messageText, rb.getString("confirm.title"));
        if (!res)
            return false;
        try {
            // for deleted node search his childs and delete it first
            DefaultMutableTreeNode deletedNode = findNodeById(q.getId());
            if (deletedNode != owner.getProfilePanel().root) {
                ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
                for (int i = 0; i < deletedNode.getChildCount(); i++) {
                    childs.add(deletedNode.getChildAt(i));
                }
                for (TreeNode node : childs) {
                    removeNode((Query) ((ProfilePanel.QueryTreeNode) node).getQuery());
                }
            }
            owner.getQueryListController().removeQuery(q);
            QueryServiceDelegator.getInstance().remove(q.getId());
            TreePath tp = view.contents.getSelectionPath();
            ProfilePanel.QueryTreeNode node = (ProfilePanel.QueryTreeNode) tp.getLastPathComponent();
            // TreeNode parent = node.getParent();
            ((DefaultTreeModel) view.contents.getModel()).removeNodeFromParent(node);
            /*
             * node.removeFromParent();
             * ((DefaultTreeModel)view.contents.getModel()).nodeStructureChanged(parent);
             * view.scroll.repaint();
             */
        }
        catch (Exception e) {
            MessageDialogs.generalError(owner, e);
        }
        // refresh();
        return true;
    }

    private boolean removeNode(Query q) {
        try {
            // for deleted node search his childs and delete it first
            DefaultMutableTreeNode deletedNode = findNodeById(q.getId());
            if (deletedNode != owner.getProfilePanel().root) {
                ArrayList<TreeNode> childs = new ArrayList<TreeNode>();
                for (int i = 0; i < deletedNode.getChildCount(); i++) {
                    childs.add(deletedNode.getChildAt(i));
                }
                for (TreeNode node : childs) {
                    removeNode((Query) ((ProfilePanel.QueryTreeNode) node).getQuery());
                }
                owner.getQueryListController().removeQuery(q);
                QueryServiceDelegator.getInstance().remove(q.getId());
                ((DefaultTreeModel) view.contents.getModel()).removeNodeFromParent(deletedNode);
            }
        } catch (Exception e) {
            MessageDialogs.generalError(owner, e);
        }
        return true;
    }

    public boolean execute(Query q) {
        boolean res;
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(q.getQueryType().getRoleName());
//        if(!ActorInfo.getInstance().permitRulles(arr)){
//            MessageDialogs.warning(owner, rb.getString("message.error.info.noPermissions") + "\n"
//                    + rb.getString("message.error.info.ascInfo"), rb.getString("message.error.info.title"));
//            return false;
//        }
        if (q.getQueryStatus() == QueryStatus.Ready) {
            res = MessageDialogs.confirm(owner, rb.getString("ProfilePanelController.confirm.reexecuteQuery"), rb
                    .getString("confirm.title"));
        } else {
            res = MessageDialogs.confirm(owner, rb.getString("ProfilePanelController.confirm.executeQuery"), rb.getString("confirm.title"));
        }
        if (!res)
            return false;
        try {
            owner.getQueryListController().removeQuery(q);
            QueryServiceDelegator.getInstance().execute(q.getId());
            owner.getQueryListController().removeLayout(q);
        } catch (Exception e) {
            MessageDialogs.generalError(owner, e);
        }
        refresh();
        return true;
    }

    private Query createRequest(Query q) {
        Query requestQuery = new Query();
        requestQuery.setQueryType(QueryHelper.getInstance().getQueryType(QueryType.REQUEST.intValue()));
        requestQuery.setName((String) q.getParameter(QueryParameterType.REQUEST_NAME));
        requestQuery.addParameter(QueryParameterType.REQUEST_ID, "" + q.getParameter(QueryParameterType.REQUEST_ID));

        try {
            Long id = createNewInstanceOfQuery(requestQuery);
            requestQuery.setId(id);
            return requestQuery;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Query createQuest(Query q, Long requestId) {
        Query questQuery = new Query();
        questQuery.setQueryType(QueryHelper.getInstance().getQueryType(QueryType.QUEST.intValue()));
        questQuery.setName((String) q.getParameter(QueryParameterType.QUEST_NAME));
        questQuery.setParent(requestId);
        questQuery.addParameter(QueryParameterType.QUEST_ID, q.getParameter(QueryParameterType.QUEST_ID));
        questQuery.addParameter(QueryParameterType.REQUEST_ID, "" + q.getParameter(QueryParameterType.REQUEST_ID));
        questQuery.addParameter(QueryParameterType.QUEST_NAME, questQuery.getName());
        try {
            questQuery.setId(createNewInstanceOfQuery(questQuery));
            return questQuery;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Query createVisualization(Query q, Long questId) {
        q.setParent(questId);
        try {
            q.setId(QueryServiceDelegator.getInstance().create(q));
            return q;
        }
        catch (Exception e) {
            return null;
        }
    }

    private Long createNewInstanceOfQuery(Query q) throws Exception {
        q.setId(QueryServiceDelegator.getInstance().create(q));
        return q.getId();
    }

    public boolean create(Query q) {
        try {
            checkQueryName(q);
            if (q.getQueryType().getId() == QueryType.VISUALIZATION.intValue()) {
                Query request = createRequest(q);
                DefaultMutableTreeNode requestNode;
                requestNode = this.findNodeById(request.getId());
                if (requestNode == owner.getProfilePanel().root)
                    requestNode = owner.getProfilePanel().addNodeAsChild(null, request);


                Query quest = createQuest(q, request.getId());
                DefaultMutableTreeNode questNode = findNodeById(quest.getId());
                if (questNode == owner.getProfilePanel().root)
                    questNode = owner.getProfilePanel().addNodeAsChild(requestNode, quest);

                q.setParent(quest.getId());
                q.setName(getChildNodeName(questNode, q.getName()));
                q.setId(QueryServiceDelegator.getInstance().create(q));
                ProfilePanel.QueryTreeNode visNode = owner.getProfilePanel().addNodeAsChild(questNode, q);
                refresh();
            }
        }
        catch (Exception e) {
            MessageDialogs.generalError(owner, e);
        }
        return true;
    }

    public boolean createAndExecute(Query q) {
        try {
            checkQueryName(q);
            q.setId(QueryServiceDelegator.getInstance().createAndExecute(q));
            owner.getProfilePanel().addNodeAsChild(null, q);
            refresh();
        } catch (Exception e) {
            MessageDialogs.generalError(owner, e);
        }
        return true;
    }

    public DefaultMutableTreeNode findNodeById(Long id) {
        Enumeration nds = owner.getProfilePanel().root.breadthFirstEnumeration();
        while (nds.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nds.nextElement();
            if (node.getUserObject() instanceof Query)
                if (((Query) node.getUserObject()).getId() == id.longValue())
                    return node;
        }
        return owner.getProfilePanel().root;
    }

    public DefaultMutableTreeNode findNetworkParentNode(Long Id) {
        Enumeration nds = owner.getProfilePanel().root.breadthFirstEnumeration();
        DefaultMutableTreeNode curentNode = null;
        while (nds.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) nds.nextElement();
            if (node.getUserObject() instanceof Query) {
                if (((Query) node.getUserObject()).getId() == Id.longValue())
                    curentNode = node;
                break;
            }
        }
        while ((curentNode != null) && (((Query) curentNode.getUserObject()).getQueryType().getId() != QueryType.VISUALIZATION)) {
            curentNode = (DefaultMutableTreeNode) curentNode.getParent();
        }
        return curentNode;
    }

    public void refreshNodeById(Long id) {
        DefaultMutableTreeNode node = findNodeById(id);
        ((DefaultTreeModel) owner.getProfilePanel().contents.getModel()).nodeChanged((TreeNode) node);
    }

    public void removeDialog(Long qID) {
        dialogs.remove(qID);
    }

    public String getChildNodeName(DefaultMutableTreeNode parent, String currentName) {
        if (parent == null)
            parent = (DefaultMutableTreeNode) (owner.getProfilePanel().root).getChildAt(0);
        Enumeration childrens = parent.children();
        int count = 0;
        String lastName = "";
        while (childrens.hasMoreElements()) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) childrens.nextElement();
            if (((Query) child.getUserObject()).getName().indexOf(currentName) == 0) {
                count++;
                lastName = ((Query) child.getUserObject()).getName();
            }
        }
        if (count == 0)
            return currentName + " 1";
        else {
            String suffix = lastName.substring(currentName.length());
            char[] charSuffix = suffix.toCharArray();
            boolean start = false;
            boolean finish = false;
            String strNumber = "";
            for (int i = 0; i < charSuffix.length; i++) {
                switch (charSuffix[i]) {
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                    case '0':
                        start = true;
                        strNumber += charSuffix[i];
                        break;
                    default:
                        if (start)
                            finish = true;
                        break;
                }
                if (finish)
                    break;
            }
            if (strNumber.length() == 0)
                return currentName + " " + (count + 1);
            else
                return currentName + " " + (Integer.parseInt(strNumber) + 1);
        }
    }

    public void finishAction(Object obj) {
        if (obj instanceof Query) {
            if (create((Query) obj))
                try {
                    QueryServiceDelegator.getInstance().execute(((Query) obj).getId());
                    refresh();
                }
                catch (Exception e) {
                    MessageDialogs.generalError(owner, e);
                }
        }
    }

    public void finishAction2(Object obj) {
        if (obj instanceof Query)
            createAndExecute((Query) obj);
    }

    public void cancelAction(Object obj) {
        return;
    }

    public boolean checkQueryName(Query q) {
        return true;
    }

    public MainFrame getMainFrame() {
        return owner;
    }
}
