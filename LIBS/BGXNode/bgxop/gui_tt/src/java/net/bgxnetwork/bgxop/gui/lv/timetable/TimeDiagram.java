/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TimeDiagram.java#4 $
$DateTime: 2007/07/05 16:46:42 $
$Change: 19428 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.transfer.tt.TDLink;
import net.bgx.bgxnetwork.transfer.tt.TDObject;
import net.bgx.bgxnetwork.transfer.tt.TDPair;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class TimeDiagram extends JPanel implements ChangeListener, MouseListener {
    private JPopupMenu popup = null;
    private JScrollPane scrollPane = null;
    private JTable table = null;
    private TDTableModel tableModel = null;
    private TDCellRenderer renderer = null;
    private JScrollBar scrollBar = null;
    private TDModel diagramModel = null;
    private TDTimeline timeline = null;
    private int layoutMaxWidth = 0;
    private JPanel spacerComponentLeft = null;
    private JPanel spacerComponentRight = null;
    private EventListenerList actionListenerList = null;
    private ActionEvent actionEvent = null;
    private Component timelineComponent = null;
    private JMenuItem menuItem = null;
    private Object latestEventSource = null;
    private String menuItemObjectText = null;
    private String menuItemLinkText = null;
    private ResourceBundle rb = null;
    private ListSelectionEvent listSelectionEvent = null;
    private EventListenerList selectionListenerList = null;
    private final int LABEL_TDTIMELINE_CONSTANT = 100;
    private int scrollPosition = 0;

    public TimeDiagram() {
        rb = PropertyResourceBundle.getBundle("gui");
        menuItemObjectText = rb.getString("Action.name.card.object");
        menuItemLinkText = rb.getString("Action.name.card.link");
        GridBagConstraints gridBagConstraints = null;
        actionListenerList = new EventListenerList();
        selectionListenerList = new EventListenerList();
        //
        Dimension spacersSize = new Dimension(TDConstants.TABLE_TEXT_COLUMNS_WIDTH, TDConstants.TIMELINE_HEIGHT);
        spacerComponentLeft = new JPanel();
        spacerComponentRight = new JPanel();
        spacerComponentLeft.setMinimumSize(spacersSize);
        spacerComponentRight.setMinimumSize(spacersSize);
        //
        renderer = new TDCellRenderer();
        tableModel = new TDTableModel(diagramModel);
        timeline = new TDTimeline();
        //
        table = new JTable(tableModel);
        //
        table.setDefaultRenderer(TDObject.class, renderer);
        table.setDefaultRenderer(VisualizationModel.class, renderer);
        table.setTableHeader(null);
        table.setRowHeight(TDConstants.LAYOUT_HEIGHT);
        table.getColumnModel().getColumn(0).setMinWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(0).setMaxWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(2).setMinWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(2).setMaxWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.addMouseListener(this);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        //
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                listSelectionEvent = e;
                fireSelectionChanged();
            }
        });
        //
        scrollPane = new JScrollPane();
        scrollPane.setViewportView(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        //
        table.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                configScrollbar();
                setupTimeline();
            }
        });
        computeMaxLayoutWidth();
        // horizontal scroll bar
        scrollBar = new JScrollBar(Adjustable.HORIZONTAL);
        scrollBar.setMinimum(0);
        scrollBar.setMaximum(layoutMaxWidth);
        scrollBar.setUnitIncrement(5);
        scrollBar.setBlockIncrement(10);
        //
        scrollBar.addAdjustmentListener(new AdjustmentListener() {
            public void adjustmentValueChanged(AdjustmentEvent e) {
                renderer.scroll(scrollPosition-e.getValue(), 0);
                timeline.scroll(scrollPosition-e.getValue(), 0);
                scrollPosition = e.getValue();
                repaint();
            }
        });
        //
        setLayout(new GridBagLayout());
        //
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
        //
        menuItem = new JMenuItem("menu item");
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fireActionPerformed(latestEventSource);
                latestEventSource = null;
            }
        });
        popup = new JPopupMenu();
        popup.add(menuItem);
    }
    //
    //
    // public part
    //
    //
    public TDModel getModel(){
        return diagramModel;
    }
    public void setModel(TDModel model) {
        if (diagramModel != null) {
            diagramModel.removeChangeListener(this);
        }
        this.diagramModel = model;
        if (diagramModel != null) {
            model.addChangeListener(this);
        }
//        renderer.scroll(-scrollPosition, 0);
//        timeline.scroll(-scrollPosition, 0);
//        scrollPosition = 0;
        scrollBar.setValue(0);
        tableModel = new TDTableModel(model);
        table.setModel(tableModel);
        // text columns width
        table.getColumnModel().getColumn(0).setMinWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(0).setMaxWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(2).setMinWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        table.getColumnModel().getColumn(2).setMaxWidth(TDConstants.TABLE_TEXT_COLUMNS_WIDTH);
        //
        if (diagramModel.getMaximum() == 0 && diagramModel.getMinimum() == 0)
            MessageDialogs.info(table,  rb.getString("TTzeroResult.msg"), rb.getString("TTzeroResult.title"));
        computeMaxLayoutWidth();
        configScrollbar();
        setupTimeline();
    }
    public List<TDPair> getSelectedPairs() {
        List<TDPair> pairs = null;
        int[] rows = table.getSelectedRows();
        if (rows.length > 0) {
            pairs = new ArrayList<TDPair>();
            for (int index : rows) {
                pairs.add(diagramModel.getPairAt(index));
            }
        }
        return pairs;
    }
    public void setSelectedPairs(List<TDPair> pairs) {
        TDPair p = pairs.get(0);
        TDPair p2 = pairs.get(pairs.size()-1);
        if(p != null) {
            table.setRowSelectionInterval(diagramModel.getIndexOf(p), diagramModel.getIndexOf(p2));
        }
    }
    public BufferedImage getImage() {
        JComponent rendererComponent = null;
        Rectangle componentBounds = null;
        Rectangle  screenshotBounds = null;
        Graphics2D clip = null;

        int objectCellWidth = table.getColumnModel().getColumn(0).getWidth();
        int imageWidth = objectCellWidth * 2 + layoutMaxWidth;
        int imageHeight = TDConstants.LAYOUT_HEIGHT * tableModel.getRowCount() + TDConstants.TIMELINE_HEIGHT;
        int rows = tableModel.getRowCount();

        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);

        int offsetX = 0;
        int offsetY = 0;
        int w = 0;
        boolean doubleBufferedFlag = false;
        boolean opaqueFlag = false;

        double scrollPosition = renderer.getScrollPosition();
        renderer.scroll(scrollPosition, 0);

        for (int row=0; row < rows; row++) {
            offsetX = 0;

            for (int column=0; column < 3; column++) {
                rendererComponent = (JComponent) renderer.getTableCellRendererComponent(table, tableModel.getValueAt(row, column), false, false, row, column);
                componentBounds = rendererComponent.getBounds();

                if (column == TDTableModel.linksColumnIndex) {
                    w = layoutMaxWidth;
                } else {
                    w = objectCellWidth;
                }

                doubleBufferedFlag = rendererComponent.isDoubleBuffered();
                opaqueFlag = rendererComponent.isOpaque();
                rendererComponent.setDoubleBuffered(false);
                rendererComponent.setOpaque(true);
                screenshotBounds = new Rectangle(0, 0, w, TDConstants.LAYOUT_HEIGHT);
                rendererComponent.setBounds(screenshotBounds);

                clip = image.getSubimage(offsetX, offsetY, w, TDConstants.LAYOUT_HEIGHT).createGraphics();
                rendererComponent.paint(clip);

                rendererComponent.setBounds(componentBounds);
                rendererComponent.setDoubleBuffered(doubleBufferedFlag);
                rendererComponent.setOpaque(opaqueFlag);

                if (column == TDTableModel.linksColumnIndex) {
                    offsetX += layoutMaxWidth;
                } else {
                    offsetX += objectCellWidth;
                }
            }
            offsetY += TDConstants.LAYOUT_HEIGHT;
        }
        // restore scroll position
        renderer.scroll(-scrollPosition, 0);

        // add timeline
        scrollPosition = timeline.getScrollPosition();
        timeline.scroll(scrollPosition, 0);
        rendererComponent = (JComponent) timelineComponent;
        Color background = rendererComponent.getBackground();
        doubleBufferedFlag = rendererComponent.isDoubleBuffered();
        opaqueFlag = rendererComponent.isOpaque();
        componentBounds = rendererComponent.getBounds();

        rendererComponent.setDoubleBuffered(false);
        rendererComponent.setOpaque(true);
        rendererComponent.setBackground(Color.WHITE);

        screenshotBounds = new Rectangle(0, 0, layoutMaxWidth, TDConstants.TIMELINE_HEIGHT);
        rendererComponent.setBounds(screenshotBounds);

        clip = image.getSubimage(objectCellWidth, offsetY, layoutMaxWidth, TDConstants.TIMELINE_HEIGHT).createGraphics();
        rendererComponent.paint(clip);

        rendererComponent.setBackground(background);
        rendererComponent.setBounds(componentBounds);
        rendererComponent.setDoubleBuffered(doubleBufferedFlag);
        rendererComponent.setOpaque(opaqueFlag);

        clip = image.getSubimage(0, offsetY, TDConstants.TABLE_TEXT_COLUMNS_WIDTH, TDConstants.TIMELINE_HEIGHT).createGraphics();
        clip.setPaint(Color.WHITE);
        clip.fillRect(0, 0, TDConstants.TABLE_TEXT_COLUMNS_WIDTH, TDConstants.TIMELINE_HEIGHT);
        clip = image.getSubimage(TDConstants.TABLE_TEXT_COLUMNS_WIDTH+layoutMaxWidth, offsetY,
                TDConstants.TABLE_TEXT_COLUMNS_WIDTH, TDConstants.TIMELINE_HEIGHT).createGraphics();
        clip.fillRect(0, 0, TDConstants.TABLE_TEXT_COLUMNS_WIDTH, TDConstants.TIMELINE_HEIGHT);

        // restore timeline scroll position
        timeline.scroll(-scrollPosition, 0);
        //
        return image;
    }
    public void stateChanged(ChangeEvent e) {
        computeMaxLayoutWidth();
        configScrollbar();
        setupTimeline();
    }
    // action listeners
    public void addActionListener(ActionListener listener) {
        actionListenerList.add(ActionListener.class, listener);
    }
    public void removeActionListener(ActionListener listener) {
        actionListenerList.remove(ActionListener.class, listener);
    }
    // selection listeners
    public void addListSelectionListener(ListSelectionListener listener) {
        selectionListenerList.add(ListSelectionListener.class, listener);
    }
    public void removeListSelectionListener(ListSelectionListener listener) {
        selectionListenerList.remove(ListSelectionListener.class, listener);
    }
    public void mouseClicked(MouseEvent e) {
        Object eventSource = getEventSource(e);
        switch(e.getButton()) {
            case MouseEvent.BUTTON1:
                // left button, double click
                if(e.getClickCount() == 2 && eventSource != null) {
                    fireActionPerformed(eventSource);
                }
                //
                break;
            case MouseEvent.BUTTON3:
                // right button
                if(eventSource != null) {
                    latestEventSource = eventSource;
                    if( eventSource instanceof TDObject ) {
                        menuItem.setText(menuItemObjectText);
                    }
                    else if( eventSource instanceof TDLink  || eventSource instanceof ArrayList) {
                        menuItem.setText(menuItemLinkText);
                    }
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
                //
                break;
        }
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mousePressed(MouseEvent e) {
    }
    public void mouseReleased(MouseEvent e) {
    }
    //
    //
    // private part
    //
    //
    private void computeMaxLayoutWidth() {
        if(diagramModel != null) {
            long msInterval = diagramModel.getMaximum() - diagramModel.getMinimum();
            TTStep step = new TTStep(msInterval);
            long pxWidth = (  1+ msInterval / step.getMinorStep()  )  *  step.getMinorRule();
            layoutMaxWidth = new Long(pxWidth).intValue() + LABEL_TDTIMELINE_CONSTANT + TDConstants.INDENT*2;
        } else {
            layoutMaxWidth = 0;
        }
    }
    private int getLinksColumnWidth() {
        //return table.getColumnModel().getColumn(TDTableModel.linksColumnIndex).getWidth();
        return getSize().width - TDConstants.TABLE_TEXT_COLUMNS_WIDTH * 2;
    }
    private void configScrollbar() {
        int scrollbarMax = layoutMaxWidth - getLinksColumnWidth();
        if (scrollbarMax > 0) {
            scrollBar.setMaximum(scrollbarMax);
            if ( ! scrollBar.isEnabled()) {
                scrollBar.setEnabled(true);
            }
        } else {
            scrollBar.setMaximum(0);
            scrollBar.setEnabled(false);
        }
    }
    private void setupTimeline() {
        GridBagConstraints constraintsLeftSpacer = null;
        GridBagConstraints constraintsRightSpacer = null;
        GridBagConstraints constraintsTimeline = null;
        GridBagConstraints constraintsScrollbar = null;

        List<Component> components = Arrays.asList(getComponents());

        if(components.contains(spacerComponentLeft)) {
            remove(spacerComponentLeft);
        }
        if(components.contains(timelineComponent)) {
            remove(timelineComponent);
        }
        if(components.contains(scrollBar)) {
            remove(scrollBar);
        }
        if(components.contains(spacerComponentRight)) {
            remove(spacerComponentRight);
        }

        if(diagramModel != null && diagramModel.getPairsCount() > 0) {
            constraintsLeftSpacer = new GridBagConstraints();
            constraintsLeftSpacer.gridx = 0;
            constraintsLeftSpacer.gridy = 1;
            constraintsLeftSpacer.gridheight = GridBagConstraints.REMAINDER;
            constraintsLeftSpacer.fill = GridBagConstraints.BOTH;
            constraintsLeftSpacer.weightx = 0.0;
            constraintsLeftSpacer.weighty = 0.0;
            add(spacerComponentLeft, constraintsLeftSpacer);
            //
            constraintsTimeline = new GridBagConstraints();
            constraintsTimeline.gridx = 1;
            constraintsTimeline.gridy = 1;
            constraintsTimeline.fill = GridBagConstraints.HORIZONTAL;
            constraintsTimeline.weightx = 1.0;
            constraintsTimeline.weighty = 0.0;
            timelineComponent = timeline.getComponent(diagramModel.getMinimum(), diagramModel.getMaximum(), getLinksColumnWidth());
            add(timelineComponent, constraintsTimeline);
            //
            constraintsScrollbar = new GridBagConstraints();
            constraintsScrollbar.gridx = 1;
            constraintsScrollbar.gridy = 2;
            constraintsScrollbar.fill = GridBagConstraints.HORIZONTAL;
            constraintsScrollbar.weightx = 1.0;
            constraintsScrollbar.weighty = 0.0;
            add(scrollBar, constraintsScrollbar);
            //
            int scrollBarWidth = 0;
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            if (sb.isShowing()) {
                scrollBarWidth = sb.getSize().width;
            }
            Dimension dim = new Dimension(TDConstants.TABLE_TEXT_COLUMNS_WIDTH + scrollBarWidth, TDConstants.TIMELINE_HEIGHT);
            spacerComponentRight.setMinimumSize(dim);
            //
            constraintsRightSpacer = new GridBagConstraints();
            constraintsRightSpacer.gridx = 2;
            constraintsRightSpacer.gridy = 1;
            constraintsRightSpacer.gridheight = GridBagConstraints.REMAINDER;
            constraintsRightSpacer.fill = GridBagConstraints.BOTH;
            constraintsRightSpacer.weightx = 0.0;
            constraintsRightSpacer.weighty = 0.0;
            add(spacerComponentRight, constraintsRightSpacer);
        }
        validate();
        repaint();
    }
    private void fireActionPerformed(Object source) {
        if(source != null) {
            actionEvent = null;
            Object[] listeners = actionListenerList.getListenerList();
            for (int i = listeners.length-2; i >= 0; i -= 2) {
                if (listeners[i] == ActionListener.class) {
                    if (actionEvent == null) {
                        actionEvent = new ActionEvent(source, ActionEvent.ACTION_PERFORMED, null);
                    }
                    ((ActionListener) listeners[ i+1 ]).actionPerformed(actionEvent);
                }
            }
        }
    }
    private void fireSelectionChanged() {
        Object[] listeners = selectionListenerList.getListenerList();
        for (int i = listeners.length-2; i >= 0; i -= 2) {
            if (listeners[i] == ListSelectionListener.class) {
                if (listSelectionEvent != null) {
                    ((ListSelectionListener) listeners[ i+1 ]).valueChanged(listSelectionEvent);
                }
            }
        }
    }
    private Object getEventSource(MouseEvent e) {
        Object eventSource = null;
        Point clickPoint = e.getPoint();
        int rowIndex = table.rowAtPoint(clickPoint);
        int columnIndex = table.columnAtPoint(clickPoint);

        if (columnIndex == TDTableModel.linksColumnIndex) { // click on links column
            VisualizationViewer viewer = (VisualizationViewer) renderer.getTableCellRendererComponent(table,
                    table.getValueAt(rowIndex, columnIndex), false, false, rowIndex, columnIndex);

            Point2D p = viewer.inverseViewTransform(clickPoint);

            Vertex vertex = viewer.getPickSupport().getVertex(p.getX()-TDConstants.TABLE_TEXT_COLUMNS_WIDTH, p.getY()-TDConstants.LAYOUT_HEIGHT * rowIndex);
            if(vertex != null) {
                TDLink linkObject = (TDLink) vertex.getUserDatum(TDConstants.KEY_VERTEX_LINKOBJECT);
                VisualizationModel vModel = tableModel.getVisualizationModelBy(rowIndex);
                TDLink lo = ((TDVisualizationModel) vModel).getLinkByVertex(vertex);
                Point2D point2 = ((TDLayout) vModel.getGraphLayout()).getLocation(vertex);
                ArrayList<Vertex> vertexes = ((TDLayout) vModel.getGraphLayout()).getVertexesByPoint(point2);

                if (vertexes.size() == 1) {
                    if (linkObject != null) {
                        eventSource = linkObject;
                    } else if (lo != null) {
                        eventSource = lo;
                    }
                } else {
                    ArrayList<TDLink> lnks = new ArrayList<TDLink>();
                    for (Vertex v : vertexes) {
                        TDLink link = ((TDVisualizationModel) vModel).getLinkByVertex(v);
                        lnks.add(link);
                    }
                    eventSource = lnks;
                }
            }
        } else { // click object column
            Object value = table.getValueAt(rowIndex, columnIndex);
            if (value instanceof TDObject) {
                eventSource = value;
            }
        }
        return eventSource;
    }
}