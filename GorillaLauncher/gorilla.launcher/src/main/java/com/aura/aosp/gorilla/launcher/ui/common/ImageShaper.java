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
import android.support.annotation.Nullable;
import android.widget.ImageView;

public class ImageShaper {

    private final static String LOGTAG = ImageShaper.class.getSimpleName();

    /**
     * Apply a shape to an image and place result in given image view.
     * TODO: Refactor to let shape return bitmap and add function which applies bitmap to image view
     * TODO: (Adjust e.g. StreamAdapter to reuse bitmaps of already computes bitmaps -> performance!)
     *
     * @param context
     * @param originalImage
     * @param shapedImage
     * @param imgView
     * @param expectedHeight
     * @param expectedWidth
     */
    public static void shape(Context context, int originalImage, int shapedImage, ImageView imgView, float expectedHeight, float expectedWidth) {

        Bitmap shapedBitmap = getShapedBitmap(context, originalImage, shapedImage, expectedHeight, expectedWidth);
        placeBitmapInView(shapedBitmap, imgView, null, null);
    }

    /**
     * Get a shape as bitmap using a shape image on a target image.
     *
     * @param context
     * @param originalImage
     * @param shapedImage
     * @param expectedHeight
     * @param expectedWidth
     */
    public static Bitmap getShapedBitmap(Context context, int originalImage, int shapedImage, float expectedHeight, float expectedWidth) {

        Bitmap oImageBitmap = BitmapFactory.decodeResource(context.getResources(), originalImage);
        Bitmap sImageBitmap = BitmapFactory.decodeResource(context.getResources(), shapedImage);

        if (oImageBitmap == null) {
            oImageBitmap = getBitmap(context, originalImage);
        }

        if (sImageBitmap == null) {
            sImageBitmap = getBitmap(context, shapedImage);
        }

        Bitmap maskBitmap = sImageBitmap;

        oImageBitmap = getResizedBitmap(oImageBitmap, expectedHeight, expectedWidth);

        Bitmap resultBitmap = Bitmap.createBitmap(maskBitmap.getWidth(), maskBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        final Canvas maskCanvas = new Canvas(resultBitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        int maskWidth = maskBitmap.getWidth();
        int maskHeight = maskBitmap.getHeight();

        float centerX = (maskWidth - oImageBitmap.getWidth()) * 0.5f;
        float centerY = (maskHeight - oImageBitmap.getHeight()) * 0.5f;

        maskCanvas.drawBitmap(oImageBitmap, centerX, centerY, null);
        maskCanvas.drawBitmap(maskBitmap, 0, 0, paint);

        paint.setXfermode(null);

        return resultBitmap;
    }

    /**
     * Place bitmap in image view
     *
     * @param bitmap
     * @param imgView
     * @param width
     * @param height
     */
    public static void placeBitmapInView(Bitmap bitmap, ImageView imgView, @Nullable Integer width, @Nullable Integer height) {

        if (width == null) {
            width = bitmap.getWidth();
        }

        if (height == null) {
            height = bitmap.getHeight();
        }

        imgView.getLayoutParams().width = width;
        imgView.getLayoutParams().height = height;
        imgView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imgView.setImageBitmap(bitmap);
        imgView.setAdjustViewBounds(true);
    }

    /**
     * Resize bitmaps.
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