package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.TimelineItem;

import java.util.Collections;
import java.util.List;

/**
 * Adapter which handles the mapping of stream data to the timeline
 * recycler view items.
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineViewHolder> {

    private List<TimelineItem> streamDataList = Collections.emptyList();
    private Context context;

    /**
     * @param list
     * @param context
     */
    public TimelineAdapter(List<TimelineItem> list, Context context) {
        this.streamDataList = list;
        this.context = context;
    }

    @Override
    public TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timeline_item, parent, false);

        TimelineViewHolder viewHolder = new TimelineViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TimelineViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Use the provided View Holder on the onCreateViewHolder method
        // to populate the current row on the RecyclerView
        TimelineItem dataSet = streamDataList.get(position);

        int useDotButtonRes;
        int useDotButtonColor;
        int usePreviewImageRes;
        int usePreviewImageColor;

        switch (dataSet.type) {
            case "default":
            default:
                useDotButtonRes = R.drawable.timeline_oval_18dp;
                useDotButtonColor = R.color.color_timeline_button;
                usePreviewImageRes = R.drawable.ic_blur_on_black_24dp;
                usePreviewImageColor = R.color.color_timeline_image_background_primary;
                break;

            case "highlight":
                useDotButtonRes = R.drawable.timeline_oval_24dp;
                useDotButtonColor = R.color.color_timeline_button_highlight;
                usePreviewImageRes = R.drawable.ic_bubble_chart_black_24dp;
                usePreviewImageColor = R.color.color_timeline_image_background_secondary;
                break;
        }

        holder.title.setText(dataSet.title);
        holder.description.setText(dataSet.description);

        holder.dotButton.setImageResource(useDotButtonRes);
        Drawable dbDrawable = holder.dotButton.getDrawable();
        DrawableCompat.setTint(dbDrawable, ContextCompat.getColor(context, useDotButtonColor));

        holder.previewImage.setImageResource(usePreviewImageRes);
        Drawable iDrawable = holder.previewImage.getDrawable();
        DrawableCompat.setTint(iDrawable, ContextCompat.getColor(context, usePreviewImageColor));
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return streamDataList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addItem(int position, TimelineItem timelineItem) {
        // Insert a new item to the RecyclerView on a predefined position
        streamDataList.add(position, timelineItem);
        notifyItemInserted(position);
    }

    public void removeItem(TimelineItem timelineItem) {
        // Remove a RecyclerView item containing a specified Data object
        int position = streamDataList.indexOf(timelineItem);
        streamDataList.remove(position);
        notifyItemRemoved(position);
    }
}
