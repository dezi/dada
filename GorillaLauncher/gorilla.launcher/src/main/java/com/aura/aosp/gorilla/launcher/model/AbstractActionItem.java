package com.aura.aosp.gorilla.launcher.model;

public abstract class AbstractActionItem {

    private ItemType type;

    public enum ItemType {
        TYPE_FINAL,
        TYPE_CLUSTER
    };

//    public static final class MyItemType {
//        public static int TYPE_FINAL = 1;
//        public static int TYPE_CLUSTER = 2;
//    };

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
