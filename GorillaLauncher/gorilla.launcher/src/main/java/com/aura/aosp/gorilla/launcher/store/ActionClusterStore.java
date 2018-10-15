package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;
import android.util.Log;

import com.aura.aosp.aura.common.univid.Identity;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.R;
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
        // TODO: Add actions based on score for this action domain
        try {

            switch (actionDomain) {
                case "com.aura.aosp.gorilla.stream.contacts":

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

                    break;

                case "com.aura.aosp.gorilla.func.content_composer":

                    items.add(new ActionItem(
                            context.getResources().getString(R.string.actions_sendMessage),
                            FuncBaseView.FuncType.OVERLAY,
                            R.drawable.ic_send_black_24dp,
                            1f,
                            getContext(),
                            StreamActivity.class.getMethod("onSendMessage", Identity.class),
                            identity));

                    break;
            }

        } catch (NoSuchMethodException e) {

            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>",
                    e.getMessage()));
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

        try {

            switch (actionDomain) {
                case "com.aura.aosp.gorilla.launcher":

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
                            new ActionCluster(actionDomain + ".cluster.COMPOSE_MESSAGE", getMessengerActionItems(context))
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
                            StreamActivity.class.getMethod("onReturnToStream")
                    ));


                    break;

                case "com.aura.aosp.gorilla.stream.contacts":

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

                    break;
            }
        } catch (NoSuchMethodException e) {
            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>",
                    e.getMessage()));
        }

        return new ActionCluster(actionDomain, items);
    }


    /**
     * Sample data for actoin button cluster
     * TODO: To be replaced with data provider(s)
     *
     * @param context
     * @return
     */
    public static final List<ActionItem> getMessengerActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_pickDate),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_date_range_black_24dp,
                    0.80f, StreamActivity.class.getMethod("onPickDate")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    0.69f, "com.aura.aosp.gorilla.launcher.action.CREATE_NOTE"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    0.99f, "com.aura.aosp.gorilla.launcher.action.CREATE_NOTE_SHARED_WITH"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_person_black_24dp,
                    0.75f, "com.aura.aosp.gorilla.launcher.action.LOOKUP_CONTENT"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_editText),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_mode_edit_black_24dp,
                    0.80f, new ActionCluster("AC-EDIT-TEXT", getEditorActionItems(context))
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_share),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_share_black_24dp,
                    0.98f, "com.aura.aosp.gorilla.launcher.action.SHARE"
            ));
        } catch (NoSuchMethodException $e) {
            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>, Activiy is <%>",
                    $e.getMessage(), StreamActivity.class));
        }

        return items;
    }

    /**
     * Sample data for actoin button cluster
     * TODO: To be replaced with data provider(s)
     *
     * @param context
     * @return
     */
    public static final List<ActionItem> getEditorActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextBold),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_bold_black_24dp,
                    1f, StreamActivity.class.getMethod("onMarkSelectedTextBold")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextItalic),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_italic_black_24dp,
                    1f, StreamActivity.class.getMethod("onMarkSelectedTextItalic")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextUnderlined),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_underlined_black_24dp,
                    1f, StreamActivity.class.getMethod("onMarkSelectedTextUnderlined")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextAlignJustify),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_align_justify_black_24dp,
                    1f, StreamActivity.class.getMethod("onMarkSelectedTextAlignJustify")
            ));
        } catch (NoSuchMethodException $e) {
            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>, Activiy is <%>",
                    $e.getMessage(), StreamActivity.class));
        }

        return items;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
