package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import dk.aau.sw802f15.tempoplayer.ControlInterface.ControlInterfaceActivity;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.R;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;
import dk.aau.sw802f15.tempoplayer.StepCounter.StepCounterService;


public class MusicPlayerActivity extends Activity{
    public static int MINIMUM_SONGS_REQUIRED = 6;

    // todo: remove stub when settings are done
    public static String _musicPathStub = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";

    public MusicPlayerService mMusicPlayerService;
    private StepCounterService mStepCounterService;

    boolean mMusicPlayerBound = false;
    boolean mStepCounterBound = false;

    private Initializers _initializers;
    private boolean songDirContainsSongs = false;
    private static MusicPlayerActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songDirContainsSongs = dirContainsSongs(_musicPathStub);

        if(!songDirContainsSongs){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
        else{
            if(!DBContainsSongs()){
                SongScanner.getInstance(this).scan();
            }
            Intent intentPlayer = new Intent(this, MusicPlayerService.class);
            bindService(intentPlayer, mMusicPlayerConnection, Context.BIND_AUTO_CREATE);

            Intent intentStep = new Intent(this, StepCounterService.class);
            bindService(intentStep, mStepCounterConnection, Context.BIND_AUTO_CREATE);

            setContentView(dk.aau.sw802f15.tempoplayer.R.layout.activity_music_player);


            SongScanner.getInstance(this).scanInBackground();

            _initializers = new Initializers(this);
            _initializers.initializeDynamicQueue();
            _initializers.initializeOnClickListeners();
            _initializers.initializeCoverFlow();
        }

        instance = this;
    }

    private boolean DBContainsSongs() {
        return new SongDatabase(this).getSongsWithBPM().size() >= MINIMUM_SONGS_REQUIRED;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(songDirContainsSongs){
            _initializers.startSeekBarPoll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(songDirContainsSongs) {
            _initializers.stopSeekBarPoll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMusicPlayerBound) {
            unbindService(mMusicPlayerConnection);
            mMusicPlayerBound = false;
            unbindService(mStepCounterConnection);
            mStepCounterBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Intent i = new Intent(this, ControlInterfaceActivity.class);
        startActivity(i);
        return false;
    }

    private ServiceConnection mMusicPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mMusicPlayerService = binder.getService();
            mMusicPlayerBound = true;
            mMusicPlayerService.loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getSongUri());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mMusicPlayerBound = false;
        }
    };

    private ServiceConnection mStepCounterConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StepCounterService.LocalBinder binder = (StepCounterService.LocalBinder) service;
            mStepCounterService = binder.getService();
            mStepCounterBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mMusicPlayerBound = false;
        }
    };

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int key = event.getKeyCode();

        if(key == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN) {
            volumeUp();
            return true;
        }
        else if(key == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN) {
            volumeDown();
            return true;
        }
        else {
            return super.dispatchKeyEvent(event);
        }
    }


    private boolean dirContainsSongs(String path){
        return dirContainsSongsHelper(path) >= MINIMUM_SONGS_REQUIRED;
    }

    private int dirContainsSongsHelper(String path) {
        int count = 0;
        if (path == null) { return 0; }

        File dir = new File(path);
        if (!dir.exists()) { return 0; }

        for(File file : dir.listFiles()){
            if (file.getPath().endsWith(".mp3")){
                count++;
            }
            else if(file.isDirectory()){
                count += dirContainsSongsHelper(file.getPath());
            }
        }
        return count;
    }

    public void volumeUp()
    {
        AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(currentVol == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol,
                                         AudioManager.FLAG_PLAY_SOUND
                                        +AudioManager.FLAG_SHOW_UI);
        }
        else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol + 1,
                                         AudioManager.FLAG_PLAY_SOUND
                                        +AudioManager.FLAG_SHOW_UI);
        }
    }

    public void volumeDown()
    {
        AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(currentVol == 0) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol,
                                         AudioManager.FLAG_PLAY_SOUND
                                        +AudioManager.FLAG_SHOW_UI);
        }
        else {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVol - 1,
                                         AudioManager.FLAG_PLAY_SOUND
                                        +AudioManager.FLAG_SHOW_UI);
        }
    }

    public void setSPMText(int spm) {
        TextView SPMTextView = (TextView) findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_spm);
        String SPM = Integer.toString(spm);
        SPMTextView.setText(SPM);
    }

    public void setSongDurationText(int duration) {
        TextView songDurationTextView = (TextView) findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_songDuration);

        SongTime songTime = new SongTime(duration);

        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public void setSongProgressText(int duration) {
        TextView songDurationTextView = (TextView) findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_currentPosition);

        SongTime songTime = new SongTime(duration);

        songDurationTextView.setText(songTime.getFormattedSongTime());
    }

    public static MusicPlayerActivity getInstance(){
        return instance;
    }

    public void updateSongInfo() {
        Song song = DynamicQueue.getInstance(getApplicationContext()).getCurrentSong();
        ((TextView) findViewById(R.id.textView_title)).setText(song.getTitle());
        ((TextView) findViewById(R.id.textView_artist)).setText(song.getArtist());
        ((TextView) findViewById(R.id.textView_album)).setText(song.getAlbum());
        ((TextView) findViewById(R.id.textView_bpm)).setText(song.getBpm().toString());

        setSongDurationText(song.getDurationInSec());
    }

    public static void changePlayPauseButton(){
        new Handler().postDelayed(
                new Runnable(){
                    @Override
                    public void run() {
                        final ImageView playButton = (ImageView) getInstance().findViewById(R.id.playButton);
                        final ImageView pauseButton = (ImageView) getInstance().findViewById(R.id.pauseButton);
                        if (getInstance().mMusicPlayerService.musicPlayer.isPlaying()) {
                            pauseButton.setVisibility(View.VISIBLE);
                            playButton.setVisibility(View.GONE);
                        }
                        else {
                            pauseButton.setVisibility(View.GONE);
                            playButton.setVisibility(View.VISIBLE);
                        }
                    }
                }, 200) ;
    }

}
