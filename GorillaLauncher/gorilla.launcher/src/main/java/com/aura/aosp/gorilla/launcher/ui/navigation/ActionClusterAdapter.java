package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItem;

import java.util.List;

/**
 * Adapter which handles the mapping of action cluster button data to the cluster button view
 * recycler view items.
 */
public class ActionClusterAdapter extends RecyclerView.Adapter<ActionClusterViewHolder> {

    private List<ActionItem> actionItems;
    private Context context;
    private Activity activity;

    /**
     * @param actionItems
     * @param context
     * @param activity
     */
    public ActionClusterAdapter(List<ActionItem> actionItems, Context context, Activity activity) {
        // Set initial items but Don't use setter: Until view holder isn't bound, we don't want to notify about changes
        this.actionItems = actionItems;
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
    public ActionClusterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ClusterButtonView clusterButtonView = (ClusterButtonView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cluster_button, parent, false);

        ActionClusterViewHolder viewHolder = new ActionClusterViewHolder(clusterButtonView);
        return viewHolder;
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
    public void onBindViewHolder(ActionClusterViewHolder holder, int position) {
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

    public List<ActionItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
        notifyDataSetChanged();
    }
}
