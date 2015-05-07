package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.R;


public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    private MediaPlayer musicPlayer;
    public boolean isLoaded = false;
    public boolean isPrepared = false;
    public boolean isPaused = false;


    private final IBinder mBinder = new LocalBinder();
    private MusicPlayerActivity _activity;

    public boolean isPlaying() {
        return musicPlayer.isPlaying();
    }

    public void seekTo(int progress) {
        musicPlayer.seekTo(progress);
    }

    public int getCurrentPosition() {
        return musicPlayer.getCurrentPosition();
        //todo     java.lang.IllegalStateException

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        MusicPlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MusicPlayerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        _activity = MusicPlayerActivity.getInstance();
        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
        initialiseMusicPlayer();
    }

    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
    }

    private void initialiseMusicPlayer(){
        if (musicPlayer == null){
            musicPlayer = new MediaPlayer();
        }
        else {
            musicPlayer.reset();
        }
        musicPlayer.setOnPreparedListener(this);

        musicPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp)
            {
                ImageView nextButton = (ImageView) _activity.findViewById(R.id.nextButton);
                nextButton.performClick();
                play();
                GUIManager.getInstance(_activity).changePlayPauseButton();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            stop();
            musicPlayer.release();
        }
    }

    public void loadSong(Uri uri) {
        isPrepared = false;
        isLoaded = false;
        String errorMessage = "Song not available.";

        if (uri == null || !new File(uri.getPath()).exists()){
            GUIManager.getInstance(_activity).showToast(errorMessage);
            return;
        }

        musicPlayer.reset();

        try {
            musicPlayer.setDataSource(this, uri);
            isLoaded = true;
            GUIManager.getInstance(_activity).updateSeekBarAndLabels();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("loadSong", Arrays.toString(e.getStackTrace()));
            GUIManager.getInstance(_activity).showToast(errorMessage);
        }

        musicPlayer.prepareAsync();
    }

    public void play(){
        if (isPrepared) {
            musicPlayer.start();
            isPaused = false;
        }
        else {
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getSongUri());

            new CountDownTimer(100, 1) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    if (isPrepared) {
                        musicPlayer.start();
                        isPaused = false;
                    }
                }
            }.start();
        }
    }

    public void stop() {
        if (musicPlayer.isPlaying() || isPaused) {
            musicPlayer.stop();
            isPrepared = false;
            isPaused = false;
        }
    }

    public void pause() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
            isPaused = true;
        }
    }

    public void next() {
        if(!DynamicQueue.getInstance(getApplicationContext()).selectNextSong()){
            GUIManager.getInstance(_activity).showToast("No Available Songs");
        }
        loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getSongUri());

        if (musicPlayer.isPlaying()) {
            play();
        }
        else {
            pause();
        }
    }

    public void previous() {
        DynamicQueue.getInstance(getApplicationContext()).selectPrevSong();
        loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getSongUri());

        if (musicPlayer.isPlaying()) {
            play();
        }
        else {
            pause();
        }
    }
}
