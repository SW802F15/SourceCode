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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.R;

/**
 * Created by Draegert on 16-02-2015.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    public MediaPlayer musicPlayer;
    public boolean isLoaded = false;
    public boolean isPrepared = false;
    public boolean isPaused = false;


    private final IBinder mBinder = new LocalBinder();
    private MusicPlayerActivity _activity;
    private static GUIManager _guiManager;

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
                _guiManager.changePlayPauseButton();
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
            _guiManager.showToast(errorMessage);
            return;
        }

        musicPlayer.reset();

        try {
            musicPlayer.setDataSource(this, uri);
            isLoaded = true;
            _guiManager.updateSeekBarAndLabels();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("loadSong", e.getStackTrace().toString());
            _guiManager.showToast(errorMessage);
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
            GUIManager.showToast("No Available Songs");
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
