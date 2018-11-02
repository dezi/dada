package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.StreamItemInterface;
import com.aura.aosp.gorilla.launcher.ui.common.StreamItemClickListener;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class StreamViewHolder extends RecyclerView.ViewHolder {

    private StreamItemClickListener streamItemClickListener;

    public StreamItemView item;
    public RecyclerView.LayoutParams itemLayoutParams;

//    public TextView title;
    public LinearLayout textContainer;
    public ImageView icon;
    public TextView text;
    public LinearLayout imageContainer;
    public ImageView image;
    public TextView imageCaption;

    public int originalHeight = 0;
    public StreamItemInterface.ItemDisplayState originalDisplayState;
    public Boolean isFullyOpened = false;

    /**
     * Construct a new view holder.
     * @param itemView
     */
    public StreamViewHolder(View itemView) {
        super(itemView);

//        streamItemClickListener = listener;
//        itemView.setOnClickListener(this);

        item = itemView.findViewById(R.id.item);
        itemLayoutParams = (RecyclerView.LayoutParams) item.getLayoutParams();

        originalHeight = itemLayoutParams.height;

        textContainer = itemView.findViewById(R.id.textContainer);
        imageContainer = itemView.findViewById(R.id.imageContainer);

        icon = itemView.findViewById(R.id.icon);
        text = itemView.findViewById(R.id.text);
        image = itemView.findViewById(R.id.image);
        imageCaption = itemView.findViewById(R.id.imageCaption);
    }

//    @Override
//    public void onClick(View itemView) {
//        streamItemClickListener.OnItemClick(itemView ,getAdapterPosition());
//    }

    /**
     * Tap listener
     */
    public class TapListener implements View.OnClickListener {

        private Context context;
        private StreamItemInterface streamItem;

        public TapListener(Context context, StreamItemInterface streamItem) {
            super();
            this.streamItem = streamItem;
            this.context = context;
        }

        @Override
        public void onClick(View v) {

            final int startHeight;
            final int newHeight;
            final int startArrowAngle;
            final int newArrowAngle;

            if (! isFullyOpened) {
                isFullyOpened = true;
                startHeight = item.getHeight();
                newHeight = item.getHeight() * 2;
                startArrowAngle = 0;
                newArrowAngle = 180;
                text.setText(streamItem.getText());
            } else {
                isFullyOpened = false;
                startHeight = item.getHeight();
                newHeight = context.getResources().getDimensionPixelSize(R.dimen.stream_item_preview_height);
                startArrowAngle = 180;
                newArrowAngle = 0;
                text.setText(streamItem.getTextExcerpt());
            }

            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    super.applyTransformation(interpolatedTime, t);
//                                    holder.itemLayoutParams.height = startHeight + (int) ((newHeight - startHeight) * interpolatedTime);
                    itemLayoutParams.height = startHeight + (int) ((newHeight - startHeight) * interpolatedTime);
                    item.setLayoutParams(itemLayoutParams);
                    icon.setRotationY(startArrowAngle + (newArrowAngle - startArrowAngle) * interpolatedTime);
                }
            };

            a.setDuration(200);
            item.startAnimation(a);

//                            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
//                            holder.itemView.setLayoutParams(lp);

            text.setText(streamItem.getText());
            streamItem.onFullyViewed();
        }
    }
}
