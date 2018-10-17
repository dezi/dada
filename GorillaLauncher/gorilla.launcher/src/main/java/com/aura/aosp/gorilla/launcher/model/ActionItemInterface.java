package com.aura.aosp.gorilla.launcher.model;

import android.support.annotation.Nullable;

public interface ActionItemInterface {

    public static enum invocationType {
        INVOCATION_TYPE_UNKNOWN,
        INVOCATION_TYPE_DISABLED,
        INVOCATION_TYPE_INTENTSTRING,
        INVOCATION_TYPE_INTENT,
        INVOCATION_TYPE_INVOKER,
        INVOCATION_TYPE_CLUSTER
    }

    public static enum invocationTarget {
        INVOCATION_TARGET_INTERN_VIEW,
        INVOCATION_TARGET_INTERN_CLUSTER,
        INVOCATION_TARGET_EXTERN,
    }

    invocationType getInvocationType();

    void setInvocationType(invocationType type);

    String getName();

    void setName(String name);

    Integer getImageId();

    void setImageId(Integer imageId);

    Float getAbsoluteScore();

    void setAbsoluteScore(@Nullable Float absoluteScore);
}
