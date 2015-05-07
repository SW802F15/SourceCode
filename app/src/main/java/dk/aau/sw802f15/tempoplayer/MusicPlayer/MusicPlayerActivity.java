package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.WindowManager;
import java.io.File;
import dk.aau.sw802f15.tempoplayer.ControlInterface.ControlInterfaceView;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScanner;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.R;
import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;
import dk.aau.sw802f15.tempoplayer.StepCounter.StepCounterService;


public class MusicPlayerActivity extends Activity{
    ////////////////////////////////////////////////////////////////////////
    //                         Stubs and Drivers                          //
    ////////////////////////////////////////////////////////////////////////
    //region
    public static String _musicPathStub = Environment.getExternalStorageDirectory() + "/"
                                        + Environment.DIRECTORY_MUSIC + "/tempo/";

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static int MINIMUM_SONGS_REQUIRED = 6;
    private static String SUPPORTED_FILE_EXTENSION = ".mp3";
    private MusicPlayerService _MusicPlayerService;
    private StepCounterService _StepCounterService;
    boolean isMusicPlayerBound = false;
    boolean isStepCounterBound = false;
    private Initializers _initializers;
    private boolean songDirContainsSongs = false;
    private static MusicPlayerActivity instance;
    private boolean controlInterfaceActive = false;
    private ControlInterfaceView controlInterfaceView;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public static int getMINIMUM_SONGS_REQUIRED() {
        return MINIMUM_SONGS_REQUIRED;
    }
    public MusicPlayerService getMusicPlayerService() {
        return _MusicPlayerService;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
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
        if(!DBContainsSongs()){
            SongScanner.getInstance(this).scan();
        }
        if(!DBContainsSongs()){
            finish();
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return;
        }
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
        if (isMusicPlayerBound) {
            unbindService(mMusicPlayerConnection);
            isMusicPlayerBound = false;
            unbindService(mStepCounterConnection);
            isStepCounterBound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!openSettingsBecauseNoSongs()) {
            if(!DBContainsSongs()){
                SongScanner.getInstance(this).scan();
            }
            if(!DBContainsSongs()){
                finish();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return;
            }

            bindMusicPlayerService();
            bindStepCounterService();

            setContentView(R.layout.activity_music_player);

            SongScanner.getInstance(this).scanInBackground();

            setInitializers();

            controlInterfaceView = new ControlInterfaceView(this);
        }

        instance = this;
    }



    private void bindMusicPlayerService() {
        Intent intentPlayer = new Intent(this, MusicPlayerService.class);
        bindService(intentPlayer, mMusicPlayerConnection, Context.BIND_AUTO_CREATE);
    }

    private void bindStepCounterService() {
        Intent intentStep = new Intent(this, StepCounterService.class);
        bindService(intentStep, mStepCounterConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean openSettingsBecauseNoSongs() {
        songDirContainsSongs = dirContainsSongs(_musicPathStub);

        if(!songDirContainsSongs){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else {
            return false;
        }
    }

    private void setInitializers() {
        _initializers = new Initializers(this);
        _initializers.initializeDynamicQueue();
        _initializers.initializeOnClickListeners();
        _initializers.initializeCoverFlow();
    }

    private boolean DBContainsSongs() {
        return new SongDatabase(this).getSongsWithBPM().size() >= MINIMUM_SONGS_REQUIRED;
    }

    private ServiceConnection mMusicPlayerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            _MusicPlayerService = binder.getService();
            isMusicPlayerBound = true;
            _MusicPlayerService.loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getSongUri());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isMusicPlayerBound = false;
        }
    };

    private ServiceConnection mStepCounterConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            StepCounterService.LocalBinder binder = (StepCounterService.LocalBinder) service;
            _StepCounterService = binder.getService();
            isStepCounterBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isMusicPlayerBound = false;
        }
    };

    private boolean dirContainsSongs(String path){
        return dirContainsSongsHelper(path) >= MINIMUM_SONGS_REQUIRED;
    }

    private int dirContainsSongsHelper(String path) {
        int count = 0;
        if (path == null) { return 0; }

        File dir = new File(path);
        if (!dir.exists()) { return 0; }

        for(File file : dir.listFiles()){
            if (file.getPath().endsWith(SUPPORTED_FILE_EXTENSION)){
                count++;
            }
            else if(file.isDirectory()){
                count += dirContainsSongsHelper(file.getPath());
            }
        }
        return count;
    }

    private void setStreamVolume(AudioManager audioManager, int volume) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                volume,
                AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);
    }


    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
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

    public void volumeUp()
    {
        AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(currentVol == audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
            setStreamVolume(audioManager, currentVol);
        }
        else {
            setStreamVolume(audioManager, currentVol + 1);
        }
    }

    public void volumeDown()
    {
        AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(currentVol == 0) {
            setStreamVolume(audioManager, currentVol);
        }
        else {
            setStreamVolume(audioManager, currentVol - 1);
        }
    }

    @Override
    public void onBackPressed() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        boolean viewActive = controlInterfaceView.removeWindow();
        if (!viewActive) {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        controlInterfaceView.addWindow();
        return false;
    }

    public static MusicPlayerActivity getInstance(){
        return instance;
    }

    public boolean isPlaying() {
        return _MusicPlayerService.isPlaying();
    }
    //endregion
}

