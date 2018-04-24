/*
$Id: //depot/External Projects/i9500/Implementation/Source/bgxop/main/modules/gui/src/java/net/bgx/bgxnetwork/bgxop/gui/lv/timetable/timedia/TDConstants.java#1 $
$DateTime: 2007/07/04 14:49:50 $
$Change: 19371 $
$Author: O.Lukashevich $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

import java.awt.Color;
import java.awt.Paint;

interface TDConstants {
    public static final Object KEY_VERTEX_LINKOBJECT = new Object();
    public static final Object KEY_VERTEX_LINKOBJECT_INTEGRATED = new Object();
    public static final Object KEY_GRAPH_LINKOBJECT_MAX = new Object();
    public static final Object KEY_GRAPH_LINKOBJECT_MIN = new Object();

    public static final int TABLE_TEXT_COLUMNS_WIDTH = 100;
    
    public static final Paint COLOR_VERTEX_INTEGRATED = Color.YELLOW;
    public static final Paint COLOR_VERTEX_SINGLE = Color.RED;

    public static final int LAYOUT_HEIGHT = 80;
    public static final int LAYOUT_Y_TOP = 20;
    public static final int LAYOUT_Y_CENTER = 40;
    public static final int LAYOUT_Y_BOTTOM = 60;

    public static final Object KEY_TIMELINE_SCALEMAIN = new Object();
    public static final Object VALUE_TIMELINE_SCALEMAIN = new Object();
    public static final Object KEY_TIMELINE_TIMESTAMP = new Object();
    public static final Object KEY_TIMELINE_TIME = new Object();

    public static final int TIMELINE_HEIGHT = 100;
    public static final int TIMELINE_SCALEMARK1_Y_TOP = 30;
    public static final int TIMELINE_SCALEMARK2_Y_TOP = 50;
    public static final int TIMELINE_BASELINE_Y = 70;
    public static final int TIMELINE_SCALEMARK2_Y_BOTTOM = 80;
    public static final int TIMELINE_SCALEMARK1_Y_BOTTOM = 90;

    public static final int INDENT = 10;
}