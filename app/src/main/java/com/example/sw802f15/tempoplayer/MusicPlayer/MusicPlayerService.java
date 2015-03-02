package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;
import com.example.sw802f15.tempoplayer.R;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Draegert on 16-02-2015.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    MediaPlayer musicPlayer;
    Boolean isLoaded = false;
    Boolean isPrepared = false;

    private final IBinder mBinder = new LocalBinder();

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
        return mBinder;
    }

    public void onCreate() {
        super.onCreate();
        Log.d("LogCat", "MusicPlayerService started.");
        initialiseMusicPlayer();
        Log.d("LogCat", "MusicPlayer initialised.");
    }

    public void onPrepared(MediaPlayer player) {
        Log.d("LogCat", "MusicPlayer Prepared.");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
    }

    public void loadSong(Uri uri) {
        isPrepared = false;
        isLoaded = false;
        if (uri == null || !new File(uri.getPath()).exists()){
            Toast.makeText(getApplicationContext(), "Song not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        musicPlayer.reset();
        try {
            musicPlayer.setDataSource(this, uri);
            isLoaded = true;
        } catch (IOException e) {
            Log.e("loadSong", e.getStackTrace().toString());
            Toast.makeText(getApplicationContext(), "Song not available.", Toast.LENGTH_SHORT).show();
        }

        musicPlayer.prepareAsync();
        updateSeekBarAndLabels();
    }

    public void updateSeekBarAndLabels()
    {
        SeekBar sb = (SeekBar) Initializers._activity.findViewById(R.id.seekBar);
        Song song = DynamicQueue.getInstance(Initializers._activity).getCurrentSong();
        sb.setMax(song.getDurationInSec());

        TextView minLabel = (TextView) Initializers._activity.findViewById(R.id.textView_currentPosition);
        TextView maxLabel = (TextView) Initializers._activity.findViewById(R.id.textView_songDuration);
        minLabel.setText("00:00");
        maxLabel.setText(song.getDurationInMinAndSec());
    }

    public void play(){
        if (isPrepared) {
            musicPlayer.start();
        }
        else {
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());

            new CountDownTimer(10, 1) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    musicPlayer.start();
                }
            }.start();
        }
    }

    public void stop() {
        musicPlayer.stop();
        isPrepared = false;
    }

    public void pause() {
        musicPlayer.pause();
    }

    public void next() {
        if (musicPlayer.isPlaying()) {
            DynamicQueue.getInstance(getApplicationContext()).selectNextSong();
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
            play();
        }
        else {
            DynamicQueue.getInstance(getApplicationContext()).selectNextSong();
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
            pause();
        }
    }

    public void previous() {
        if (musicPlayer.isPlaying()) {
            DynamicQueue.getInstance(getApplicationContext()).selectPrevSong();
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
            play();
        }
        else {
            DynamicQueue.getInstance(getApplicationContext()).selectPrevSong();
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
            pause();
        }
    }
}
