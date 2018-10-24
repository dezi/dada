package com.aura.aosp.gorilla.launcher.model.actions;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Action item that invokes an Android SDK intent.
 */
public class IntentActionItem extends ActionItem {

    protected Intent intent;

    /**
     * @param name
     * @param imageId
     * @param initialScore
     * @param intent
     */
    public IntentActionItem(String name, Integer imageId, @Nullable Float initialScore, Intent intent) {
        super(name, imageId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INTENT);
        setIntent(intent);
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
