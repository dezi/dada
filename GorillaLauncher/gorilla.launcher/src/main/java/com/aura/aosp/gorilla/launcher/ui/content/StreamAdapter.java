package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aura.aosp.aura.common.simple.Dates;
import com.aura.aosp.gorilla.launcher.LauncherActivity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.GenericStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.MessageStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItem;
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

    private static final int ITEM_TYPE_DOT = 0;
    private static final int ITEM_TYPE_CIRCLE = 5;
    private static final int ITEM_TYPE_PREVIEW_LEFT = 10;
    private static final int ITEM_TYPE_PREVIEW_RIGHT = 11;
    private static final int ITEM_TYPE_FULLVIEW_LEFT = 20;
    private static final int ITEM_TYPE_FULLVIEW_RIGHT = 21;

    /**
     * @param filteredStream
     * @param context
     * @param activity
     */
    public StreamAdapter(FilteredStream filteredStream, Context context, StreamActivity activity) {
        // Set initial items but Don't use setter: Until view holder isn't bound, we don't want to notify about changes
        this.streamItems = filteredStream;
        this.context = context;
        this.activity = activity;
    }

    /**
     * Create new views (invoked by the layout manager)
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public StreamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        StreamItemView itemView;

        switch (viewType) {

            case ITEM_TYPE_PREVIEW_LEFT:
            default:
                itemView = (StreamItemView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_stream_item_preview_left, parent, false);
                break;

            case ITEM_TYPE_PREVIEW_RIGHT:
                itemView = (StreamItemView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_stream_item_preview_right, parent, false);
                break;
        }

        StreamViewHolder viewHolder = new StreamViewHolder(itemView);
        return viewHolder;
    }

    /**
     * Retrieve item view type based on dataset of passed position.
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        final StreamItem dataSet = streamItems.get(position);
        int itemType;

        switch (dataSet.getType()) {

            default:
                itemType = ITEM_TYPE_DOT;
                break;

            case TYPE_STREAMITEM_CONTACT:
                ContactStreamItem contactStreamItem = (ContactStreamItem) dataSet;

                if (contactStreamItem.isOwnerUser()) {
                    itemType = ITEM_TYPE_PREVIEW_RIGHT;
                } else {
                    itemType = ITEM_TYPE_PREVIEW_LEFT;
                }

                break;

            case TYPE_STREAMITEM_MESSAGE:
                MessageStreamItem messageStreamItem = (MessageStreamItem) dataSet;

                if (messageStreamItem.getOwnerUser().equals(((LauncherActivity) activity).getMyUser())) {
                    itemType = ITEM_TYPE_PREVIEW_RIGHT;
                } else {
                    itemType = ITEM_TYPE_PREVIEW_LEFT;
                }

                break;

            case TYPE_STREAMITEM_DRAFT:
                itemType = ITEM_TYPE_PREVIEW_RIGHT;
                break;
        }

        return itemType;
    }

    @Override
    public void onViewRecycled(@NonNull StreamViewHolder holder) {
        super.onViewRecycled(holder);
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     * Uses the provided View Holder on the onCreateViewHolder invokeMethod
     * to populate the current row on the RecyclerView
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final StreamViewHolder holder, int position) {

        final StreamItem dataSet = streamItems.get(position);

        // TODO: Initialize item directly through implementing class.
        // TODO: Probably it makes sense to initialize child views of the StreamItemView independently
//        holder.item.initWithItem(dataSet);

        final int usePlaceholderImageRes = dataSet.getImageId();
        final int usePlaceholderImageColor = R.color.color_stream_image_drawable;

        float previewItemHeight = context.getResources().getDimension(R.dimen.stream_preview_item_image_height);
        float previewItemWidth = context.getResources().getDimension(R.dimen.stream_preview_item_image_width);

        final int useImageShapeBgRes;
        final int useShapeBgColor;

        Integer avatarResId = null;

        // Modify some attributes based  on stream item type
        switch (dataSet.getType()) {
            case TYPE_STREAMITEM_GENERIC:
            default:
                useShapeBgColor = R.color.color_stream_preview_bg_generic;
                break;

            case TYPE_STREAMITEM_CONTACT:
                useShapeBgColor = R.color.color_stream_preview_bg_contact;
                break;

            case TYPE_STREAMITEM_MESSAGE:
                useShapeBgColor = R.color.color_stream_preview_bg_message;
                break;

            case TYPE_STREAMITEM_INVISIBLE:
                useShapeBgColor = R.color.color_transparent;
                break;
        }

        switch (holder.getItemViewType()) {

            default:
                useImageShapeBgRes = R.drawable.stream_preview_rounded_corners_right;
                break;

            case ITEM_TYPE_PREVIEW_LEFT:
                useImageShapeBgRes = R.drawable.stream_preview_rounded_corners_right;
                break;

            case ITEM_TYPE_PREVIEW_RIGHT:
                useImageShapeBgRes = R.drawable.stream_preview_rounded_corners_left;
                break;
        }

//        // Set title
//        String title = dataSet.getTitle();
//
//        Long timeStamp = dataSet.getTimeCreated();
//
//        if (timeStamp != null) {
//            String datestring = Dates.getLocalDateAndTime(timeStamp);
//            String timeTag = datestring.substring(8, 10) + ":" + datestring.substring(10, 12);
//            title += " – " + timeTag;
//        }
//
//        holder.previewTitle.setText(title);

        // Set text
        holder.previewText.setText(dataSet.getTextExcerpt());

        // Set color of background shape drawables
        Drawable shapePreviewTextDrawable = holder.previewTextContainer.getBackground();
        DrawableCompat.setTint(shapePreviewTextDrawable, ContextCompat.getColor(context, useShapeBgColor));

        Drawable shapeImageContainerDrawable = holder.previewImageContainer.getBackground();
        DrawableCompat.setTint(shapeImageContainerDrawable, ContextCompat.getColor(context, useShapeBgColor));

        // Adjust shape, content and visualization according to stream item type
        switch (dataSet.getType()) {

            default:
                break;

            case TYPE_STREAMITEM_MESSAGE:

                final MessageStreamItem messageStreamItem = (MessageStreamItem) dataSet;

                avatarResId = messageStreamItem.getOwnerUser().getContactAvatarImageRes();

                if (avatarResId != null) {
                    ImageShaper.shape(context, avatarResId, useImageShapeBgRes, holder.previewImage, previewItemHeight, previewItemWidth);
                    holder.previewImage.setImageAlpha(context.getResources().getInteger(R.integer.streamitem_message_preview_avatar_alpha));
                }

                // TODO: Add OnClick actions (see below). Important: OnClick should be captured for whole items, not only images/placeholders

                break;

            case TYPE_STREAMITEM_CONTACT:

                final ContactStreamItem contactStreamItem = (ContactStreamItem) dataSet;

                avatarResId = contactStreamItem.getContactUser().getContactAvatarImageRes();

                if (avatarResId != null) {
                    ImageShaper.shape(context, avatarResId, useImageShapeBgRes, holder.previewImage, previewItemHeight, previewItemWidth);
                    holder.previewImage.setImageAlpha(context.getResources().getInteger(R.integer.streamitem_contact_preview_avatar_alpha));
                }

                // Invoke intent based action
                holder.previewImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // TODO: Create transition feedback etc.
                        // Create initial action button cluster (attach it to "root" container)
                        actionClusterStore = new ActionClusterStore(context);

                        ActionCluster itemActionCluster = actionClusterStore.getClusterForAction(
                                "stream.contacts", contactStreamItem.getContactUser());

                        activity.createActionClusterView(itemActionCluster, null, true);
                    }
                });

                break;
        }


        if (avatarResId == null) {
            // Set drawable images and color + alpha for placeholder image (background drawable)
//            holder.previewImage.setBackgroundResource(usePlaceholderImageRes);
            holder.previewImage.setBackground(context.getResources().getDrawable(usePlaceholderImageRes, context.getTheme()));
            Drawable previewImageBgDrawable = holder.previewImage.getBackground();
            previewImageBgDrawable.setAlpha(context.getResources().getInteger(R.integer.streamitem_generic_preview_placeholder_alpha));
            DrawableCompat.setTint(previewImageBgDrawable, ContextCompat.getColor(context, usePlaceholderImageColor));
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
