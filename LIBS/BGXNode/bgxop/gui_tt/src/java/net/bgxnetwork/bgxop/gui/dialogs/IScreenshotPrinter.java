package net.bgx.bgxnetwork.bgxop.gui.dialogs;

import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;

/**
 * Class IScreenshotPrinter
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public interface IScreenshotPrinter {

  void showPrintDialog(BufferedImage image, PageFormat page);

  PageFormat getPageFormat();

}
