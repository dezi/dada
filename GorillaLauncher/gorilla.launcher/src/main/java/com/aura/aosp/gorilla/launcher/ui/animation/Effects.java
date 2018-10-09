package com.aura.aosp.gorilla.launcher.ui.animation;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.ui.animation.easing.Easing;
import com.aura.aosp.gorilla.launcher.ui.animation.easing.EasingInterpolator;
import com.aura.aosp.gorilla.launcher.ui.navigation.ActionClusterView;

/**
 * Effects collection
 */
public class Effects {

    /**
     * Standard fade in transition for views
     *
     * @param view
     * @param context
     * @param duration
     */
    public static void fadeInView(View view, Context context, @Nullable Integer duration) {

        if (duration == null) {
            duration = context.getResources().getInteger(R.integer.effects_default_view_fadein_transition_duration);
        }

        view.setAlpha(0f);
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .setDuration(duration)
                .start();
    }

    /**
     * Standard fade out transition for views
     *
     * @param view
     * @param context
     * @param duration
     */
    public static void fadeOutView(final View view, Context context, @Nullable Integer duration) {

        if (duration == null) {
            duration = context.getResources().getInteger(R.integer.effects_default_view_fadeout_transition_duration);
        }

        view.animate()
                .alpha(0.f)
                .scaleX(0.f).scaleY(0.f)
                .setDuration(duration)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    /**
     * Bounce animation for introducing/demoing action cluster usage
     *
     * @param targetView
     */
    public static void doClusterDemoAnimation(View targetView) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(targetView, "translationX", 450, 150, 0);
        animator.setInterpolator(new EasingInterpolator(Easing.BACK_IN));
        animator.setStartDelay(40);
        animator.setDuration(1500);
        animator.start();
    }

    /**
     * Testing...
     */
    public static void doTestAnimation(ViewGroup viewGroup) {

        final float INITIAL_ALPHA = 0.8f;

        // Animation set for action cluster view
        AnimationSet abcAnimationSet = new AnimationSet(true);

        Animation fabAlphaAnimation = new AlphaAnimation(0.2f, INITIAL_ALPHA);
        fabAlphaAnimation.setDuration(1200);

//        final Interpolator abcInterpolator = PathInterpolatorCompat.create(0.550f, 0.055f, 0.675f, 0.19f);
//        Interpolator bi = new BounceInterpolator();
//        EasingInterpolator ioInterpolator = new EasingInterpolator(Easing.BOUNCE_IN_OUT);

        ScaleAnimation abcScaleAnimationGrow = new ScaleAnimation(0.5f, 1.2f, 0.5f, 1.2f);
        abcScaleAnimationGrow.setInterpolator(new BounceInterpolator());
        abcScaleAnimationGrow.setDuration(600);

        ScaleAnimation abcScaleAnimationShrink = new ScaleAnimation(1.2f, 1f, 1.2f, 1f);
        abcScaleAnimationShrink.setInterpolator(new BounceInterpolator());
        abcScaleAnimationShrink.setDuration(600);

        abcAnimationSet.addAnimation(fabAlphaAnimation);
        abcAnimationSet.addAnimation(abcScaleAnimationGrow);
        abcAnimationSet.addAnimation(abcScaleAnimationShrink);

        viewGroup.setAnimation(abcAnimationSet);
        abcAnimationSet.start();
    }
}
