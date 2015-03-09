package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * This class is an adapter that provides base, abstract class for images
 * adapter.
 *
 */
public abstract class AbstractCoverFlowImageAdapter extends BaseAdapter {

    private static final String TAG = AbstractCoverFlowImageAdapter.class.getSimpleName();
    private float width = 0;
    private float height = 0;
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    public AbstractCoverFlowImageAdapter() {
        super();
    }

    public synchronized void setWidth(final float width) {
        this.width = width;
    }

    public synchronized void setHeight(final float height) {
        this.height = height;
    }

    protected abstract Bitmap createBitmap(int position);

    @Override
    public final synchronized long getItemId(final int position) {
        return position;
    }

    @Override
    public final Bitmap getItem(final int position) {
        final WeakReference<Bitmap> weakBitmapReference = bitmapMap.get(position);
        if (weakBitmapReference != null) {
            final Bitmap bitmap = weakBitmapReference.get();
            if (bitmap == null) {
                Log.v(TAG, "Empty bitmap reference at position: " + position + ":" + this);
            } else {
                Log.v(TAG, "Reusing bitmap item at position: " + position + ":" + this);
                return bitmap;
            }
        }
        Log.v(TAG, "Creating item at position: " + position + ":" + this);
        final Bitmap bitmap = createBitmap(position);
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        Log.v(TAG, "Created item at position: " + position + ":" + this);
        return bitmap;
    }

    @Override
    public final synchronized ImageView getView(final int position, final View convertView, final ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            final Context context = parent.getContext();
            Log.v(TAG, "Creating Image view at position: " + position + ":" + this);
            imageView = new ImageView(context);
            imageView.setLayoutParams(new CoverFlow.LayoutParams((int) width, (int) height));
        } else {
            Log.v(TAG, "Reusing view at position: " + position + ":" + this);
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(getItem(position));
        return imageView;
    }
}
