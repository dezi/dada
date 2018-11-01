package com.aura.aosp.gorilla.launcher.model.stream;

import com.aura.aosp.aura.common.simple.Simple;
import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.model.user.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Predicate;

/**
 * The filtered stream model.
 */
public class FilteredStream extends ArrayList<StreamItemInterface> {

    /**
     * Sort stream items by creation time. Remove items of type
     * "bouhndary stream item" before sorting.
     *
     * @param asc
     */
    public void sortyByCreateTime(final boolean asc) {
        removeIf(new Predicate<StreamItemInterface>() {
            @Override
            public boolean test(StreamItemInterface streamItemInterface) {
                return streamItemInterface instanceof BoundaryStreamItem;
            }
        });
        sort(new TimeComparator(true, asc));
    }

    /**
     * Sort stream items by modification time. Remove items of type
     * "bouhndary stream item" before sorting.
     *
     * @param asc
     */
    public void sortyByModifyTime(final boolean asc) {
        removeIf(new Predicate<StreamItemInterface>() {
            @Override
            public boolean test(StreamItemInterface streamItemInterface) {
                return streamItemInterface instanceof BoundaryStreamItem;
            }
        });
        sort(new TimeComparator(false, asc));
    }

    /**
     * Called if item was fully visible to user.
     * TODO: Extract listener object: ItemInteractionListener
     *
     * @param pos
     */
    public void onItemViewed(final int pos, User viewedByUser) {

        StreamItemInterface streamItem = get(pos);
        streamItem.onFullyViewed();
    }

    /**
     * Retrieve position for inserting a new item to the end of stream. Check
     * if last item is "boundary stream item" and adjust position accordingly.
     *
     * @return
     */
    public int getInsertToEndPos() {

        int currentItemCount = this.size();
        int currentEndPos = currentItemCount - 1;

        StreamItemInterface lastPosStreamItem = get(currentEndPos);

        return (lastPosStreamItem instanceof BoundaryStreamItem)
                ? currentEndPos
                : currentItemCount;
    }

    /**
     * Comparator for sorting by creation date asc/desc.
     */
    class TimeComparator implements Comparator<StreamItemInterface> {

        boolean asc = true;
        boolean created = true;

        public TimeComparator(boolean created, boolean asc) {
            this.asc = asc;
            this.created = created;
        }

        @Override
        public int compare(StreamItemInterface o1, StreamItemInterface o2) {

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
