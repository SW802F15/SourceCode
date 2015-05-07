package dk.aau.sw802f15.tempoplayer.ControlInterface;

import android.os.CountDownTimer;
import android.widget.Toast;


import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;

public class TapCounter {
    private static final long MS_BETWEEN_TAPS = 500;
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private int taps;
    private CountDownTimer counter;

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void doTapAction(int taps) {
        MusicPlayerActivity activity = MusicPlayerActivity.getInstance();
        switch (taps){
            case 1:
                if (!activity.mMusicPlayerService.isPlaying()) {
                    GUIManager.getInstance(activity).findPlayButton().callOnClick();
                }
                else {
                    GUIManager.getInstance(activity).findPauseButton().callOnClick();
                }
                break;
            case 2:
                GUIManager.getInstance(activity).findNextButton().callOnClick();
                break;
            case 3:
                GUIManager.getInstance(activity).findPreviousButton().callOnClick();
                break;
            default:
                //do nothing
        }
        Toast.makeText(activity, "taps: " + taps, Toast.LENGTH_SHORT).show();
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public void clear() {
        taps = 0;
        counter.cancel();
    }


    public void increment() {
        taps++;

        if (counter != null) {
            counter.cancel();
        }

        counter = new CountDownTimer(MS_BETWEEN_TAPS, MS_BETWEEN_TAPS) {
            @Override
            public void onTick(long millisUntilFinished) {
            }
            @Override
            public void onFinish() {
                doTapAction(taps);
                taps = 0;
            }
        }.start();
    }
    //endregion
}
