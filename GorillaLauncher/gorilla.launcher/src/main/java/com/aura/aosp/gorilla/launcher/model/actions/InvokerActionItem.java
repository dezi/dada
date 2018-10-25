package com.aura.aosp.gorilla.launcher.model.actions;

import android.support.annotation.Nullable;

import java.lang.reflect.Method;

/**
 * Action item that invokes an generic invoker method identified by an object and method,
 * and with an optional payload passed to it as argument.
 */
public class InvokerActionItem extends ActionItem {

    protected Object invokeObject;
    protected Method invokeMethod;
    protected Object invokePayload;
    protected ActionCluster actionCluster;

    /**
     * @param name
     * @param imagePlaceholderId
     * @param invokedMethod
     */
    public InvokerActionItem(String name, Integer imagePlaceholderId, @Nullable Float initialScore, Method invokedMethod) {
        super(name, imagePlaceholderId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INVOKER);
        setInvokeMethod(invokedMethod);
    }

    /**
     * @param name
     * @param imagePlaceholderId
     * @param invokedObject
     * @param invokedMethod
     * @param invokedPayload
     */
    public InvokerActionItem(String name, Integer imagePlaceholderId, @Nullable Float initialScore, Object invokedObject, Method invokedMethod, Object invokedPayload) {
        super(name, imagePlaceholderId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INVOKER);
        setInvokeObject(invokedObject);
        setInvokeMethod(invokedMethod);
        setInvokePayload(invokedPayload);
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

    public ActionCluster getActionCluster() {
        return actionCluster;
    }

    public void setActionCluster(ActionCluster actionCluster) {
        this.actionCluster = actionCluster;
    }
}
