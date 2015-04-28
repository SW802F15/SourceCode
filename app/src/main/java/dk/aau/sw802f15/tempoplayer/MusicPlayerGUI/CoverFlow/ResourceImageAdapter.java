package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * This class is an adapter that provides images from a fixed set of resource
 * ids. Bitmaps and ImageViews are kept as weak references so that they can be
 * cleared by garbage collection when not needed.
 *
 */
public class ResourceImageAdapter extends AbstractCoverFlowImageAdapter {

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static final String TAG = ResourceImageAdapter.class.getSimpleName();
    private static final List<Bitmap> IMAGE_RESOURCE_IDS = new ArrayList<>();
    private static final List<Bitmap> DEFAULT_RESOURCE_LIST = new ArrayList<>();
    private final Map<Integer, WeakReference<Bitmap>> bitmapMap = new HashMap<Integer, WeakReference<Bitmap>>();

    private final Context context;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    public synchronized int getCount() {
        return IMAGE_RESOURCE_IDS.size();
    }

    public final synchronized void setResources(final List<Bitmap> resourceSongs) {
        IMAGE_RESOURCE_IDS.clear();
        for (final Bitmap albumCover : resourceSongs) {
            IMAGE_RESOURCE_IDS.add(albumCover);
        }
        notifyDataSetChanged();
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public ResourceImageAdapter(final Context context) {
        super();
        this.context = context;
        setResources(DEFAULT_RESOURCE_LIST);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    protected Bitmap createBitmap(final int position) {
        Log.v(TAG, "creating item " + position);
        Bitmap bitmap = IMAGE_RESOURCE_IDS.get(position);
        bitmapMap.put(position, new WeakReference<Bitmap>(bitmap));
        return bitmap;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    //endregion








}