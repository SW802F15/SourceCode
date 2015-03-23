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
                i++;
                Toast.makeText(getApplicationContext(), "Completed " + i + " times.", Toast.LENGTH_SHORT).show();

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
        if (uri == null || !new File(uri.getPath()).exists()){
            Toast.makeText(getApplicationContext(), "Song not available.", Toast.LENGTH_SHORT).show();
            return;
        }

        musicPlayer.reset();
        try {
            musicPlayer.setDataSource(this, uri);
            isLoaded = true;
            updateSeekBarAndLabels();
        } catch (IOException e) {
            Log.e("loadSong", e.getStackTrace().toString());
            Toast.makeText(getApplicationContext(), "Song not available.", Toast.LENGTH_SHORT).show();
        }

        musicPlayer.prepareAsync();
    }

    public void updateSeekBarAndLabels()
    {
        if(Initializers._activity != null) {
            SeekBar sb = (SeekBar) Initializers._activity.findViewById(R.id.seekBar);
            final Song song = DynamicQueue.getInstance(Initializers._activity).getCurrentSong();
            sb.setMax(song.getDurationInSec());

            final TextView minLabel = (TextView) Initializers._activity.findViewById(R.id.textView_currentPosition);
            final TextView maxLabel = (TextView) Initializers._activity.findViewById(R.id.textView_songDuration);

            Handler handler = new Handler();

            handler.post(new Runnable(){
                public void run() {
                    minLabel.setText("00:00");
                    maxLabel.setText(song.getDurationInMinAndSec());
                }
            });
        }
    }

    public void play(){
        if (isPrepared) {
            musicPlayer.start();
        }
        else {
            loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());

            new CountDownTimer(100, 1) {
                @Override
                public void onTick(long millisUntilFinished) { }
                @Override
                public void onFinish() {
                    if (isPrepared) {
                        musicPlayer.start();
                    }
                }
            }.start();
        }
    }

    public void stop() {
        musicPlayer.stop();
        isPrepared = false;
    }

    public void pause() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
        }
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
