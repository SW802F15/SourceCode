package com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;
import com.example.sw802f15.tempoplayer.R;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 *
 */
public class ResourceImageAdapter extends AbstractCoverFlowImageAdapter {

    private static final String TAG = ResourceImageAdapter.class.getSimpleName();
    private static final List<WeakReference<Bitmap>> IMAGE_RESOURCE_IDS = new ArrayList<>();
    private static final List<WeakReference<Bitmap>> DEFAULT_RESOURCE_LIST = new ArrayList<>();
    public final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    private final Context context;

    public ResourceImageAdapter(final Context context) {
        super();
        this.context = context;
        setResources(DEFAULT_RESOURCE_LIST);
    }

    @Override
    public synchronized int getCount() {
        return IMAGE_RESOURCE_IDS.size();
    }

    public final synchronized void setResources(final List<WeakReference<Bitmap>> resourceSongs) {
        IMAGE_RESOURCE_IDS.clear();
        for (final WeakReference<Bitmap> albumCover : resourceSongs) {
            IMAGE_RESOURCE_IDS.add(albumCover);
        }
        notifyDataSetChanged();
    }

    @Override
    protected Bitmap createBitmap(final int position) {
        Log.v(TAG, "creating item " + position);
        WeakReference<Bitmap> bitmap =  IMAGE_RESOURCE_IDS.get(position);
        bitmapMap.put(position, bitmap);
        return bitmap.get();
    }
}