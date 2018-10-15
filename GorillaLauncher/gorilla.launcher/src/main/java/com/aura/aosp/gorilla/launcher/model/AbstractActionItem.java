package com.aura.aosp.gorilla.launcher.model;

public abstract class AbstractActionItem {

    private ItemType type;

    public enum ItemType {
        TYPE_ACTION_INTERN,
        TYPE_ACTION_EXTERN,
        TYPE_CLUSTER
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
