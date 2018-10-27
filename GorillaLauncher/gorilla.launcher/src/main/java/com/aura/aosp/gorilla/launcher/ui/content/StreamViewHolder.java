package com.aura.aosp.gorilla.launcher.ui.content;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class StreamViewHolder extends RecyclerView.ViewHolder {
    public StreamItemView item;
//    public TextView title;
    public LinearLayout previewTextContainer;
    public ImageView previewIcon;
    public TextView previewTitle;
    public TextView previewText;
    public LinearLayout previewImageContainer;
    public ImageView previewImage;

    public StreamViewHolder(View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.item);
        previewIcon = itemView.findViewById(R.id.previewIcon);
        previewTitle = itemView.findViewById(R.id.previewTitle);
        previewText = itemView.findViewById(R.id.previewText);
        previewTextContainer = itemView.findViewById(R.id.previewTextContainer);
        previewImage = itemView.findViewById(R.id.previewImage);
        previewImageContainer = itemView.findViewById(R.id.previewImageContainer);
    }
}
