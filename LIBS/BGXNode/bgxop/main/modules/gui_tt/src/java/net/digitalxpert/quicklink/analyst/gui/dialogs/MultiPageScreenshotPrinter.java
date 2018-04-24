package net.bgx.bgxnetwork.bgxop.gui.dialogs;
import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;
import net.bgx.bgxnetwork.bgxop.gui.popups.GlobalPopupUtil;
import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.awt.print.Printable;
import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class MultiPageScreenshotPrinter
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class MultiPageScreenshotPrinter extends JDialog implements Printable, IScreenshotPrinter, ActionListener, ItemListener{
    private String imageName = null;
    private BufferedImage original;
    private BufferedImage scaled;
    private PageFormat pf;
    private double scale = 1;
    private JRadioButton portraitRB, landscapeRB;
    private JComboBox scaleCombo;
    private Object lastValue;
    private JButton pfB, backB, fwdB;
    private JButton fitWidth, fitHeight, fitPage;
    private JLabel pageCounter;
    private PreviewPanel preview;
    private JButton ok, cancel;
    private JToggleButton singlePage, multiPage;
    private Integer[] scales = { 25, 50, 75, 100, 125 };
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui_dialogs");
    public MultiPageScreenshotPrinter(Dialog owner, String title){
        this(owner, title, null);
    }
    public MultiPageScreenshotPrinter(Dialog owner, String title, String imageName){
        super(owner, title, true);
        init(imageName);
    }
    public MultiPageScreenshotPrinter(Frame owner, String title){
        this(owner, title, null);
    }
    public MultiPageScreenshotPrinter(Frame owner, String title, String imageName){
        super(owner, title, true);
        init(imageName);
    }
    private void init(String imageName){
        this.imageName = imageName;
        JPanel orientation = new JPanel();
        orientation.setOpaque(false);
        orientation.setBorder(new TitledBorder(rb.getString("MultiPageScreenshotPrinter.orientationLabel")));
        portraitRB = new JRadioButton(rb.getString("MultiPageScreenshotPrinter.portraitLabel"));
        landscapeRB = new JRadioButton(rb.getString("MultiPageScreenshotPrinter.landscapeLabel"));
        portraitRB.addActionListener(this);
        landscapeRB.addActionListener(this);
        ButtonGroup bg = new ButtonGroup();
        bg.add(portraitRB);
        bg.add(landscapeRB);
        orientation.add(portraitRB);
        orientation.add(landscapeRB);
        scaleCombo = new JComboBox(scales);
        scaleCombo.setSelectedIndex(3);
        scaleCombo.setEditable(true);
        scaleCombo.addItemListener(this);
        pfB = new JButton(rb.getString("MultiPageScreenshotPrinter.pageSetupButton"));
        pfB.addActionListener(this);
        Insets topi = new Insets(10, 10, 5, 10), ins = new Insets(5, 5, 5, 5);
        preview = new PreviewPanel();
        backB = (JButton) initButton(new JButton(), "MultiPageScreenshotPrinter.pageBack.img");
        backB.setToolTipText(rb.getString("MultiPageScreenshotPrinter.pageBack.tooltip"));
        fwdB = (JButton) initButton(new JButton(), "MultiPageScreenshotPrinter.pageForward.img");
        fwdB.setToolTipText(rb.getString("MultiPageScreenshotPrinter.pageForward.tooltip"));
        pageCounter = new JLabel(preview.getCurrentPage() + " " + rb.getString("MultiPageScreenshotPrinter.of") + ""
                + preview.getTotalPages());
        JPanel pager = new JPanel(new GridBagLayout());
        pager.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, new Insets(0, 3, 0, 3), 0, 0);
        pager.add(new JLabel(rb.getString("MultiPageScreenshotPrinter.pageLabel")), gbc);
        pager.add(backB, gbc);
        pager.add(pageCounter, gbc);
        pager.add(fwdB, gbc);
        JPanel scaler = new JPanel(new GridBagLayout());
        scaler.setOpaque(false);
        fitWidth = (JButton) initButton(new JButton(), "MultiPageScreenshotPrinter.fitWidth.img");
        fitWidth.setToolTipText(rb.getString("MultiPageScreenshotPrinter.fitWidth.tooltip"));
        fitHeight = (JButton) initButton(new JButton(), "MultiPageScreenshotPrinter.fitHeight.img");
        fitHeight.setToolTipText(rb.getString("MultiPageScreenshotPrinter.fitHeight.tooltip"));
        fitPage = (JButton) initButton(new JButton(), "MultiPageScreenshotPrinter.fitPage.img");
        fitPage.setToolTipText(rb.getString("MultiPageScreenshotPrinter.fitPage.tooltip"));
        GridBagConstraints gbc1 = new GridBagConstraints(GridBagConstraints.RELATIVE, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE, ins, 0, 0);
        scaler.add(fitWidth, gbc1);
        scaler.add(fitHeight, gbc1);
        scaler.add(fitPage, gbc1);
        scaler.setBorder(new TitledBorder(rb.getString("MultiPageScreenshotPrinter.scaleTitle")));
        ok = new JButton(rb.getString("MultiPageScreenshotPrinter.print"));
        ok.addActionListener(this);
        cancel = new JButton(rb.getString("MultiPageScreenshotPrinter.cancel"));
        cancel.addActionListener(this);
        singlePage = (JToggleButton) initButton(new JToggleButton(), "MultiPageScreenshotPrinter.singlePage.img");
        singlePage.setToolTipText(rb.getString("MultiPageScreenshotPrinter.singlePage.tooltip"));
        multiPage = (JToggleButton) initButton(new JToggleButton(), "MultiPageScreenshotPrinter.multiPage.img");
        multiPage.setToolTipText(rb.getString("MultiPageScreenshotPrinter.multiPage.tooltip"));
        bg = new ButtonGroup();
        bg.add(singlePage);
        bg.add(multiPage);
        multiPage.setSelected(true);
        JPanel mode = new JPanel(new GridBagLayout());
        mode.setOpaque(false);
        mode.add(singlePage, gbc1);
        mode.add(multiPage, gbc1);
        mode.setBorder(new TitledBorder(rb.getString("MultiPageScreenshotPrinter.previewModeLabel")));
        getContentPane().setLayout(new GridBagLayout());
        // getContentPane().add(mode, gbc);
        getContentPane().add(pager,
                new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, topi, 0, 0));
        getContentPane().add(
                pfB,
                new GridBagConstraints(2, 0, GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                        topi, 0, 0));
        getContentPane().add(
                preview,
                new GridBagConstraints(0, 1, 2, 6, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 5, 10),
                        300, 300));
        GridBagConstraints gbc2 = new GridBagConstraints(2, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, ins, 0, 0);
        getContentPane().add(orientation, gbc2);
        getContentPane().add(
                new JLabel(rb.getString("MultiPageScreenshotPrinter.scaleLabel")),
                new GridBagConstraints(2, GridBagConstraints.RELATIVE, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(5, 10, 5, 5), 0, 0));
        getContentPane().add(
                scaleCombo,
                new GridBagConstraints(3, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 10), 0, 0));
        getContentPane().add(scaler, gbc2);
        getContentPane().add(mode, gbc2);
        getContentPane().add(
                new JLabel(),
                new GridBagConstraints(2, GridBagConstraints.RELATIVE, GridBagConstraints.REMAINDER, 1, 0.0, 1.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH, ins, 0, 0));
        getContentPane().add(
                ok,
                new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 15, 10),
                        0, 0));
        getContentPane().add(
                cancel,
                new GridBagConstraints(2, 7, GridBagConstraints.REMAINDER, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                        new Insets(10, 10, 15, 5), 0, 0));
        GlobalPopupUtil.initListeners(this);
    }
    private AbstractButton initButton(AbstractButton button, String resource){
        Icon icon = ResourceLoader.getInstance().getIconByResource(rb, resource);
        button.setIcon(icon);
        button.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconWidth()));
        button.addActionListener(this);
        return button;
    }
    public void showPrintDialog(BufferedImage image, PageFormat pf){
        if(pf.getOrientation() == PageFormat.PORTRAIT)
            portraitRB.setSelected(true);
        else
            landscapeRB.setSelected(true);
        original = image;
        this.pf = pf;
        relayoutPages();
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
    public PageFormat getPageFormat(){
        return pf;
    }
    public void itemStateChanged(ItemEvent e){
        int st = e.getStateChange();
        if(st == ItemEvent.DESELECTED){
            lastValue = e.getItem();
            return;
        }
        Object item = scaleCombo.getSelectedItem();
        if(item instanceof Integer)
            scale = ((Integer) item) / 100d;
        else if(item instanceof String)
            try{
                scale = Integer.parseInt((String) item) / 100d;
            }catch (Exception ex){
                scaleCombo.setSelectedItem(lastValue);
                return;
            }
        else{
            scaleCombo.setSelectedItem(lastValue);
            return;
        }
        relayoutPages();
    }
    protected void relayoutPages(){
        preview.countPages();
        if(preview.getCurrentPage() > preview.getTotalPages())
            preview.setCurrentPage(preview.getTotalPages());
        checkPagingButtons();
        preview.repaint();
    }
    protected void checkPagingButtons(){
        pageCounter.setText(preview.getCurrentPage() +" "+rb.getString("MultiPageScreenshotPrinter.of")+" "+ preview.getTotalPages());
        if(preview.getCurrentPage() == 1)
            backB.setEnabled(false);
        else if(!backB.isEnabled())
            backB.setEnabled(true);
        if(preview.getCurrentPage() == preview.getTotalPages())
            fwdB.setEnabled(false);
        else if(!fwdB.isEnabled())
            fwdB.setEnabled(true);
    }
    protected boolean printImage() throws PrinterException{
        if(scale == 1d)
            scaled = original;
        else{
            AffineTransform t = new AffineTransform();
            t.scale(scale, scale);
            AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
            scaled = op.filter(original, null);
        }
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this, pf);
        boolean res = printJob.printDialog();
        if(!res)
            return res;
        printJob.print();
        if(scaled != original)
            scaled = null;
        return res;
    }
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException{
        if(pageIndex > preview.getTotalPages() - 1)
            return NO_SUCH_PAGE;
        int i = pageIndex / preview.getCols();
        int j = pageIndex % preview.getCols();
        int px = (int) pageFormat.getImageableX();
        int py = (int) pageFormat.getImageableY();
        int pw = (int) pageFormat.getImageableWidth();
        int ph = (int) pageFormat.getImageableHeight();
        int x = (int) (j * pageFormat.getImageableWidth());
        int y = (int) (i * pageFormat.getImageableHeight());
        // int w = Math.min((int) pageFormat.getImageableWidth(),
        // scaled.getWidth() - x);
        // int h = Math.min((int) pageFormat.getImageableHeight(),
        // scaled.getHeight() - y);
        // BufferedImage printImage = scaled.getSubimage(x, y, w, h);
        // paint image using clip
        int tx = px - x;
        int ty = py - y;
        graphics.translate(tx, ty);
        Rectangle clip = graphics.getClipBounds();
        graphics.setClip(x, y, pw, ph);
        graphics.drawImage(scaled, 0, 0, this);
        graphics.setClip(clip.x, clip.y, clip.width, clip.height);
        graphics.translate(-tx, -ty);
        // x = (int) pageFormat.getImageableX();
        // y = (int) pageFormat.getImageableY();
        // graphics.drawImage(printImage, x, y, this);
        // printImage = null;
        graphics.drawRect(px, py, pw, ph);
        String s = "";
        if(imageName != null)
            s = imageName;
        s = s + " [" + (pageIndex + 1) + " of " + preview.getTotalPages() + "]";
        graphics.drawString(s, px + 20, py - 3);
        return PAGE_EXISTS;
    }
    public void actionPerformed(ActionEvent e){
        double sc;
        if(e.getSource() == landscapeRB){
            pf.setOrientation(PageFormat.LANDSCAPE);
            relayoutPages();
        }else if(e.getSource() == portraitRB){
            pf.setOrientation(PageFormat.PORTRAIT);
            relayoutPages();
        }else if(e.getSource() == pfB){
            if(pf == null)
                pf = PrinterJob.getPrinterJob().defaultPage();
            pf = PrinterJob.getPrinterJob().pageDialog(pf);
            if(pf.getOrientation() == PageFormat.PORTRAIT)
                portraitRB.setSelected(true);
            else
                landscapeRB.setSelected(true);
            relayoutPages();
        }
        // fit buttons
        else if(e.getSource() == fitWidth){
            sc = 100 * pf.getImageableWidth() / original.getWidth();
            scaleCombo.setSelectedItem((int) sc);
        }else if(e.getSource() == fitHeight){
            sc = 100 * pf.getImageableHeight() / original.getHeight();
            scaleCombo.setSelectedItem((int) sc);
        }else if(e.getSource() == fitPage){
            sc = 100 * Math.min(pf.getImageableWidth() / original.getWidth(), pf.getImageableHeight() / original.getHeight());
            scaleCombo.setSelectedItem((int) sc);
        }
        // paging buttons
        else if(e.getSource() == backB){
            preview.setCurrentPage(preview.getCurrentPage() - 1);
            checkPagingButtons();
            preview.repaint();
        }else if(e.getSource() == fwdB){
            preview.setCurrentPage(preview.getCurrentPage() + 1);
            checkPagingButtons();
            preview.repaint();
        }
        // control buttons
        else if(e.getSource() == singlePage){
            preview.setMultiPageMode(false);
        }else if(e.getSource() == multiPage){
            preview.setMultiPageMode(true);
        }else if(e.getSource() == cancel){
            this.dispose();
        }else if(e.getSource() == ok){
            try{
                boolean res = printImage();
                if(res)
                    processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }catch (PrinterException e1){
                // JOptionPane.showMessageDialog(this, e1.getMessage(), "Print
                // error", JOptionPane.ERROR_MESSAGE);
                MessageDialogs.generalError(this, e1);
            }
        }
    }
    // ******************** Preview panel class ************************
    class PreviewPanel extends JPanel{
        private Color shaded = new Color(200, 200, 200, 80);
        private Color pageOutline = Color.red;
        private boolean multiPageMode = true;
        private int currentPage = 1;
        private int totalPages = 1;
        private int rows = 0, cols = 0;
        private double cacheScale = 0;
        private BufferedImage cacheImage = null;
        public PreviewPanel(){
            setBorder(new EtchedBorder(EtchedBorder.RAISED));
        }
        public boolean isMultiPageMode(){
            return multiPageMode;
        }
        public void setMultiPageMode(boolean multiPageMode){
            this.multiPageMode = multiPageMode;
            repaint();
        }
        public int getCurrentPage(){
            return currentPage;
        }
        public int getTotalPages(){
            return totalPages;
        }
        public int getRows(){
            return rows;
        }
        public int getCols(){
            return cols;
        }
        public void setCurrentPage(int currentPage){
            this.currentPage = currentPage;
        }
        public void countPages(){
            rows = (int) Math.ceil(original.getHeight() * scale / pf.getImageableHeight());
            cols = (int) Math.ceil(original.getWidth() * scale / pf.getImageableWidth());
            totalPages = cols * rows;
        }
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            Color curc = g.getColor();
            if(multiPageMode)
                paintMultiPage((Graphics2D) g);
            else
                paintSinglePage((Graphics2D) g);
            g.setColor(curc);
        }
        protected void paintMultiPage(Graphics2D g2d){
            Dimension d = getSize();
            double hScale = 1d * (d.width - 10) / original.getWidth();
            double vScale = 1d * (d.height - 10) / original.getHeight();
            double sc = Math.min(hScale, vScale);
            AffineTransform af = new AffineTransform();
            af.scale(sc, sc);
            int imagew = (int) (original.getWidth() * sc), imageh = (int) (original.getHeight() * sc), imagex = (d.width - imagew) / 2, imagey = (d.height - imageh) / 2;
            // draw
            g2d.drawImage(original, new AffineTransformOp(af, AffineTransformOp.TYPE_BILINEAR), imagex, imagey);
            g2d.setColor(Color.black);
            g2d.drawRect(imagex, imagey, imagew, imageh);
            if(totalPages > 1){
                // paint page outlines
                int pagew = (int) (pf.getImageableWidth() * sc / scale), pageh = (int) (pf.getImageableHeight() * sc / scale);
                int i = 0, j, w, h;
                while (i * pf.getImageableHeight() / scale <= original.getHeight()){
                    j = 0;
                    h = Math.min(pageh, imageh - i * pageh);
                    while (j * pf.getImageableWidth() / scale <= original.getWidth()){
                        w = Math.min(pagew, imagew - j * pagew);
                        if(cols * i + j != currentPage - 1){
                            g2d.setColor(shaded);
                            g2d.fillRect(imagex + j * pagew, imagey + i * pageh, w, h);
                        }
                        g2d.setColor(pageOutline);
                        g2d.drawRect(imagex + j * pagew, imagey + i * pageh, w, h);
                        j++;
                    }
                    i++;
                }
            }
        }
        protected void paintSinglePage(Graphics2D g2d){
            Dimension d = getSize();
            double hScale = 1d * (d.width - 10) / pf.getWidth();
            double vScale = 1d * (d.height - 10) / pf.getHeight();
            double pagesc = Math.min(hScale, vScale);
            double sc = pagesc * scale;
            if(Math.abs(sc - cacheScale) > 0.0001){
                AffineTransform t = new AffineTransform();
                t.scale(sc, sc);
                AffineTransformOp op = new AffineTransformOp(t, AffineTransformOp.TYPE_BILINEAR);
                cacheImage = op.filter(original, null);
            }
            int pw = (int) (pf.getImageableWidth() * pagesc);
            int ph = (int) (pf.getImageableHeight() * pagesc);
            int i = (currentPage - 1) / cols;
            int j = (currentPage - 1) % cols;
            // BufferedImage page = cacheImage.getSubimage(j*pw, i*ph,
            // Math.min(pw, cacheImage.getWidth()-j*pw),
            // Math.min(ph, cacheImage.getHeight()-i*ph));
            // draw
            g2d.setColor(Color.white);
            int w = (int) (pf.getWidth() * pagesc);
            int h = (int) (pf.getHeight() * pagesc);
            g2d.fillRect((d.width - w) / 2, (d.height - h) / 2, w, h);
            g2d.setColor(Color.black);
            g2d.drawRect((d.width - w) / 2, (d.height - h) / 2, w, h);
            // paint image using clip
            int tx = (d.width - pw) / 2 - j * pw;
            int ty = (d.height - ph) / 2 - i * ph;
            g2d.translate(tx, ty);
            Rectangle clip = g2d.getClipBounds();
            g2d.setClip(j * pw, i * ph, pw, ph);
            g2d.drawImage(cacheImage, 0, 0, this);
            g2d.setClip(clip.x, clip.y, clip.width, clip.height);
            g2d.translate(-tx, -ty);
            // g2d.drawImage(page, (d.width-pw)/2, (d.height-ph)/2, this);
            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect((d.width - pw) / 2, (d.height - ph) / 2, pw, ph);
        }
    }
}
