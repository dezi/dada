package com.aura.aosp.gorilla.launcher.ui.common;

import android.view.View;

import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;

import java.util.HashMap;
import java.util.Map;

/**
 * "Function view" manager responsible for keeping state of activate and inactive functions.
 */
public class FuncViewManager {

    private Map<FuncBaseView.FuncType, FuncBaseView> funcViews;

    public FuncViewManager() {
        funcViews = new HashMap<>();
    }

    public Map<FuncBaseView.FuncType, FuncBaseView> getFuncViews() {
        return funcViews;
    }

    public void addFuncView(FuncBaseView.FuncType funcType, FuncBaseView funcView) {
        funcViews.put(funcType, funcView);
    }

    public void removeFuncView(View funcView) {
        this.funcViews.remove(funcView);
    }
}
