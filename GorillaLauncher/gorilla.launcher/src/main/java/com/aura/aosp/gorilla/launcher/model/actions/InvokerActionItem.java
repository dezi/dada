package com.aura.aosp.gorilla.launcher.model.actions;

import android.support.annotation.Nullable;

import com.aura.aosp.gorilla.launcher.model.actions.ActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItemInterface;

import java.lang.reflect.Method;

public class InvokerActionItem extends ActionItem {

    protected Object invokeObject;
    protected Method invokeMethod;
    protected Object invokePayload;

    /**
     * @param name
     * @param imageId
     * @param invokedMethod
     */
    public InvokerActionItem(String name, Integer imageId, @Nullable Float initialScore, Method invokedMethod) {
        super(name, imageId, initialScore);
        setInvocationType(ActionItemInterface.invocationType.INVOCATION_TYPE_INVOKER);
        setInvokeMethod(invokedMethod);
    }

    /**
     * @param name
     * @param imageId
     * @param invokedObject
     * @param invokedMethod
     * @param invokedPayload
     */
    public InvokerActionItem(String name, Integer imageId, @Nullable Float initialScore, Object invokedObject, Method invokedMethod, Object invokedPayload) {
        super(name, imageId, initialScore);
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
}
