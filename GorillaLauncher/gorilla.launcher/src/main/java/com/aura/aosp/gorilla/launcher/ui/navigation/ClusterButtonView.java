package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.view.View;

import com.aura.aosp.aura.common.simple.Log;
import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.actions.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.actions.ActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.ClusterActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.IntentActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.IntentstringActionItem;
import com.aura.aosp.gorilla.launcher.model.actions.InvokerActionItem;

import java.lang.reflect.Method;

public class ClusterButtonView extends FloatingActionButton {

    private static final String LOGTAG = ClusterButtonView.class.getSimpleName();
    private String label;

    public ClusterButtonView(Context context) {
        super(context);
        init();
    }

    public ClusterButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClusterButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
//        this.header = (TextView)findViewById(R.id.header);
    }

    /**
     * Initialize action cluster button from actionItem model.
     *
     * @param actionItem
     * @param activity
     */
    public void initWithAction(final ActionItem actionItem, final Activity activity) {
//        this.header = (TextView)findViewById(R.id.header);

        // Create action cluster button
        int useEnabledColor;
        int usePressedColor;
        int useIconColor;

        setLabel(actionItem.getName());

        switch (actionItem.getInvocationTarget()) {

            default:

                useEnabledColor = R.color.color_clusterbutton_enabled_generic;
                usePressedColor = R.color.color_clusterbutton_pressed_generic;
                useIconColor = R.color.color_clusterbutton_icon_generic;

                break;

            case INVOCATION_TARGET_INTERN_VIEW:

                useEnabledColor = R.color.color_clusterbutton_enabled_action_intern;
                usePressedColor = R.color.color_clusterbutton_pressed_action_intern;
                useIconColor = R.color.color_clusterbutton_icon_action_intern;

                break;

            case INVOCATION_TARGET_EXTERN:

                useEnabledColor = R.color.color_clusterbutton_enabled_action_extern;
                usePressedColor = R.color.color_clusterbutton_pressed_action_extern;
                useIconColor = R.color.color_clusterbutton_icon_action_extern;

                break;

            case INVOCATION_TARGET_INTERN_CLUSTER:

                useEnabledColor = R.color.color_clusterbutton_enabled_cluster;
                usePressedColor = R.color.color_clusterbutton_pressed_cluster;
                useIconColor = R.color.color_clusterbutton_icon_cluster;

                break;
        }

        switch (actionItem.getInvocationType()) {

            // A "dead end" button (-> icon) just representing information itself
            case INVOCATION_TYPE_UNKNOWN:
            case INVOCATION_TYPE_DISABLED:
            default:

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("Executing onClick for INVOCATION_TYPE_DISABLED");
                    }
                });

                break;

            // Call to an internal or activity via intent string
            case INVOCATION_TYPE_INTENTSTRING:

                final IntentstringActionItem intentstringActionItem = (IntentstringActionItem) actionItem;

                // Set on click (action) listener
                if (intentstringActionItem.getAction() != null) {
                    // Invoke intent based action
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Executing onClick for action <%s>", intentstringActionItem.getAction());
                            try {
                                Intent intent = new Intent(intentstringActionItem.getAction());
                                getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Log.e("Could not invoke INVOCATION_TYPE_INTENTSTRING <%s>", intentstringActionItem.getAction());
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;

            // Call to an internal or activity via intent string
            case INVOCATION_TYPE_INTENT:

                final IntentActionItem intentActionItem = (IntentActionItem) actionItem;

                if (intentActionItem.getIntent() != null) {
                    // Invoke action based on invokeMethod of current activity
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("Executing onClick for intent <%s>", intentActionItem.getIntent().toString());
                            try {
                                getContext().startActivity(intentActionItem.getIntent());
                            } catch (ActivityNotFoundException e) {
                                Log.e("Could not invoke INVOCATION_TYPE_INTENT <%s>", intentActionItem.getIntent().toString());
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;

            // Call to an invoker action
            case INVOCATION_TYPE_INVOKER:

                final InvokerActionItem invokerActionItem = (InvokerActionItem) actionItem;

                if (invokerActionItem.getInvokeMethod() != null) {
                    if (invokerActionItem.getInvokeObject() == null) {
                        // Invoke action based on current context and without any arguments
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("Executing onClick for object <%s> and method <%s>",
                                        getContext().toString(),
                                        invokerActionItem.getInvokeMethod().toString());
                                try {
                                    invokerActionItem.getInvokeMethod().invoke(getContext());
                                } catch (Exception e) {
                                    Log.e("Could not invoke INVOCATION_TYPE_INVOKER for object <%s> and method <%s>",
                                            getContext().toString(),
                                            invokerActionItem.getInvokeMethod().toString());
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else {
                        // Invoke action based on given object, method and arguments
                        setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Log.d("Executing onClick for object <%s>, method <%s> and args <%s>",
                                        invokerActionItem.getInvokeObject().toString(),
                                        invokerActionItem.getInvokeMethod().toString(),
                                        invokerActionItem.getInvokePayload() != null ? invokerActionItem.getInvokePayload().toString() : "(null)");
                                try {
                                    invokerActionItem.getInvokeMethod().invoke(invokerActionItem.getInvokeObject(), invokerActionItem.getInvokePayload());
                                } catch (Exception e) {
                                    Log.e("Could not invoke INVOCATION_TYPE_INVOKER for object <%s>, method <%s> and args <%s>",
                                            invokerActionItem.getInvokeObject().toString(),
                                            invokerActionItem.getInvokeMethod().toString(),
                                            invokerActionItem.getInvokePayload() != null ? invokerActionItem.getInvokePayload().toString() : "(null)");
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }

                break;

            // Call to a nested action cluster
            case INVOCATION_TYPE_CLUSTER:

                final ClusterActionItem clusterActionItem = (ClusterActionItem) actionItem;

                // Invoke action which attaches and shows another action cluster
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Method addActionClusterMethod = activity.getClass().getMethod("createActionClusterView",
                                    ActionCluster.class, ClusterButtonView.class, boolean.class);
                            addActionClusterMethod.invoke(activity, clusterActionItem.getActionCluster(), view, true);
                        } catch (Exception e) {
                            Log.e("Could not invoke CLUSTER action <%s>", actionItem.getName());
                            e.printStackTrace();
                        }
                    }
                });

                break;
        }

        // Adjust button display size depending on current relevance score
        setSize(FloatingActionButton.SIZE_NORMAL);

        setScaleX(actionItem.getAbsoluteScore());
        setScaleY(actionItem.getAbsoluteScore());

        // Set drawable (icon) color
        Drawable iDrawable = getDrawable();
        DrawableCompat.setTint(iDrawable, ContextCompat.getColor(getContext(), useIconColor));

        // Programmatically define background colors for states because XML definition leads to obscure error with
        // "Invalid drawable added to LayerDrawable! Drawable already belongs to another owner but does not expose a constant state"
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };
        int[] colors = new int[]{
                getContext().getColor(useEnabledColor),
                getContext().getColor(usePressedColor)
        };

        ColorStateList fabColorList = new ColorStateList(states, colors);
        setBackgroundTintList(fabColorList);

        // Set action icon
        setImageDrawable(getContext().getResources().getDrawable(actionItem.getImageId(), getContext().getTheme()));
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
