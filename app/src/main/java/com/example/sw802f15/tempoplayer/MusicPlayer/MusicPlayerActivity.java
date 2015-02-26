package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CircleButton.CircleButton;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import com.example.sw802f15.tempoplayer.R;
import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;

import java.io.File;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class MusicPlayerActivity extends Activity{

    public Song testSongValid;
    private SeekBar seekBar;
    MusicPlayerService mService;
    boolean mBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initializeCoverFlow();
        initializeOnClickListeners();
        initializeSeekBar();
        initializeDynamicQueue();

        testGUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent musicPlayerService = new Intent(getApplicationContext(), MusicPlayerService.class);
        //musicPlayerService.setAction("Quit");
        stopService(musicPlayerService);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //////////////////////////////////////////////
            DRIVER_MusicPlayerService();
            //////////////////////////////////////////////
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int key = event.getKeyCode();

        if(key == KeyEvent.KEYCODE_VOLUME_UP && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            volumeUp();
            return true;
        }
        else if(key == KeyEvent.KEYCODE_VOLUME_DOWN && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            volumeDown();
            return true;
        }else
        {
            return super.dispatchKeyEvent(event);
        }
    }

    private void DRIVER_MusicPlayerService(){
        play(new Song("", "", "", null,Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Music/music_sample.mp3")), null, 2));
    }

    public void play(Song song){
        Intent musicPlayerService = new Intent(getApplicationContext(), MusicPlayerService.class);
        musicPlayerService.setAction("Load");
        musicPlayerService.setDataAndType(song.getUri(), "mp3");
        new CountDownTimer(1000, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                Intent playIntent = new Intent(getApplicationContext(), MusicPlayerService.class);
                playIntent.setAction("Play");
                startService(playIntent);
            }
        }.start();
        startService(musicPlayerService);
    }

    public void stop() {
        Intent stopIntent = new Intent(getApplicationContext(), MusicPlayerService.class);
        stopIntent.setAction("Stop");
        startService(stopIntent);
    }

    public void pause() {
        Intent pauseIntent = new Intent(getApplicationContext(), MusicPlayerService.class);
        pauseIntent.setAction("Pause");
        startService(pauseIntent);
    }

    public void volumeUp()
    {
        AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(curVol == am.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
        {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, curVol,
                    AudioManager.FLAG_PLAY_SOUND+AudioManager.FLAG_SHOW_UI);
        }else
        {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, curVol + 1,
                    AudioManager.FLAG_PLAY_SOUND+AudioManager.FLAG_SHOW_UI);
        }
    }

    public void volumeDown()
    {
        AudioManager am = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        final int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        if(curVol == 0)
        {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, curVol,
                    AudioManager.FLAG_PLAY_SOUND+AudioManager.FLAG_SHOW_UI);
        }else
        {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, curVol - 1,
                    AudioManager.FLAG_PLAY_SOUND+AudioManager.FLAG_SHOW_UI);
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    //////////////////////////////////////////////////////////////////////////////////////////////
////                    DELETE THIS AFTER TESTS                                           ////
//////////////////////////////////////////////////////////////////////////////////////////////
    private void testGUI(){
        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC
                + "/music_sample.mp3";
        testSongValid = new Song(1, "Tristram", "Matt", "Diablo", null, Uri.parse(path), null, 460);



        setBPMText(52);
        setSPMText(85);
    }









    private void initializeDynamicQueue() {
        DynamicQueue.getInstance().selectNextSong();
        setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
    }

    private void initializeCoverFlow() {
        final CoverFlow coverFlow = (CoverFlow) findViewById(R.id.coverflow);
        BaseAdapter coverImageAdapter = new ResourceImageAdapter(this);
        coverFlow.setAdapter(coverImageAdapter);
        coverFlow.setSpacing(-10);
        coverFlow.setMaxZoom(-200);
    }

    private void initializeOnClickListeners() {
        initializeOnClickPlay();
        initializeOnClickPause();
        initializeOnClickStop();
        initializeOnClickPrevious();
        initializeOnClickNext();
        initializeOnClickSettings();
    }

    private void initializeOnClickPlay() {
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);
        final ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);

        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play(DynamicQueue.getInstance().getCurrentSong());
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Play loads song every time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeOnClickPause() {
        final ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);

        pauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pause();
                pauseButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Make runnable that checks if MusicPlayer is playing, then show/gone play/pause buttons.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = (ImageView) findViewById(R.id.stopButton);
        final ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
                if (playButton.getVisibility() == View.GONE) {
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initializeOnClickPrevious() {
        ImageView previousButton = (ImageView) findViewById(R.id.previousButton);
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);
        final ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);

        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DynamicQueue.getInstance().selectPrevSong();
                setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
                play(DynamicQueue.getInstance().getCurrentSong());
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = (ImageView) findViewById(R.id.nextButton);
        final ImageView playButton = (ImageView) findViewById(R.id.playButton);
        final ImageView pauseButton = (ImageView) findViewById(R.id.pauseButton);

        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DynamicQueue.getInstance().selectNextSong();
                setSongDurationText(DynamicQueue.getInstance().getCurrentSong().getDurationInSec());
                play(DynamicQueue.getInstance().getCurrentSong());
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initializeOnClickSettings() {
        ImageView settingsButton = (ImageView) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Start settings activity
            }

        });
    }

    public void setBPMText(int bpm) {
        TextView BPMTextView = (TextView) findViewById(R.id.textView_bpm);
        String BPM = Integer.toString(bpm);
        BPMTextView.setText(BPM);
    }

    public void setSPMText(int spm) {
        TextView SPMTextView = (TextView) findViewById(R.id.textView_spm);
        String SPM = Integer.toString(spm);
        SPMTextView.setText(SPM);
    }

    public void setSongDurationText(int duration) {
        TextView songDurationTextView = (TextView) findViewById(R.id.textView_songDuration);
        Time time = new Time();
        if (duration >= 3600) {
            time.hour = duration / 3600;
            duration = duration % 3600;
        }
        if (duration >= 60) {
            time.minute = duration / 60;
            duration = duration % 60;
        }
        time.second = duration;

        String durationString = time.format("%M:%S");

        if (time.hour > 0) {
            durationString = time.format("%H:%M:%S");
        }

        songDurationTextView.setText(durationString);
    }

    private void initializeSeekBar() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
    }


    private void setSeekBarForCurrentSong(int songDuration) {
        seekBar.setMax(songDuration);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Intent seekToIntent = new Intent(getApplicationContext(), MusicPlayerService.class);
                    seekToIntent.setAction("SeekTo");
                    seekToIntent.setFlags(progress);
                    startService(seekToIntent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

    }

    private void updateSeekBar() {

    }



}
