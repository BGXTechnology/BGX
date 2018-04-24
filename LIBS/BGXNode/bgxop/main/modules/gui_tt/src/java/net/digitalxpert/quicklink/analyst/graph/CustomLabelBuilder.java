package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.Vertex;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.lv.tools.LVGraphNetworkUtil;
import net.bgx.bgxnetwork.persistence.view.LabelElement;
import net.bgx.bgxnetwork.persistence.view.LabelGraph;
import net.bgx.bgxnetwork.transfer.query.ObjectType;
import ru.zsoft.jung.viewer.BufferedRenderer;
import ru.zsoft.jung.viewer.BufferedViewer;

import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * User: O.Gerasimenko
 * Date: 14.02.2007
 * Time: 13:47:03
 * To change this template use File | Settings | File Templates.
 */
public class CustomLabelBuilder extends LabelBuilder {
    public CustomLabelBuilder(BufferedViewer view, ResourceBundle rb, Font font) {
        super(view, rb, font);
    }

    public String getLabel(Vertex v) {
        net.bgx.bgxnetwork.persistence.metadata.ObjectType typeObject =
                NotationModel.getInstance().getObjectTypes().get(GraphNetworkUtil.getType(v).getValue());
        if (typeObject != null && typeObject.getLabels() != null && typeObject.getLabels().size() > 0) {
            return buildLabel(typeObject.getLabels().iterator().next(), v);

        }
        return null;
    }

    protected String buildLabel(LabelGraph labelGraph, Vertex v) {
        String pattern = labelGraph.getPattern();
        String patternElement;
        Set<LabelElement> labelElements = new TreeSet(new Comparator<LabelElement>() {
            public int compare(LabelElement a1, LabelElement a2) {
                return a1.getSortIndex().compareTo(a2.getSortIndex());
            }
        });

        if (labelGraph.getLabelElements() != null) {
            labelElements.addAll(labelGraph.getLabelElements());
            for (LabelElement labelElement : labelElements) {
                patternElement = labelElement.getPattern();
                if (patternElement.indexOf(IMAGE_PREF) > -1)
                    patternElement = patternElement.replaceFirst(IMAGE_PREF, "data:image/png;base64,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");
                if (patternElement.indexOf(TEXT_PREF) > -1)
                    patternElement = patternElement.replaceFirst(TEXT_PREF, "unknown block");
                if (pattern.indexOf(labelElement.getKeyName() + LABEL) > -1)
                    pattern = pattern.replaceFirst(labelElement.getKeyName() + LABEL, labelElement.getPropertyType().getNameDescShort());
                if (pattern.indexOf(labelElement.getKeyName()) > -1)
                    pattern = pattern.replaceFirst(labelElement.getKeyName(), patternElement);
            }
        }
        return pattern;
    }

    public int getNumLines(Vertex v, int widthRestangle) {
        String name = LVGraphNetworkUtil.getName(v);

//        if (GraphNetworkUtil.getType(v) == ObjectType.UL ||
//            GraphNetworkUtil.getType(v) == ObjectType.Bank){
//            if (kpp == null) kpp = "";
//            name += "\n" + strKpp + " - " + kpp;
//        }
        NameParser parser = new NameParser(name, widthRestangle, view.getFontMetrics(vertexFont));

        return parser.getNumLines();
    }

    public void drawLabel(Graphics2D g, Vertex v, Rectangle r) {

        int h = 0;
        int w = 0;
        ObjectType type = GraphNetworkUtil.getType(v);
        Icon icon = NotationModel.getInstance().getIcon4Object(type);

        if (icon != null) {
            icon.paintIcon(view, g, r.x + 1, r.y + 1);
            h = icon.getIconHeight();
            w = icon.getIconWidth();
        }

        String name = LVGraphNetworkUtil.getName(v);

        g.setColor(Color.BLACK);
        if (name != null)
            g.drawString(name, r.x + r.width / 2 - 10, r.y + r.height + 15);
        Font curFont = g.getFont();
        g.setFont(vertexFont);
        int offset = 0;
        offset = r.height;
        offset = drawIconElement(g, v, r, offset);
        //drawTextInLines(g, name, r, v, offset);
        g.setFont(curFont);
    }

    private int drawIconElement(Graphics2D g, Vertex v, Rectangle r, int offset) {
        int h = 1;
        return h + offset;
    }

    private int drawTextInLines(Graphics2D g, String name, Rectangle r, Vertex v, int offset) {
        FontMetrics metrics = view.getFontMetrics(vertexFont);
        int height = 12;
        if (name != null) {
            NameParser parser = new NameParser(name, r.width, metrics);
            int numLines = parser.getNumLines();
            Rectangle textRect = ((BufferedRenderer) view.getRenderer()).getVertexShapeFunction().getShape(v).getBounds();
            fillRectangle(g, v, new Rectangle(r.x, r.y + offset + 1, r.width, textRect.height - r.height));
            g.setColor(Color.BLACK);

            for (int count = 0; count < numLines; count++) {
                g.drawString(parser.getLine(count), r.x, r.y + offset + height);
                height += metrics.getHeight();
            }
        }
        return height + offset;
    }

}
