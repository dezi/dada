package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.GenericStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface;
import com.aura.aosp.gorilla.launcher.store.ActionClusterStore;
import com.aura.aosp.gorilla.launcher.ui.common.ImageShaper;

import java.util.List;

/**
 * Adapter which handles the mapping of stream data to the content stream item view
 * recycler view items.
 */
public class StreamAdapter extends RecyclerView.Adapter<StreamViewHolder> {

    private static final String LOGTAG = StreamAdapter.class.getSimpleName();

    private List<StreamItemInterface> streamItems;

    private SparseArray<Bitmap> imageShapeCache = new SparseArray<Bitmap>();
    private SparseArray<Bitmap> imageLeftShapeCache = new SparseArray<Bitmap>();
    private SparseArray<Bitmap> imageRightShapeCache = new SparseArray<Bitmap>();

    private Context context;

    private StreamActivity activity;
    private ActionClusterStore actionClusterStore;

    private static final int ITEM_TYPE_DOT = 0;
    private static final int ITEM_TYPE_CIRCLE = 5;
    private static final int ITEM_TYPE_PREVIEW_LEFT = 10;
    private static final int ITEM_TYPE_PREVIEW_RIGHT = 11;
    private static final int ITEM_TYPE_FULLVIEW_LEFT = 20;
    private static final int ITEM_TYPE_FULLVIEW_RIGHT = 21;

    private int lastPosition = - 1;

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

            case ITEM_TYPE_DOT:
            default:
                itemView = (StreamItemView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_stream_item_dot, parent, false);
                break;

            case ITEM_TYPE_PREVIEW_LEFT:
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
        final StreamItemInterface dataSet = streamItems.get(position);
        int itemType;

        switch (dataSet.getType()) {

            default:
                itemType = ITEM_TYPE_DOT;
                break;

            case TYPE_STREAMITEM_CONTACT:

                ContactStreamItem contactStreamItem = (ContactStreamItem) dataSet;

                if (contactStreamItem.isMyIdentity()) {
                    itemType = ITEM_TYPE_PREVIEW_RIGHT;
                } else {
                    itemType = ITEM_TYPE_PREVIEW_LEFT;
                }

                break;

            case TYPE_STREAMITEM_MESSAGE:

                if (false && position < getStreamItems().size() - 7) {
                    itemType = ITEM_TYPE_DOT;
                } else {
                    if (dataSet.isMyItem()) {
                        itemType = ITEM_TYPE_PREVIEW_RIGHT;
                    } else {
                        itemType = ITEM_TYPE_PREVIEW_LEFT;
                    }
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

        final StreamItemInterface streamItem = streamItems.get(position);

        // TODO: Initialize item directly through implementing class.
        // TODO: Probably it makes sense to initialize child views of the StreamItemView independently
//        holder.item.initWithItem(streamItem);

        final int usePlaceholderImageRes = streamItem.getImagePlaceholderId();
        final int usePlaceholderImageColor = R.color.color_stream_image_drawable;

        int useIconImageRes = streamItem.getImagePlaceholderId();
        int useIconImageColor = R.color.color_stream_icon_drawable_state_default;

        float previewItemHeight;
        float previewItemWidth;

        final Integer useImageShapeBgRes;
        int useShapeBgColor = R.color.color_transparent;

        final Drawable shapeTextContainerBgDrawable = holder.previewTextContainer.getBackground();
        Drawable shapeImageContainerBgDrawable = holder.previewImageContainer.getBackground();

        // Do animations

//        Log.d("##### holder.getLayoutPosition: <%d>", holder.getLayoutPosition());

//        holder.item.setResizeAnimation();
        Animation moveAnimation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.ip_up_from_bottom
                        : R.anim.ip_down_from_top);

        moveAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                return;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        holder.item.startAnimation(moveAnimation);

//        // Animations after updating content
//        if (position > lastPosition) {
//            ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 1.5f, 1f, 1.5f);
//            scaleAnimation.setDuration(200);
//            holder.item.startAnimation(scaleAnimation);
//        }

        lastPosition = position;

        // Modify some attributes based  on stream item type
        switch (streamItem.getType()) {

            case TYPE_STREAMITEM_GENERIC:
                useShapeBgColor = R.color.color_stream_preview_bg_generic;
                break;

            case TYPE_STREAMITEM_CONTACT:
                useShapeBgColor = R.color.color_stream_preview_bg_contact;
                break;

            case TYPE_STREAMITEM_MESSAGE:

                if (streamItem.isMyItem()) {

                    useShapeBgColor = R.color.color_stream_preview_bg_mymessage_read;

                    if (streamItem.shareIsQueued()) {
                        useIconImageRes =  R.drawable.ic_access_time_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_default;
                    }

                    if (streamItem.shareIsSent()) {
                        useIconImageRes =  R.drawable.ic_access_time_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_success;
                    }

                    if (streamItem.shareIsReceived()) {
                        useIconImageRes =  R.drawable.ic_check_circle_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_default;
                    }

                    if (streamItem.shareIsRead()) {
                        useIconImageRes =  R.drawable.ic_check_circle_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_success;
                    }

                } else {

                    if (streamItem.isFullyViewed()) {
                        useShapeBgColor = R.color.color_stream_preview_bg_message_unread;
                    } else {
                        useShapeBgColor = R.color.color_stream_preview_bg_mymessage_read;
                    }
                }

                break;

            case TYPE_STREAMITEM_INVISIBLE:
                useShapeBgColor = R.color.color_transparent;
                break;
        }

        switch (holder.getItemViewType()) {

            case ITEM_TYPE_DOT:
            default:
                useImageShapeBgRes = R.drawable.stream_dot;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_dot_item_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_dot_item_image_width);
                break;

            case ITEM_TYPE_PREVIEW_LEFT:
                useImageShapeBgRes = R.drawable.stream_preview_rounded_corners_right;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_preview_item_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_preview_item_image_width);
                break;

            case ITEM_TYPE_PREVIEW_RIGHT:
                useImageShapeBgRes = R.drawable.stream_preview_rounded_corners_left;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_preview_item_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_preview_item_image_width);
                break;
        }

//        // Set title
//        String title = streamItem.getTitle();
//
//        Long timeStamp = streamItem.getTimeCreated();
//
//        if (timeStamp != null) {
//            String datestring = Dates.getLocalDateAndTime(timeStamp);
//            String timeTag = datestring.substring(8, 10) + ":" + datestring.substring(10, 12);
//            title += " – " + timeTag;
//        }
//
//        holder.previewTitle.setText(title);

        // Set text
        holder.previewText.setText(streamItem.getTextExcerpt());

        // Cleanup resources applied to preview image before applying new ones
        holder.previewImage.setBackground(null);
        holder.previewImage.setImageBitmap(null);

        // Set color of background shape drawables for text and image part (by now reflecting item state like "unread", "unopened")
        DrawableCompat.setTint(shapeTextContainerBgDrawable, ContextCompat.getColor(context, useShapeBgColor));
        DrawableCompat.setTint(shapeImageContainerBgDrawable, ContextCompat.getColor(context, useShapeBgColor));

        //
        // Get image or placeholder image to insert for item (preview + full view)
        //
        Integer imageId = streamItem.getImageId();

        if (imageId != null) {

            // Create or get shaped bitmap from cache and place as previewImage
            Bitmap imageBitmap;

            switch (holder.getItemViewType()) {

                case ITEM_TYPE_DOT:
                default:

                    if (imageShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageShapeCache.get(imageId);
                    }

                    break;

                case ITEM_TYPE_PREVIEW_LEFT:

                    if (imageLeftShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageLeftShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageLeftShapeCache.get(imageId);
                    }

                    break;

                case ITEM_TYPE_PREVIEW_RIGHT:

                    if (imageRightShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageRightShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageRightShapeCache.get(imageId);
                    }

                    break;
            }

            ImageShaper.placeBitmapInView(imageBitmap, holder.previewImage, null, null);
            holder.previewImage.setImageAlpha(context.getResources().getInteger(R.integer.streamitem_message_preview_avatar_alpha));

        } else {

            // Set drawable image, color and alpha for placeholder image (background drawable)
            holder.previewImage.setBackground(context.getResources().getDrawable(usePlaceholderImageRes, context.getTheme()));

            Drawable previewImageBgDrawable = holder.previewImage.getBackground();
            previewImageBgDrawable.setAlpha(context.getResources().getInteger(R.integer.streamitem_generic_preview_placeholder_alpha));
            DrawableCompat.setTint(previewImageBgDrawable, ContextCompat.getColor(context, usePlaceholderImageColor));
        }


        // Set drawable image, color and alpha for icon image (background drawable)
        holder.previewIcon.setBackground(context.getResources().getDrawable(useIconImageRes, context.getTheme()));

        Drawable previewIconBgDrawable = holder.previewIcon.getBackground();
        previewIconBgDrawable.setAlpha(context.getResources().getInteger(R.integer.streamitem_generic_preview_icon_alpha));
        DrawableCompat.setTint(previewIconBgDrawable, ContextCompat.getColor(context, useIconImageColor));

        // Adjust shape, content and visualization according to stream item type
        switch (streamItem.getType()) {

            default:
                break;

            case TYPE_STREAMITEM_MESSAGE:

                // TODO: Reorganize the whole onClick binding in conjunction with refactoring
                // TODO: actions and action clusters

                if (! streamItem.isMyItem()) {

                    holder.previewImageContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            // TODO: Create transition feedback etc.
                            // Create initial action button cluster (attach it to "root" container)
                            actionClusterStore = new ActionClusterStore(context);

                            ActionCluster itemActionCluster = actionClusterStore.getClusterForAction(
                                    "stream.contacts", streamItem.getOwnerUser());

                            activity.createActionClusterView(itemActionCluster, null, true);
                        }
                    });

                    holder.previewTextContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            streamItem.onFullyViewed();
                        }
                    });
                }

                break;

            case TYPE_STREAMITEM_CONTACT:

                final ContactStreamItem contactStreamItem = (ContactStreamItem) streamItem;

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
    }

    @Override
    public int getItemCount() {
        // Return the size of your streamItem (invoked by the layout manager)
        return streamItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull StreamViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    @Override
    public void registerAdapterDataObserver(@NonNull RecyclerView.AdapterDataObserver observer) {
        super.registerAdapterDataObserver(observer);
        // TODO: Register own observer with special handling for various stream item data types!
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

    public List<StreamItemInterface> getStreamItems() {
        return streamItems;
    }

    public void setStreamItems(List<StreamItemInterface> streamItems) {
        this.streamItems = streamItems;
        notifyDataSetChanged();
    }
}
