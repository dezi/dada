package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;
import android.util.Log;

import com.aura.aosp.gorilla.launcher.LauncherActivity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.SampleData;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;
import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class ActionClusterStore {

    private static final String LOGTAG = SampleData.class.getSimpleName();

    private ActionCluster actionCluster;
    private Context context;

    public ActionClusterStore(Context context) {
        this.setContext(context);
    }

    /**
     * Get action cluster for given URI
     *
     * @param actionDomain
     * @return
     */
    public ActionCluster getClusterForActionEvent(String actionDomain) {

        List<ActionItem> items = new ArrayList<>();

        switch (actionDomain) {
            case "com.aura.aosp.gorilla.launcher":

                try {
                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_openCalendar),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_add_a_photo_black_24dp,
                            LauncherActivity.class.getMethod("onOpenSimpleCalendar"),
                            0.970f
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_call_black_24dp,
                            "com.aura.aosp.gorilla.launcher.action.START_PHONE_CALL",
                            0.95f
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_switchProfile),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_account_circle_black_24dp,
                            context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp"),
                            0.95f
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_message_black_24dp,
                            "com.aura.aosp.gorilla.launcher.action.EDIT_TEXT",
                            0.60f
                    ));
                } catch (NoSuchMethodException e) {
                    Log.e(LOGTAG, String.format("No such action invocation method found: <%s>, Activiy is <%>",
                            e.getMessage(), LauncherActivity.class));
                }
        }

        return new ActionCluster(actionDomain, items);
    }

    public ActionCluster getActionCluster() {
        return actionCluster;
    }

    public void setActionCluster(ActionCluster actionCluster) {
        this.actionCluster = actionCluster;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
