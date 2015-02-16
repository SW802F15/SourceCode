package com.example.sw802f15.tempoplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

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

        switch (intent.getAction()){
            case "Play":
                musicPlayer.prepareAsync();
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
        }

        return 1;
    }

    public void onPrepared(MediaPlayer player) {
        musicPlayer.start();
        Log.d("LogCat", "MusicPlayer Playing.");
    }

    private void initialiseMusicPlayer(){
        if (musicPlayer == null){
            musicPlayer = MediaPlayer.create(this, R.raw.music_sample);
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


}
