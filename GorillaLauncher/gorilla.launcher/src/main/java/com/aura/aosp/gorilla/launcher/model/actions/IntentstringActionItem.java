package com.aura.aosp.gorilla.launcher.model.actions;

import android.support.annotation.Nullable;

/**
 * Action item that invokes an Android SDK intent.
 */
public class IntentstringActionItem extends ActionItem {

    protected String action;

    /**
     *
     * @param name
     * @param imagePlaceholderId
     * @param initialScore
     * @param action
     */
    public IntentstringActionItem(String name, Integer imagePlaceholderId, @Nullable Float initialScore, String action) {
        super(name, imagePlaceholderId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INTENTSTRING);
        setAction(action);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
