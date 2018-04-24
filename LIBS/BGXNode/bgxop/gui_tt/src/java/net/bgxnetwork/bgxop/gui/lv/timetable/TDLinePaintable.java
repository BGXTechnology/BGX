/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/TDLinePaintable.java#1 $
$DateTime: 2007/08/06 17:28:33 $
$Change: 20537 $
$Author: a.borisenko $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;

import edu.uci.ics.jung.visualization.VisualizationViewer;

class TDLinePaintable implements VisualizationViewer.Paintable {
    private Stroke stroke = new BasicStroke(1);
    private Paint paint = Color.BLACK;
    private boolean useTransform = false;
    private int x1 = 0;
    private int x2 = 0;
    private int y1 = 0;
    private int y2 = 0;

    public TDLinePaintable() {
    }
    public TDLinePaintable(Stroke stroke, Paint paint, boolean useTransform, int x1, int y1, int x2, int y2) {
        this.stroke = stroke;
        this.paint = paint;
        this.useTransform = useTransform;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    public void setPaint(Paint paint) {
        this.paint = paint;
    }
    public void setCoordinates(int x1, int y1, int x2, int y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }
    public void shouldUseTransform(boolean useTransform) {
        this.useTransform = useTransform;
    }
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Paint oldPaint = g2d.getPaint();
        Stroke oldStroke = g2d.getStroke();
        g2d.setPaint(paint);
        g2d.setStroke(stroke);
        g2d.drawLine(x1, y1, x2, y2);
        g2d.setPaint(oldPaint);
        g2d.setStroke(oldStroke);
    }
    public boolean useTransform() {
        return useTransform;
    }
}