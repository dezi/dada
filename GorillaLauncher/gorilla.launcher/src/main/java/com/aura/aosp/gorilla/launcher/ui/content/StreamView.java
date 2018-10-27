package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.user.User;
import com.aura.aosp.gorilla.launcher.ui.animation.Effects;

/**
 * "Content Stream" view (child of "Launcher" view)
 */
public class StreamView extends RecyclerView {

    private static final String LOGTAG = StreamView.class.getSimpleName();

    protected User myUser;

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

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                LinearLayoutManager layoutmanager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                StreamAdapter streamAdapter = (StreamAdapter) recyclerView.getAdapter();
//                FilteredStream filteredStream = (FilteredStream) streamAdapter.getStreamItems();
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
