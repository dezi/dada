package com.aura.aosp.gorilla.launcher;

import android.content.Context;
import android.util.Log;

import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;
import com.aura.aosp.gorilla.launcher.model.TimelineItem;
import com.aura.aosp.gorilla.launcher.ui.content.FuncBaseView;

import java.util.ArrayList;
import java.util.List;

public class SampleData {

    private static final String LOGTAG = SampleData.class.getSimpleName();

    /**
     * Sample data for actoin button cluster
     * TODO: To be replaced with data provider(s)
     *
     * @param context
     * @return
     */
    public final static List<ActionItem> getLauncherInitialActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_openCalendar),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_date_range_black_24dp,
                    LauncherActivity.class.getMethod("onOpenSimpleCalendar"),
                    0.970f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    new ActionCluster("AC-COMPOSE-MESSAGE", getMessengerActionItems(context)),
                    0.92f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_startPhoneCall),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_call_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.START_PHONE_CALL",
                    0.95f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_addNote),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_add_circle_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.ADD_NOTE",
                    0.40f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.NEW_TEXT_SHARED_WITH",
                    0.99f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_account_circle_black_24dp,
                    context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp"),
                    0.95f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_person_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.LOOKUP_CONTENT",
                    0.75f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_viewMovie),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_local_movies_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.VIEW_MOVIE",
                    0.70f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_editText),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_mode_edit_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.EDIT_TEXT",
                    0.60f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_share),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_share_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.SHARE",
                    0.98f
            ));
        } catch (NoSuchMethodException e) {
            Log.e(LOGTAG, String.format("No such action invocation method found: <%s>, Activiy is <%>",
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
    public final static List<ActionItem> getLauncherActionItems(Context context) {
        List<ActionItem> items = new ArrayList<>();

        try {
            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_openCalendar),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_date_range_black_24dp,
                    LauncherActivity.class.getMethod("onOpenSimpleCalendar"),
                    0.970f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    new ActionCluster("AC-COMPOSE-MESSAGE", getMessengerActionItems(context)),
                    0.92f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_startPhoneCall),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_call_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.START_PHONE_CALL",
                    0.95f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_addNote),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_add_circle_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.ADD_NOTE",
                    0.40f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.NEW_TEXT_SHARED_WITH",
                    0.99f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_account_circle_black_24dp,
                    context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp"),
                    0.95f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_person_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.LOOKUP_CONTENT",
                    0.75f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_viewMovie),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_local_movies_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.VIEW_MOVIE",
                    0.70f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_editText),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_mode_edit_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.EDIT_TEXT",
                    0.60f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_share),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_share_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.SHARE",
                    0.98f
            ));
        } catch (NoSuchMethodException e) {
            Log.e(LOGTAG, String.format("No such action invocation method found: <%s>, Activiy is <%>",
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
                    LauncherActivity.class.getMethod("onPickDate"),
                    0.80f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_composeMessage),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_sms_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.COMPOSE_MESSAGE",
                    0.69f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_newtextsharedwith),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_contact_mail_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.NEW_TEXT_SHARED_WITH",
                    0.99f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_lookupContact),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_person_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.LOOKUP_CONTENT",
                    0.75f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_editText),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_mode_edit_black_24dp,
                    new ActionCluster("AC-EDIT-TEXT", getEditorActionItems(context)),
                    0.80f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_share),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_share_black_24dp,
                    "com.aura.aosp.gorilla.launcher.action.SHARE",
                    0.98f
            ));
        } catch (NoSuchMethodException $e) {
            Log.e(LOGTAG, String.format("No such action invocation method found: <%s>, Activiy is <%>",
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
                    LauncherActivity.class.getMethod("onMarkSelectedTextBold"),
                    1f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextItalic),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_italic_black_24dp,
                    LauncherActivity.class.getMethod("onMarkSelectedTextItalic"),
                    1f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextUnderlined),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_underlined_black_24dp,
                    LauncherActivity.class.getMethod("onMarkSelectedTextUnderlined"),
                    1f
            ));

            items.add(new ActionItem(
                    context.getResources().getString(R.string.actions_markSelectedTextAlignJustify),
                    FuncBaseView.FuncType.OVERLAY,
                    R.drawable.ic_format_align_justify_black_24dp,
                    LauncherActivity.class.getMethod("onMarkSelectedTextAlignJustify"),
                    1f
            ));
        } catch (NoSuchMethodException $e) {
            Log.e(LOGTAG, String.format("No such action invocation method found: <%s>, Activiy is <%>",
                    $e.getMessage(), LauncherActivity.class));
        }

        return items;
    }

    /**
     * Sample data for stream/timeline interface
     * TODO: To be replaced with data provider(s)
     *
     * @return
     */
    public final static List<TimelineItem> getStreamData() {
        List<TimelineItem> items = new ArrayList<>();

        items.add(new TimelineItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "",
                "",
                0));

        items.add(new TimelineItem(
                "highlight",
                "Abi",
                "I need some really holistic...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "Andreas",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "Here's your account data...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Caroline",
                "I need some really holistic...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Mr. Hoi",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nilie",
                "Here's your account data...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "How are you. Please send me some...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Ola",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Abi",
                "I need some really universal...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Matthias",
                "Was?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "Here's your license accoutn data...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "",
                "",
                0));

        items.add(new TimelineItem(
                "highlight",
                "Abi",
                "I need some really holistic...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "Andreas",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "Here's your account data...",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Malte",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Caroline",
                "I need some really holistic...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Mr. Hoi",
                "Hello everybody, greetings from Hanoi!",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "default",
                "Matthias",
                "Ich schreibe mir am liebsten selbst.",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nilie",
                "Here's your account data...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "How are you. Please send me some...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Ola",
                "Howdy, here are the files...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Abi",
                "I need some really universal...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "highlight",
                "dezi",
                "Wie wäre es mit einem Kaffee?",
                R.drawable.ic_blur_on_black_24dp));

        items.add(new TimelineItem(
                "highlight",
                "Matthias",
                "Was?",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Laurie",
                "Here's your license accoutn data...",
                R.drawable.timeline_oval_24dp));

        items.add(new TimelineItem(
                "default",
                "Nixie",
                "Hello, everything okay?",
                R.drawable.ic_blur_on_black_24dp));

        return items;
    }
}
