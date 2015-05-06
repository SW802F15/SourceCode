package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI;

import android.widget.BaseAdapter;
import android.widget.Toast;

import dk.aau.sw802f15.tempoplayer.MusicPlayer.DynamicQueue;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import dk.aau.sw802f15.tempoplayer.R;

/**
 * Created by Draegert on 06-05-2015.
 */
public class CoverFlowManager {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static MusicPlayerActivity _activity;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public CoverFlowManager(MusicPlayerActivity activity) {
        _activity = activity;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void updateAlbumCovers(int currentIndex, CoverFlow coverFlow) {
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        DynamicQueue dynamicQueue = DynamicQueue.getInstance(_activity);

        //Updates coverflow to use new album covers.
        ((ResourceImageAdapter) coverImageAdapter).setResources(dynamicQueue.getDynamicQueueAsList());
        coverFlow.setAdapter(coverImageAdapter);

        //Sets the middle cover to current song.
        if (dynamicQueue.getPrevSongsSizeBeforeAdd() == dynamicQueue.getPrevSize()) {
            currentIndex--;
        }
        coverFlow.setSelection(currentIndex);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected void nextAlbumCover(CoverFlow coverFlow) {
        int nextPosition = coverFlow.getSelectedItemPosition();

        if (nextPosition < coverFlow.getCount()) {
            nextPosition = coverFlow.getSelectedItemPosition() + 1;
        }
        else {
            GUIManager.getInstance(_activity).showToast("No next song.");
        }
        updateAlbumCovers(nextPosition, coverFlow);
    }

    protected void previousAlbumCover(CoverFlow coverFlow) {
        int previousPosition = coverFlow.getSelectedItemPosition() - 1;

        if (coverFlow.getItemAtPosition(previousPosition) != null) {
            coverFlow.setSelection(previousPosition);
        }
        else {
            GUIManager.getInstance(_activity).showToast("No previous song.");
        }
    }

    protected void setCoverFlowImages(CoverFlow coverFlow) {
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(_activity);
        ((ResourceImageAdapter) coverImageAdapter).setResources(DynamicQueue.getInstance(_activity).getDynamicQueueAsList());
        coverFlow.setAdapter(coverImageAdapter);
    }
    //endregion


















}
