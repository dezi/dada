package com.aura.aosp.gorilla.launcher;

import android.content.Context;
import android.util.Log;

import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;
import com.aura.aosp.gorilla.launcher.ui.common.FuncBaseView;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    private static final String LOGTAG = SampleData.class.getSimpleName();

    /**
     * Sample data for action cluster
     * @param context
     * @return
     */
    public final static ActionCluster getLauncherActionCluster(Context context) {
        return new ActionCluster(context.getPackageName(), getLauncherActionItems(context));
    }

    /**
     * Sample data for action button items
     * TODO: To be replaced with data provider(s)
     *
     * @param context
     * @return
     */
    public final static List<ActionItem> getLauncherActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_openCalendar),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_date_range_black_24dp,
                    0.970f, LauncherActivity.class.getMethod("onOpenSimpleCalendar")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    0.92f, new ActionCluster("AC-COMPOSE-MESSAGE", getMessengerActionItems(context))
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_startPhoneCall),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_call_black_24dp,
                    0.95f,
                    "com.aura.aosp.gorilla.launcher.action.START_PHONE_CALL"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_addNote),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_add_circle_black_24dp,
                    0.40f, "com.aura.aosp.gorilla.launcher.action.ADD_NOTE"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    0.99f, "com.aura.aosp.gorilla.launcher.action.NEW_TEXT_SHARED_WITH"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_account_circle_black_24dp,
                    0.95f, context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_person_black_24dp,
                    0.75f, "com.aura.aosp.gorilla.launcher.action.LOOKUP_CONTENT"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_viewMovie),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_local_movies_black_24dp,
                    0.70f, "com.aura.aosp.gorilla.launcher.action.VIEW_MOVIE"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_editText),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_mode_edit_black_24dp,
                    0.60f, "com.aura.aosp.gorilla.launcher.action.EDIT_TEXT"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_share),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_share_black_24dp,
                    0.98f, "com.aura.aosp.gorilla.launcher.action.SHARE"
            ));
        } catch (NoSuchMethodException e) {
            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>, Activiy is <%>",
                    e.getMessage(), LauncherActivity.class));
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
    public final static List<ActionItem> getMessengerActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_pickDate),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_date_range_black_24dp,
                    0.80f, LauncherActivity.class.getMethod("onPickDate")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    0.69f, "com.aura.aosp.gorilla.launcher.action.NEW_NOTE"
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    0.99f, "com.aura.aosp.gorilla.launcher.action.NEW_NOTE_SHARED_WITH"
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
                    $e.getMessage(), LauncherActivity.class));
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
    public final static List<ActionItem> getEditorActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextBold),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_bold_black_24dp,
                    1f, LauncherActivity.class.getMethod("onMarkSelectedTextBold")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextItalic),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_italic_black_24dp,
                    1f, LauncherActivity.class.getMethod("onMarkSelectedTextItalic")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextUnderlined),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_underlined_black_24dp,
                    1f, LauncherActivity.class.getMethod("onMarkSelectedTextUnderlined")
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextAlignJustify),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_align_justify_black_24dp,
                    1f, LauncherActivity.class.getMethod("onMarkSelectedTextAlignJustify")
            ));
        } catch (NoSuchMethodException $e) {
            Log.e(LOGTAG, String.format("No such action invocation invokeMethod found: <%s>, Activiy is <%>",
                    $e.getMessage(), LauncherActivity.class));
        }

        return items;
    }
}
