package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.aura.common.simple.Simple;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * The filtered stream model.
 */
public class FilteredStream extends ArrayList<StreamItem> {

    /**
     * Sort stream items by creation time.
     *
     * @param asc
     */
    public void sortyByCreateTime(final boolean asc) {
        sort(new TimeComparator(true, asc));
    }

    /**
     * Sort stream items by modification time.
     *
     * @param asc
     */
    public void sortyByModifyTime(final boolean asc) {
        sort(new TimeComparator(false, asc));
    }

    /**
     * Comparator for sorting by creation date asc/desc.
     */
    class TimeComparator implements Comparator<StreamItem> {

        boolean asc = true;
        boolean created = true;

        public TimeComparator(boolean created, boolean asc) {
            this.asc = asc;
            this.created = created;
        }

        @Override
        public int compare(StreamItem o1, StreamItem o2) {

            Long time1;
            Long time2;

            if (created) {
                time1 = asc ? o1.getCreateTime() : o2.getCreateTime();
                time2 = asc ? o2.getCreateTime() : o1.getCreateTime();
            } else {
                time1 = asc ? o1.getModifyTime() : o2.getModifyTime();
                time2 = asc ? o2.getModifyTime() : o1.getModifyTime();
            }

            return Simple.compareTo(time1, time2);
        }
    }
}
