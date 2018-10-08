package com.aura.aosp.gorilla.launcher.model;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;

import java.lang.reflect.Method;

/**
 * The ActionItem model represents the universal action button found throughout all
 * navigation actions like the "action cluster".
 */
public class ActionItem extends AbstractActionItem {

    protected String name;
    protected FuncBaseView.FuncType funcType;
    protected Integer imageId;
    protected String action;
    protected Method method;
    protected Intent intent;
    protected ActionCluster actionCluster;
    protected Float absoluteScore;

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param action
     * @param initialScore
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, String action, @Nullable Float initialScore) {
        setType(ItemType.TYPE_FINAL);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setAction(action);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param method
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, Method method, @Nullable Float initialScore) {
        setType(ItemType.TYPE_FINAL);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setMethod(method);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param intent
     * @param initialScore
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, Intent intent,@Nullable Float initialScore) {
        setType(ItemType.TYPE_FINAL);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setIntent(intent);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param actionCluster
     * @param initialScore
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, ActionCluster actionCluster, @Nullable Float initialScore) {
        setType(ItemType.TYPE_CLUSTER);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setActionCluster(actionCluster);
        setAbsoluteScore(initialScore);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FuncBaseView.FuncType getFuncType() {
        return funcType;
    }

    public void setFuncType(FuncBaseView.FuncType funcType) {
        this.funcType = funcType;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    public Float getAbsoluteScore() {
        return absoluteScore;
    }

    public void setAbsoluteScore(@Nullable  Float absoluteScore) {
        this.absoluteScore = absoluteScore != null? absoluteScore : 1.0f;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
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
