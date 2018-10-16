package com.aura.aosp.gorilla.launcher.model;

import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * The ActionItem model represents the universal action button found throughout all
 * navigation action button collections like the "action cluster".
 * <p>
 * TODO: Extract more subclasses: GenericActionItem, IntentActionItem, ClusterActionItem
 */
public class ActionItem implements ActionItemInterface {

    protected invocationType invocationType = ActionItemInterface.invocationType.INVOCATION_TYPE_UNKNOWN;
    protected invocationTarget invocationTarget = ActionItemInterface.invocationTarget.INVOCATION_TARGET_INTERN_VIEW;

    protected String name;
    protected Integer imageId;
    protected String action = null;
    protected Intent intent;
    protected ActionCluster actionCluster;
    protected Float absoluteScore;

    /**
     * @param name
     * @param imageId
     * @param initialScore
     */
    public ActionItem(String name, Integer imageId, @Nullable Float initialScore) {
        setName(name);
        setImageId(imageId);
        setAbsoluteScore(initialScore);
    }

    /**
     *
     * @param name
     * @param imageId
     * @param initialScore
     * @param action
     */
    public ActionItem(String name, Integer imageId, @Nullable Float initialScore, String action) {
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INTENTSTRING);
        setName(name);
        setImageId(imageId);
        setAction(action);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param imageId
     * @param initialScore
     * @param intent
     */
    public ActionItem(String name, Integer imageId, @Nullable Float initialScore, Intent intent) {
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INTENT);
        setName(name);
        setImageId(imageId);
        setIntent(intent);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param imageId
     * @param initialScore
     * @param actionCluster
     */
    public ActionItem(String name, Integer imageId, @Nullable Float initialScore, ActionCluster actionCluster) {
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_CLUSTER);
        setInvocationTarget(ActionItemInterface.invocationTarget.INVOCATION_TARGET_INTERN_CLUSTER);
        setName(name);
        setImageId(imageId);
        setActionCluster(actionCluster);
        setAbsoluteScore(initialScore);
    }

    @Override
    public invocationType getInvocationType() {
        return invocationType;
    }

    @Override
    public void setInvocationType(invocationType invocationType) {
        this.invocationType = invocationType;
    }

    public invocationTarget getInvocationTarget() {
        return invocationTarget;
    }

    public void setInvocationTarget(invocationTarget invocationTarget) {
        this.invocationTarget = invocationTarget;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getImageId() {
        return imageId;
    }

    @Override
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    @Override
    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    @Override
    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public ActionCluster getActionCluster() {
        return actionCluster;
    }

    public void setActionCluster(ActionCluster actionCluster) {
        this.actionCluster = actionCluster;
    }
}
