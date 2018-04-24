package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.transfer.query.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.*;
import javax.swing.border.EtchedBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Class ProfilePanel
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ProfilePanel extends JPanel implements TreeSelectionListener {
  protected static final Dimension toolButtonSize = new Dimension(28,28);
  protected static int treeToggleClickCount = 3;
  protected JTree contents;
  protected JScrollPane scroll;
  protected DefaultMutableTreeNode root;
  protected Action openA, editA, removeA, executeA, createA, refreshA, copyA;
  protected ProfilePanelController controller;

  private SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
  protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

  private JComponent toolMenu;
  private JButton iconEditButton;

  private AppAction _actions = AppAction.getInstance();

  public ProfilePanel(MainFrame parent) {
    this(new ProfilePanelController(parent));
  }

  public ProfilePanel(ProfilePanelController controller) {
    this.controller = controller;
    controller.setView(this);

    root = new DefaultMutableTreeNode(rb.getString("ProfilePanel.treeRootNode"));
    contents = new JTree(root);
      contents.setOpaque(false);
    ProfileTreeCellRenderer renderer = new ProfileTreeCellRenderer();
    Icon icon = ResourceLoader.getInstance().getIconByResource(rb, "tree.folderClosed.img");
    if (icon!=null) renderer.setClosedIcon(icon);
    icon = ResourceLoader.getInstance().getIconByResource(rb, "tree.folderOpen.img");
    if (icon!=null) renderer.setOpenIcon(icon);
    contents.setCellRenderer(renderer);
    contents.setToggleClickCount(treeToggleClickCount);
    contents.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    contents.getSelectionModel().addTreeSelectionListener(this);
    scroll = new JScrollPane(contents);
    scroll.setBorder(new EtchedBorder(EtchedBorder.RAISED));
    scroll.setOpaque(false);

    _actions.setProfilePanelController(this.controller);
    _actions.setOwner(this.controller.getMainFrame());
    openA = _actions.getAction(AppAction.OPEN_ACTION);
    editA = _actions.getAction(AppAction.EDIT_ACTION);
    copyA = _actions.getAction(AppAction.COPY_ACTION);
    removeA = _actions.getAction(AppAction.REMOVE_ACTION);
    executeA = new ExecuteAction();
    createA = _actions.getAction(AppAction.CREATE_ACTION);
    refreshA = new RefreshAction();

    setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints(0,GridBagConstraints.RELATIVE,
        GridBagConstraints.REMAINDER,1,1.0,0.0,GridBagConstraints.CENTER,
        GridBagConstraints.HORIZONTAL,new Insets(0,5,5,5),0,0);

//    toolMenu = createToolBar();
//    add(toolMenu, gbc);

    add(scroll, new GridBagConstraints(0,GridBagConstraints.RELATIVE,
        GridBagConstraints.REMAINDER,1,1.0,1.0,GridBagConstraints.CENTER,
        GridBagConstraints.BOTH,new Insets(0,5,5,5),0,0));

    JPopupMenu queryPopup = new JPopupMenu(rb.getString("ProfilePanel.popupMenuTitle"));
    queryPopup.add(openA);
    queryPopup.add(editA);
    queryPopup.add(copyA);
    queryPopup.add(removeA);
    queryPopup.add(executeA);
    queryPopup.addSeparator();
    queryPopup.add(createA);
    queryPopup.add(refreshA); 
    contents.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            if(e.getButton()==MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                JTree tree = (JTree)e.getSource();
                TreePath path = tree.getPathForLocation(e.getX(), e.getY());
                if(path != null) {
                    Object selectedObject = path.getLastPathComponent();
                    Query q = null;
                    if (selectedObject instanceof ProfilePanel.QueryTreeNode) {
                        q = ((ProfilePanel.QueryTreeNode)selectedObject).getQuery();
                    }
                    if (q != null && q.getId() != -1) {
                        getController().openQuery(q);
                    }
                }
            }
        }
    });
    contents.addMouseListener(new TreePopupListener(queryPopup, contents, this));
  }

  private JComponent createToolBar() {
    JPanel res = new JPanel(new GridBagLayout());
    res.setOpaque(false);

    Insets standard = new Insets(5,2,5,2);
    GridBagConstraints tools = new GridBagConstraints(GridBagConstraints.RELATIVE,0,1,1,0.0,0.0,
        GridBagConstraints.CENTER,GridBagConstraints.NONE,new Insets(5,10,5,2),0,0);

    //create toolbar
    add(createToolButton(createA, "ProfilePanel.new.img"), tools);
    tools.insets = standard;
    iconEditButton = createToolButton(editA, "ProfilePanel.edit.img");
    add(iconEditButton, tools);
    add(createToolButton(removeA, "ProfilePanel.remove.img"), tools);
    add(new JLabel(), new GridBagConstraints(GridBagConstraints.RELATIVE,0,1,1,0.5,0.0,
        GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
    add(createToolButton(refreshA, "ProfilePanel.refresh.img"), tools);
    add(new JLabel(), new GridBagConstraints(GridBagConstraints.RELATIVE,0,1,1,0.5,0.0,
        GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
    add(createToolButton(executeA, "ProfilePanel.execute.img"), tools);
    tools.insets = new Insets(5,2,5,10);
    add(createToolButton(openA, "ProfilePanel.open.img"), tools);

    return res;
  }

  private JButton createToolButton(Action a, String resource) {
    JButton res = new JButton(a);
    res.setIcon(ResourceLoader.getInstance().getIconByResource(rb, resource));
    res.setText(null);
    res.setMinimumSize(toolButtonSize);
    res.setMaximumSize(toolButtonSize);
    return res;
  }

  public ProfilePanelController getController() {
    return controller;
  }

  public void loadData(java.util.List<Query> data) {
    //save selection
    TreePath path = contents.getSelectionPath();

    Object selected = null;
    if (path!=null) {
      selected = path.getLastPathComponent();
    }

    //root.removeAllChildren();
    QueryTreeNode node;

      // sort by name
    Collections.sort(data, new Comparator(){
        public int compare(Object o1, Object o2) {
            Query q1 = (Query)o1;
            Query q2 = (Query)o2;
            if (q2 == null || q2.getName() == null) return -1;
            if (q1 == null || q1.getName() == null) return 1;
            return q1.getName().compareTo(q2.getName());
        }
    });

    if (root.getChildCount() == 0){
        Query folder = new Query();
        folder.setName(rb.getString("tree.folder.search.query"));

        folder.setId(-1L);
        folder.setQueryType(null);
        node = new QueryTreeNode(folder);
        root.add(node);
        for (Query q : data) {
            buildTreeFromNode(node, q);
        }
    }
    else{
        Enumeration treeNodes = root.breadthFirstEnumeration();
        while(treeNodes.hasMoreElements()){
            DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)treeNodes.nextElement();
            if (treeNode.getUserObject() instanceof Query){
                long qId = ((Query)treeNode.getUserObject()).getId();
                Query query = findQueryById(data, qId);
                // todo pe`khgnb`r|: eqkh me m`xkh rn m`bepmne m`dn sd`kr| h sgek b depebe
                if (query != null) {
                  boolean isChange = false;
                  if((((Query)treeNode.getUserObject()).getQueryStatus()).getValue() != query.getQueryStatus().getValue()){
                      ((Query)treeNode.getUserObject()).setQueryStatus(query.getQueryStatus());
                      isChange = true;
                  }
                  if (((Query)treeNode.getUserObject()).getPercent() != query.getPercent()){
                      ((Query)treeNode.getUserObject()).setPercent(query.getPercent());
                      isChange = true;
                  }
                  if(isChange) {
                    ((Query)treeNode.getUserObject()).setCompletedDate(query.getCompletedDate());
                    ((Query)treeNode.getUserObject()).setStartedDate(query.getStartedDate());
                    ((DefaultTreeModel)contents.getModel()).nodeChanged(treeNode);
                  }
               }
            }
        }
    }
    // todo nap`anr`r| b`ph`mr jncd` sd`khkh bepxhms ` nm` bundhk` b osr|
    if (path == null){
        contents.expandPath(new TreePath(new Object[] {root}));
    }
    else {
        contents.expandPath(path);
        contents.setExpandsSelectedPaths(true);
        contents.setSelectionPath(path);
        TreeSelectionEvent tse = new TreeSelectionEvent(contents, path, false, path, path);
        valueChanged(tse);
    }
  }

    /**
     * Onhqj sgk` b d`mm{u onkswemm{u q qepbep` m` bund ond`erq qrpsjrsp` h
     * hdemrhthj`rnp sgk` onkswemm{i hg depeb`
     * menaundhln oepedek`r| onkswemhe d`mm{u me b bhde qrpsjrsp{ ` wepeg HashMap
     * }rn sqjnphr bpel onhqj` j`j m` a`ge r`j h m` jkhemre.
     * @param data - qohqnj bepxhm Query bepumecn spnbm
     * @param id - hdemrhthj`rnp sgk` b depebe dk jnrnpncn onksw`el namnbkemhe
     * @return Query object or null if query objects not found
     */
    private Query findQueryById(List<Query> data, long id){
        for(Query q : data){
            if (q != null){
              if (q.getId() == id) return q;
              Query retQ = findQueryById(q.getChilds(), id);
              if (retQ != null) return retQ;
            }
        }
        return null;
    }
    /**
     *
     * @param node - root node for this branch
     * @param query - object connected as child
     */
    private void buildTreeFromNode(QueryTreeNode node, Query query){
        QueryTreeNode leaf = addNodeAsChild(node, query);
        List<Query> childs = query.getChilds();
        for (Query q : childs){
            buildTreeFromNode(leaf, q);
        }
    }

    public QueryTreeNode addNodeAsChild(DefaultMutableTreeNode parent, Query q){
        // eqkh parent = null rn menaundhln dna`bhr| sgek b jnpem| onk|gnb`rek|qjhu
        // g`opnqnb
        if (parent == null)
            parent = (DefaultMutableTreeNode)root.getChildAt(0);

        //q.setQueryStatus(QueryStatus.Saved);
        QueryTreeNode child = new ProfilePanel.QueryTreeNode(q);
        ((DefaultTreeModel)contents.getModel()).
            insertNodeInto(child,
                           (ProfilePanel.QueryTreeNode)parent,
                           parent.getChildCount());
        return child;
    }

    //stub
    public void clearQueryInfo() {
        ;
    }

  public void valueChanged(TreeSelectionEvent e) {
    TreePath[] paths = contents.getSelectionPaths();
    Query q = null;
    if (paths!=null && paths.length>0) {
      Object o = paths[0].getLastPathComponent();
      if (o instanceof QueryTreeNode) q = ((QueryTreeNode)o).getQuery();
    }

    if (q==null || q.getId() == -1) {
        openA.setEnabled(false);
        editA.setEnabled(false);
        removeA.setEnabled(false);
        executeA.setEnabled(false);
        copyA.setEnabled(false);
    }
    else {
        if (q.getQueryType().getId() == QueryType.QUEST){
            editA.setEnabled(true);
            removeA.setEnabled(true);
            executeA.setEnabled(false);
            openA.setEnabled(false);
            copyA.setEnabled(false);

        }
        else if (q.getQueryType().getId() == QueryType.REQUEST){
            editA.setEnabled(false);
            removeA.setEnabled(true);
            executeA.setEnabled(false);
            openA.setEnabled(false);
            copyA.setEnabled(false);

        }
        else if (q.getQueryType().getId() == QueryType.VISUALIZATION){
            editA.setEnabled(false);
            removeA.setEnabled(true);
            executeA.setEnabled(true);
            openA.setEnabled(true);
            copyA.setEnabled(true);

        }
        else{
            editA.setEnabled(false);
            removeA.setEnabled(false);
            executeA.setEnabled(false);
            openA.setEnabled(false);
            copyA.setEnabled(false);

        }
    }

    if (q!=null && q.getId() != -1) {
        QueryStatus s = q.getQueryStatus();
        if (s==null) s = QueryStatus.NotSaved;
        switch (s) {
            case NotSaved:
            case Executing:
            case Saved :
                executeA.setEnabled(false);
            case Error :
                openA.setEnabled(false);
                copyA.setEnabled(false);
        }
    }
  }

  public Query findSelectedQuery() {
    TreePath[] paths = contents.getSelectionPaths();
    if (paths.length==0) return null;
    QueryTreeNode node = (QueryTreeNode) paths[0].getLastPathComponent();
    return node.getQuery();
  }

  public static class QueryTreeNode extends DefaultMutableTreeNode{
    public QueryTreeNode(Query q) {
      setUserObject(q);
    }

    public Query getQuery() {
      return (Query) getUserObject();
    }

    public String toString() {
      String progress = "";
        try {
            if (getQuery().getQueryType().getId() == QueryType.VISUALIZATION) {
                int percent = (int) getQuery().getPercent();
                progress = percent + "";
                for (int i = progress.length(); i < 3; i++) {
                    progress = " " + progress;
                }
                progress += "% ";
                if (getQuery().getId() <= 0) progress = "";
            }
        }
        catch (Exception e) {
            ;
        }
        return progress + getQuery().getName();
    }
  }

  private class RefreshAction extends AbstractAction {
    public RefreshAction() {
      super(rb.getString("ProfilePanel.refresh.name"));
      putValue(SHORT_DESCRIPTION, rb.getString("ProfilePanel.refresh.toolTip"));
    }

    public void actionPerformed(ActionEvent e) {
      controller.refresh();
    }
  }

    private class ExecuteAction extends AbstractAction {
      public ExecuteAction() {
        super(rb.getString("ProfilePanel.execute.name"));
        putValue(SHORT_DESCRIPTION, rb.getString("ProfilePanel.execute.toolTip"));
        setEnabled(false);
      }

      public void actionPerformed(ActionEvent e) {
        Query q = findSelectedQuery();
        if (q!=null) controller.execute(q);
      }
    }

  public QueryType getQueryTypeById(Long id){
      return controller.getQueryTypeById(id);
  }
}
