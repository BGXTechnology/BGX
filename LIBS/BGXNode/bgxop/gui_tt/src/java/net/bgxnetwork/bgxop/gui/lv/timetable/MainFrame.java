/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/MainFrame.java#1 $
$DateTime: 2007/08/06 17:28:33 $
$Change: 20537 $
$Author: a.borisenko $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import net.bgx.bgxnetwork.bgxop.gui.dialogs.MultiPageScreenshotPrinter;
import net.bgx.bgxnetwork.bgxop.gui.dialogs.ScreenshotWriter;
import net.bgx.bgxnetwork.bgxop.gui.lv.timetable.test.TestDataTDModel;

import net.bgx.bgxnetwork.transfer.tt.*;
import net.bgx.bgxnetwork.transfer.data.FieldObject;
import net.bgx.bgxnetwork.persistence.metadata.LinkObject;
import net.bgx.bgxnetwork.persistence.metadata.ControlObject;

public class MainFrame extends JFrame implements ActionListener {
    private TimeDiagram timediagram = null;
    private TDModel diagramModel = null;
    private JButton screenShotButton = null;
    private JButton printButton = null;
    private JButton moveUpButton = null;
    private JButton moveDownButton = null;
    private JButton deleteButton = null;
    private JButton insertButton = null;
    private JButton exitButton = null;
    private JToggleButton statisticsButton = null;
    private Thread statisticsThread = null;
    private String frameTitle = null;

    public MainFrame () {
        frameTitle = new String("time diagram test application");
        screenShotButton = new JButton("screenshot");
        printButton = new JButton("print");
        moveUpButton = new JButton("move up");
        moveDownButton = new JButton("move down");
        deleteButton = new JButton("delete pairs");
        insertButton = new JButton("insert pairs");
        statisticsButton = new JToggleButton("statistics");
        exitButton = new JButton("exit");
        //
        diagramModel = new TestDataTDModel(20, 20, 10);
        timediagram = new TimeDiagram();
        timediagram.setModel(diagramModel);

    }
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if ( source == screenShotButton ) {
            //
            try {
                ScreenshotWriter writer = new ScreenshotWriter(null);
                writer.saveScreenshot(timediagram.getImage(), new String(System.currentTimeMillis()+""));
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            //
        } else if ( source == exitButton ) {
            //
            System.exit(0);
            //
        } else if ( source == printButton ) {
            //
            MultiPageScreenshotPrinter printer = new MultiPageScreenshotPrinter(new JDialog(), frameTitle);
            printer.showPrintDialog(timediagram.getImage(), PrinterJob.getPrinterJob().defaultPage());
            //
        } else if ( source == moveUpButton ) {
            //
            List<TDPair> selectedPairs = timediagram.getSelectedPairs();
            if (selectedPairs != null) {
                int index = diagramModel.getIndexOf(selectedPairs.get(0));
                diagramModel.movePairs(selectedPairs, index - 1);
            }
            //
        } else if ( source == moveDownButton ) {
            //
            List<TDPair> selectedPairs = timediagram.getSelectedPairs();
            if (selectedPairs != null) {
                int index = diagramModel.getIndexOf(selectedPairs.get(selectedPairs.size() - 1));
                if (selectedPairs.size() == 1) {
                    index++;
                }
                diagramModel.movePairs(selectedPairs, index);
            }
            //
        } else if ( source == insertButton ) {
            //
            int count = diagramModel.getPairsCount();
            diagramModel.insertPairs(getTestData(3, 10, 10), count);
            //
        } else if ( source == deleteButton ) {
            //
            List<TDPair> selectedPairs = timediagram.getSelectedPairs();
            if (selectedPairs != null) {
                diagramModel.removePairs(selectedPairs);
            }
            //
        } else if ( source == statisticsButton ) {
            //
            if(statisticsButton.isSelected()) {
                if (statisticsThread == null ) {
                    initStatisticsThread();
                }
                statisticsThread.start();
            } else {
                statisticsThread.interrupt();
                statisticsThread = null;
            }
        }
    }

    private void init() {
        initStatisticsThread();
        GridBagConstraints gridBagConstraints;

        setTitle(frameTitle);
        setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.gridheight = 8;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(timediagram, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(screenShotButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(printButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(moveUpButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(moveDownButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(deleteButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(insertButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(statisticsButton, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.weighty = 0.0;
        add(exitButton, gridBagConstraints);

        screenShotButton.addActionListener(this);
        printButton.addActionListener(this);
        moveUpButton.addActionListener(this);
        moveDownButton.addActionListener(this);
        deleteButton.addActionListener(this);
        insertButton.addActionListener(this);
        statisticsButton.addActionListener(this);
        exitButton.addActionListener(this);
}
    private List<TDPair> getTestData(int pairsNumber, int linksNumber, long interval) {
        List<TDPair> pairs = new LinkedList<TDPair>();
        TDObject objectOne = null;
        TDObject objectTwo = null;
        TDPair pair = null;
        TDLink link = null;
        List<TDLink> links = null;
        for (int pairIdx = 0; pairIdx < pairsNumber; pairIdx++) {
            TDObject initiator = null;
            objectOne = new TestDataTDObject(""+System.currentTimeMillis(), new ControlObject());
            objectTwo = new TestDataTDObject(""+System.currentTimeMillis(), new ControlObject());
            pair = new TestDataTDPair(objectOne, objectTwo);
            links = new ArrayList<TDLink>();
            long timestamp = System.currentTimeMillis();
            for (int linkIdx = 0; linkIdx < linksNumber; linkIdx++) {
                initiator = initiator != null && initiator == objectOne ? objectTwo : objectOne;
                timestamp += interval;
                link = new TestDataTDLink(new LinkObject(), initiator, pair, timestamp);
                links.add(link);
            }
            pair.setLinks(links);
            pairs.add(pair);
        }
        return pairs;
    }
    private Map<String, String> attributes(int count) {
        Map<String, String> attributes = new HashMap<String, String>();
        for (int attributeIdx = 0; attributeIdx < count; attributeIdx++) {
            attributes.put(new String("attribute key "+attributeIdx), new String("attribute value "+attributeIdx));
        }
        return attributes;
    }
    private void initStatisticsThread() {
        statisticsThread = new Thread() {
            private Runtime runtime = Runtime.getRuntime();
            public void run() {
//                System.out.println("---------- Memory statistics, Mbytes ----------");
//                System.out.println("Max:\t\tFree:\t\tTotal:");
                while ( ! isInterrupted()) {
//                    System.out.print(runtime.maxMemory() / 1024 ^ 2);
//                    System.out.print("\t\t"+ (runtime.totalMemory() / 1024 ^ 2));
//                    System.out.println("\t\t "+ (runtime.freeMemory() / 1024 ^ 2));
                    try {
                        Thread.sleep(3000);
                    }
                    catch (InterruptedException e) {
                        return;
                    }
                }
            }
        };
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MainFrame frame = new MainFrame();
                frame.setBounds(200, 50, 800, 600);
                frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.init();
                frame.getContentPane().invalidate();
                frame.getContentPane().repaint();
            }
        });
    }
}
