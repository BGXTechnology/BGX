package net.bgx.bgxnetwork.bgxop.wizard;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import net.bgx.bgxnetwork.bgxop.uitools.MessageDialogs;
import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardInvalidQueryNameException;

/**
 * User: A.Borisenko
 * Date: 16.01.2007
 * Time: 11:54:04
 */
public class WizardController implements ActionListener {
    protected ResourceBundle rb = PropertyResourceBundle.getBundle("wzd");
    private Wizard wizard;
    private ActionEvent latestActionEvent = null;

    public WizardController(Wizard w) {
        wizard = w;
    }

    public void actionPerformed(ActionEvent e) {
        latestActionEvent = e;
        if (e.getActionCommand().equals(Wizard.CANCEL_BUTTON_ACTION_COMMAND))
            cancelButtonPressed();
        else if (e.getActionCommand().equals(Wizard.BACK_BUTTON_ACTION_COMMAND))
            backButtonPressed();
        else if (e.getActionCommand().equals(Wizard.NEXT_BUTTON_ACTION_COMMAND))
            nextButtonPressed();
        else if (e.getActionCommand().equals(Wizard.FINISH_BUTTON_ACTION_COMMAND2))
            finish2ButtonPressed();
    }

    public void continueWithAction() {
        if (latestActionEvent != null) {
            actionPerformed(latestActionEvent);
        }
    }

    private void cancelButtonPressed() {
        wizard.close(Wizard.CANCEL_RETURN_CODE);
    }

    private void nextButtonPressed() {
        WizardModel model = wizard.getModel();
        WizardPanelDescriptor descriptor = model.getCurrentPanelDescriptor();

        //WizardPanelDescriptor nextPanelDescriptor = (WizardPanelDescriptor)descriptor.getNextPanelDescriptorId();
        Object nextPanelId = descriptor.getNextPanelDescriptorId();

        if (nextPanelId == null ) {
            try{
                wizard.setReturnObject(descriptor.getReturnObject());
                wizard.close(Wizard.FINISH_RETURN_CODE);
            }
            catch(WizardInvalidQueryNameException exception) {
                //
                WizardQueryPanel panel = (WizardQueryPanel) descriptor.getRealPanelComponent();
                panel.handleException(exception, wizard);
                //
            }
            catch(Exception e){
                MessageDialogs.generalError((Frame)null, null, rb.getString("warning.msg.requiredParameter.isAbsent"), rb.getString("warning.title.requiredParameter.isAbsent"));
            }
        }
        else {
            wizard.setCurrentPanel(nextPanelId);
        }

    }

    private void backButtonPressed() {
        WizardModel model = wizard.getModel();
        WizardPanelDescriptor descriptor = model.getCurrentPanelDescriptor();

        //  Get the descriptor that the current panel identifies as the previous
        //  panel, and display it.

        Object backPanelDescriptor = descriptor.getBackPanelDescriptorId();
        wizard.setCurrentPanel(backPanelDescriptor);
    }

    private void finish2ButtonPressed(){
        WizardModel model = wizard.getModel();
        WizardPanelDescriptor descriptor = model.getCurrentPanelDescriptor();
        Object nextPanelId = descriptor.getNextPanelDescriptorId();

        if (nextPanelId == null ) {
            try{
                wizard.setReturnObject(descriptor.getReturnObject());
                wizard.close(Wizard.FINISH2_RETURN_CODE);
            }
            catch(WizardInvalidQueryNameException exception) {
                //
                WizardQueryPanel panel = (WizardQueryPanel) descriptor.getRealPanelComponent();
                panel.handleException(exception, wizard);
                //
            }
            catch(Exception e){
                MessageDialogs.generalError((Frame)null, null, rb.getString("warning.msg.requiredParameter.isAbsent"), rb.getString("warning.title.requiredParameter.isAbsent"));
            }
        }
        else {
            wizard.setCurrentPanel(nextPanelId);
        }
    }

    void resetButtonsToPanelRules() {

        //  Reset the buttons to support the original panel rules,
        //  including whether the next or back buttons are enabled or
        //  disabled, or if the panel is finishable.

        WizardModel model = wizard.getModel();
        WizardPanelDescriptor descriptor = model.getCurrentPanelDescriptor();

        model.setCancelButtonText(Wizard.CANCEL_TEXT);
        model.setFinish2ButtonText(Wizard.FINISH_TEXT2);
        //model.setCancelButtonIcon(Wizard.CANCEL_ICON);

        //  If the panel in question has another panel behind it, enable
        //  the back button. Otherwise, disable it.

        model.setBackButtonText(Wizard.BACK_TEXT);
        //model.setBackButtonIcon(Wizard.BACK_ICON);

        if (descriptor.getBackPanelDescriptorId() != null)
            model.setBackButtonEnabled(Boolean.TRUE);
        else
            model.setBackButtonEnabled(Boolean.FALSE);

        //  If the panel in question has one or more panels in front of it,
        //  enable the next button. Otherwise, disable it.

        if (descriptor.getNextPanelDescriptorId() != null)
            model.setNextFinishButtonEnabled(Boolean.TRUE);
        else
            model.setNextFinishButtonEnabled(Boolean.FALSE);

        //  If the panel in question is the last panel in the series, change
        //  the Next button to Finish. Otherwise, set the text back to Next.

        if (descriptor.isFinal()) {

            model.setNextFinishButtonText(Wizard.FINISH_TEXT);
            //model.setNextFinishButtonIcon(Wizard.FINISH_ICON);
            model.setNextFinishButtonEnabled(Boolean.TRUE);
        } else {
            model.setNextFinishButtonText(Wizard.NEXT_TEXT);
            //model.setNextFinishButtonIcon(Wizard.NEXT_ICON);
        }

    }
}
