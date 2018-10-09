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
import com.aura.aosp.gorilla.launcher.model.ContentStreamItem;

import java.util.Collections;
import java.util.List;

/**
 * Adapter which handles the mapping of stream data to the content stream item view
 * recycler view items.
 */
public class ContentStreamAdapter extends RecyclerView.Adapter<ContentStreamViewHolder> {

    private List<ContentStreamItem> streamDataList = Collections.emptyList();
    private Context context;

    /**
     * @param list
     * @param context
     */
    public ContentStreamAdapter(List<ContentStreamItem> list, Context context) {
        this.streamDataList = list;
        this.context = context;
    }

    @Override
    public ContentStreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.content_stream_item, parent, false);

        ContentStreamViewHolder viewHolder = new ContentStreamViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ContentStreamViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Use the provided View Holder on the onCreateViewHolder method
        // to populate the current row on the RecyclerView
        ContentStreamItem dataSet = streamDataList.get(position);

        int useDotButtonRes;
        int useDotButtonColor;
        int usePreviewImageRes;
        int usePreviewImageColor;

        switch (dataSet.type) {
            case "default":
            default:
                useDotButtonRes = R.drawable.contentstream_oval_18dp;
                useDotButtonColor = R.color.color_contentstream_button;
                usePreviewImageRes = dataSet.getImageId();
                usePreviewImageColor = R.color.color_contentstream_image_background_primary;
                break;

            // Very hacky by now:
            case "contact":
                useDotButtonRes = R.drawable.contentstream_oval_18dp;
                useDotButtonColor = R.color.color_contentstream_button;
                usePreviewImageRes = dataSet.getImageId();
                usePreviewImageColor = R.color.color_contentstream_button;
                break;

            case "highlight":
                useDotButtonRes = R.drawable.contentstream_oval_24dp;
                useDotButtonColor = R.color.color_contentstream_button_highlight;
                usePreviewImageRes = R.drawable.ic_bubble_chart_black_24dp;
                usePreviewImageColor = R.color.color_contentstream_image_background_secondary;
                break;

            case "transparent":
                useDotButtonRes = R.drawable.contentstream_oval_transparent_24dp;
                useDotButtonColor = R.color.color_transparent;
                usePreviewImageRes = R.drawable.contentstream_oval_transparent_24dp;
                usePreviewImageColor = R.color.color_transparent;
                break;
        }

//        holder.title.setText(dataSet.title);
        holder.previewText.setText(dataSet.text);

        holder.dotButton.setImageResource(useDotButtonRes);
        Drawable dbDrawable = holder.dotButton.getDrawable();
        DrawableCompat.setTint(dbDrawable, ContextCompat.getColor(context, useDotButtonColor));

        holder.previewImage.setImageResource(usePreviewImageRes);
        Drawable iDrawable = holder.previewImage.getDrawable();
        DrawableCompat.setTint(iDrawable, ContextCompat.getColor(context, usePreviewImageColor));

        // Very hacky by now:
        if (dataSet.type == "contact") {
            holder.dotButton.setScaleX(0.5f);
            holder.dotButton.setScaleY(0.5f);
            holder.previewImage.setScaleX(1.5f);
            holder.previewImage.setScaleY(1.5f);
        }
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

    public void addItem(int position, ContentStreamItem contentStreamItem) {
        // Insert a new item to the RecyclerView on a predefined position
        streamDataList.add(position, contentStreamItem);
        notifyItemInserted(position);
    }

    public void removeItem(ContentStreamItem contentStreamItem) {
        // Remove a RecyclerView item containing a specified Data object
        int position = streamDataList.indexOf(contentStreamItem);
        streamDataList.remove(position);
        notifyItemRemoved(position);
    }
}
