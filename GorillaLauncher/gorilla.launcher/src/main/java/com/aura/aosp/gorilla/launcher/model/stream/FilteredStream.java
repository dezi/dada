package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.gorilla.launcher.model.user.User;

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
     * Called if item was fully visible to user.
     *
     * @param pos
     */
    public void onItemViewed(final int pos, User viewedByUser) {
        StreamItem streamItem = get(pos);

        // TODO: Hier weiter, knallt noch weil viewedByUser null ist:
//        streamItem.onFullyViewed(viewedByUser);
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
                time1 = asc ? o1.getTimeCreated() : o2.getTimeCreated();
                time2 = asc ? o2.getTimeCreated() : o1.getTimeCreated();
            } else {
                time1 = asc ? o1.getTimeModified() : o2.getTimeModified();
                time2 = asc ? o2.getTimeModified() : o1.getTimeModified();
            }

            return Simple.compareTo(time1, time2);
        }
    }
}
