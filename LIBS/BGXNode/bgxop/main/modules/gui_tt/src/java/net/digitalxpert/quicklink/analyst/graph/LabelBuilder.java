package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.Vertex;
import ru.zsoft.jung.viewer.BufferedViewer;

import java.awt.*;
import java.util.ResourceBundle;

/**
 * User: O.Gerasimenko
 * Date: 14.02.2007
 * Time: 13:45:04
 * To change this template use File | Settings | File Templates.
 */
public abstract class LabelBuilder {
    protected ResourceBundle rb;
    protected Font vertexFont;
    protected BufferedViewer view;
    protected final String IMAGE_PREF = "#image";
    protected final String TEXT_PREF  = "#text";
    protected final String LABEL      = "Label";

    protected LabelBuilder(BufferedViewer view, ResourceBundle rb, Font font) {
        this.rb = rb;
        this.view = view;
        this.vertexFont = font;
    }

    public String getLabel(Vertex v) {
        return null;
    }

    public int getNumLines(Vertex v, int widthRestangle) {
        return 0;
    }

    public void drawLabel(Graphics2D g, Vertex v, Rectangle r) {
    }

    ;

    protected void fillRectangle(Graphics2D g, Vertex v, Rectangle r) {

        Color textColor = view.getBackground();
        if (textColor == null)
            textColor = Color.WHITE;
        g.setColor(textColor);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    protected String buildLabel(String pattern, Vertex v) {
        return null;
    }

}
