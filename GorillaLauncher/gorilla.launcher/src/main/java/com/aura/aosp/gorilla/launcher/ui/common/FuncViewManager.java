package com.aura.aosp.gorilla.launcher.ui.common;

import android.view.View;

import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;

import java.util.HashMap;
import java.util.Map;

/**
 * "Function view" manager responsible for keeping state of activate and inactive functions.
 */
public class FuncViewManager {

    private Map<FuncBaseView.FuncType, View> funcViews;

    public FuncViewManager() {
        funcViews = new HashMap<>();
    }

    public Map<FuncBaseView.FuncType, View> getFuncViews() {
        return funcViews;
    }

    public void addFuncView(FuncBaseView.FuncType funcType, View funcView) {
        funcViews.put(funcType, funcView);
    }

    public void removeFuncView(View funcView) {
        this.funcViews.remove(funcView);
    }
}
