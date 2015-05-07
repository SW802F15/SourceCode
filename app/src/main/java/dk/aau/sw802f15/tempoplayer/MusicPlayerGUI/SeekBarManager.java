package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI;

import android.os.Handler;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;

/**
 * Created by Draegert on 06-05-2015.
 */
public class SeekBarManager {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static MusicPlayerActivity _activity;
    private final static int POLL_RATE = 100;
    private Handler durationHandler = new Handler();
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public SeekBarManager(MusicPlayerActivity activity) {
        _activity = activity;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if(_activity.getMusicPlayerService() == null) { //|| _activity.mMusicPlayerService.musicPlayer == null){
                return;
            }
            int timeElapsed = _activity.getMusicPlayerService().getCurrentPosition() / 1000;
            android.widget.SeekBar seekBar = GUIManager.getInstance(_activity).findSeekBar();
            seekBar.setProgress(timeElapsed);
            GUIManager.getInstance(_activity).setSongProgressText(timeElapsed);
            durationHandler.postDelayed(this, POLL_RATE);
        }
    };
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected void startSeekBarPoll()
    {
        durationHandler.postDelayed(updateSeekBarTime, POLL_RATE);
    }

    protected void stopSeekBarPoll(){
        durationHandler.removeCallbacks(updateSeekBarTime);
    }

    protected void resetSeekBar() {
        if (_activity.getMusicPlayerService().isPlaying() ||
            _activity.getMusicPlayerService().isPaused) {
            _activity.getMusicPlayerService().seekTo(0);
        }
    }
    //endregion
}