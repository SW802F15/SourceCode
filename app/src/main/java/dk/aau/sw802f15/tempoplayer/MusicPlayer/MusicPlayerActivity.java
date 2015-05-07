package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import dk.aau.sw802f15.tempoplayer.ControlInterface.ControlInterfaceView;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
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

    ControlInterfaceView controlInterfaceView;

    boolean mMusicPlayerBound = false;
    boolean mStepCounterBound = false;

    private Initializers _initializers;
    private boolean songDirContainsSongs = false;
    private static MusicPlayerActivity instance;
    private boolean controlInterfaceActive = false;

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

            controlInterfaceView = new ControlInterfaceView(this);
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
            GUIManager.getInstance(getInstance()).startSeekBarPoll();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(songDirContainsSongs) {
            GUIManager.getInstance(getInstance()).stopSeekBarPoll();
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
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        if (!controlInterfaceActive) {
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT, 150,
                    WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    PixelFormat.TRANSLUCENT);
            windowManager.addView(controlInterfaceView, params);
            controlInterfaceActive = true;
        }

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
        public void onServiceConnected(ComponentName className, IBinder service) {
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

    public static MusicPlayerActivity getInstance(){
        return instance;
    }

    public void doTapAction(int taps) {
        switch (taps){
            case 0:
                mMusicPlayerService.stop();
                break;
            case 1:
                if (mMusicPlayerService.isPaused) {
                    mMusicPlayerService.play();
                }
                else {
                    mMusicPlayerService.pause();
                }
                break;
            case 2:
                mMusicPlayerService.next();
                break;
            case 3:
                mMusicPlayerService.previous();
                break;
            default:
                //do nothing
        }
        Toast.makeText(this, "taps: " + taps, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (controlInterfaceActive) {
            return controlInterfaceView.detector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        if (controlInterfaceActive){
            controlInterfaceActive = false;
            getWindowManager().removeView(controlInterfaceView);
        }
        else {
            super.onBackPressed();
        }
    }
}

