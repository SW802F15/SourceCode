package com.example.sw802f15.tempoplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;

/**
 * Created by Draegert on 16-02-2015.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    MediaPlayer musicPlayer;

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
            case "Play":
                //musicPlayer.prepareAsync();
                Log.d("LogCat", "MusicPlayer started.");
                break;
            case "Pause":
                musicPlayer.pause();
                break;
            case "Stop":
                musicPlayer.stop();
                Log.d("LogCat", "MusicPlayer stopped.");
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
        musicPlayer.start();
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

    public void loadSong(Song song){
        if (song == null || !new File(song.getPath()).exists()){
            return;
        }
        try {
            musicPlayer.reset();
            musicPlayer.setDataSource(song.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
