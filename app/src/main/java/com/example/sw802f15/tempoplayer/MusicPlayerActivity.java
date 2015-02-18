package com.example.sw802f15.tempoplayer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewSwitcher;

import java.io.File;


public class MusicPlayerActivity extends Activity implements ViewSwitcher.ViewFactory{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

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
        play(new Song(1, "", "", Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/Music/music_sample.mp3")), 2));
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


    private void testGUI(){
        final CoverFlow coverFlow1 = (CoverFlow) findViewById(R.id.coverflow);
        setupCoverFlow(coverFlow1, true);

    }


    private void setupCoverFlow(final CoverFlow mCoverFlow, final boolean reflect) {
        BaseAdapter coverImageAdapter;
        if (reflect) {
            coverImageAdapter = new ReflectingImageAdapter(new ResourceImageAdapter(this));
        } else {
            coverImageAdapter = new ResourceImageAdapter(this);
        }
        mCoverFlow.setAdapter(coverImageAdapter);
        mCoverFlow.setSelection(2, true);
        setupListeners(mCoverFlow);
    }
    private void setupListeners(final CoverFlow mCoverFlow) {
        mCoverFlow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView< ? > parent, final View view, final int position, final long id) {
                //textView.setText("Item clicked! : " + id);
            }

        });
        mCoverFlow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView< ? > parent, final View view, final int position, final long id) {
                //textView.setText("Item selected! : " + id);
            }

            @Override
            public void onNothingSelected(final AdapterView< ? > parent) {
                //textView.setText("Nothing clicked!");
            }
        });
    }





    @Override
    public View makeView() {
        return null;
    }
}
