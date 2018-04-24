package net.bgx.bgxnetwork.bgxop.uitools;

import javax.swing.plaf.ColorUIResource;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class StyleInstaller
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class StyleInstaller {

  public static void install() {
    //set look and feel
    javax.swing.UIDefaults defaults = javax.swing.UIManager.getDefaults();

    //define fonts
    Font baseFont = null, largerFont = null, boldFont = null;
    String fontfamily = System.getProperty("ui.font.family");
    int normalSize = 11;
    try {
      normalSize = Integer.parseInt(System.getProperty("ui.font.size.normal"));
    } catch (Exception e) {}
    int largerSize = 12;
    try {
      largerSize = Integer.parseInt(System.getProperty("ui.font.size.larger"));
    } catch (Exception e) {}
    if (fontfamily!=null) {
      baseFont = new Font(fontfamily, Font.PLAIN, normalSize);
    }
    if (baseFont==null) {
      baseFont = UIManager.getFont("Label.font").deriveFont(Font.PLAIN, normalSize);
    }
    largerFont = baseFont.deriveFont((float)largerSize);
    boldFont = largerFont.deriveFont(Font.BOLD);

    defaults.put("TextField.font", baseFont);
    defaults.put("FormattedTextField.font", baseFont);
    defaults.put("TextArea.font", baseFont);
    defaults.put("Label.font", baseFont);
    defaults.put("Button.font", baseFont);
    defaults.put("ToggleButton.font", baseFont);
    defaults.put("Tree.font", baseFont);
    defaults.put("CheckBox.font", baseFont);
    defaults.put("RadioButton.font", baseFont);
    defaults.put("ComboBox.font", baseFont);
    defaults.put("Table.font", baseFont);

    defaults.put("Menu.font", largerFont);
    defaults.put("MenuItem.font", largerFont);
    defaults.put("CheckBoxMenuItem.font", largerFont);
    defaults.put("RadioButtonMenuItem.font", largerFont);

    defaults.put("TitledBorder.font", boldFont);
    defaults.put("TableHeader.font", boldFont);
    defaults.put("TabbedPane.font", boldFont);

    //graph font
    defaults.put("graph.vertex.font", baseFont);
    defaults.put("graph.edge.font", boldFont);

    //set UI
    ArrayList gradient = new ArrayList();
    gradient.add(new Float(1));
    gradient.add(new Float(0));
    gradient.add(new ColorUIResource(255,255,255));
    gradient.add(new ColorUIResource(222,241,255));
    gradient.add(new ColorUIResource(222,241,255));
    defaults.put("default.gradient", new Color(222,241,255));
    defaults.put("Panel.background", Color.white);
    defaults.put("PanelUI", "net.bgx.bgxnetwork.bgxop.uitools.ui.PanelGradientUI");
    defaults.put("Tree.background", Color.white);
    defaults.put("TreeUI", "net.bgx.bgxnetwork.bgxop.uitools.ui.TreeGradientUI");
    defaults.put("CheckBoxUI", "net.bgx.bgxnetwork.bgxop.uitools.ui.CheckBoxTransparentUI");
    defaults.put("RadioButtonUI", "net.bgx.bgxnetwork.bgxop.uitools.ui.RadioButtonTransparentUI");
    defaults.put("TabbedPane.unselectedBackground", new Color(222,241,255));
    defaults.put("Viewport.background", new Color(222,241,255));
    defaults.put("SplitPane.background", new Color(233,246,255));
    defaults.put("MenuBar.gradient", gradient);

    defaults.put("Table.background", Color.white);
    defaults.put("TableUI", "net.bgx.bgxnetwork.bgxop.uitools.ui.TableGradientUI");
    defaults.put("TableHeader.background", new Color(200,221,242));
    defaults.put("TableHeader.selected.background", new Color(181,209,238));

    //Strings for standard components
    ResourceBundle br = PropertyResourceBundle.getBundle("uitools");
    setUIDefault(br, "FileChooser.lookInLabelText");
    setUIDefault(br, "FileChooser.fileNameLabelText");
    setUIDefault(br, "FileChooser.filesOfTypeLabelText");
    setUIDefault(br, "FileChooser.upFolderToolTipText");
    setUIDefault(br, "FileChooser.homeFolderToolTipText");
    setUIDefault(br, "FileChooser.newFolderToolTipText");
    setUIDefault(br, "FileChooser.listViewButtonToolTipText");
    setUIDefault(br, "FileChooser.detailsViewButtonToolTipText");
    setUIDefault(br, "FileChooser.cancelButtonText");
    setUIDefault(br, "FileChooser.cancelButtonToolTipText");
  }

  protected static final void setUIDefault(ResourceBundle b, String key) {
    UIManager.put(key, b.getString(key));
  }

}
