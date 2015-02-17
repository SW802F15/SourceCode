package com.example.sw802f15.tempoplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
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

    public IBinder onBind(Intent intent) {
        return null;
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

            default:
                break;
        }
        return 1;
    }

    public void onPrepared(MediaPlayer player) {
        //musicPlayer.start();
        Log.d("LogCat", "MusicPlayer Playing.");
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
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.release();
        }
    }

    private void loadSong(Uri uri) {
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
