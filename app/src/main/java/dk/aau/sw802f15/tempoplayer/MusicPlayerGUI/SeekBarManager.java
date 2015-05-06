package dk.aau.sw802f15.tempoplayer.MusicPlayerGUI;

import android.os.Handler;

import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.R;

/**
 * Created by Draegert on 06-05-2015.
 */
public class SeekBarManager {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static MusicPlayerActivity _activity;
    private static GUIManager _guiManager;
    private final static int POLL_RATE = 100;
    private Handler durationHandler = new Handler();
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public SeekBarManager(MusicPlayerActivity activity) {
        _activity = activity;
        _guiManager = new GUIManager(_activity);
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            if(_activity.mMusicPlayerService == null ||
                    _activity.mMusicPlayerService.musicPlayer == null){
                return;
            }
            int timeElapsed = _activity.mMusicPlayerService.musicPlayer.getCurrentPosition() / 1000;
            android.widget.SeekBar sb = (android.widget.SeekBar) _activity.findViewById(R.id.seekBar);
            sb.setProgress(timeElapsed);
            _guiManager.setSongProgressText(timeElapsed);
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
        if (_activity.mMusicPlayerService.musicPlayer.isPlaying() ||
            _activity.mMusicPlayerService.isPaused) {
            _activity.mMusicPlayerService.musicPlayer.seekTo(0);
        }
    }
    //endregion



}
