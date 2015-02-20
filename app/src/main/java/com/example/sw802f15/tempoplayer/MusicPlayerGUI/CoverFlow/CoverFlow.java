package com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.example.sw802f15.tempoplayer.R;

/**
 * Cover Flow implementation.
 *
 */
public class CoverFlow extends Gallery {

    /**
     * Graphics Camera used for transforming the matrix of ImageViews.
     */
    private final Camera camera = new Camera();
    private int maxRotationAngle = 60;
    private int maxZoom = -120;
    private int coverflowCenter;
    private float imageHeight;
    private float imageWidth;

    public float getImageHeight() {
        return imageHeight;
    }
    public void setImageHeight(final float imageHeight) {
        this.imageHeight = imageHeight;
    }
    public float getImageWidth() {
        return imageWidth;
    }
    public void setImageWidth(final float imageWidth) {
        this.imageWidth = imageWidth;
    }
    public int getMaxRotationAngle() {
        return maxRotationAngle;
    }
    public void setMaxRotationAngle(final int maxRotationAngle) {
        this.maxRotationAngle = maxRotationAngle;
    }
    public int getMaxZoom() {
        return maxZoom;
    }
    public void setMaxZoom(final int maxZoom) {
        this.maxZoom = maxZoom;
    }

    public CoverFlow(final Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
    }

    public CoverFlow(final Context context, final AttributeSet attrs) {
        this(context, attrs, android.R.attr.galleryStyle);
    }

    public CoverFlow(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        parseAttributes(context, attrs);
        this.setStaticTransformationsEnabled(true);
    }

    /**
     * Sets the.
     *
     * @param adapter
     *            the new adapter
     */
    @Override
    public void setAdapter(final SpinnerAdapter adapter) {
        if (!(adapter instanceof AbstractCoverFlowImageAdapter)) {
            throw new IllegalArgumentException("The adapter should derive from "
                    + AbstractCoverFlowImageAdapter.class.getName());
        }
        final AbstractCoverFlowImageAdapter coverAdapter = (AbstractCoverFlowImageAdapter) adapter;
        coverAdapter.setWidth(imageWidth);
        coverAdapter.setHeight(imageHeight);
        super.setAdapter(adapter);
    }

    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(final View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    /**
     * {@inheritDoc}
     *
     * @see #setStaticTransformationsEnabled(boolean)
     */
    @Override
    protected boolean getChildStaticTransformation(final View child, final Transformation t) {

        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        if (childCenter == coverflowCenter) {
            transformImageBitmap((ImageView) child, t, 0);
        } else {
            rotationAngle = (int) ((float) (coverflowCenter - childCenter) / childWidth * maxRotationAngle);
            if (Math.abs(rotationAngle) > maxRotationAngle) {
                rotationAngle = rotationAngle < 0 ? -maxRotationAngle : maxRotationAngle;
            }

            transformImageBitmap((ImageView) child, t, rotationAngle);
        }

        return true;
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w
     *            Current width of this view.
     * @param h
     *            Current height of this view.
     * @param oldw
     *            Old width of this view.
     * @param oldh
     *            Old height of this view.
     */
    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        coverflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * Transform the Image Bitmap by the Angle passed.
     *
     * @param imageView
     *            ImageView the ImageView whose bitmap we want to rotate
     * @param t
     *            transformation
     * @param rotationAngle
     *            the Angle by which to rotate the Bitmap
     */
    private void transformImageBitmap(final ImageView child, final Transformation t, final int rotationAngle) {
        camera.save();
        final Matrix imageMatrix = t.getMatrix();

        final int height = child.getLayoutParams().height;

        final int width = child.getLayoutParams().width;
        final int rotation = Math.abs(rotationAngle);

        camera.translate(0.0f, 0.0f, 100.0f);

        // As the angle of the view gets less, zoom in
        if (rotation < maxRotationAngle) {
            final float zoomAmount = (float) (maxZoom + rotation * 1.5);
            camera.translate(0.0f, 0.0f, zoomAmount);
        }

        camera.rotateY(rotationAngle);
        camera.getMatrix(imageMatrix);
        imageMatrix.preTranslate(-(width / 2.0f), -(height / 2.0f));
        imageMatrix.postTranslate((width / 2.0f), (height / 2.0f));
        camera.restore();
    }

    /**
     * Parses the attributes.
     *
     * @param context
     *            the context
     * @param attrs
     *            the attrs
     */
    private void parseAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CoverFlow);
        try {
            imageWidth = a.getDimension(R.styleable.CoverFlow_imageWidth, 480);
            imageHeight = a.getDimension(R.styleable.CoverFlow_imageHeight, 320);
            setSpacing(-15);
        } finally {
            a.recycle();
        }
    }

}