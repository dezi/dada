package com.aura.aosp.gorilla.launcher.ui.content;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
    public TextView previewText;
    public ImageView previewImage;
    public ImageView dotButton;

    public StreamViewHolder(View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.item);
//        title = itemView.findViewById(R.id.title);
        previewText = itemView.findViewById(R.id.previewText);
        previewImage = itemView.findViewById(R.id.previewImage);
        dotButton = itemView.findViewById(R.id.dotButton);
    }
}
