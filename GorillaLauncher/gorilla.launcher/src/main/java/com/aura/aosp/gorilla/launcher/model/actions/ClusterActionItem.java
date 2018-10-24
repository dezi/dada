package com.aura.aosp.gorilla.launcher.model.actions;

import android.support.annotation.Nullable;

import java.lang.reflect.Method;

/**
 * Action item that invokes the generation and rendering of another action cluster.
 */
public class ClusterActionItem extends ActionItem {

    protected Object invokeObject;
    protected Method invokeMethod;
    protected Object invokePayload;
    protected ActionCluster actionCluster;

    /**
     * @param name
     * @param imageId
     * @param initialScore
     * @param actionCluster
     */
    public ClusterActionItem(String name, Integer imageId, @Nullable Float initialScore, ActionCluster actionCluster) {
        super(name, imageId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_CLUSTER);
        setInvocationTarget(ActionItemInterface.invocationTarget.INVOCATION_TARGET_INTERN_CLUSTER);
        setActionCluster(actionCluster);
    }

    public ActionCluster getActionCluster() {
        return actionCluster;
    }

    public void setActionCluster(ActionCluster actionCluster) {
        this.actionCluster = actionCluster;
    }
}
