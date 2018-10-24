package com.aura.aosp.gorilla.launcher.store;

import android.content.Context;
import android.support.annotation.Nullable;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.StreamActivity;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItemInterface;
import com.aura.aosp.gorilla.launcher.model.actions.ClusterActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.IntentActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.IntentstringActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.InvokerActionItem;
import com.aura.aosp.gorilla.launcher.model.user.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Store for retrieving action clusters
 */
public class ActionClusterStore {

    private static final String LOGTAG = ActionClusterStore.class.getSimpleName();
    private static String baseDomain;

    private Context context;

    public ActionClusterStore(Context context) {
        setContext(context);
        setBaseDomain(getContext().getPackageName());
    }

    /**
     * Get action cluster for given action domain.
     * <p>
     * TODO: Add actions based on score for this action domain
     * <p>
     * TODO: Objects like contact identity are probably superflous as soon as we
     * TODO: an action scoring based on Gorilla functions where identity information
     * TODO: is passed through actionDomain/subContext...
     *
     * @param actionPath
     * @param contactUser
     * @return
     */
    public ActionCluster getClusterForAction(String actionPath, @Nullable User contactUser) {

        List<ActionItem> items = new ArrayList<>();

        try {

            switch (actionPath) {

                case "start":

                    items.add(new ClusterActionItem(
                            context.getResources().getString(R.string.actions_openCalendar),
                            R.drawable.ic_add_black_24dp,
                            1f,
                            getClusterForAction("launcher", contactUser)
                    ));

                    break;

                case "launcher":

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_openCalendar),
                            R.drawable.ic_add_a_photo_black_24dp,
                            0.870f,
                            StreamActivity.class.getMethod("onOpenSimpleCalendar")
                    ));

                    items.add(new IntentstringActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            R.drawable.ic_call_black_24dp,
                            0.95f,
                            getActionDomainForFragment(actionPath + ".START_PHONE_CALL")
                    ));

                    items.add(new ClusterActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            R.drawable.ic_sms_black_24dp,
                            0.92f,
                            getClusterForAction("func.content_composer", contactUser)
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            R.drawable.ic_message_black_24dp,
                            0.98f,
                            getContext(),
                            StreamActivity.class.getMethod("setCurrentAtomContext", String.class),
                            StreamStore.ATOMCONTEXT_UXSTREAM_MESSAGES
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_lookupContact),
                            R.drawable.ic_person_black_24dp,
                            0.98f,
                            getContext(),
                            StreamActivity.class.getMethod("setCurrentAtomContext", String.class),
                            StreamStore.ATOMCONTEXT_UXSTREAM_CONTACTS
                    ));

                    // Special action "Switch Profile". TODO: Remove for 0.1:
                    IntentActionItem switchProfileActionItem = new IntentActionItem(
                            context.getResources().getString(R.string.actions_switchProfile),
                            R.drawable.ic_account_circle_black_24dp,
                            0.97f,
                            context.getPackageManager().getLaunchIntentForPackage("com.aura.aosp.gorilla.sysapp")
                    );

                    switchProfileActionItem.setInvocationTarget(ActionItemInterface.invocationTarget.INVOCATION_TARGET_EXTERN);
                    items.add(switchProfileActionItem);

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_addAlarm),
                            R.drawable.ic_add_alarm_black_24dp,
                            0.88f,
                            StreamActivity.class.getMethod("onOpenAlarmClock")
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_openStream),
                            R.drawable.stream_oval_24dp,
                            0.93f,
                            StreamActivity.class.getMethod("onReturnToStream")
                    ));

                    break;

                case "stream.contacts":

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_composeMessage),
                            R.drawable.ic_message_black_24dp,
                            1f,
                            getContext(),
                            StreamActivity.class.getMethod("onOpenContentComposer", User.class),
                            contactUser
                    ));

                    items.add(new IntentstringActionItem(
                            context.getResources().getString(R.string.actions_startPhoneCall),
                            R.drawable.ic_call_black_24dp,
                            0.95f,
                            getActionDomainForFragment(actionPath + ".START_PHONE_CALL")
                    ));

                    items.add(new IntentstringActionItem(
                            context.getResources().getString(R.string.actions_addPerson),
                            R.drawable.ic_person_add_black_24dp,
                            0.95f,
                            getActionDomainForFragment(actionPath + ".ADD_PERSON")
                    ));

                    break;

                case "func.content_composer":

                    if (contactUser != null) {

                        items.add(new InvokerActionItem(
                                context.getResources().getString(R.string.actions_sendMessage),
                                R.drawable.ic_send_black_24dp,
                                1f,
                                getContext(),
                                StreamActivity.class.getMethod("onSendMessage", User.class),
                                contactUser
                        ));

                        break;

                    } else {

                        items.add(new InvokerActionItem(
                                context.getResources().getString(R.string.actions_pickDate),
                                R.drawable.ic_date_range_black_24dp,
                                0.80f,
                                StreamActivity.class.getMethod("onPickDate")
                        ));

                        items.add(new IntentstringActionItem(
                                context.getResources().getString(R.string.actions_composeMessage),
                                R.drawable.ic_sms_black_24dp,
                                0.69f,
                                getActionDomainForFragment("launcher.CREATE_NOTE")
                        ));

                        items.add(new IntentstringActionItem(
                                context.getResources().getString(R.string.actions_newtextsharedwith),
                                R.drawable.ic_contact_mail_black_24dp,
                                0.99f,
                                getActionDomainForFragment("launcher.CREATE_NOTE_SHARED_WITH")
                        ));

                        items.add(new IntentstringActionItem(
                                context.getResources().getString(R.string.actions_lookupContact),
                                R.drawable.ic_person_black_24dp,
                                0.75f,
                                getActionDomainForFragment("launcher.LOOKUP_CONTENT")
                        ));

                        items.add(new ClusterActionItem(
                                context.getResources().getString(R.string.actions_editText),
                                R.drawable.ic_mode_edit_black_24dp,
                                0.80f,
                                getClusterForAction(actionPath + ".SELECT_TEXT", contactUser)
                        ));

                        items.add(new IntentstringActionItem(
                                context.getResources().getString(R.string.actions_share),
                                R.drawable.ic_share_black_24dp,
                                0.98f,
                                getActionDomainForFragment("launcher.SHARE")
                        ));
                    }

                    break;

                case "func.content_composer.SELECT_TEXT":

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_markSelectedTextBold),
                            R.drawable.ic_format_bold_black_24dp,
                            1f,
                            StreamActivity.class.getMethod("onMarkSelectedTextBold")
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_markSelectedTextItalic),
                            R.drawable.ic_format_italic_black_24dp,
                            1f,
                            StreamActivity.class.getMethod("onMarkSelectedTextItalic")
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_markSelectedTextUnderlined),
                            R.drawable.ic_format_underlined_black_24dp,
                            1f,
                            StreamActivity.class.getMethod("onMarkSelectedTextUnderlined")
                    ));

                    items.add(new InvokerActionItem(
                            context.getResources().getString(R.string.actions_markSelectedTextAlignJustify),
                            R.drawable.ic_format_align_justify_black_24dp,
                            1f,
                            StreamActivity.class.getMethod("onMarkSelectedTextAlignJustify")
                    ));

                    break;
            }

        } catch (NoSuchMethodException e) {

            Log.e("No such action invocation invokeMethod found: <%s>", e.getMessage());
        }

        return new ActionCluster(actionPath, items);
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public static String getBaseDomain() {
        return baseDomain;
    }

    public static void setBaseDomain(String baseDomain) {
        ActionClusterStore.baseDomain = baseDomain;
    }

    public static String getActionDomainForFragment(String actionFragment) {
        return getBaseDomain() + "." + actionFragment;
    }
}
