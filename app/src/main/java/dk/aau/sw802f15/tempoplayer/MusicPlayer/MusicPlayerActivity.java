package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;


public class MusicPlayerActivity extends Activity{
    public static int MINIMUM_SONGS_REQUIRED = 5;

    // todo: remove stub when settings are done
    public static String _musicPathStub = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";

    MusicPlayerService mService;
    boolean mBound = false;
    private Initializers _initializers;
    private boolean songDirContainsSongs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        songDirContainsSongs = dirContainsSongs(_musicPathStub);
        if(!songDirContainsSongs){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(this, MusicPlayerService.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            setContentView(dk.aau.sw802f15.tempoplayer.R.layout.activity_music_player);


            SongScanner.getInstance(this).scanInBackground();

            _initializers = new Initializers(this);
            _initializers.initializeDynamicQueue();
            _initializers.initializeOnClickListeners();
            _initializers.initializeCoverFlow();
        }

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
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(dk.aau.sw802f15.tempoplayer.R.menu.menu_music_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == dk.aau.sw802f15.tempoplayer.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
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

    public void setBPMText(int bpm) {
        TextView BPMTextView = (TextView) findViewById(dk.aau.sw802f15.tempoplayer.R.id.textView_bpm);
        String BPM = Integer.toString(bpm);
        BPMTextView.setText(BPM);
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
}
