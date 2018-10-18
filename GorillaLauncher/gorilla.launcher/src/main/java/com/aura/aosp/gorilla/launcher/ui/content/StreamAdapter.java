package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.GenericStreamItem;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.store.ActionClusterStore;
import com.aura.aosp.gorilla.launcher.ui.common.ImageShaper;

import java.util.Collections;
import java.util.List;

/**
 * Adapter which handles the mapping of stream data to the content stream item view
 * recycler view items.
 */
public class StreamAdapter extends RecyclerView.Adapter<StreamViewHolder> {

    private static final String LOGTAG = StreamAdapter.class.getSimpleName();

    private List<StreamItem> streamItems = Collections.emptyList();
    private Context context;
    private StreamActivity activity;
    private ActionClusterStore actionClusterStore;

    /**
     * @param streamItems
     * @param context
     * @param activity
     */
    public StreamAdapter(List<StreamItem> streamItems, Context context, StreamActivity activity) {
        // Set initial items but Don't use setter: Until view holder isn't bound, we don't want to notify about changes
        this.streamItems = streamItems;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public StreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        StreamItemView itemView = (StreamItemView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_stream_item_preview_left, parent, false);

        StreamViewHolder viewHolder = new StreamViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final StreamViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Use the provided View Holder on the onCreateViewHolder invokeMethod
        // to populate the current row on the RecyclerView
        // TODO: Probably it makes sense to initialize child views of the
        // TODO: StreamItemView independently
        final StreamItem dataSet = streamItems.get(position);
//        holder.item.initWithItem(dataSet);

        int useDotButtonRes;
        int useDotButtonColor;
        int usePreviewImageRes;
        final int[] usePreviewImageColor = new int[1];
        final int useShapeColor;

        switch (dataSet.getType()) {
            case TYPE_STREAMITEM_GENERIC:
            default:
                useDotButtonRes = R.drawable.stream_oval_18dp;
                useDotButtonColor = R.color.color_stream_button;
                usePreviewImageRes = dataSet.getImageId();
                usePreviewImageColor[0] = R.color.color_stream_image_background_primary;
                useShapeColor = R.color.color_stream_preview_generic;
                break;

            case TYPE_STREAMITEM_CONTACT:
                useDotButtonRes = R.drawable.stream_oval_18dp;
                useDotButtonColor = R.color.color_stream_button;
                usePreviewImageRes = dataSet.getImageId();
                usePreviewImageColor[0] = R.color.color_stream_button;
                useShapeColor = R.color.color_stream_preview_contact;
                break;

            case TYPE_STREAMITEM_MESSAGE:
            case TYPE_STREAMITEM_INVISIBLE:
                useDotButtonRes = R.drawable.stream_oval_24dp;
                useDotButtonColor = R.color.color_stream_button_highlight;
                usePreviewImageRes = R.drawable.ic_bubble_chart_black_24dp;
                usePreviewImageColor[0] = R.color.color_stream_image_background_secondary;
                useShapeColor = R.color.color_stream_preview_message;
                break;

            case TYPE_STREAMITEM_UNKNOWN:
                useDotButtonRes = R.drawable.stream_oval_transparent_24dp;
                useDotButtonColor = R.color.color_transparent;
                usePreviewImageRes = R.drawable.stream_oval_transparent_24dp;
                usePreviewImageColor[0] = R.color.color_transparent;
                useShapeColor = R.color.color_transparent;
                break;
        }

//        holder.title.setText(dataSet.title);
        holder.previewText.setText(dataSet.getTitle());

//        holder.dotButton.setImageResource(useDotButtonRes);
//        Drawable dbDrawable = holder.dotButton.getDrawable();
//        DrawableCompat.setTint(dbDrawable, ContextCompat.getColor(context, useDotButtonColor));

        holder.previewImage.setImageResource(usePreviewImageRes);

        Drawable previewImageDrawable = holder.previewImage.getDrawable();
        DrawableCompat.setTint(previewImageDrawable, ContextCompat.getColor(context, usePreviewImageColor[0]));

        Drawable shapeTextDrawable = holder.previewText.getBackground();
        DrawableCompat.setTint(shapeTextDrawable, ContextCompat.getColor(context, useShapeColor));

//        Drawable shapeImageDrawable = holder.previewImage.getBackground();
//        DrawableCompat.setTint(shapeImageDrawable, ContextCompat.getColor(context, useShapeColor));

        // TODO: Clean up, very hacky by now:
        if (dataSet.getType() == GenericStreamItem.ItemType.TYPE_STREAMITEM_CONTACT) {

            ContactStreamItem contactStreamItem = (ContactStreamItem) dataSet;

            Identity contactIdentity = contactStreamItem.getContactIdentity();

            if (contactIdentity != null) {
                switch (contactIdentity.getNick()) {

                    case "matthias":
                        ImageShaper.shape(context, R.drawable.avatar_matthias_250x250, R.drawable.stream_preview_rounded_corners_right, holder.previewImage, 48, 48);
                        break;

                    case "ola":
                        ImageShaper.shape(context, R.drawable.avatar_ola_586x586, R.drawable.stream_preview_rounded_corners_right, holder.previewImage, 48, 48);
                        break;
                }
            }

//            holder.dotButton.setScaleX(0.5f);
//            holder.dotButton.setScaleY(0.5f);
//            holder.previewImage.setScaleX(1.8f);
//            holder.previewImage.setScaleY(1.8f);

            // Invoke intent based action
            holder.previewImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    holder.previewImage.setScaleX(1.2f);
//                    holder.previewImage.setScaleY(1.2f);

                    // Create initial action button cluster (attach it to "root" container)
                    actionClusterStore = new ActionClusterStore(context);

                    ActionCluster itemActionCluster = actionClusterStore.getClusterForAction(
                            "stream.contacts", ((ContactStreamItem) dataSet).getContactIdentity());

                    activity.createActionClusterView(itemActionCluster, null, true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return streamItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addItem(int position, GenericStreamItem streamItem) {
        // Insert a new item to the RecyclerView on a predefined position
        streamItems.add(position, streamItem);
        notifyItemInserted(position);
    }

    public void removeItem(GenericStreamItem streamItem) {
        // Remove a RecyclerView item containing a specified Data object
        int position = streamItems.indexOf(streamItem);
        streamItems.remove(position);
        notifyItemRemoved(position);
    }

    public List<StreamItem> getStreamItems() {
        return streamItems;
    }

    public void setStreamItems(List<StreamItem> streamItems) {
        this.streamItems = streamItems;
        notifyDataSetChanged();
    }
}
