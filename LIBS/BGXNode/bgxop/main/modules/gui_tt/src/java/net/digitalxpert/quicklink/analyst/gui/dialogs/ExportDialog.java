package net.bgx.bgxnetwork.bgxop.gui.dialogs;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import net.bgx.bgxnetwork.bgxop.gui.MainFrame;
import net.bgx.bgxnetwork.bgxop.gui.QueryPanel;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.services.audit.AuditManager;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;

/**
 * Class ExportDialog
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class ExportDialog extends JDialog implements ActionListener{
    private ExportDialogController controller;
    private JTextField objFileNameTF, linkFileNameTF;
    private ExtensibleFileFilter ffilter;
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    private String pathToMyDocuments = null;

    public ExportDialog(String title, QueryPanel dataPanel, Frame owner){
        super(owner, title, true);
        controller = new ExportDialogController(dataPanel);
        controller.setView(this);
        //
        String queryName = dataPanel.getQuery().getName();
        //
        ffilter = new ExtensibleFileFilter(rb.getString("export.filterName") + " (*.csv, *.txt)");
        ffilter.addExtension("csv");
        ffilter.addExtension("txt");
        JPanel expObjects = new JPanel(new GridBagLayout());
        expObjects.setOpaque(false);
        expObjects.setBorder(new TitledBorder(rb.getString("ExportDialog.exportObjectsLabel")));
        objFileNameTF = new JTextField();
        objFileNameTF.setText(queryName+rb.getString("ExportDialog.filename.objects") + "." +
                rb.getString("ExportDialog.filename.default.extension"));
        //
        JButton browse1 = new JButton(rb.getString("ExportDialog.browseButton"));
        browse1.setActionCommand("objects");
        browse1.addActionListener(this);
        expObjects.add(new JLabel(rb.getString("ExportDialog.fileNameLabel")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        expObjects.add(objFileNameTF, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL, new Insets(5, 0, 5, 5), 0, 0));
        expObjects.add(browse1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(
                0, 5, 5, 5), 0, 0));
        JPanel expLinks = new JPanel(new GridBagLayout());
        expLinks.setOpaque(false);
        expLinks.setBorder(new TitledBorder(rb.getString("ExportDialog.exportLinksLabel")));
        linkFileNameTF = new JTextField();
        linkFileNameTF.setText(queryName+rb.getString("ExportDialog.filename.links") + "." +
                rb.getString("ExportDialog.filename.default.extension"));
        //
        JButton browse2 = new JButton(rb.getString("ExportDialog.browseButton"));
        browse2.setActionCommand("links");
        browse2.addActionListener(this);
        expLinks.add(new JLabel(rb.getString("ExportDialog.fileNameLabel")), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
        expLinks.add(linkFileNameTF, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(5, 0, 5, 5), 0, 0));
        expLinks.add(browse2, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,
                5, 5, 5), 0, 0));
        Dimension dim = objFileNameTF.getPreferredSize();
        dim.width = 200;
        objFileNameTF.setPreferredSize(dim);
        linkFileNameTF.setPreferredSize(dim);
        browse1.setMargin(new Insets(0, 5, 0, 5));
        browse2.setMargin(new Insets(0, 5, 0, 5));
        JButton export = new JButton(rb.getString("ExportDialog.exportButton"));
        export.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                // ����� ������ ������ ��� ����������� �������� ������
                // �������������
                try{
                    String queryName = controller.getDataPanel().getName();
                    AuditManager auditManager = new AuditManager();
                    auditManager.addExportEvent(controller.getDataPanel().getQuery().getId());
                }catch (Exception ex){
                    MessageDialogs.generalError(ExportDialog.this, ex);
                }
                boolean res = false;
                String text;
                if(controller.getDataPanel().getController().isVerticesExportable()){
                    text = objFileNameTF.getText();
                    if(text != null && text.length() > 0){
                        text = modifyPath(text);
                        res = controller.exportObjects(text);
                        if(!res)
                            return;
                    }
                }
                if(controller.getDataPanel().getController().isEdgesExportable()){
                    text = linkFileNameTF.getText();
                    if(text != null && text.length() > 0){
                        text = modifyPath(text);
                        res = controller.exportLinks(text);
                        if(!res)
                            return;
                    }
                }
                if(res){
                    MessageDialogs.info(ExportDialog.this, rb.getString("export.dataStoredMsg"), rb.getString("message.operationComplete"));
                    processWindowEvent(new WindowEvent(ExportDialog.this, WindowEvent.WINDOW_CLOSING));
                }
            }
        });
        JButton cancel = new JButton(rb.getString("ExportDialog.cancelButton"));
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                processWindowEvent(new WindowEvent(ExportDialog.this, WindowEvent.WINDOW_CLOSING));
            }
        });
        getContentPane().setLayout(new GridBagLayout());
        if(dataPanel.getController().isVerticesExportable())
            getContentPane().add(
                    expObjects,
                    new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10,
                            10, 5, 10), 0, 0));
        if(dataPanel.getController().isEdgesExportable())
            getContentPane().add(
                    expLinks,
                    new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,
                            10, 10, 10), 0, 0));
        getContentPane().add(
                export,
                new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 10, 10),
                        0, 0));
        getContentPane().add(
                cancel,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 10, 10),
                        0, 0));
        GlobalPopupUtil.initListeners(this);
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setVisible(true);
    }
    // if string - is a simple filename that add as prefix path to CurrentDir
    private String modifyPath(String path){
        if(path.contains("\\") || path.contains("/"))
            return ffilter.formatFileName(path);
        if(pathToMyDocuments == null){
            JFileChooser chooser = new JFileChooser("");
            GlobalPopupUtil.initListeners(chooser);
            pathToMyDocuments = chooser.getCurrentDirectory().getPath();
        }
//        return ffilter.formatFileName(pathToMyDocuments + "\\" + path);
        return ffilter.formatFileName(pathToMyDocuments + System.getProperty("file.separator") + path);
    }

    public void actionPerformed(ActionEvent e){
        Color c = UIManager.getColor("Panel.background");
        UIManager.put("Panel.background", UIManager.getColor("default.gradient"));
        JFileChooser chooser = new JFileChooser(MainFrame.currentDir);
        chooser.setFileFilter(ffilter);
        GlobalPopupUtil.initListeners(chooser);
        //
        String selectedFilename = "";
        if(e.getActionCommand().equals("objects")) {
            selectedFilename = objFileNameTF.getText();
        }
        else if(e.getActionCommand().equals("links")) {
            selectedFilename = linkFileNameTF.getText();
        }
        if (selectedFilename.length() != 0) {
            chooser.setSelectedFile(new File(selectedFilename));
        }
        //
        int res = chooser.showDialog(ExportDialog.this, rb.getString("ExportDialog.browseButton"));
        UIManager.put("Panel.background", c);
        String text = "";
        if(res == JFileChooser.APPROVE_OPTION){
            MainFrame.currentDir = chooser.getCurrentDirectory().getPath();
            File f = chooser.getSelectedFile();
            text = f.getAbsolutePath();
        }
        if(e.getActionCommand().equals("objects"))
            objFileNameTF.setText(ffilter.formatFileName(text));
        else if(e.getActionCommand().equals("links"))
            linkFileNameTF.setText(ffilter.formatFileName(text));
    }
}
