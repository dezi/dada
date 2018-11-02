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

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;
import com.aura.aosp.gorilla.launcher.model.stream.FilteredStream;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface;
import com.aura.aosp.gorilla.launcher.store.ActionClusterStore;
import com.aura.aosp.gorilla.launcher.ui.common.ImageShaper;
import com.aura.aosp.gorilla.launcher.ui.common.StreamItemClickListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface.ItemDisplayState.DSTATE_EXPANDED;
import static com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface.ItemDisplayState.DSTATE_PREVIEW;

/**
 * Adapter which handles the mapping of stream data to the content stream item view
 * recycler view items.
 */
public class StreamAdapter extends RecyclerView.Adapter<StreamViewHolder> {

    private static final String LOGTAG = StreamAdapter.class.getSimpleName();

    private List<StreamItemInterface> streamItems;

    private SparseArray<Bitmap> imageSquareShapeCache = new SparseArray<Bitmap>();
    private SparseArray<Bitmap> imageCircleShapeCache = new SparseArray<Bitmap>();
    private SparseArray<Bitmap> imageLeftRoundedShapeCache = new SparseArray<Bitmap>();
    private SparseArray<Bitmap> imageRightRoundedShapeCache = new SparseArray<Bitmap>();

    private Context context;

    private StreamActivity activity;
    private ActionClusterStore actionClusterStore;

    private StreamItemClickListener previewItemClickListener;

    private static final int VIEW_TYPE_DOT = 0;
    private static final int VIEW_TYPE_CIRCLE = 5;
    private static final int VIEW_TYPE_PREVIEW_LEFT = 10;
    private static final int VIEW_TYPE_PREVIEW_RIGHT = 11;
    private static final int VIEW_TYPE_EXPANDED_LEFT = 20;
    private static final int VIEW_TYPE_EXPANDED_RIGHT = 21;

    private int lastPosition = -1;

    private static final Map<Integer, Integer> viewTypeLayoutMap = new HashMap<>();

    static {
        viewTypeLayoutMap.put(VIEW_TYPE_DOT, R.layout.fragment_stream_item_dot);
        viewTypeLayoutMap.put(VIEW_TYPE_CIRCLE, R.layout.fragment_stream_item_circle);
        viewTypeLayoutMap.put(VIEW_TYPE_PREVIEW_LEFT, R.layout.fragment_stream_item_preview_left);
        viewTypeLayoutMap.put(VIEW_TYPE_PREVIEW_RIGHT, R.layout.fragment_stream_item_preview_right);
        viewTypeLayoutMap.put(VIEW_TYPE_EXPANDED_LEFT, R.layout.fragment_stream_item_expanded_left);
        viewTypeLayoutMap.put(VIEW_TYPE_EXPANDED_RIGHT, R.layout.fragment_stream_item_expanded_right);
    }

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

        itemView = (StreamItemView) LayoutInflater.from(parent.getContext())
                .inflate(viewTypeLayoutMap.get(viewType), parent, false);

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
        final StreamItemInterface streamItem = streamItems.get(position);

        int itemViewType;

        switch (streamItem.getDisplayState()) {

            case DSTATE_DOT:
            default:
                itemViewType = VIEW_TYPE_DOT;
                break;

            case DSTATE_PREVIEW:

                if (false && position < getStreamItems().size() - 7) {
                    itemViewType = VIEW_TYPE_DOT;
                } else {
                    if (streamItem.isMyItem()) {
                        itemViewType = VIEW_TYPE_PREVIEW_RIGHT;
                    } else {
                        itemViewType = VIEW_TYPE_PREVIEW_LEFT;
                    }
                }

                break;

            case DSTATE_EXPANDED:

                if (false && position < getStreamItems().size() - 7) {
                    itemViewType = VIEW_TYPE_DOT;
                } else {
                    if (streamItem.isMyItem()) {
                        itemViewType = VIEW_TYPE_EXPANDED_RIGHT;
                    } else {
                        itemViewType = VIEW_TYPE_EXPANDED_LEFT;
                    }
                }

                break;

            case DSTATE_CIRCLE:

                itemViewType = VIEW_TYPE_CIRCLE;
                break;
        }

        return itemViewType;
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
    public void onBindViewHolder(final StreamViewHolder holder, final int position) {

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

        final Drawable shapeTextContainerBgDrawable = holder.textContainer.getBackground();
        Drawable shapeImageContainerBgDrawable = holder.imageContainer.getBackground();

        holder.originalDisplayState = streamItem.getDisplayState();

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

            default:
            case ITEMTYPE_IMAGE:
                useShapeBgColor = R.color.color_transparent;
                useIconImageColor = R.color.color_transparent;
                break;

            case ITEMTYPE_DRAFT:
                useShapeBgColor = R.color.color_stream_item_bg_aura_ai;
                break;

            case ITEMTYPE_CONTACT:
                useShapeBgColor = R.color.color_stream_item_bg_contact;
                break;

            case ITEMTYPE_MESSAGE:

                if (streamItem.isMyItem()) {

                    useShapeBgColor = R.color.color_stream_item_bg_mymessage_default;

                    if (streamItem.shareIsQueued()) {
                        useIconImageRes = R.drawable.ic_access_time_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_default;
                    }

                    if (streamItem.shareIsSent()) {
                        useIconImageRes = R.drawable.ic_access_time_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_success;
                    }

                    if (streamItem.shareIsReceived()) {
                        useIconImageRes = R.drawable.ic_check_circle_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_default;
                    }

                    if (streamItem.shareIsRead()) {
                        useIconImageRes = R.drawable.ic_check_circle_black_24dp;
                        useIconImageColor = R.color.color_stream_icon_drawable_state_success;
                    }

                } else {

                    if (streamItem.isFullyViewed()) {
                        useShapeBgColor = R.color.color_stream_item_bg_message_read;
                    } else {
                        useShapeBgColor = R.color.color_stream_item_bg_message_default;
                    }
                }

                break;
        }

        switch (holder.getItemViewType()) {

            case VIEW_TYPE_DOT:
            default:
                useImageShapeBgRes = R.drawable.shape_stream_item_dot;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_item_dot_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_item_dot_image_width);
                break;

            case VIEW_TYPE_CIRCLE:
                useImageShapeBgRes = R.drawable.shape_stream_item_circle;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_item_preview_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_item_preview_image_width);
                break;

            case VIEW_TYPE_PREVIEW_LEFT:
                useImageShapeBgRes = R.drawable.shape_stream_item_rounded_corners_right;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_item_preview_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_item_preview_image_width);
                break;

            case VIEW_TYPE_PREVIEW_RIGHT:
                useImageShapeBgRes = R.drawable.shape_stream_item_rounded_corners_left;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_item_preview_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_item_preview_image_width);
                break;

            case VIEW_TYPE_EXPANDED_LEFT:
            case VIEW_TYPE_EXPANDED_RIGHT:
                useImageShapeBgRes = R.drawable.shape_stream_item_square;
                previewItemHeight = context.getResources().getDimension(R.dimen.stream_item_expanded_image_height);
                previewItemWidth = context.getResources().getDimension(R.dimen.stream_item_expanded_image_width);
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
        holder.text.setText(streamItem.getTextExcerpt());

        // Cleanup resources applied to preview image before applying new ones
        holder.image.setBackground(null);
        holder.image.setImageBitmap(null);

        // Set color of background shape drawables for text and image part (by now reflecting item state like "unread", "unopened")
        DrawableCompat.setTint(shapeTextContainerBgDrawable, ContextCompat.getColor(context, useShapeBgColor));
        DrawableCompat.setTint(shapeImageContainerBgDrawable, ContextCompat.getColor(context, useShapeBgColor));

        //
        // Get image or placeholder image to insert for item (preview + full view)
        //
        Integer imageId = streamItem.getImageId();

        if (imageId != null) {

            // Create or get shaped bitmap from cache and place as image
            Bitmap imageBitmap;

            switch (holder.getItemViewType()) {

                case VIEW_TYPE_DOT:
                case VIEW_TYPE_CIRCLE:
                default:

                    if (imageCircleShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageCircleShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageCircleShapeCache.get(imageId);
                    }

                    break;

                case VIEW_TYPE_PREVIEW_LEFT:

                    if (imageLeftRoundedShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageLeftRoundedShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageLeftRoundedShapeCache.get(imageId);
                    }

                    break;

                case VIEW_TYPE_PREVIEW_RIGHT:

                    if (imageRightRoundedShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageRightRoundedShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageRightRoundedShapeCache.get(imageId);
                    }

                    break;

                case VIEW_TYPE_EXPANDED_LEFT:
                case VIEW_TYPE_EXPANDED_RIGHT:

                    if (imageSquareShapeCache.get(imageId) == null) {
                        imageBitmap = ImageShaper.getShapedBitmap(context, imageId, useImageShapeBgRes, previewItemHeight, previewItemWidth);
                        imageSquareShapeCache.put(imageId, imageBitmap);
                    } else {
                        imageBitmap = imageSquareShapeCache.get(imageId);
                    }

                    if (streamItem.getImageCaption() != null) {
                        holder.imageCaption.setText(streamItem.getImageCaption());
                    }

                    break;
            }

            ImageShaper.placeBitmapInView(imageBitmap, holder.image, null, null);
            holder.image.setImageAlpha(context.getResources().getInteger(R.integer.streamitem_message_preview_avatar_alpha));

        } else {

            // Set drawable image, color and alpha for placeholder image (background drawable)
            holder.image.setBackground(context.getResources().getDrawable(usePlaceholderImageRes, context.getTheme()));

            Drawable previewImageBgDrawable = holder.image.getBackground();
            previewImageBgDrawable.setAlpha(context.getResources().getInteger(R.integer.streamitem_generic_preview_placeholder_alpha));
            DrawableCompat.setTint(previewImageBgDrawable, ContextCompat.getColor(context, usePlaceholderImageColor));
        }


        // Set drawable image, color and alpha for icon image (background drawable)
        holder.icon.setBackground(context.getResources().getDrawable(useIconImageRes, context.getTheme()));

        Drawable previewIconBgDrawable = holder.icon.getBackground();
        previewIconBgDrawable.setAlpha(context.getResources().getInteger(R.integer.streamitem_generic_preview_icon_alpha));
        DrawableCompat.setTint(previewIconBgDrawable, ContextCompat.getColor(context, useIconImageColor));

        // Adjust shape, content and visualization according to stream item type
        switch (streamItem.getType()) {

            default:
                break;

            case ITEMTYPE_MESSAGE:

                // TODO: Reorganize the whole onClick binding in conjunction with refactoring
                // TODO: actions and action clusters

                if (!streamItem.isMyItem()) {

                    holder.imageContainer.setOnClickListener(new View.OnClickListener() {
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
                }


                holder.textContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Vielleicht auch das ganze Type-Gehampel anpassen!

                        switch (streamItem.getDisplayState()) {

                            // TODO: Hier weiter...:
                            case DSTATE_EXPANDED:
                                streamItem.setDisplayState(DSTATE_PREVIEW);
                                notifyItemChanged(position);
                                break;

                            case DSTATE_CIRCLE:
                                streamItem.setDisplayState(DSTATE_PREVIEW);
                                notifyItemChanged(position);
                                break;

                            case DSTATE_PREVIEW:
                                streamItem.setDisplayState(DSTATE_EXPANDED);
                                notifyItemChanged(position);
                                break;
                        }
//
//                        final int startHeight;
//                        final int newHeight;
//                        final int startArrowAngle;
//                        final int newArrowAngle;
//
//                        if (!holder.isFullyOpened) {
//                            holder.isFullyOpened = true;
//                            startHeight = holder.item.getHeight();
//                            newHeight = holder.item.getHeight() * 2;
//                            startArrowAngle = 0;
//                            newArrowAngle = 180;
//                            holder.text.setText(streamItem.getText());
//
//                        } else {
//
//                            holder.isFullyOpened = false;
//                            startHeight = holder.item.getHeight();
//                            newHeight = context.getResources().getDimensionPixelSize(R.dimen.stream_item_preview_height);
//                            startArrowAngle = 180;
//                            newArrowAngle = 0;
//                            holder.text.setText(streamItem.getTextExcerpt());
//                        }
//
//                        // TODO: Hier weiter...
//                        Animation a = new Animation() {
//                            @Override
//                            protected void applyTransformation(float interpolatedTime, Transformation t) {
//                                super.applyTransformation(interpolatedTime, t);
////                                    holder.itemLayoutParams.height = startHeight + (int) ((newHeight - startHeight) * interpolatedTime);
//                                holder.itemLayoutParams.height = startHeight + (int) ((newHeight - startHeight) * interpolatedTime);
//                                holder.item.setLayoutParams(holder.itemLayoutParams);
//                                holder.icon.setRotationY(startArrowAngle + (newArrowAngle - startArrowAngle) * interpolatedTime);
//                            }
//                        };
//
//                        a.setDuration(200);
//                        holder.item.startAnimation(a);
//
//                        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
//                        holder.itemView.setLayoutParams(lp);
//
//                        streamItem.onFullyViewed();
                    }
                });


                break;

            case ITEMTYPE_CONTACT:

                final ContactStreamItem contactStreamItem = (ContactStreamItem) streamItem;

                // Invoke intent based action
                holder.image.setOnClickListener(new View.OnClickListener() {
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

    public void addItem(int position, StreamItemInterface streamItem) {
        // Insert a new item to the RecyclerView on a predefined position
        streamItems.add(position, streamItem);
        notifyItemInserted(position);
    }

    public void removeItem(StreamItemInterface streamItem) {
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
