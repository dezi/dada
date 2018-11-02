package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;

/**
 * View representing an item of a stream.
 */
public class StreamItemView extends ConstraintLayout {

    private static final String LOGTAG = StreamItemView.class.getSimpleName();

    public View item;
    public LinearLayout textContainer;
    public ImageView icon;
    public TextView text;
    public LinearLayout imageContainer;
    public ImageView image;

    public StreamItemView(Context context) {
        super(context);
        init();
    }

    public StreamItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StreamItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        item = findViewById(R.id.item);
        textContainer = findViewById(R.id.textContainer);
        icon = findViewById(R.id.icon);
        text = findViewById(R.id.text);
        imageContainer = findViewById(R.id.imageContainer);
        image = findViewById(R.id.image);
    }

    /**
     * Initialize content stream item from contactStreamItem model.
     */
    public void initWithItem(final ContactStreamItem contactStreamItem) {
        // TODO: Implement (extract from StreamAdapter)
    }

    public void setFadeAnimation() {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500);
        startAnimation(anim);
    }

    public void setResizeAnimation() {
        this.setScaleX(1.2f);
        this.setScaleY(1.2f);
    }
}
