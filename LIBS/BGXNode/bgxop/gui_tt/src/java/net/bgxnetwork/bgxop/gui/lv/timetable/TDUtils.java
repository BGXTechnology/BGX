/*

$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui_tt/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/TDUtils.java#1 $

$DateTime: 2007/08/06 17:28:33 $

$Change: 20537 $

$Author: a.borisenko $

*/

package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;



import java.util.Date;

import java.text.SimpleDateFormat;

import java.util.List;



import net.bgx.bgxnetwork.transfer.tt.TDLink;

import net.bgx.bgxnetwork.transfer.tt.TDPair;



public class TDUtils {

    private static String separatorTilde = "~~~~~~~~~~~~~~~~~~~";

    private static String separatorPercent = "%%%%%%%%%%%%%%%%%%%";

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss:SSS"); 



    public static void printPairsList(List<TDPair> pairs) {

        StringBuilder builder = new StringBuilder();

        builder.append(separatorPercent);

        builder.append("\n");

        builder.append("List: "+pairs);

        builder.append("\n");

        builder.append("Size:"+ pairs == null ? 0 : pairs.size());

        builder.append("\n");

        builder.append("Elements:");

        builder.append("\n");

        if(pairs != null) {

            for(TDPair pair : pairs) {

                appendPairInfo(pair, builder);

            }

        }

//        System.out.println(builder.toString());

    }



    public static void printLinkList(List<TDLink> links) {

        StringBuilder builder = new StringBuilder();

        builder.append(separatorPercent);

        builder.append("\n");

        builder.append("List: "+links);

        builder.append("\n");

        builder.append("Size:"+ links == null ? 0 : links.size());

        builder.append("\n");

        builder.append("Elements:");

        builder.append("\n");

        if(links != null) {

            for(TDLink link : links) {

                appendLinkInfo(link, builder);

            }

        }

//        System.out.println(builder.toString());

    }



    public static void appendLinkInfo(TDLink link, StringBuilder builder) {

        builder.append("Timestamp: "+link == null ? "null": formatter.format(new Date(link.getTimestamp())));

        builder.append("\n");

    }



    public static void appendPairInfo(TDPair pair, StringBuilder builder) {

        builder.append(separatorTilde);

        builder.append("\n");

        List<TDLink> links = pair.getLinks();

        builder.append("Links: "+links);

        builder.append("\n");

        builder.append("Size: "+ links == null ? 0 : links.size());

        builder.append("\n");

        builder.append("Elements:");

        builder.append("\n");

        if(links != null) {

            for(TDLink link : links) {

                appendLinkInfo(link, builder);

            }

        }

    }

}