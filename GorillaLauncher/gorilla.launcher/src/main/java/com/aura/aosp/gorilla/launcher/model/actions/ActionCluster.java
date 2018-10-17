package com.aura.aosp.gorilla.launcher.model.actions;

import java.util.Comparator;
import java.util.List;

/**
 * The ActionCluster model.
 */
public class ActionCluster {

    public String name;
    public List<ActionItem> actionItems;

    /**
     * @param name
     * @param items
     */
    public ActionCluster(String name, List<ActionItem> items) {
        setName(name);
        setActionItems(items);
    }

    /**
     * Get sorted action cluster items by relevance computed from score
     *
     * @return
     */
    public List<ActionItem> getItemsByRelevance() {

        List<ActionItem> sortedActioneItems = actionItems;

        sortedActioneItems.sort(new Comparator<ActionItem>() {
            @Override
            public int compare(ActionItem o1, ActionItem o2) {
                if (o1.getAbsoluteScore() == o2.getAbsoluteScore()) {
                    return 0;
                }
                return o1.getAbsoluteScore() < o2.getAbsoluteScore() ? 1 : -1;
            }
        });

        return sortedActioneItems;
    }


    public List<ActionItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
