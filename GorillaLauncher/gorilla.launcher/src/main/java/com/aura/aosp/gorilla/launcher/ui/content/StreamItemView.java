package com.aura.aosp.gorilla.launcher.ui.content;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aura.aosp.gorilla.launcher.R;
import com.aura.aosp.gorilla.launcher.model.stream.ContactStreamItem;

/**
 * View representing an item of a stream.
 */
public class StreamItemView extends ConstraintLayout {

    private static final String LOGTAG = StreamItemView.class.getSimpleName();

    public View item;
    public RelativeLayout previewTextContainer;
    public TextView previewTitle;
    public TextView previewText;
    public RelativeLayout previewImageContainer;
    public ImageView previewImage;

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
        previewTextContainer = findViewById(R.id.previewTextContainer);
        previewTitle = findViewById(R.id.previewTitle);
        previewText = findViewById(R.id.previewText);
        previewImageContainer = findViewById(R.id.previewImageContainer);
        previewImage = findViewById(R.id.previewImage);
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
