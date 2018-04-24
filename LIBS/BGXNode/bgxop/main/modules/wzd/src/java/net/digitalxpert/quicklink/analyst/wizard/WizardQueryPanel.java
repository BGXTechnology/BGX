package net.bgx.bgxnetwork.bgxop.wizard;

import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotEnoughParameters;
import net.bgx.bgxnetwork.exception.query.QueryBusinesException;

/**
 * Date: 12.02.2007
 * Time: 12:47:10
 */
public interface WizardQueryPanel {
    public Object getDataFromPanel() throws WizardPanelNotEnoughParameters;
    public void setDataToPanel(Object obj) throws QueryBusinesException;
    public void handleException(Exception e, Wizard w);
}
