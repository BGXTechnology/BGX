package net.bgx.bgxnetwork.bgxop.gui.lv.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.bgx.bgxnetwork.bgxop.gui.QueryController;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.gui.SelectableFormattedField;
import net.bgx.bgxnetwork.bgxop.gui.background.lv.CreateTDThread;
import net.bgx.bgxnetwork.bgxop.gui.calendar.CalendarDialog;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.ControlObjectPair;
import net.bgx.bgxnetwork.bgxop.gui.lv.model.TimedDiagramTableModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TimeDiagram;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.TDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.Util;
import net.bgx.bgxnetwork.bgxop.gui.lv.dlg.WaitingDialog;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.tools.lv.LinkWorker;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.uitools.WaitDialog;
import net.bgx.bgxnetwork.bgxop.uitools.UITools;
import net.bgx.bgxnetwork.bgxop.services.audit.AuditManager;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.NodePK;
import net.bgx.bgxnetwork.transfer.tt.*;
import net.bgx.bgxnetwork.transfer.data.LVObject;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import oracle.spatial.network.Node;
import oracle.spatial.network.Link;
import oracle.spatial.network.Network;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Vertex;

public class TimedDiagramParameterPanel extends JPanel {
    private TestDataTDModel tdModel = null;
    private ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private ResourceBundle rb_uitools = PropertyResourceBundle.getBundle("uitools");
    private ResourceBundle grb = PropertyResourceBundle.getBundle("gui");
    private JTable researchObjectLinkTable = new JTable();
    private JButton addPairButton = new JButton();
    private JButton removePairButton = new JButton();
    private JButton showLinkDiagramButton = new JButton();
    private JButton upButton = new JButton();
    private JButton downButton = new JButton();
    private JPanel topPanel = new JPanel();
    private SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    private SelectableFormattedField dateFrom = new SelectableFormattedField(df);
    private SelectableFormattedField dateTo = new SelectableFormattedField(df);
    private JButton fromCalendarButton = new JButton();
    private JButton toCalendarButton = new JButton();
    private JButton fromCalendarClearButton = new JButton();
    private JButton toCalendarClearButton = new JButton();
    private TimedDiagramTableModel model;
    private DialogAction da = new DialogAction();
    private TimeDiagram timeDiagram;
    private JSplitPane split = new JSplitPane();
    private QueryController queryController;
    private JPanel centerPanel = new JPanel();
    private String showPairErrorMessage = rb.getString("TimedDiagramParameterPanel.error.showPairErrorMessage");
    private String showPairTimeErrorMessage = rb.getString("TimedDiagramParameterPanel.error.showPairTimeErrorMessage");
    private String linksLimitMessage = rb.getString("TimedDiagramParameterPanel.error.linksLimitMessage");
    private ArrayList<ControlObjectPair> objects = new ArrayList<ControlObjectPair>();
    private int MAX_SIZE_PAIR_TABLE = 20;
    // default links limit
    private int linksLimit = 1000;

    public TimedDiagramParameterPanel(QueryController query) {
        this.queryController = query;
        parseLinksLimit();
        initPanel();
    }

    private void parseLinksLimit() {
        String linksLimitString = grb.getString("TimeDiagram.links.limit");
        try {
            linksLimit = Integer.parseInt(linksLimitString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPanel() {
        setLayout(new BorderLayout());
        String title = rb.getString("TimedDiagramParameterPanel.title");
        setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.CENTER));
        split.setOrientation(JSplitPane.VERTICAL_SPLIT);
        add(split);
        split.setTopComponent(getTopPanel());
        split.setBottomComponent(getCenterPanel());
        split.setOneTouchExpandable(true);
        GlobalPopupUtil.initListeners(this);
        setSize(new Dimension(800, 600));
        createData();
    }

    private Component getCenterPanel() {
        centerPanel.setLayout(new BorderLayout());
        String title = rb.getString("TimedDiagramParameterPanel.diagramPanel.title");
        centerPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER));
        centerPanel.add(getDiagramPanel(), BorderLayout.CENTER);
        centerPanel.add(getDiagramButtonPanel(), BorderLayout.EAST);
        return centerPanel;
    }

    private Component getDiagramButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JPanel underButtonPanel = new JPanel();
        underButtonPanel.add(buttonPanel);
        buttonPanel.setLayout(new GridLayout(0, 1));
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        JPanel underUpButtonPanel = new JPanel();
        underUpButtonPanel.add(upButton);
        underUpButtonPanel.setOpaque(false);
        buttonPanel.add(underUpButtonPanel);
        upButton.setPreferredSize(new Dimension(70, 24));
        downButton.setPreferredSize(new Dimension(70, 24));
        JPanel underDownButtonPanel = new JPanel();
        underDownButtonPanel.setOpaque(false);
        underDownButtonPanel.add(downButton);
        buttonPanel.add(underDownButtonPanel);
        upButton.addActionListener(da);
        downButton.addActionListener(da);
        upButton.setText(rb.getString("TimedDiagramParameterPanel.button.upButton"));
        downButton.setText(rb.getString("TimedDiagramParameterPanel.button.downButton"));
        return underButtonPanel;
    }

    private Component getDiagramPanel() {
        timeDiagram = new TimeDiagram();
        timeDiagram.addActionListener(da);
        timeDiagram.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean upButtonEnabled = false;
                boolean downButtonEnabled = false;
                int upperPairIndex = 0;
                int lowerPairIndex = 0;
                List<TDPair> selectedPairs = timeDiagram.getSelectedPairs();
                if (selectedPairs == null) {
                    upButtonEnabled = false;
                    downButtonEnabled = false;
                } else {
                    TDModel model = timeDiagram.getModel();
                    upperPairIndex = model.getIndexOf(selectedPairs.get(0));
                    lowerPairIndex = model.getIndexOf(selectedPairs.get(selectedPairs.size() - 1));
                    upButtonEnabled = upperPairIndex > 0 ? true : false;
                    downButtonEnabled = lowerPairIndex < model.getPairsCount() - 1 ? true : false;
                }
                upButton.setEnabled(upButtonEnabled);
                downButton.setEnabled(downButtonEnabled);
            }
        });
        return timeDiagram;
    }

    private Component getTopPanel() {
        topPanel.setLayout(new BorderLayout());

        topPanel.add(getIntervalPanel(), BorderLayout.WEST);
        topPanel.add(getTablePanel(), BorderLayout.CENTER);
        topPanel.setPreferredSize(new Dimension(800, 150));
        return topPanel;
    }

    private Component getTablePanel() {
        String header = rb.getString("TimedDiagramParameterPanel.table.header");
        StringTokenizer st = new StringTokenizer(header, ",");
        int count = st.countTokens();
        Object[] headerArray = new Object[count + 1];
        headerArray[0] = "";
        for (int i = 1; i <= count; i++) {
            headerArray[i] = st.nextElement();
        }
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        JScrollPane scroll = new JScrollPane();
        scroll.getViewport().add(researchObjectLinkTable);
        model = new TimedDiagramTableModel(headerArray, researchObjectLinkTable);
        researchObjectLinkTable.setModel(model);
        researchObjectLinkTable.getColumn(headerArray[0]).setMaxWidth(24);
        tablePanel.add(scroll, BorderLayout.CENTER);
        tablePanel.add(getTableButtonPanel(), BorderLayout.EAST);
        String title = rb.getString("TimedDiagramParameterPanel.tablePanel.title");
        tablePanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER));
        return tablePanel;
    }

    private void createData() {
        if (objects.size() == 0) {
            for (Node node : queryController.getData().getNetwork().getNodeArray()) {
                if (node == null)
                    continue;
                ControlObject co = GraphNetworkUtil.getControlObject(node);
                Set<ArchetypeVertex> set = GraphNetworkUtil.getVertex(node).getNeighbors();
                Set<ControlObject> o = createControlObjectSet(set);
                ControlObjectPair cop = new ControlObjectPair();
                if (co == null)
                    continue;
                cop.setControlObject(co);
                cop.setNeighbors(o);
                objects.add(cop);
            }
        }
        //  model.setControlObjects(objects);
    }

    private Set<ControlObject> createControlObjectSet(Set<ArchetypeVertex> set) {
        Set<ControlObject> out = new HashSet<ControlObject>();
        for (ArchetypeVertex v : set) {
            out.add(GraphNetworkUtil.getControlObject((Vertex) v));
        }
        return out;
    }

    private Component getTableButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        JPanel underButtonPanel = new JPanel();
        underButtonPanel.setOpaque(false);
        underButtonPanel.add(buttonPanel);
        buttonPanel.setLayout(new GridLayout(0, 1));
        addPairButton.setPreferredSize(new Dimension(115, 24));
        removePairButton.setPreferredSize(new Dimension(115, 24));
        buttonPanel.add(getUnderPanel(BorderLayout.NORTH, addPairButton, removePairButton));
        // buttonPanel.add(getUnderPanel(removePairButton));
        buttonPanel.add(getUnderPanel(BorderLayout.SOUTH, showLinkDiagramButton));
        addPairButton.addActionListener(da);
        showLinkDiagramButton.addActionListener(da);
        removePairButton.addActionListener(da);
        addPairButton.setText(rb.getString("TimedDiagramParameterPanel.button.addPairButton"));
        showLinkDiagramButton.setText(rb.getString("TimedDiagramParameterPanel.button.showLinkDiagramButton"));
        removePairButton.setText(rb.getString("TimedDiagramParameterPanel.button.removePairButton"));
        return underButtonPanel;
    }

    private Component getUnderPanel(String layout, Component... components) {
        JPanel out = new JPanel();
        out.setOpaque(false);
        out.setLayout(new BorderLayout());
        JPanel componentsPanel = new JPanel();
        componentsPanel.setOpaque(false);
        componentsPanel.setLayout(new GridLayout(0, 1));
        componentsPanel.setAlignmentX(0f);
        componentsPanel.setAlignmentY(0f);
        out.add(componentsPanel, layout);
        for (Component c : components) {
            componentsPanel.add(getUnderPanel(c));
        }
        return out;
    }

    private Component getUnderPanel(Component button) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.add(button);
        panel.setAlignmentX(0f);
        panel.setAlignmentY(0f);
        // panel.setPreferredSize(button.getPreferredSize());
        return panel;
    }

    private Component getIntervalPanel() {
        JPanel intervalPanel = new JPanel();
        intervalPanel.setOpaque(false);
        String title = rb.getString("TimedDiagramParameterPanel.intervalPanel.title");
        intervalPanel.setBorder(new LineBorder(Color.LIGHT_GRAY));
        dateFrom.setEditable(false);
        dateFrom.setPreferredSize(new Dimension(80, 22));
        dateTo.setEditable(false);
        dateTo.setPreferredSize(new Dimension(80, 22));
        intervalPanel.setLayout(new GridLayout(0, 1));
        JLabel fromLabel = new JLabel();
        fromLabel.setPreferredSize(new Dimension(22, 22));
        fromLabel.setText(rb.getString("TimedDiagramParameterPanel.label.fromLabel"));
        JLabel toLabel = new JLabel();
        toLabel.setText(rb.getString("TimedDiagramParameterPanel.label.toLabel"));
        toLabel.setPreferredSize(new Dimension(22, 22));
        intervalPanel.add(getDataPanel(fromLabel, dateFrom, fromCalendarButton, fromCalendarClearButton));
        intervalPanel.add(getDataPanel(toLabel, dateTo, toCalendarButton, toCalendarClearButton));
        fromCalendarButton.addMouseListener(new DialogMouseAdapter());
        toCalendarButton.addMouseListener(new DialogMouseAdapter());
        fromCalendarButton.setIcon(ResourceLoader.getInstance().getIconByResource(rb, "TimedDiagramParameterPanel.icon.calendar"));
        toCalendarButton.setIcon(ResourceLoader.getInstance().getIconByResource(rb, "TimedDiagramParameterPanel.icon.calendar"));
        toCalendarClearButton.setIcon(ResourceLoader.getInstance().getIconByResource(rb, "TimedDiagramParameterPanel.icon.clear"));
        fromCalendarClearButton.setIcon(ResourceLoader.getInstance().getIconByResource(rb, "TimedDiagramParameterPanel.icon.clear"));
        fromCalendarButton.setMargin(new Insets(0, 0, 0, 0));
        toCalendarButton.setMargin(new Insets(0, 0, 0, 0));
        fromCalendarClearButton.setMargin(new Insets(0, 0, 0, 0));
        toCalendarClearButton.setMargin(new Insets(0, 0, 0, 0));
        JPanel underIntervalPanel = new JPanel();
        underIntervalPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.LIGHT_GRAY), title, TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.CENTER));
        underIntervalPanel.add(intervalPanel);
        return underIntervalPanel;
    }

    private Component getDataPanel(JComponent... components) {
        JPanel dp = new JPanel();
        dp.setOpaque(false);
        for (JComponent comp : components) {
            if (comp instanceof JButton) {
                ((JButton) comp).addActionListener(da);
            }
            dp.add(comp);
        }
        return dp;
    }

    private class DialogMouseAdapter extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            // System.out.println(e.getSource());
        }

        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub
        }
    }

    private class DialogAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if (source instanceof TDLink) {
                LinkObject linkObject = ((TDLink) source).getObject();
                Network network = queryController.getView().getData().getNetwork();
                JDialog dlg = null;
                Link selectLink = null;
                for (Link link : network.getLinkArray()) {
                    LinkObject networkLinkObject = GraphNetworkUtil.getLinkObject(link);
                    if (networkLinkObject == null) continue;
                    if (networkLinkObject.getPk().equals(linkObject.getPk())) {
                        selectLink = link;
                        dlg = GraphNetworkUtil.getCard(link);
                        if (dlg != null)
                            dlg.setVisible(true);
                        break;
                    }
                }
                if (dlg == null) {
                    CardLinkDialogPanel cardLinkDialogPanel = new CardLinkDialogPanel(queryController.getOwner(), linkObject);
                    if (selectLink != null) {
                        GraphNetworkUtil.setCard(selectLink, cardLinkDialogPanel);
                        queryController.getView().addDialogs(cardLinkDialogPanel);
                    }
                    cardLinkDialogPanel.setVisible(true);
                    try {
                        AuditManager.getInstance().addOpenCardLinkEvent(queryController.getQuery().getId(),
                                linkObject.getLinkType().getNameObjectType());
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            } else if (source instanceof TDObject) {
                ControlObject conrolObject = ((TDObject) source).getObject();
                Network network = queryController.getView().getData().getNetwork();
                JDialog dlg = null;
                Node selectNode = null;
                for (Node node : network.getNodeArray()) {
                    ControlObject nodeControlObject = GraphNetworkUtil.getControlObject(node);
                    if (nodeControlObject == null) continue;
                    if (nodeControlObject.getPk().equals(conrolObject.getPk())) {
                        selectNode = node;
                        dlg = GraphNetworkUtil.getCard(node);
                        if (dlg != null)
                            dlg.setVisible(true);
                        break;
                    }
                }
                if (dlg == null) {
                    ObjectCardDialogPanel objectCardDialogPanel = new ObjectCardDialogPanel(queryController.getOwner(), conrolObject);
                    if (selectNode != null) {
                        GraphNetworkUtil.setCard(selectNode, objectCardDialogPanel);
                        queryController.getView().addDialogs(objectCardDialogPanel);
                        try {
                            AuditManager.getInstance().addOpenCardObjectEvent(queryController.getQuery().getId(),
                                GraphNetworkUtil.getName(GraphNetworkUtil.getVertex(selectNode)));
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                    objectCardDialogPanel.setVisible(true);
                }
            } else if (source instanceof ArrayList) {
                ArrayList<LinkObject> links = new ArrayList<LinkObject>();
                ControlObject first = null, second = null;
                for (Object obj : (ArrayList) source) {
                    if (obj instanceof TDLink) {
                        links.add(((TDLink) obj).getObject());
                        first = ((TDLink) obj).getInitiator().getObject();
                        second = ((TDLink) obj).getPair().getObjectOne().getObject().equals(first) ? ((TDLink) obj).getPair().getObjectTwo()
                                .getObject() : ((TDLink) obj).getPair().getObjectOne().getObject();
                    }
                }

                SimpleIntegrateLinkDialogPanel dialog = new SimpleIntegrateLinkDialogPanel(queryController.getOwner(), links, LVGraphNetworkUtil
                        .getSimpleName(first), LVGraphNetworkUtil.getSimpleName(second), queryController.getQuery().getId());
                queryController.getView().addDialogs(dialog);
                try {
                    AuditManager.getInstance().addOpenCardIntegrationLinkEvent(queryController.getQuery().getId(),
                            "AC" );
                } catch (Exception e1) {
                    e1.printStackTrace(); 
                }
                dialog.setVisible(true);
            } else if (source == upButton) {
                List<TDPair> selectedPairs = timeDiagram.getSelectedPairs();
                if (selectedPairs != null) {
                    int index = timeDiagram.getModel().getIndexOf(selectedPairs.get(0));
                    timeDiagram.getModel().movePairs(selectedPairs, index - 1);
                    timeDiagram.setSelectedPairs(selectedPairs);
                }
            } else if (source == downButton) {
                List<TDPair> selectedPairs = timeDiagram.getSelectedPairs();
                if (selectedPairs != null) {
                    int index = timeDiagram.getModel().getIndexOf(selectedPairs.get(selectedPairs.size() - 1));
                    if (selectedPairs.size() == 1) {
                        index++;
                    }
                    timeDiagram.getModel().movePairs(selectedPairs, index);
                    timeDiagram.setSelectedPairs(selectedPairs);
                }
            } else if (source == fromCalendarButton) {
                Point point = fromCalendarButton.getLocationOnScreen();
                CalendarDialog cd = new CalendarDialog(queryController.getOwner(), point, dateFrom);
            } else if (source == toCalendarButton) {
                Point point = toCalendarButton.getLocationOnScreen();
                CalendarDialog cd = new CalendarDialog(queryController.getOwner(), point, dateTo);
            } else if (source == fromCalendarClearButton) {
                dateFrom.setEditable(true);
                dateFrom.setText("");
                dateFrom.setValue(null);
                dateFrom.setEditable(false);
            } else if (source == toCalendarClearButton) {
                dateTo.setEditable(true);
                dateTo.setText("");
                dateTo.setValue(null);
                dateTo.setEditable(false);
            } else if (source == addPairButton) {
                if (model.getTableData().size() < MAX_SIZE_PAIR_TABLE) {
                    createData();
                    AddPairDialog ad = new AddPairDialog(queryController.getOwner(), objects, model);
                    ad.setModal(true);
                    ad.setVisible(true);
                    //  model.addRow(objects);
                } else {
                    MessageDialogs.warning(queryController.getOwner(),
                            rb.getString("TimedDiagramParameterPanel.error.OutboundSize") +
                                    " (" + MAX_SIZE_PAIR_TABLE + ")",
                            rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                    return;
                }

            } else if (source == removePairButton) {
                model.removeCheckedRows();
            } else if (source == showLinkDiagramButton) {
                WaitingDialog dialog =
                        new WaitingDialog(researchObjectLinkTable, rb.getString("TimedDiagramParameterPanel.wait.msg"),
                                rb.getString("TimedDiagramParameterPanel.wait.msg"),
                                null, null,
                                UITools.loadIcon(rb_uitools.getString("MessageDialogs.info.img"), null), null);
                dialog.setResizable(false);
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                    }
                });
                dialog.setControl(showLinkDiagramButton);
                queryController.getView().addDialogs(dialog);

                Date startDate, finishDate = null;
                try {
                    startDate = (Date) dateFrom.getValue();
                    finishDate = (Date) dateTo.getValue();
                    if (startDate == null || finishDate == null) {
                        MessageDialogs.warning(queryController.getOwner(), rb.getString("TimedDiagramParameterPanel.error.date"),
                                rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                        return;
                    }
                    if (startDate.getTime() > finishDate.getTime()) {
                        MessageDialogs.warning(queryController.getOwner(), rb.getString("TimedDiagramParameterPanel.error.wrongIntervalDate"),
                                rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                        return;
                    }
                } catch (Exception e1) {
                    MessageDialogs.warning(queryController.getOwner(), rb.getString("TimedDiagramParameterPanel.error.date"),
                            rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                    return;
                }
                LinkedList<TDPair> pairs = getData(startDate, finishDate);
                if (pairs == null || pairs.size() == 0) {
                    MessageDialogs.warning(queryController.getOwner(), showPairErrorMessage,
                            rb.getString("TimedDiagramParameterPanel.error.ATTENTION"));
                    return;
                }
                int linksCount = 0;
                List<TDLink> links = null;
                for (TDPair pair : pairs) {
                    links = pair.getLinks();
                    if (links != null) {
                        linksCount += links.size();
                    }
                }
                boolean answer = true;
                if (linksCount > linksLimit) {
                    answer = MessageDialogs.choose(queryController.getOwner(), linksLimitMessage,
                            rb.getString("TimedDiagramParameterPanel.error.ATTENTION"),
                            rb.getString("TimedDiagramParameterPanel.error.YES"),
                            rb.getString("TimedDiagramParameterPanel.error.NO"));
                }
                if (answer) {
                    dialog.setVisible(true);
                    showLinkDiagramButton.setEnabled(false);
                    CreateTDThread thread = new CreateTDThread(dialog, tdModel, timeDiagram, pairs, queryController.getView());
                    thread.start();
                }
            }
        }

        private void checkCorrectedDate(Long dateFromLong, Long dateToLong) throws Exception {
            if (dateFromLong >= dateToLong) {
                throw new Exception();
            }
            // TODO Auto-generated method stub
        }
    }

    private LinkedList<TDPair> getData(Date startIntervalDate, Date finishIntervalDate) {
        LinkedList<TDPair> data = new LinkedList<TDPair>();
        ArrayList<ControlObjectPair> pairs = model.getSelectedData();
        // LVObject linkVisibleProperty =
        // Util.getVisibleFieldsForLink(queryController.getQuery().getViewedAttributes());
        for (ControlObjectPair coPair : pairs) {
            ControlObject firstObject = coPair.getControlObject();
            ControlObject secondObject = coPair.getLinkedObject();
            TDObject fObject = new TestDataTDObject(LVGraphNetworkUtil.getSimpleName(firstObject), firstObject);
            TDObject sObject = new TestDataTDObject(LVGraphNetworkUtil.getSimpleName(secondObject), secondObject);
            TestDataTDPair tdPair = new TestDataTDPair(fObject, sObject);
            List<TDLink> links = new ArrayList<TDLink>();
            for (Node node : queryController.getData().getNetwork().getNodeArray()) {
                ControlObject co = GraphNetworkUtil.getControlObject(node);
                if (co.equals(firstObject)) {
                    if (node.getInLinks() != null)
                        for (Link link : node.getInLinks()) {
                            Node node2 = link.getStartNode();
                            ControlObject co2 = GraphNetworkUtil.getControlObject(node2);
                            if (co2.equals(secondObject)) {
                                LinkObject linkObject = GraphNetworkUtil.getLinkObject(link);
                                // ArrayList<FieldObject> attributes =
                                // getAttributes(linkObject,
                                // linkVisibleProperty);
                                long timestamp = Util.getTimeStampByDate(linkObject);
                                if (dateInInterval(timestamp, startIntervalDate, finishIntervalDate)) {
                                    TestDataTDLink tdLink = new TestDataTDLink(linkObject, sObject, tdPair, timestamp);
                                    links.add(tdLink);
                                }
                            }
                        }
                    if (node.getOutLinks() != null)
                        for (Link link : node.getOutLinks()) {
                            Node node2 = link.getEndNode();
                            ControlObject co2 = GraphNetworkUtil.getControlObject(node2);
                            if (co2.equals(secondObject)) {
                                LinkObject linkObject = GraphNetworkUtil.getLinkObject(link);
                                // ArrayList<FieldObject> attributes =
                                // getAttributes(linkObject,
                                // linkVisibleProperty);
                                long timestamp = Util.getTimeStampByDate(linkObject);
                                if (dateInInterval(timestamp, startIntervalDate, finishIntervalDate)) {
                                    TestDataTDLink tdLink = new TestDataTDLink(linkObject, fObject, tdPair, timestamp);
                                    links.add(tdLink);
                                }
                            }
                        }
                    break;
                } else if (co.equals(secondObject)) {
                    if (node.getInLinks() != null)
                        for (Link link : node.getInLinks()) {
                            Node node2 = link.getStartNode();
                            ControlObject co2 = GraphNetworkUtil.getControlObject(node2);
                            if (co2.equals(firstObject)) {
                                LinkObject linkObject = GraphNetworkUtil.getLinkObject(link);
                                // ArrayList<FieldObject> attributes =
                                // getAttributes(linkObject,
                                // linkVisibleProperty);
                                long timestamp = Util.getTimeStampByDate(linkObject);
                                if (dateInInterval(timestamp, startIntervalDate, finishIntervalDate)) {
                                    TestDataTDLink tdLink = new TestDataTDLink(linkObject, fObject, tdPair, timestamp);
                                    links.add(tdLink);
                                }
                            }
                        }
                    if (node.getOutLinks() != null)
                        for (Link link : node.getOutLinks()) {
                            Node node2 = link.getEndNode();
                            ControlObject co2 = GraphNetworkUtil.getControlObject(node2);
                            if (co2.equals(firstObject)) {
                                LinkObject linkObject = GraphNetworkUtil.getLinkObject(link);
                                // ArrayList<FieldObject> attributes =
                                // getAttributes(linkObject,
                                // linkVisibleProperty);
                                long timestamp = Util.getTimeStampByDate(linkObject);
                                if (dateInInterval(timestamp, startIntervalDate, finishIntervalDate)) {
                                    TestDataTDLink tdLink = new TestDataTDLink(linkObject, sObject, tdPair, timestamp);
                                    links.add(tdLink);
                                }
                            }
                        }
                    break;
                }
            }
            tdPair.setLinks(links);
            data.add(tdPair);
        }
        // data complete
        return data;
    }

    private ArrayList<FieldObject> getAttributes(LinkObject linkObject, LVObject linkVisibleProperty) {
        ArrayList<FieldObject> attributes = new ArrayList<FieldObject>();
        LinkWorker worker = new LinkWorker(linkObject);
        for (FieldObject fieldObject : linkVisibleProperty.getFields()) {
            if (fieldObject == null)
                continue;
            if (!fieldObject.isVisible())
                continue;
            fieldObject.setValue(worker.getValueByPropertyCode(fieldObject.getCode()));
            attributes.add(fieldObject);
        }
        return attributes;
    }

    private long getTimeStampByDate(ArrayList<FieldObject> fields) {
        for (FieldObject field : fields) {
            if (field.getCode() != null && 14 == field.getCode().intValue()) {
                String str = (String) field.getValue();
                Date dt = FieldObject.convertValueToDate(str);
                if (dt == null)
                    return 0L;
                return dt.getTime();
            }
        }
        return 0L;
    }

    public void setSnapshot(TimedDiagrammDataSnapshot snapshot) {
        dateFrom.setText(getStringFromDate(snapshot.getDateFrom()));
        dateFrom.setValue(new Date(snapshot.getDateFrom()));
        dateTo.setText(getStringFromDate(snapshot.getDateTo()));
        dateTo.setValue(new Date(snapshot.getDateTo()));

        createData();

        ArrayList<TransferControlObjectPair> transferPairs = snapshot.getPairs();
        ArrayList<ControlObjectPair> pairs = new ArrayList<ControlObjectPair>();
        for (TransferControlObjectPair transferPair : transferPairs) {
            Long qId = transferPair.getQueryId();
            Long objId = transferPair.getObjectId();
            Long lnkId = transferPair.getLinkedId();
            NodePK firstObjectPK = new NodePK(objId, qId);
            NodePK secondObjectPK = new NodePK(lnkId, qId);

            ControlObjectPair pair = new ControlObjectPair();

            for (ControlObjectPair cop : objects) {
                ControlObject co = cop.getControlObject();
                if (co == null) continue;
                if (co.getPk().equals(firstObjectPK)) {
                    pair.setControlObject(co);
                    pair.setNeighbors(cop.getNeighbors());
                }
                if (co.getPk().equals(secondObjectPK)) {
                    pair.setLinkedObject(co);
                }
            }

//            pair.setControlObject(transferPair.getControlObject());
//            pair.setLinkedObject(transferPair.getLinkedObject());
//            pair.setNeighbors(transferPair.getNeighbors());
            pair.setSelected(transferPair.isSelected());
            pairs.add(pair);
        }

        model.setSavedData(pairs);

    }

    public TimedDiagrammDataSnapshot getSnapshot() {
        TimedDiagrammDataSnapshot out = new TimedDiagrammDataSnapshot();
        ArrayList<ControlObjectPair> pairs = model.getTableData();
        ArrayList<TransferControlObjectPair> transferPairs = new ArrayList<TransferControlObjectPair>();
        for (ControlObjectPair pair : pairs) {
            TransferControlObjectPair transferPair = new TransferControlObjectPair();
//            transferPair.setControlObject(pair.getControlObject());
//            transferPair.setLinkedObject(pair.getLinkedObject());
            transferPair.setObjectId(pair.getControlObject().getPk().getNodeId());
            transferPair.setLinkedId(pair.getLinkedObject().getPk().getNodeId());
            transferPair.setQueryId(pair.getControlObject().getPk().getQueryId());
//            transferPair.setNeighbors(pair.getNeighbors());
            transferPair.setSelected(pair.isSelected());
            transferPairs.add(transferPair);
        }
        out.setPairs(transferPairs);
        out.setDateFrom(getDateFromString(dateFrom.getText()));
        out.setDateTo(getDateFromString(dateTo.getText()));

        return out;
    }

    private boolean dateInInterval(Long dateTimestamp, Date intervalStart, Date intervalEnd) {
        return (intervalStart.getTime() <= dateTimestamp && dateTimestamp <= intervalEnd.getTime());
    }

    private String getStringFromDate(Long time) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(new Date(time));
    }

    private Long getDateFromString(String dt) {
        Date date = FieldObject.convertValueToDate(dt);
        return date.getTime();
    }

    public TimeDiagram getTimeDiagram() {
        return timeDiagram;
    }
}
