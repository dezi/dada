package com.aura.aosp.gorilla.launcher.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;

import com.aura.aosp.gorilla.launcher.R;

public class ImageShaper {

    private final static String LOGTAG = ImageShaper.class.getSimpleName();

    /**
     *
     *
     * @param context
     * @param originalImage
     * @param shapedImage
     * @param imgView
     * @param expectedHeight
     * @param expectedWidth
     */
    public static void shape(Context context, int originalImage, int shapedImage, ImageView imgView, int expectedHeight, int expectedWidth) {

        Bitmap oImageBitmap = BitmapFactory.decodeResource(context.getResources(), originalImage);
        Bitmap sImageBitmap = BitmapFactory.decodeResource(context.getResources(), shapedImage);

        if (sImageBitmap == null) {
            android.util.Log.d(LOGTAG, "#### sImageBitmap == null !!!");
            sImageBitmap = getBitmap(context, shapedImage);
            sImageBitmap.getHeight();
        }

        Bitmap mask = sImageBitmap;

//        original = getResizedBitmap(original, expectedHeight, expectedWidth);
        oImageBitmap = getResizedBitmap(oImageBitmap, dipToPixels(context, expectedHeight), dipToPixels(context, expectedWidth));

        int bitmapHeight = sImageBitmap.getHeight();
        int bitmapWidth = sImageBitmap.getWidth();

        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas mCanvas = new Canvas(result);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        int widthMask = mask.getWidth();
        int heightMask = mask.getHeight();

        float centerX = (widthMask - oImageBitmap.getWidth()) * 0.5f;
        float centerY = (heightMask - oImageBitmap.getHeight()) * 0.5f;

        mCanvas.drawBitmap(oImageBitmap, centerX, centerY, null);
        mCanvas.drawBitmap(mask, 0, 0, paint);

        paint.setXfermode(null);

        imgView.getLayoutParams().height = bitmapHeight;
        imgView.getLayoutParams().width = bitmapWidth;
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setImageBitmap(result);
        imgView.setAdjustViewBounds(true);
    }

    /**
     * By using this method you can resize your image
     *
     * @param image     pass a bitmap image for resizing.
     * @param newHeight pass your expected new height in px (int value).
     * @param newWidth  pass your expected new width in px (int value).
     */
    public static Bitmap getResizedBitmap(Bitmap image, float newHeight, float newWidth) {
        int width = image.getWidth();
        int height = image.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);

        return resizedBitmap;
    }

    /**
     * Convert dip to pixel
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    /**
     * Get bitmap from drawable (shape)
     *
     * @param context
     * @param drawableRes
     * @return
     */
    public static Bitmap getBitmap(Context context, int drawableRes) {

        Drawable drawable = context.getResources().getDrawable(drawableRes, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();

        canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}