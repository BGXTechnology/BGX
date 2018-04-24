package net.bgx.bgxnetwork.bgxop.wizard;

import net.bgx.bgxnetwork.bgxop.wizard.exception.WizardPanelNotEnoughParameters;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * User: A.Borisenko
 * Date: 18.01.2007
 * Time: 9:59:24
 */
public class WizardPanelCaseDescriptor extends WizardPanelDescriptor implements ActionListener {
    private ButtonGroup _buttonGrp;
    private int _selectedButton = -1;
    private ArrayList<Object> _ids;
    private int _width = 0;
    private int _height = 0;

    public WizardPanelCaseDescriptor(Object id, ArrayList<String> caseTitles, ArrayList<Object> panelsIds, Object backId, String overview) {
        super(id, (JPanel)null, backId);
        if (caseTitles.size() != panelsIds.size()) throw new WizardPanelNotEnoughParameters();

        initCasePanel(caseTitles, panelsIds, overview);
    }

    private void initCasePanel(ArrayList<String> caseTitles, ArrayList<Object> nextIds, String overview){
        JPanel casePanel = new JPanel(new GridBagLayout());
        casePanel.add(new JLabel(overview), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));

        _buttonGrp = new ButtonGroup();
        _ids = new ArrayList<Object>();
        int i = 1;
        boolean first = true;
        for (String title : caseTitles){
            JRadioButton rb = new JRadioButton(title, first);
            if (first) first = false;
            rb.addActionListener(this);
            _buttonGrp.add(rb);
            casePanel.add(rb, new GridBagConstraints(0, i, 1, 1, 0.0, 0.0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 5, 5, 5), 0, 0));
            _ids.add(nextIds.get(_buttonGrp.getButtonCount()-1));
            i++;
        }
        setPanelComponent(casePanel);
        setWidth((int)casePanel.getPreferredSize().getWidth()+20);
        setHeight((int)casePanel.getPreferredSize().getHeight()+20);
        _selectedButton = 0;
    }

    //return null if button should be disabled
    public Object getNextPanelDescriptorId() {
        if (_selectedButton < 0) return null;
        return _ids.get(_selectedButton);
    }

    public void actionPerformed(ActionEvent e) {
        setNextButtonAccordingToRadioButton();
    }


    private void setNextButtonAccordingToRadioButton() {
        boolean isEnabled = false;
        _selectedButton = -1;
        Enumeration enumBut = _buttonGrp.getElements();
        while (enumBut.hasMoreElements()){
            JRadioButton rb = (JRadioButton)enumBut.nextElement();
            _selectedButton ++;
            if (rb.isSelected()) {
                isEnabled = true;
                break;
            }
        }
        //getWizard().setNextButtonEnabled(isEnabled);
    }

    public void aboutToDisplayPanel() {
        int minW = (int)getWizard().getWizardDialog().getMinimumSize().getWidth();
        int minH = (int)getWizard().getWizardDialog().getMinimumSize().getHeight();
        if (_width<minW) _width = minW;
        if (_height<minH) _height = minH;

        if (_width < (int)getWizard().getMinimalWidth()) _width = (int)getWizard().getMinimalWidth();
        if (_height < (int)getWizard().getMinimalHeight()) _height = (int)getWizard().getMinimalHeight();
        //set panel title
        getWizard().setTitle(getWizard().getInitialTitle() +": " + getTitle());

        getWizard().getWizardDialog().setPreferredSize(new Dimension(_width, _height+100));
        getWizard().getWizardDialog().pack();
        getWizard().getWizardDialog().setLocationRelativeTo(null);
    }

}
