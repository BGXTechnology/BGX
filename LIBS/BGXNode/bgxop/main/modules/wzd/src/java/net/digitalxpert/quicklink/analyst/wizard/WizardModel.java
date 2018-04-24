package net.bgx.bgxnetwork.bgxop.wizard;

import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotFoundException;

import javax.swing.*;
import java.util.HashMap;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

/**
 * User: A.Borisenko
 * Date: 16.01.2007
 * Time: 11:53:49
 * To change this template use File | Settings | File Templates.
 */
public class WizardModel {
    public static final String CURRENT_PANEL_DESCRIPTOR_PROPERTY = "currentPanelDescriptorProperty";

    public static final String BACK_BUTTON_TEXT_PROPERTY = "backButtonTextProperty";
    public static final String BACK_BUTTON_ICON_PROPERTY = "backButtonIconProperty";
    public static final String BACK_BUTTON_ENABLED_PROPERTY = "backButtonEnabledProperty";
    public static final String NEXT_FINISH_BUTTON_TEXT_PROPERTY = "nextButtonTextProperty";
    public static final String NEXT_FINISH_BUTTON_ICON_PROPERTY = "nextButtonIconProperty";
    public static final String NEXT_FINISH_BUTTON_ENABLED_PROPERTY = "nextButtonEnabledProperty";
    public static final String CANCEL_BUTTON_TEXT_PROPERTY = "cancelButtonTextProperty";
    public static final String CANCEL_BUTTON_ICON_PROPERTY = "cancelButtonIconProperty";
    public static final String CANCEL_BUTTON_ENABLED_PROPERTY = "cancelButtonEnabledProperty";
    public static final String FINISH2_BUTTON_TEXT_PROPERTY = "finish2ButtonTextProperty";

    private WizardPanelDescriptor currentPanel;
    private HashMap panelHashMap;

    private HashMap buttonTextHashmap;
    private HashMap buttonIconHashmap;
    private HashMap buttonEnabledHashmap;

    private PropertyChangeSupport propertyChangeSupport;


    /**
     * Default constructor.
     */
    public WizardModel() {

        panelHashMap = new HashMap();

        buttonTextHashmap = new HashMap();
        buttonIconHashmap = new HashMap();
        buttonEnabledHashmap = new HashMap();

        propertyChangeSupport = new PropertyChangeSupport(this);

    }

    /**
     * Returns the currently displayed WizardPanelDescriptor.
     * @return The currently displayed WizardPanelDescriptor
     */
    WizardPanelDescriptor getCurrentPanelDescriptor() {
        return currentPanel;
    }

    protected WizardPanelDescriptor getPanelDescriptorBy(Object id){
        return (WizardPanelDescriptor)panelHashMap.get(id);    
    }

    /**
     * Registers the WizardPanelDescriptor in the model using the Object-identifier specified.
     * @param id Object-based identifier
     * @param descriptor WizardPanelDescriptor that describes the panel
     */
     void registerPanel(Object id, WizardPanelDescriptor descriptor) {
        panelHashMap.put(id, descriptor);
     }

    /**
     * Sets the current panel to that identified by the Object passed in.
     * @param id Object-based panel identifier
     * @return boolean indicating success or failure
     */
     boolean setCurrentPanel(Object id) {
        //  First, get the hashtable reference to the panel that should
        //  be displayed.
        WizardPanelDescriptor nextPanel =
            (WizardPanelDescriptor)panelHashMap.get(id);

        //  If we couldn't find the panel that should be displayed, return
        //  false.
        if (nextPanel == null)
            throw new WizardPanelNotFoundException();

        WizardPanelDescriptor oldPanel = currentPanel;
        currentPanel = nextPanel;

        if (oldPanel != currentPanel)
            firePropertyChange(CURRENT_PANEL_DESCRIPTOR_PROPERTY, oldPanel, currentPanel);

        return true;
    }
    Object getBackButtonText() {
        return buttonTextHashmap.get(BACK_BUTTON_TEXT_PROPERTY);
    }

    void setBackButtonText(Object newText) {

        Object oldText = getBackButtonText();
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(BACK_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(BACK_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    Object getNextFinishButtonText() {
        return buttonTextHashmap.get(NEXT_FINISH_BUTTON_TEXT_PROPERTY);
    }

    void setNextFinishButtonText(Object newText) {

        Object oldText = getNextFinishButtonText();
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(NEXT_FINISH_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(NEXT_FINISH_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    Object getCancelButtonText() {
        return buttonTextHashmap.get(CANCEL_BUTTON_TEXT_PROPERTY);
    }

    Object getFinish2ButtonText(){
        return buttonTextHashmap.get(FINISH2_BUTTON_TEXT_PROPERTY);
    }

    void setCancelButtonText(Object newText) {

        Object oldText = getCancelButtonText();
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(CANCEL_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(CANCEL_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    void setFinish2ButtonText(Object newText){
        Object oldText = getFinish2ButtonText();
        if (!newText.equals(oldText)) {
            buttonTextHashmap.put(FINISH2_BUTTON_TEXT_PROPERTY, newText);
            firePropertyChange(FINISH2_BUTTON_TEXT_PROPERTY, oldText, newText);
        }
    }

    Icon getBackButtonIcon() {
        return (Icon)buttonIconHashmap.get(BACK_BUTTON_ICON_PROPERTY);
    }

    void setBackButtonIcon(Icon newIcon) {

        Object oldIcon = getBackButtonIcon();
        if (!newIcon.equals(oldIcon)) {
            buttonIconHashmap.put(BACK_BUTTON_ICON_PROPERTY, newIcon);
            firePropertyChange(BACK_BUTTON_ICON_PROPERTY, oldIcon, newIcon);
        }
    }

    Icon getNextFinishButtonIcon() {
        return (Icon)buttonIconHashmap.get(NEXT_FINISH_BUTTON_ICON_PROPERTY);
    }

    public void setNextFinishButtonIcon(Icon newIcon) {

        Object oldIcon = getNextFinishButtonIcon();
        if (!newIcon.equals(oldIcon)) {
            buttonIconHashmap.put(NEXT_FINISH_BUTTON_ICON_PROPERTY, newIcon);
            firePropertyChange(NEXT_FINISH_BUTTON_ICON_PROPERTY, oldIcon, newIcon);
        }
    }

    Icon getCancelButtonIcon() {
        return (Icon)buttonIconHashmap.get(CANCEL_BUTTON_ICON_PROPERTY);
    }

    void setCancelButtonIcon(Icon newIcon) {

        Icon oldIcon = getCancelButtonIcon();
        if (!newIcon.equals(oldIcon)) {
            buttonIconHashmap.put(CANCEL_BUTTON_ICON_PROPERTY, newIcon);
            firePropertyChange(CANCEL_BUTTON_ICON_PROPERTY, oldIcon, newIcon);
        }
    }


    Boolean getBackButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(BACK_BUTTON_ENABLED_PROPERTY);
    }

    void setBackButtonEnabled(Boolean newValue) {

        Boolean oldValue = getBackButtonEnabled();
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(BACK_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(BACK_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
    }

    Boolean getNextFinishButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(NEXT_FINISH_BUTTON_ENABLED_PROPERTY);
    }

    void setNextFinishButtonEnabled(Boolean newValue) {

        Boolean oldValue = getNextFinishButtonEnabled();
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(NEXT_FINISH_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(NEXT_FINISH_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
    }

    Boolean getCancelButtonEnabled() {
        return (Boolean)buttonEnabledHashmap.get(CANCEL_BUTTON_ENABLED_PROPERTY);
    }

    void setCancelButtonEnabled(Boolean newValue) {

        Boolean oldValue = getCancelButtonEnabled();
        if (newValue != oldValue) {
            buttonEnabledHashmap.put(CANCEL_BUTTON_ENABLED_PROPERTY, newValue);
            firePropertyChange(CANCEL_BUTTON_ENABLED_PROPERTY, oldValue, newValue);
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener p) {
        propertyChangeSupport.addPropertyChangeListener(p);
    }

    public void removePropertyChangeListener(PropertyChangeListener p) {
        propertyChangeSupport.removePropertyChangeListener(p);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    protected void setButtonSize(int w, int h){

    }
}
