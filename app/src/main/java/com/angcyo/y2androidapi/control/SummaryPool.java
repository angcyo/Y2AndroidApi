package com.angcyo.y2androidapi.control;

import com.angcyo.y2androidapi.moudle.SummaryWrap;

/**
 * Created by angcyo on 15-07-28-028.
 */
public class SummaryPool {
    public SummaryWrap summaryWrap;
    public String title = "";
    public String fullTitle = "";

    public SummaryPool() {
    }

    public SummaryWrap getSummaryWrap() {
        return summaryWrap;
    }

    public void setSummaryWrap(SummaryWrap summaryWrap) {
        this.summaryWrap = summaryWrap;
    }

    public static int getPositionWithSpan(SummaryPool pool, String span) {
        if (pool == null || span == null) {
            return -1;
        }

        for (int i = 0; i < pool.summaryWrap.getSpan().size(); i++) {
            if (pool.summaryWrap.getSpan().get(i).equalsIgnoreCase(span)) {
                return pool.summaryWrap.getSpanStartPosition().get(i);
            }
        }

        return -1;
    }

}
