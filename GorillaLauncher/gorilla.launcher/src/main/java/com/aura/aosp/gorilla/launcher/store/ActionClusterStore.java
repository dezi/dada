package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;
import android.util.Log;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.SampleData;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class ActionClusterStore {

    private static final String LOGTAG = ActionClusterStore.class.getSimpleName();

    private Context context;

    public ActionClusterStore(Context context) {
        this.setContext(context);
    }

    /**
     * @param identity
     * @return
     */
    public ActionCluster getClusterForSelectedIdentity(String actionDomain, Identity identity) {

        List<ActionItem> items = new ArrayList<>();

        switch (actionDomain) {
            case "com.aura.aosp.gorilla.stream.contacts":

                try {
                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_message_black_24dp,
                            1f,
                            getContext(),
                            StreamActivity.class.getMethod("onOpenContentComposer", Identity.class),
                            identity));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_call_black_24dp,
                            0.95f,
                            actionDomain + ".action.START_PHONE_CALL"
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_addPerson),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_person_add_black_24dp,
                            0.95f, actionDomain + ".action.ADD_PERSON"
                    ));
                } catch (NoSuchMethodException e) {
                    Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>",
                            e.getMessage()));
                }

                break;
        }

        return new ActionCluster(actionDomain, items);
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
                            0.870f,
                            StreamActivity.class.getMethod("onOpenSimpleCalendar")
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_call_black_24dp,
                            0.95f,
                            actionDomain + ".action.START_PHONE_CALL"
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_sms_black_24dp,
                            0.92f,
                            new ActionCluster(actionDomain + ".cluster.COMPOSE_MESSAGE", SampleData.getMessengerActionItems(context))
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_message_black_24dp,
                            0.98f,
                            actionDomain + ".action.CREATE_NOTE"
                    ));

                    ActionItem switchProfileActionItem = new ActionItem(
                            context.getResources().getString(R.string.actions_switchProfile),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_account_circle_black_24dp,
                            0.97f,
                            context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp")
                    );

                    switchProfileActionItem.setType(ActionItem.ItemType.TYPE_ACTION_EXTERN);
                    items.add(switchProfileActionItem);

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_addAlarm),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_add_alarm_black_24dp,
                            0.88f,
                            StreamActivity.class.getMethod("onOpenAlarmClock")
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_openStream),
                            FuncBaseView.FuncType.FULLSCREEN,
                            R.drawable.stream_oval_24dp,
                            0.93f,
                            StreamActivity.class.getMethod("onOpenStream")
                    ));

                } catch (NoSuchMethodException e) {
                    Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>",
                            e.getMessage()));
                }

                break;

            case "com.aura.aosp.gorilla.stream.contacts":

                try {
                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_message_black_24dp,
                            1f,
                            getContext(),
                            StreamActivity.class.getMethod("onOpenContentComposer", Identity.class),
                            null));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_call_black_24dp,
                            0.95f,
                            actionDomain + ".action.START_PHONE_CALL"
                    ));

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_addPerson),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_person_add_black_24dp,
                            0.95f, actionDomain + ".action.ADD_PERSON"
                    ));
                } catch (NoSuchMethodException e) {
                    Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>",
                            e.getMessage()));
                }

                break;
        }

        return new ActionCluster(actionDomain, items);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
