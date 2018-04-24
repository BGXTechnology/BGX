/* $Id: //depot/MNS/bgxnetwork/server/main/toolkit/src/java/net/bgx/bgxnetwork/toolkit/GMTDateHelper.java#1 $
 * $DateTime: 2006/03/07 13:52:51 $
 * $Change: 6772 $
 * $Author: grouzintsev $
 */
package net.bgx.bgxnetwork.toolkit;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * ѕредоставл€ет методы дл€ работы с датой.
 */
public class GMTDateHelper {
    public static Long toLong(Date aDate) {
        if (aDate==null) return null;
        return new Long(getCalendarOnDate(aDate.getTime()).getTimeInMillis());
    }

    public static Date toDate(Long aTime) {
        if (aTime==null) return null;
        return toDate(aTime.longValue());
    }

    public static Timestamp toTimestamp(long aTime) {
        return new Timestamp(aTime);
    }

    public static Date toDate(long aTime) {
        return new Date(getCalendarOnDate(aTime).getTimeInMillis());
    }

    public static int compareDates(Date aLeft, Date aRight) {
        return compareDates(aLeft.getTime(), aRight.getTime());
    }

    public static int compareDates(Long aLeft, Long aRight) {
        return compareDates(aLeft.longValue(), aRight.longValue());
    }

    public static int compareDates(long aLeft, long aRight) {
        Calendar left = getCalendarOnDate(aLeft);
        Calendar right = getCalendarOnDate(aRight);
        if (left.before(right)) return -1;
        if (left.after(right)) return 1;
        return 0;
    }

    public static long getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        clearTimePart(calendar);
        return calendar.getTimeInMillis();
    }

    private static void clearTimePart(Calendar aCalendar) {
        aCalendar.clear(Calendar.HOUR_OF_DAY);
        aCalendar.clear(Calendar.HOUR);
        aCalendar.clear(Calendar.MINUTE);
        aCalendar.clear(Calendar.SECOND);
        aCalendar.clear(Calendar.MILLISECOND);
    }

    public static Calendar getCalendarOnDate(long aTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(aTime);
        clearTimePart(calendar);
        return calendar;
    }
}