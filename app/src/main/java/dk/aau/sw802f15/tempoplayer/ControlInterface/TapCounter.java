package dk.aau.sw802f15.tempoplayer.ControlInterface;


import android.os.CountDownTimer;


import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;

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
                MusicPlayerActivity.getInstance().doTapAction(taps);
                taps = 0;
            }
        }.start();
    }
    //endregion
}
