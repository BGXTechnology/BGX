package net.bgx.bgxnetwork.bgxop.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * User: A.Borisenko
 * Date: 16.01.2007
 * Time: 16:02:24
 * To change this template use File | Settings | File Templates.
 */
public class WizardPanelDescriptor {
    private Wizard wizard;
    private Component _targetPanel;
    private JScrollPane _viewerPanel;

    private Object _panelIdentifier;
    private Object _nextPanelIdentifier = null;
    private Object _backPanelIdentifier = null;
    private int _width = 0;
    private int _height = 0;

    private boolean _isFinal = false;
    private String _panelTitle = "";

    public WizardPanelDescriptor(Object id, Component panel, Object backId) {
        _panelIdentifier = id;
        _targetPanel = panel;
        _viewerPanel = new JScrollPane(panel);
        if (panel != null){
            _width = (int)panel.getPreferredSize().getWidth();
            _height = (int)panel.getPreferredSize().getHeight();
        }
        _backPanelIdentifier = backId;
    }

    public void setNextPanelIdentifier(Object id){
        _nextPanelIdentifier = id;
    }

    public void setBackPanelIdentifier(Object id){
        _backPanelIdentifier = id;
    }

    public void setIsFinal(boolean b){
        _isFinal = b;
    }

    public boolean isFinal(){
        return _isFinal;
    }
    
    /**
     * Returns to java.awt.Component that serves as the actual panel.
     * @return A reference to the java.awt.Component that serves as the panel
     */
    public final Component getPanelComponent() {
        return _viewerPanel;
    }

    public final Component getRealPanelComponent() {
        return _targetPanel;
    }

    /**
     * Sets the panel's component as a class that extends java.awt.Component
     * @param panel java.awt.Component which serves as the wizard panel
     */
    public final void setPanelComponent(Component panel) {
        _viewerPanel = new JScrollPane(panel);
    }

    /**
     * Returns the unique Object-based identifier for this panel descriptor.
     * @return The Object-based identifier
     */
    public final Object getPanelDescriptorIdentifier() {
        return _panelIdentifier;
    }

    /**
     * Sets the Object-based identifier for this panel. The identifier must be unique
     * from all the other identifiers in the panel.
     * @param id Object-based identifier for this panel.
     */
    public final void setPanelDescriptorIdentifier(Object id) {
        _panelIdentifier = id;
    }

    final void setWizard(Wizard w) {
        wizard = w;
    }

    /**
     * Returns a reference to the Wizard component.
     * @return The Wizard class hosting this descriptor.
     */
    public final Wizard getWizard() {
        return wizard;
    }

    /**
     * Returns a reference to the current WizardModel for this Wizard component.
     * @return The current WizardModel for this Wizard component.
     */
    public WizardModel getWizardModel() {
        return wizard.getModel();
    }

    //return null if button should be disabled
    public Object getNextPanelDescriptorId() {
        return _nextPanelIdentifier;
    }

    //return null if button should be disabled
    public Object getBackPanelDescriptorId() {
        return _backPanelIdentifier;
    }

    /**
     * ����� ���������� ����� ������������ ������
     */
    public void aboutToDisplayPanel() {
        int minH = getWizard().getHeight();
        //if (_height<minH-60) minH = _height;

        //set panel title
        getWizard().setTitle(getWizard().getInitialTitle() + ": "+ getTitle());
        //getWizard().getWizardDialog().setPreferredSize(new Dimension(getWizard().getWidth(), minH+60));
        getWizard().getWizardDialog().setPreferredSize(new Dimension(getWizard().getWidth(), getWizard().getHeight()));
        getWizard().getWizardDialog().setVisible(false);
        getWizard().getWizardDialog().pack();
        getWizard().getWizardDialog().setLocationRelativeTo(null);
        getWizard().getWizardDialog().setVisible(true);
    }

    /**
     * ����������� ������
     */
    public void displayingPanel() {
        //����������� ������, �������� ���, ������ ����������� ����� ��� �����
    }

    //  Override this method in the subclass if you wish it to be called
    //  just before the panel is switched to another or finished.

    public void aboutToHidePanel() {}

    public void setWidth(int w){
        _width = w;
    }

    public void setHeight(int h){
        _height = h;
    }

    public Object getReturnObject(){
        if (_targetPanel instanceof WizardQueryPanel)
            return ((WizardQueryPanel)_targetPanel).getDataFromPanel();
        return null;
    }

    public void setTitle(String ttl){
        _panelTitle = ttl;
    }

    public String getTitle(){
        return _panelTitle;
    }
}