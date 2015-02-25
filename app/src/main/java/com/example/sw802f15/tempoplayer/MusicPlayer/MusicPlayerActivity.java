package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CircleButton.CircleButton;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.CoverFlow;
import com.example.sw802f15.tempoplayer.MusicPlayerGUI.CoverFlow.ResourceImageAdapter;
import com.example.sw802f15.tempoplayer.R;
import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;

import java.io.File;


public class MusicPlayerActivity extends Activity{

    public Song testSongValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        initializeCoverFlow();
        initializeOnClickListeners();


        testGUI();
    }

    @Override
    protected void onResume() {

        super.onResume();
     //   LinearLayout covers = (LinearLayout) findViewById(R.id.covers);
     //   covers.setLayoutParams(new ActionBar.LayoutParams(covers.getWidth(), covers.getWidth()));
    }

    @Override
    protected void onPause() {
        super.onPause();
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
        play(new Song(1, "", "", "", Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Music/music_sample.mp3")), 2));
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

//////////////////////////////////////////////////////////////////////////////////////////////
////                    DELETE THIS AFTER TESTS                                           ////
//////////////////////////////////////////////////////////////////////////////////////////////
    private void testGUI(){
        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC
                + "/music_sample.mp3";
        testSongValid = new Song(1, "Tristram", "Matt", "Diablo", Uri.parse(path), 460);

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
        initializeOnClickStop();
        initializeOnClickPrevious();
        initializeOnClickNext();
        initializeOnClickSettings();
    }

    private void initializeOnClickPlay() {
        ImageView playButton = (ImageView) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                play(DynamicQueue.getInstance().getCurrentSong());
            }
        });
    }

    private void initializeOnClickStop() {
        ImageView stopButton = (ImageView) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stop();
            }
        });
    }

    private void initializeOnClickPrevious() {
        ImageView previousButton = (ImageView) findViewById(R.id.previousButton);
        previousButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DynamicQueue.getInstance().selectPrevSong();
                play(DynamicQueue.getInstance().getCurrentSong());
            }
        });
    }

    private void initializeOnClickNext() {
        ImageView nextButton = (ImageView) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DynamicQueue.getInstance().selectNextSong();
                play(DynamicQueue.getInstance().getCurrentSong());
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
}
