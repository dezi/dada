package com.aura.aosp.gorilla.launcher.model;

import android.content.Intent;
import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;

import java.lang.reflect.Method;

/**
 * The ActionItem model represents the universal action button found throughout all
 * navigation action button collections like the "action cluster".
 */
public class ActionItem extends AbstractActionItem {

    protected String name;
    protected FuncBaseView.FuncType funcType;
    protected Integer imageId;
    protected String action;
    protected Object invokeObject;
    protected Method invokeMethod;
    protected Object invokePayload;
    protected Intent intent;
    protected ActionCluster actionCluster;
    protected Float absoluteScore;

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param initialScore
     * @param action
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, @Nullable Float initialScore, String action) {
        setType(ItemType.TYPE_ACTION_INTERN);
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
     * @param invokedMethod
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, @Nullable Float initialScore, Method invokedMethod) {
        setType(ItemType.TYPE_ACTION_INTERN);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setInvokeMethod(invokedMethod);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param invokedObject
     * @param invokedMethod
     * @param invokedPayload
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, @Nullable Float initialScore, Object invokedObject, Method invokedMethod, Object invokedPayload) {
        setType(ItemType.TYPE_ACTION_INTERN);
        setName(name);
        setFuncType(funcType);
        setImageId(imageId);
        setInvokeObject(invokedObject);
        setInvokeMethod(invokedMethod);
        setInvokePayload(invokedPayload);
        setAbsoluteScore(initialScore);
    }

    /**
     * @param name
     * @param funcType
     * @param imageId
     * @param initialScore
     * @param intent
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, @Nullable Float initialScore, Intent intent) {
        setType(ItemType.TYPE_ACTION_INTERN);
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
     * @param initialScore
     * @param actionCluster
     */
    public ActionItem(String name, FuncBaseView.FuncType funcType, Integer imageId, @Nullable Float initialScore, ActionCluster actionCluster) {
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

    public void setAbsoluteScore(@Nullable Float absoluteScore) {
        this.absoluteScore = absoluteScore != null ? absoluteScore : 1.0f;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Object getInvokeObject() {
        return invokeObject;
    }

    public void setInvokeObject(Object invokeObject) {
        this.invokeObject = invokeObject;
    }

    public Method getInvokeMethod() {
        return invokeMethod;
    }

    public void setInvokeMethod(Method invokeMethod) {
        this.invokeMethod = invokeMethod;
    }

    public Object getInvokePayload() {
        return invokePayload;
    }

    public void setInvokePayload(Object invokePayload) {
        this.invokePayload = invokePayload;
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
