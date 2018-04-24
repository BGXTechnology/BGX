/*
$Id: $
$DateTime:  $
$Change:  $
$Author: $
 */
package net.bgx.bgxnetwork.bgxop.gui.lv.timetable;

/**
 * User: A.Borisenko
 * Date: 15.07.2007
 * Time: 18:11:53
 */
public class TTStep {
    private final Long ONE_HOUR = 3600000L;
    private final Long TWENTY_MINUTES = 1200000L;
    private final Long ONE_WEEK = 604800000L;
    private final Long TWENTY_FOUR_HOURS = ONE_HOUR *24L;
    private final Long EIGHT_HOUR = ONE_HOUR *8L;
    private final Long ONE_MONTH = TWENTY_FOUR_HOURS *30L;
    private final Long ONE_YEAR = 365L*TWENTY_FOUR_HOURS;
    private final Long FIVE_YEAR = ONE_YEAR * 5L;

    private Long majorStep = null;
    private Long minorStep = null;
    private int majorRule;
    private int minorRule;

    public TTStep(Long interval) {
        majorRule = 120;
        minorRule = 40;

        if (interval <= TWENTY_FOUR_HOURS){
            majorStep = ONE_HOUR;
            minorStep = TWENTY_MINUTES;
        }
        else if (interval <= ONE_WEEK){
            majorStep = TWENTY_FOUR_HOURS;
            minorStep = EIGHT_HOUR;
        }
        else if (interval <= ONE_MONTH){
            majorStep = ONE_WEEK;
            minorStep = TWENTY_FOUR_HOURS;
            majorRule = 210;
            minorRule = 30;
        }
        else if (interval <= ONE_YEAR){
            majorStep = ONE_MONTH;
            minorStep = TWENTY_FOUR_HOURS*10L;
        }
        else if (interval <= FIVE_YEAR){
            majorStep = ONE_MONTH * 3L;
            minorStep = ONE_MONTH;
        }
        else {
            majorStep = ONE_MONTH * 6L;
            minorStep = ONE_MONTH * 2L;
        }

    }

    public Long getMajorStep() {
        return majorStep;
    }

    public Long getMinorStep() {
        return minorStep;
    }

    public int getMajorRule() {
        return majorRule;
    }

    public int getMinorRule() {
        return minorRule;
    }
}
