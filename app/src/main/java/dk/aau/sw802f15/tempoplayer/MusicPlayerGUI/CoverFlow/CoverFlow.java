package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import dk.aau.sw802f15.tempoplayer.R;

/**
 * Cover Flow implementation.
 *
 */
public class CoverFlow extends Gallery {

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    //Graphics Camera used for transforming the matrix of ImageViews.
    private final Camera camera = new Camera();

    private int maxRotationAngle = 45;
    private int maxZoom = -120;
    private int coverflowCenter;
    private float imageHeight;
    private float imageWidth;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
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
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
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
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    private static int getCenterOfView(final View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected boolean getChildStaticTransformation(final View child, final Transformation t) {

        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;

        t.clear();
        t.setTransformationType(Transformation.TYPE_MATRIX);

        if (childCenter == coverflowCenter) {
            transformImageBitmap((ImageView) child, t, 0);
        } else if (childCenter + (childWidth / 2) >= coverflowCenter && childCenter - (childWidth / 2) <= coverflowCenter) {
            transformImageBitmap((ImageView) child, t, 0);
        } else {
            rotationAngle = (int) ((float) (coverflowCenter - childCenter) / childWidth * maxRotationAngle);
            rotationAngle = rotationAngle < 0 ? -maxRotationAngle : maxRotationAngle;
            /*if (Math.abs(rotationAngle) > maxRotationAngle) {
                rotationAngle = rotationAngle < 0 ? -maxRotationAngle : maxRotationAngle;
            }*/

            transformImageBitmap((ImageView) child, t, rotationAngle);
        }


        return true;
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        coverflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

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
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
    //endregion
}
