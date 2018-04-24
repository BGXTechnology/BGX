package net.bgx.bgxnetwork.bgxop.gui;

import net.bgx.bgxnetwork.transfer.query.Query;
import net.bgx.bgxnetwork.transfer.query.QueryParameterType;
import net.bgx.bgxnetwork.transfer.query.QueryType;
import net.bgx.bgxnetwork.bgxop.uitools.ui.TransparentTreeCellRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;
import java.util.PropertyResourceBundle;

/**
 * Class ProfileTreeCellRenderer
 *
 * @author Yerokhin Yuri
 *         copyright by Zsoft Company
 * @version 1.0
 */

public class ProfileTreeCellRenderer extends TransparentTreeCellRenderer {

    protected ResourceBundle rb = PropertyResourceBundle.getBundle("gui");

    public ProfileTreeCellRenderer() {
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                  boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component c = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (!(value instanceof ProfilePanel.QueryTreeNode)) {
            if (leaf) setIcon(getOpenIcon());
        }
        else if (value instanceof ProfilePanel.QueryTreeNode) {
            Query q = ((ProfilePanel.QueryTreeNode) value).getQuery();
            if (q.getQueryType() != null)
            if (q.getQueryType().getId() == QueryType.VISUALIZATION)
                switch (q.getQueryStatus()) {
                    case Saved:
                        setIcon(ResourceLoader.getInstance().getIconByResource(rb, "tree.savedQuery.img"));
                        break;
                    case Executing:
                        setIcon(ResourceLoader.getInstance().getIconByResource(rb, "tree.executingQuery.img"));
                        break;
                    case Ready:
                        setIcon(ResourceLoader.getInstance().getIconByResource(rb, "tree.readyQuery.img"));
                        break;
                    case Error:
                        setIcon(ResourceLoader.getInstance().getIconByResource(rb, "tree.errorQuery.img"));
                        break;
                    case Limited:
                        setIcon(ResourceLoader.getInstance().getIconByResource(rb, "tree.limitedQuery.img"));
                        break;
                }
            else
                setIcon(null);
        }
        return c;

    }


    public void paint(Graphics g) {
        super.paint(g);
    }
}
