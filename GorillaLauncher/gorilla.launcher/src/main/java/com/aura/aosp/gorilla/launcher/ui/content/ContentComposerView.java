package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.util.AttributeSet;

import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;

/**
 * View for composing every kind of content (like messages) - this is going to be the main
 * component for what we call the "Universal Editor"
 */
public class ContentComposerView extends FuncBaseView {
    public ContentComposerView(Context context) {
        super(context);
    }

    public ContentComposerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentComposerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
