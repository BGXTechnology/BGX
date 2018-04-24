package net.bgx.bgxnetwork.bgxop.engine;
import oracle.spatial.network.Link;
import oracle.spatial.network.Node;
import java.io.PrintStream;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;
import net.bgx.bgxnetwork.transfer.query.LinkType;
import net.bgx.bgxnetwork.bgxop.tools.GraphNetworkUtil;
import net.bgx.bgxnetwork.bgxop.gui.IFormatTableModel;
import javax.swing.table.TableModel;
import edu.uci.ics.jung.graph.Vertex;

/**
 * Class DataExporter
 * 
 * @author Yerokhin Yuri copyright by Zsoft Company
 * @version 1.0
 */
public class DataExporter{
    protected static String delim = ",";
    protected static ResourceBundle trb = null;
    protected static ResourceBundle rb = null;
    protected static void loadResources(){
        if(trb == null)
            trb = PropertyResourceBundle.getBundle("transfer");
        if(rb == null)
            rb = PropertyResourceBundle.getBundle("engine");
    }
    public static int exportTable(TableModel tableModel, String fileName) throws FileNotFoundException{
        PrintStream ps = null;
        int counter = 0;
        try{
            ps = new PrintStream(fileName);
            String line = null;
            for(int i = 0; i < tableModel.getColumnCount(); i++)
                if(tableModel.getColumnClass(i) == String.class)
                    if(line == null)
                        line = cellValueToCsvFormat(tableModel.getColumnName(i));
                    else
                        line += delim + cellValueToCsvFormat(tableModel.getColumnName(i));
            ps.println(line);
            for(int j = 0; j < tableModel.getRowCount(); j++){
                line = null;
                for(int i = 0; i < tableModel.getColumnCount(); i++)
                    if(tableModel.getColumnClass(i) == String.class)
                        if(line == null)
                            line = (tableModel.getValueAt(j, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(j, i).toString()) : "");
                        else
                            line += delim
                                    + (tableModel.getValueAt(j, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(j, i).toString())
                                            : "");
                ps.println(line);
                counter++;
            }
        }finally{
            if(ps != null)
                ps.close();
        }
        return counter;
    }
    /*
     * public static int exportLinks(IVertexListTableModel tableModel, String
     * fileName) throws IOException { loadResources(); PrintStream ps = null;
     * int counter = 0; try { ps = new PrintStream(fileName); String line =
     * null; String prefix =
     * rb.getString("DataExporter.linkList.source.prefix")+" "; String suffix = "
     * "+rb.getString("DataExporter.linkList.source.suffix"); for (int i=0; i<tableModel.getColumnCount();
     * i++) if (tableModel.getColumnClass(i)==String.class) if (line==null) line =
     * prefix+tableModel.getColumnName(i)+suffix; else line += delim +
     * prefix+tableModel.getColumnName(i)+suffix; line += delim +
     * rb.getString("DataExporter.linkList.link.type"); prefix =
     * rb.getString("DataExporter.linkList.target.prefix")+" "; suffix = "
     * "+rb.getString("DataExporter.linkList.target.suffix"); for (int i=0; i<tableModel.getColumnCount();
     * i++) if (tableModel.getColumnClass(i)==String.class) if (line==null) line =
     * prefix+tableModel.getColumnName(i)+suffix; else line += delim +
     * prefix+tableModel.getColumnName(i)+suffix; ps.println(line);
     * 
     * Vertex v, v1; LinkType lt; int ln; for (int j=0; j<tableModel.getRowCount();
     * j++) { v = tableModel.getObjectAt(j); Link[] inLinks =
     * GraphNetworkUtil.getNode(v).getInLinks(); if (inLinks != null) for (Link
     * l : inLinks) { v1 = GraphNetworkUtil.getVertex(l.getStartNode()); ln =
     * tableModel.indexOf(v1); if (ln>=0) { line = null; for (int i=0; i<tableModel.getColumnCount();
     * i++) if (tableModel.getColumnClass(i)==String.class) if (line==null) line =
     * tableModel.getValueAt(ln,i).toString(); else line += delim +
     * tableModel.getValueAt(ln,i).toString(); lt = GraphNetworkUtil.getType(l);
     * line = line + delim + trb.getString("LinkType."+lt.name()); for (int i=0;
     * i<tableModel.getColumnCount(); i++) if
     * (tableModel.getColumnClass(i)==String.class) if (line==null) line =
     * tableModel.getValueAt(j,i).toString(); else line += delim +
     * tableModel.getValueAt(j,i).toString(); ps.println(line); counter++; } } } }
     * finally { if (ps!=null) ps.close(); } return counter; }
     */
    private static Node getNodeByObject(Object o){
        if(o instanceof Node)
            return (Node) o;
        else if(o instanceof Vertex)
            return GraphNetworkUtil.getNode((Vertex) o);
        return null;
    }
    public static int exportLinks(IFormatTableModel tableModel, String fileName) throws IOException{
        loadResources();
        PrintStream ps = null;
        int counter = 0;
        try{
            ps = new PrintStream(fileName);
            String line = null;
            String prefix = rb.getString("DataExporter.linkList.source.prefix") + " ";
            String suffix = " " + rb.getString("DataExporter.linkList.source.suffix");
            for(int i = 0; i < tableModel.getColumnCount(); i++)
                if(tableModel.getColumnClass(i) == String.class)
                    if(line == null)
                        line = cellValueToCsvFormat(prefix.trim() + tableModel.getColumnName(i) + suffix);
                    else
                        line += delim + cellValueToCsvFormat(prefix.trim() + tableModel.getColumnName(i) + suffix);
            line += delim + rb.getString("DataExporter.linkList.link.type");
            prefix = rb.getString("DataExporter.linkList.target.prefix") + " ";
            suffix = " " + rb.getString("DataExporter.linkList.target.suffix");
            for(int i = 0; i < tableModel.getColumnCount(); i++)
                if(tableModel.getColumnClass(i) == String.class)
                    if(line == null)
                        line = cellValueToCsvFormat(prefix.trim() + tableModel.getColumnName(i) + suffix);
                    else
                        line += delim + cellValueToCsvFormat(prefix.trim() + tableModel.getColumnName(i) + suffix);
            ps.println(line);
            Node n, n1;
            LinkType lt;
            int ln;
            for(int j = 0; j < tableModel.getRowCount(); j++){
                n = getNodeByObject(tableModel.getObjectAt(j));
                if(n == null)
                    continue;
                Link[] inLinks = n.getInLinks();
                if(inLinks != null)
                    for(Link l : inLinks){
                        n1 = l.getStartNode();
                        ln = tableModel.indexOf(n1);
                        if(ln >= 0){
                            line = null;
                            for(int i = 0; i < tableModel.getColumnCount(); i++)
                                if(tableModel.getColumnClass(i) == String.class)
                                    if(line == null)
                                        line = (tableModel.getValueAt(ln, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(ln, i)
                                                .toString()) : "");
                                    else
                                        line += delim
                                                + (tableModel.getValueAt(ln, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(ln, i)
                                                        .toString()) : "");
                            lt = GraphNetworkUtil.getType(l);
                            line += delim;
                            if(lt != null)
                                line += trb.getString("LinkType." + lt.name());
                            for(int i = 0; i < tableModel.getColumnCount(); i++)
                                if(tableModel.getColumnClass(i) == String.class)
                                    if(line == null)
                                        line = (tableModel.getValueAt(j, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(j, i)
                                                .toString()) : "");
                                    else
                                        line += delim
                                                + (tableModel.getValueAt(j, i) != null ? cellValueToCsvFormat(tableModel.getValueAt(j, i)
                                                        .toString()) : "");
                            ps.println(line);
                            counter++;
                        }
                    }
            }
        }finally{
            if(ps != null)
                ps.close();
        }
        return counter;
    }
    private static String cellValueToCsvFormat(String currentCell){
        if(currentCell.indexOf('#') != 0){
            if(currentCell.indexOf('\"') != -1){
                currentCell = currentCell.replace("\"", "\"\"");
                currentCell = "\"" + currentCell + "\"";
            }else if(currentCell.indexOf(delim) != -1){
                currentCell = "\"" + currentCell + "\"";
            }
            if(currentCell.indexOf('\n') != -1){
                currentCell = currentCell.replace("\r\n", " | ");
//                currentCell = currentCell.replace("\n", " | ");
            }
            currentCell.trim();
        }else{
            currentCell = "";
        }
        return currentCell;
    }
}
