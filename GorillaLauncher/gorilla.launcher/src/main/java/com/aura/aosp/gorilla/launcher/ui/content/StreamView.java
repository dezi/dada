package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.BoundaryStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface;
import com.aura.aosp.gorilla.launcher.model.user.User;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;
import com.aura.aosp.gorilla.launcher.ui.common.LoadMoreListener;

/**
 * "Content Stream" view (child of "Launcher" view)
 */
public class StreamView extends RecyclerView {

    private static final String LOGTAG = StreamView.class.getSimpleName();
    private static final int VISIBLE_ITEM_TRESHOLD = 10;

    private User myUser;
    private LoadMoreListener loadMoreListener;

    private int lastVisibleItemPos;
    private int firstVisibleItemPos;

    private boolean isLoadingMore = false;

    public StreamView(Context context) {
        super(context);
        init();
    }

    public StreamView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StreamView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * Initialize stream view with custom onScrollListener
     */
    protected void init() {

        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

            /**
             * TODO: Extract different handlers, create own classes (or create inline instances)
             * TODO: and add them dependent on items stream view state via
             * TODO: recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() directly within StreamAdapter
             *
             * @param recyclerView
             * @param dx
             * @param dy
             */
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
                StreamAdapter streamAdapter = (StreamAdapter) recyclerView.getAdapter();
                FilteredStream filteredStream = (FilteredStream) streamAdapter.getStreamItems();

                BoundaryStreamItem boundaryStreamItem = new BoundaryStreamItem();

                int currentItemCount = filteredStream.size();
                int currentItemEndPos = currentItemCount - 1;

                firstVisibleItemPos = layoutmanager.findLastVisibleItemPosition();
                lastVisibleItemPos = layoutmanager.findLastVisibleItemPosition();

                if (!isLoadingMore && currentItemCount <= (lastVisibleItemPos + VISIBLE_ITEM_TRESHOLD)) {
                    isLoadingMore = true;
                    if (loadMoreListener != null) {
                        // TODO: Implement for dynamic loading more items, probably showing
                        // TODO: some nice progress item. It might also make sense to
                        // TODO: restrict list size and cut items from the beginning + implmenting
                        // TODO: same method for scrolling to the beginning of list
                        loadMoreListener.onLoadMore();
                    }
                }

                if (currentItemEndPos == lastVisibleItemPos) {

                    StreamItemInterface lastStreamItem = streamAdapter.getStreamItems().get(lastVisibleItemPos);

                    if (!(lastStreamItem instanceof BoundaryStreamItem)) {
                        streamAdapter.addItem(currentItemCount, boundaryStreamItem);
                        streamAdapter.notifyItemChanged(currentItemCount);
                    }

                } else if (currentItemEndPos == firstVisibleItemPos) {

                    // Exclusive else because we just want ONE boundary item when there are
                    // no items at all yet!

                    StreamItemInterface firstStreamItem = streamAdapter.getStreamItems().get(0);

                    if (!(firstStreamItem instanceof BoundaryStreamItem)) {
                        streamAdapter.addItem(0, boundaryStreamItem);
                        streamAdapter.notifyItemChanged(0);
                    }
                }
//
//                int fvItemPosition = layoutmanager.findFirstCompletelyVisibleItemPosition();
//                int lvItemPosition = layoutmanager.findLastCompletelyVisibleItemPosition();
//
//                for (int pos = fvItemPosition; pos < lvItemPosition; pos++) {
//                    filteredStream.onItemViewed(pos, getMyUser());
//                    streamAdapter.notifyItemChanged(pos);
//                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

//                if (newState == SCROLL_STATE_IDLE) {
//                    LinearLayoutManager layoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    StreamAdapter streamAdapter = (StreamAdapter) recyclerView.getAdapter();
//                    FilteredStream filteredStream = (FilteredStream) streamAdapter.getStreamItems();
//
//                    int fvItemPosition = layoutmanager.findFirstCompletelyVisibleItemPosition();
//                    int lvItemPosition = layoutmanager.findLastCompletelyVisibleItemPosition();
//
//                    for (int pos = fvItemPosition; pos < lvItemPosition; pos++) {
//                        filteredStream.onItemViewed(pos, getMyUser());
//                    }
//                }
            }
        };

        addOnScrollListener(onScrollListener);
    }

    public void fadeIn() {
        Integer duration = getContext().getResources().getInteger(R.integer.streamview_fadein_transition_duration);
        Effects.fadeInView(this, getContext(), duration);
    }

    public void fadeOut() {
        Integer duration = getContext().getResources().getInteger(R.integer.streamview_fadeout_transition_duration);
        Effects.fadeOutView(this, getContext(), duration);
    }

    /**
     * Scroll to end of stream view.
     */
    public void smoothScrollToStreamEnd() {
        if (getAdapter().getItemCount() > 0) {
            smoothScrollToPosition(getAdapter().getItemCount() - 1);
        }
    }

    /**
     * Scroll to end of stream view.
     */
    public void scrollToStreamEnd() {
        if (getAdapter().getItemCount() > 0) {
            scrollToPosition(getAdapter().getItemCount() - 1);
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        Log.d("onTouchEvent Action: |%10d|", ev.getAction());
//        Log.d("onTouchEvent Axis X: |%10f|", ev.getAxisValue(MotionEvent.AXIS_X));
//        Log.d("onTouchEvent Axis Y: |%10f|", ev.getAxisValue(MotionEvent.AXIS_Y));
//        return super.onTouchEvent(ev);
//    }

    public User getMyUser() {
        return myUser;
    }

    public void setMyUser(User myUser) {
        this.myUser = myUser;
    }
}
