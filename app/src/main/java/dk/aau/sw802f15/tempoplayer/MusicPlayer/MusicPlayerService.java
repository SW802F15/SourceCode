package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.R;

import java.io.File;
import java.io.IOException;

/**
 * Created by Draegert on 16-02-2015.
 */
public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener{

    MediaPlayer musicPlayer;
    public boolean isLoaded = false;
    public boolean isPrepared = false;
    public boolean isPaused = false;


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
        initialiseMusicPlayer();
    }

    public void onPrepared(MediaPlayer player) {
        isPrepared = true;
    }


    private int i = 0;
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
                ImageView nextButton = (ImageView) Initializers._activity.findViewById(dk.aau.sw802f15.tempoplayer.R.id.nextButton);
                nextButton.performClick();
                play();
                Initializers.changePlayPauseButton();
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
            showToast(errorMessage);
            return;
        }

        musicPlayer.reset();

        try {
            musicPlayer.setDataSource(this, uri);
            isLoaded = true;
            updateSeekBarAndLabels();
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("loadSong", e.getStackTrace().toString());
            showToast(errorMessage);
        }

        musicPlayer.prepareAsync();
    }

    public void updateSeekBarAndLabels()
    {
        MusicPlayerActivity musicPlayerActivity = Initializers._activity;
        if(musicPlayerActivity == null) {
            return;
        }

        SeekBar seekBar = (SeekBar) musicPlayerActivity.findViewById(R.id.seekBar);
        final Song song = DynamicQueue.getInstance(musicPlayerActivity).getCurrentSong();
        final TextView minLabel = (TextView) musicPlayerActivity.findViewById(R.id.textView_currentPosition);
        final TextView maxLabel = (TextView) musicPlayerActivity.findViewById(R.id.textView_songDuration);
        Handler handler = new Handler();

        seekBar.setMax(song.getDurationInSec());

        handler.post(new Runnable(){
            public void run() {
                minLabel.setText("00:00");
                maxLabel.setText(song.getDurationInMinAndSec());
            }
        });
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
        DynamicQueue.getInstance(getApplicationContext()).selectNextSong();
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

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),
                       message,
                       Toast.LENGTH_SHORT).show();
    }
}
