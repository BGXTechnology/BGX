package net.bgx.bgxnetwork.bgxop.graph;

import edu.uci.ics.jung.graph.decorators.*;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.algorithms.blockmodel.GraphCollapser;

import java.awt.*;

/**
 * User: O.Gerasimenko
 * Date: 25.06.2007
 * Time: 18:55:30
 */
public class CustomVertexSizeFunction  extends EllipseVertexShapeFunction
        implements VertexSizeFunction, VertexAspectRatioFunction {
        protected boolean stretch = false;
        protected boolean scale = false;
        protected boolean funny_shapes = false;
        protected NumberVertexValue voltages;

        public CustomVertexSizeFunction (NumberVertexValue voltages)
        {
            super();
            setSizeFunction(this);
            setAspectRatioFunction(this);
            this.voltages = voltages;
            setSizeFunction(this);
            setAspectRatioFunction(this);
        }

        public void setStretching(boolean stretch)
        {
            this.stretch = stretch;
        }

        public void setScaling(boolean scale)
        {
            this.scale = scale;
        }

        public void useFunnyShapes(boolean use)
        {
            this.funny_shapes = use;
        }

        public int getSize(Vertex v)
        {
            if (scale)
                return (int)(voltages.getNumber(v).doubleValue() * 30) + 20;
            else
                return 40;
        }

        public float getAspectRatio(Vertex v)
        {
            if (stretch)
                return (float)(v.inDegree() + 1) / (v.outDegree() + 1);
            else
                return 1.0f;
        }

        public Shape getShape(Vertex v)
        {
            if (v instanceof GraphCollapser.CollapsedVertex){
               /* if (v.degree() < 5)
                {
                    int sides = Math.max(v.degree(), 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else*/
                    return factory.getRegularStar(v, 7);
            }
            else
                return factory.getEllipse(v);
        }

    }

