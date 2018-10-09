package com.aura.aosp.gorilla.launcher.ui.navigation;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.AbstractActionItem;
import com.aura.aosp.gorilla.launcher.model.ActionCluster;
import com.aura.aosp.gorilla.launcher.model.ActionItem;

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

        switch (actionItem.getType()) {

            // Call to an internal or external action/function view
            case TYPE_ACTION_INTERN:
            default:

                if (actionItem.getType() == AbstractActionItem.ItemType.TYPE_ACTION_INTERN) {
                    useEnabledColor = R.color.color_clusterbutton_enabled_action_intern;
                    usePressedColor = R.color.color_clusterbutton_pressed_action_intern;
                    useIconColor = R.color.color_clusterbutton_icon_action_intern;
                } else {
                    useEnabledColor = R.color.color_clusterbutton_enabled_action_extern;
                    usePressedColor = R.color.color_clusterbutton_pressed_action_extern;
                    useIconColor = R.color.color_clusterbutton_icon_action_extern;
                }

                // Set on click (action) listener
                if (actionItem.getAction() != null) {
                    // Invoke intent based action
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(LOGTAG, String.format("Executing onClick for ACTION <%s>", actionItem.getAction()));
                            try {
                                Intent intent = new Intent(actionItem.getAction());
                                getContext().startActivity(intent);
                            } catch (ActivityNotFoundException e) {
                                Log.e(LOGTAG, String.format("Could not invoke ACTION <%s>", actionItem.getAction()));
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (actionItem.getIntent() != null) {
                    // Invoke action based on method of current activity
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(LOGTAG, String.format("Executing onClick for INTENT <%s>", actionItem.getIntent().toString()));
                            try {
                                getContext().startActivity(actionItem.getIntent());
                            } catch (ActivityNotFoundException e) {
                                Log.e(LOGTAG, String.format("Could not invoke INTENT <%s>", actionItem.getIntent().toString()));
                                e.printStackTrace();
                            }
                        }
                    });
                } else if (actionItem.getMethod() != null) {
                    // Invoke action based on method of current activity
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(LOGTAG, String.format("Executing onClick for METHOD <%s>", actionItem.getMethod().toString()));
                            try {
                                actionItem.getMethod().invoke(activity);
                            } catch (Exception e) {
                                Log.e(LOGTAG, String.format("Could not invoke METHOD <%s>", actionItem.getMethod().toString()));
                                e.printStackTrace();
                            }
                        }
                    });
                }

                break;

            // Call to a nested action cluster
            case TYPE_CLUSTER:

                useEnabledColor = R.color.color_clusterbutton_enabled_cluster;
                usePressedColor = R.color.color_clusterbutton_pressed_cluster;
                useIconColor = R.color.color_clusterbutton_icon_cluster;

                // Invoke action which attaches and shows another action cluster
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Method addActionClusterMethod = activity.getClass().getMethod("createActionClusterView",
                                    ActionCluster.class, ClusterButtonView.class, boolean.class);
                            addActionClusterMethod.invoke(activity, actionItem.getActionCluster(), view, true);
                        } catch (Exception e) {
                            Log.e(LOGTAG, String.format("Could not invoke CLUSTER action <%s>", actionItem.getName()));
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

//        if (actionItem.getAbsoluteScore() <= 0.9f) {
//            setScaleX(0.88f);
//            setScaleY(0.88f);
//        }
//
//        if (actionItem.getAbsoluteScore() <= 0.7f) {
//            setScaleX(0.75f);
//            setScaleY(0.75f);
//        }

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
