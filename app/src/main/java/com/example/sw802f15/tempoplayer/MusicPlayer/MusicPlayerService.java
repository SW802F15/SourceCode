package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

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

    public int onStartCommand(Intent intent, int flags, int startId){
        if (intent.getAction() == null){
            return 0;
        }
        switch (intent.getAction()){
            case "Load":
                loadSong(intent.getData());
                Log.d("onStartCommand", "Song loaded.");
                break;
            case "Play":
                musicPlayer.start();
                Log.d("onStartCommand", "MusicPlayer started.");
                break;
            case "Pause":
                musicPlayer.pause();
                Log.d("onStartCommand", "MusicPlayer paused.");
                break;
            case "Stop":
                musicPlayer.stop();
                Log.d("onStartCommand", "MusicPlayer stopped.");
                break;
            case "Next":
                break;
            case "Previous":
                break;
            case "Repeat":
                musicPlayer.setLooping(!musicPlayer.isLooping());
                break;
            case "SeekTo":
                musicPlayer.seekTo(intent.getFlags());
                break;

            default:
                break;
        }
        return 1;
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
    }

}
