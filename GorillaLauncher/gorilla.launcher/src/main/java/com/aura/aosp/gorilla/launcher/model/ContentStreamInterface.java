package com.aura.aosp.gorilla.launcher.model;

interface ContentStreamInterface {

    void addItem(int position, AbstractStreamDataItem item);
    void removeItem(AbstractStreamDataItem item);
}
