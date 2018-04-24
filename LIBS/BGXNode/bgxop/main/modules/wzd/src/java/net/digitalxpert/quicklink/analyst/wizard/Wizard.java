package net.bgx.bgxnetwork.bgxop.wizard;

//import net.bgx.bgxnetwork.bgxop.gui.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * User: A.Borisenko
 * Date: 16.01.2007
 * Time: 11:52:59
 */
public class Wizard extends WindowAdapter implements PropertyChangeListener {
//    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");
    private WizardCommand _callBackObject;

    public static final int FINISH_RETURN_CODE = 0;
    public static final int FINISH2_RETURN_CODE = 3;
    public static final int CANCEL_RETURN_CODE = 1;
    public static final int ERROR_RETURN_CODE = 2;


    public static final String NEXT_BUTTON_ACTION_COMMAND = "NextButtonActionCommand";
    public static final String FINISH_BUTTON_ACTION_COMMAND2 = "FinishButtonActionCommand2";
    public static final String BACK_BUTTON_ACTION_COMMAND = "BackButtonActionCommand";
    public static final String CANCEL_BUTTON_ACTION_COMMAND = "CancelButtonActionCommand";

    static String BACK_TEXT;
    static String NEXT_TEXT;
    static String FINISH_TEXT;
    static String CANCEL_TEXT;
    static String FINISH_TEXT2;

/*
    static Icon BACK_ICON;
    static Icon NEXT_ICON;
    static Icon FINISH_ICON;
    static Icon CANCEL_ICON;
*/

    private WizardModel wizardModel;
    private WizardController wizardController;

    private JDialog wizardDialog;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    private JButton backButton;
    private JButton nextButton;
    private JButton finishButton2;
    private JButton cancelButton;

    private int returnCode;
    private Object returnObject = null;

    private int x = 0;
    private int y = 0;

    private String initialTitle ="";

    private int minimalWidth = 500;
    private int minimalHeight = 200;

    private Dialog lock = null;

    //Constuctors
    public Wizard(JDialog owner, WizardCommand controller){
        wizardModel = new WizardModel();
        wizardDialog = new JDialog(owner);
        wizardController = new WizardController(this);
        _callBackObject = controller;
        initComponents();
    }

    public Wizard(Frame owner, WizardCommand controller) {
        wizardModel = new WizardModel();
        wizardDialog = new JDialog(owner);
        wizardController = new WizardController(this);
        _callBackObject = controller;
        initComponents();
    }

    public Wizard(JDialog owner, WizardCommand controller, String title){
        this(owner, controller);
        setTitle(title);
    }

    public Wizard(Frame owner, WizardCommand controller, String title){
        this(owner, controller);
        setTitle(title);
    }

    public Wizard(Frame owner,  WizardCommand controller, ArrayList<String> cases){
        this(owner, controller);
    }

    public Wizard(JDialog owner, WizardCommand controller, ArrayList<String> cases){
        this(owner, controller);
    }

    public JDialog getWizardDialog(){
        return wizardDialog;
    }

    public Component getOwner() {
        return wizardDialog.getOwner();
    }

    public void setLocked(Dialog lock) {
//        System.out.println("Wizard.setLocked() "+ lock);
        if( lock != null) {
            wizardDialog.getGlassPane().setVisible(true);
        }
        else {
            wizardDialog.getGlassPane().setVisible(false);
        }
        this.lock = lock;
    }
    /**
     * Sets the title of the wizard
     * @param s The title of the dialog.
     */
    public void setTitle(String s) {
        wizardDialog.setTitle(s);
    }

    /**
     * Returns the current title of wizard
     * @return The String-based title of the generated dialog.
     */
    public String getTitle() {
        return wizardDialog.getTitle();
    }

    /**
     * Returns the main panel
     * @return the panel for display dialogs
     */
    public JPanel getMainPanel(){
        return mainPanel;
    }
    /**
     * Sets the modality of the generated javax.swing.JDialog.
     * @param b the modality of the dialog
     */
    public void setModal(boolean b) {
        wizardDialog.setModal(b);
    }

    /**
     * Returns the modality of the dialog.
     * @return A boolean indicating whether or not the generated javax.swing.JDialog is modal.
     */
    public boolean isModal() {
        return wizardDialog.isModal();
    }

    /**
     * Convienence method that displays a modal wizard dialog and blocks until the dialog
     * has completed.
     * @return Indicates how the dialog was closed. Compare this value against the RETURN_CODE
     * constants at the beginning of the class.
     */
    public Object showModalDialog(boolean modal) {
        wizardDialog.setModal(modal);
        wizardDialog.setResizable(false);
        wizardDialog.pack();
        wizardDialog.setLocationRelativeTo(null);
        wizardDialog.setVisible(true);

        return getReturnObject();
    }

    public WizardModel getModel() {
        return wizardModel;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(WizardModel.CURRENT_PANEL_DESCRIPTOR_PROPERTY)) {
            wizardController.resetButtonsToPanelRules();
        } else if (evt.getPropertyName().equals(WizardModel.NEXT_FINISH_BUTTON_TEXT_PROPERTY)) {
            nextButton.setText(evt.getNewValue().toString());
            finishButton2.setVisible(false);
        } else if (evt.getPropertyName().equals(WizardModel.BACK_BUTTON_TEXT_PROPERTY)) {
            backButton.setText(evt.getNewValue().toString());
            finishButton2.setVisible(false);
        } else if (evt.getPropertyName().equals(WizardModel.CANCEL_BUTTON_TEXT_PROPERTY)) {
            cancelButton.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardModel.FINISH2_BUTTON_TEXT_PROPERTY)) {
            finishButton2.setText(evt.getNewValue().toString());
        } else if (evt.getPropertyName().equals(WizardModel.NEXT_FINISH_BUTTON_ENABLED_PROPERTY)) {
            nextButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
            finishButton2.setVisible(true);
        } else if (evt.getPropertyName().equals(WizardModel.BACK_BUTTON_ENABLED_PROPERTY)) {
            backButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardModel.CANCEL_BUTTON_ENABLED_PROPERTY)) {
            cancelButton.setEnabled(((Boolean)evt.getNewValue()).booleanValue());
        } else if (evt.getPropertyName().equals(WizardModel.NEXT_FINISH_BUTTON_ICON_PROPERTY)) {
            nextButton.setIcon((Icon)evt.getNewValue());
        } else if (evt.getPropertyName().equals(WizardModel.BACK_BUTTON_ICON_PROPERTY)) {
            backButton.setIcon((Icon)evt.getNewValue());
        } else if (evt.getPropertyName().equals(WizardModel.CANCEL_BUTTON_ICON_PROPERTY)) {
            cancelButton.setIcon((Icon)evt.getNewValue());
        }
    }

    public Object getReturnObject() {
        return returnObject;
    }

    public void setReturnObject(Object obj){
        returnObject = obj;
    }
    /**
     * Closes the dialog and sets the return code to the integer parameter.
     * @param code The return code.
     */
    void close(int code) {
        returnCode = code;
        if (returnCode == Wizard.FINISH_RETURN_CODE)
            _callBackObject.finishAction(getReturnObject());
        else if (returnCode == Wizard.FINISH2_RETURN_CODE)
            _callBackObject.finishAction2(getReturnObject());
        wizardDialog.dispose();
    }

    public boolean getBackButtonEnabled() {
        return wizardModel.getBackButtonEnabled().booleanValue();
    }

    public void setBackButtonEnabled(boolean newValue) {
        wizardModel.setBackButtonEnabled(new Boolean(newValue));
    }

    public boolean getNextButtonEnabled() {
        return wizardModel.getNextFinishButtonEnabled().booleanValue();
    }

    public void setNextButtonEnabled(boolean newValue) {
        wizardModel.setNextFinishButtonEnabled(new Boolean(newValue));
    }

    public boolean getCancelButtonEnabled() {
        return wizardModel.getCancelButtonEnabled().booleanValue();
    }

    public void setCancelButtonEnabled(boolean newValue) {
        wizardModel.setCancelButtonEnabled(new Boolean(newValue));
    }

    public void registerWizardPanel(Object id, WizardPanelDescriptor panel) {
        mainPanel.add(panel.getPanelComponent(), id.toString());
        //  Set a callback to the current wizard.
        panel.setWizard(this);
        //  Place a reference to it in the model.
        wizardModel.registerPanel(id, panel);
        if (panel.getBackPanelDescriptorId() != null){
            WizardPanelDescriptor backPanel = wizardModel.getPanelDescriptorBy(panel.getBackPanelDescriptorId());
            if (backPanel != null) backPanel.setNextPanelIdentifier(id);
        }
    }

    public void setCurrentPanel(Object id) {
        if (id == null)
            close(ERROR_RETURN_CODE);

        WizardPanelDescriptor oldPanelDescriptor = wizardModel.getCurrentPanelDescriptor();
        if (oldPanelDescriptor != null)
            oldPanelDescriptor.aboutToHidePanel();

        wizardModel.setCurrentPanel(id);
        wizardModel.getCurrentPanelDescriptor().aboutToDisplayPanel();

        //  Show the panel in the dialog.
        cardLayout.show(mainPanel, id.toString());
        wizardModel.getCurrentPanelDescriptor().displayingPanel();
    }

    /**
     * This method initializes the components for the wizard dialog: it creates a JDialog
     * as a CardLayout panel surrounded by a small amount of space on each side, as well
     * as three buttons at the bottom.
     */
    private void initComponents() {
        wizardModel.addPropertyChangeListener(this);
        wizardController = new WizardController(this);

        wizardDialog.getContentPane().setLayout(new BorderLayout());
        wizardDialog.addWindowListener(this);

        //  Create the outer wizard panel, which is responsible for three buttons:
        //  Next, Back, and Cancel. It is also responsible a JPanel above them that
        //  uses a CardLayout layout manager to display multiple panels in the
        //  same spot.

        JPanel buttonPanel = new JPanel();
        JSeparator separator = new JSeparator();
        Box buttonBox = new Box(BoxLayout.X_AXIS);

        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        //mainPanel.setMinimumSize(new Dimension(200, 300));

        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        backButton = new JButton();
        nextButton = new JButton();
        finishButton2 = new JButton();
        cancelButton = new JButton();

        setButtonSize(90, 25);

        backButton.setActionCommand(BACK_BUTTON_ACTION_COMMAND);
        nextButton.setActionCommand(NEXT_BUTTON_ACTION_COMMAND);
        cancelButton.setActionCommand(CANCEL_BUTTON_ACTION_COMMAND);
        finishButton2.setActionCommand(FINISH_BUTTON_ACTION_COMMAND2);

        backButton.addActionListener(wizardController);
        nextButton.addActionListener(wizardController);
        cancelButton.addActionListener(wizardController);
        finishButton2.addActionListener(wizardController);

        //  Create the buttons with a separator above them, then place them
        //  on the east side of the panel with a small amount of space between
        //  the back and the next button, and a larger amount of space between
        //  the next button and the cancel button.

        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(separator, BorderLayout.NORTH);

        buttonBox.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        buttonBox.add(backButton);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(nextButton);
        buttonBox.add(Box.createHorizontalStrut(10));
        buttonBox.add(finishButton2);
        finishButton2.setVisible(false);
        buttonBox.add(Box.createHorizontalStrut(30));
        buttonBox.add(cancelButton);
        cancelButton.setSize(170, 22);
        buttonBox.doLayout();

        buttonPanel.add(buttonBox, java.awt.BorderLayout.EAST);

        wizardDialog.getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);
        wizardDialog.getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);
        //wizardDialog.setMinimumSize(new Dimension(300, 120));
        wizardDialog.getGlassPane().addMouseListener(new MouseAdapter() {
            private void killEvent(MouseEvent e) {
                if(lock != null) {
                    e.consume();
                }
            }
            public void mouseClicked(MouseEvent e) {
                killEvent(e);
            }
            public void mousePressed(MouseEvent e) {
                killEvent(e);
            }
            public void mouseReleased(MouseEvent e) {
                killEvent(e);
            }
        });
    }

    public void windowClosing(WindowEvent e) {
        if (lock != null) {
            lock.dispose();
        }
        returnCode = CANCEL_RETURN_CODE;
    }

    public void windowActivated(WindowEvent e) {
        if (lock != null) {
            lock.toFront();
        }
    }

    static {
        try {
            ResourceBundle rb = PropertyResourceBundle.getBundle("wzd");

            BACK_TEXT = rb.getString("button.back.title");
            NEXT_TEXT = rb.getString("button.next.title");
            CANCEL_TEXT = rb.getString("button.cancel.title");
            FINISH_TEXT = rb.getString("button.finish.title");
            FINISH_TEXT2 = rb.getString("button.finish2.title");

            //BACK_ICON = new ImageIcon("/backIcon.gif");
            //NEXT_ICON = new ImageIcon("/nextIcon.gif");
            //CANCEL_ICON = new ImageIcon("/cancelIcon.gif");
            //FINISH_ICON = new ImageIcon("/finishIcon.gif");
        }
        catch (MissingResourceException mre) {
            System.out.println(mre);
            System.exit(1);
        }
    }

    public void setPrefferedSize(int width, int height){
        x = width;
        y = height;
    }

    public int getWidth() {
        return x;
    }

    public int getHeight() {
        return y;
    }

    public String getInitialTitle() {
        return initialTitle;
    }

    public void setInitialTitle(String initialTitle) {
        if (wizardDialog.getTitle() == null || wizardDialog.getTitle().length() == 0) setTitle(initialTitle);
        this.initialTitle = initialTitle;
    }

    public int getMinimalWidth() {
        return minimalWidth;
    }

    public void setMinimalWidth(int minimalWidth) {
        this.minimalWidth = minimalWidth;
    }

    public int getMinimalHeight() {
        return minimalHeight;
    }

    public void setMinimalHeight(int minimalHeight) {
        this.minimalHeight = minimalHeight;
    }

    public WizardController getController() {
        return wizardController;
    }

    private void setButtonSize(int w, int h){
//        backButton.setMinimumSize(new Dimension(w,h));
//        nextButton.setMinimumSize(new Dimension(w,h));
//        finishButton2.setMinimumSize(new Dimension(w,h));
//        cancelButton.setMinimumSize(new Dimension(w,h));

        backButton.setPreferredSize(new Dimension(w,h));
        nextButton.setPreferredSize(new Dimension(w,h));
        finishButton2.setPreferredSize(new Dimension(w,h));
        cancelButton.setPreferredSize(new Dimension(w,h));

        backButton.setMaximumSize(new Dimension(w,h));
        nextButton.setMaximumSize(new Dimension(w,h));
        finishButton2.setMaximumSize(new Dimension(w,h));
        cancelButton.setMaximumSize(new Dimension(w,h));
    }
}
