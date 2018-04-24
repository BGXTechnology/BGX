package ru.zsoft.jung.viewer;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import net.bgx.bgxnetwork.bgxop.tools.GraphDataUtil;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.AbstractVertexShapeFunction;

/**
 * Class CustomShapeFunction
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class CustomShapeFunction extends AbstractVertexShapeFunction{
    private static final int DEFAULT_HEIGHT = 32;
	protected static final Rectangle2D edgeRect = new Rectangle2D.Float(-60, -25, 125, DEFAULT_HEIGHT);
	public CustomShapeFunction(){
	}
	public Shape getShape(Vertex vertex){
		int height = GraphDataUtil.getHeight(vertex);
		if(height == 0)
			height = DEFAULT_HEIGHT;
		return new Rectangle2D.Float(-60, -25, 120, height);
	}
	public Shape getEdgeShape(Vertex vertex){
		return edgeRect;
	}
	public void setShape(Vertex vertex, int h){
		GraphDataUtil.setHeight(vertex, DEFAULT_HEIGHT + h);
	}
}
