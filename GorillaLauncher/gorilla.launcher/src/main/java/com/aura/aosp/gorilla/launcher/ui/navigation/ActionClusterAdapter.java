package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.ActionItem;

import java.util.List;

/**
 * Adapter which handles the mapping of stream data to the timeline
 * recycler view items.
 */
public class ActionClusterAdapter extends RecyclerView.Adapter<ActionClusterViewHolder> {

    private List<ActionItem> actionItems;
    private Context context;
    private Activity activity;

    /**
     * @param list
     * @param context
     * @param activity
     */
    public ActionClusterAdapter(List<ActionItem> list, Context context, Activity activity) {
        this.actionItems = list;
        this.context = context;
        this.activity = activity;
    }

    @Override
    public ActionClusterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create new views (invoked by the layout manager)
        ClusterButtonView clusterButtonView = (ClusterButtonView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cluster_button, parent, false);

        ActionClusterViewHolder viewHolder = new ActionClusterViewHolder(clusterButtonView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ActionClusterViewHolder holder, int position) {
        // Replace the contents of a view (invoked by the layout manager)
        // Use the provided View Holder on the onCreateViewHolder method
        // to populate the current row on the RecyclerView
        ActionItem dataSet = actionItems.get(position);

        holder.actionButton.initWithAction(dataSet, activity);
    }

    @Override
    public int getItemCount() {
        // Return the size of your dataset (invoked by the layout manager)
        return actionItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addItem(int position, ActionItem actionItem) {
        // Insert a new item to the actionItem on a predefined position
        actionItems.add(position, actionItem);
        notifyItemInserted(position);
    }

    public void removeItem(ActionItem actionItem) {
        // Remove a RecyclerView item containing a specified Data object
        int position = actionItems.indexOf(actionItem);
        actionItems.remove(position);
        notifyItemRemoved(position);
    }
}
