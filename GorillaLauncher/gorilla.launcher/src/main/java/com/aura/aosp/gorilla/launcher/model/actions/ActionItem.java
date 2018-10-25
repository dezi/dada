package com.aura.aosp.gorilla.launcher.model.actions;

import android.support.annotation.Nullable;

/**
 * The ActionItem model represents the universal action button found throughout all
 * navigation action button collections like the "action cluster".
 */
public class ActionItem implements ActionItemInterface {

    protected invocationType invocationType = ActionItemInterface.invocationType.INVOCATION_TYPE_UNKNOWN;
    protected invocationTarget invocationTarget = ActionItemInterface.invocationTarget.INVOCATION_TARGET_INTERN_VIEW;

    protected String name;
    protected Integer imagePlaceholderId;
    protected Float absoluteScore;

    /**
     * @param name
     * @param imagePlaceholderId
     * @param initialScore
     */
    public ActionItem(String name, Integer imagePlaceholderId, @Nullable Float initialScore) {
        setName(name);
        setImagePlaceholderId(imagePlaceholderId);
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

    @Override
    public invocationTarget getInvocationTarget() {
        return invocationTarget;
    }

    @Override
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
    public Integer getImagePlaceholderId() {
        return imagePlaceholderId;
    }

    @Override
    public void setImagePlaceholderId(Integer imagePlaceholderId) {
        this.imagePlaceholderId = imagePlaceholderId;
    }

    @Override
    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    @Override
    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }
}
