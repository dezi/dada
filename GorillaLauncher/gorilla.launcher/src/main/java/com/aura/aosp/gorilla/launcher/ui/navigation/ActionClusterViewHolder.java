package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.support.v7.widget.RecyclerView;

import com.aura.aosp.gorilla.launcher.R;

/**
 * Provide a reference to the views for each data item
 * Complex data items may need more than one view per item, and
 * you provide access to all the views for a data item in a view holder
 */
public class ActionClusterViewHolder extends RecyclerView.ViewHolder {

    public ClusterButtonView actionButton;

    public ActionClusterViewHolder(ClusterButtonView clusterButtonView) {
        super(clusterButtonView);
        actionButton = clusterButtonView.findViewById(R.id.actionButton);
    }
}
