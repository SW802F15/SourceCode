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




        Gallery gallery = (Gallery) findViewById(R.id.gallery);
        CustomGallery adapter = new CustomGallery(this);
        adapter.addImageToGallery(R.drawable.lonely_island_wack_album);
        adapter.addImageToGallery(R.drawable.ic_launcher);
        adapter.addImageToGallery(R.drawable.adele_21);
        adapter.addImageToGallery(R.drawable.nirvananevermindalbumco);
        adapter.addImageToGallery(R.drawable.dark_side);
        gallery.setAdapter(adapter);











/*        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        float density = getResources().getDisplayMetrics().density;
        float heightInDp = displayMetrics.heightPixels / density;
        float widthInDp = displayMetrics.widthPixels / density;

        ImageSwitcher imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);




        ImageView imgPrevPrev = (ImageView) findViewById(R.id.imageView_PreviousPrevious);
        ImageView imgPrev = (ImageView) findViewById(R.id.imageView_Previous);
        ImageView imgCurrent = (ImageView) findViewById(R.id.imageView_Current);
        ImageView imgNext = (ImageView) findViewById(R.id.imageView_Next);
        ImageView imgNextNext = (ImageView) findViewById(R.id.imageView_NextNext);

        int small = 3;
        int med = 2;
        int large = 1;

        imgPrevPrev.setMinimumWidth((int)widthInDp / small);
        imgPrevPrev.setMaxWidth((int)widthInDp / small);
        imgPrevPrev.setMinimumHeight((int)widthInDp / small);
        imgPrevPrev.setMaxHeight((int)widthInDp / small);

        imgPrev.setMinimumWidth((int)widthInDp / med);
        imgPrev.setMaxWidth((int)widthInDp / med);
        imgPrev.setMinimumHeight((int)widthInDp / med);
        imgPrev.setMaxHeight((int)widthInDp / med);

        imgCurrent.setMinimumWidth((int)widthInDp / large);
        imgCurrent.setMaxWidth((int)widthInDp / large);
        imgCurrent.setMinimumHeight((int)widthInDp / large);
        imgCurrent.setMaxHeight((int)widthInDp / large);

        imgNext.setMinimumWidth((int)widthInDp / med);
        imgNext.setMaxWidth((int)widthInDp / med);
        imgNext.setMinimumHeight((int)widthInDp / med);
        imgNext.setMaxHeight((int)widthInDp / med);

        imgNextNext.setMinimumWidth((int)widthInDp / small);
        imgNextNext.setMaxWidth((int)widthInDp / small);
        imgNextNext.setMinimumHeight((int)widthInDp / small);
        imgNextNext.setMaxHeight((int)widthInDp / small);


        imgPrevPrev.setImageResource(R.drawable.lonely_island_wack_album);
        imgPrev.setImageResource(R.drawable.lonely_island_wack_album);

        imgCurrent.setImageResource(R.drawable.lonely_island_wack_album);

        imgNext.setImageResource(R.drawable.lonely_island_wack_album);
        imgNextNext.setImageResource(R.drawable.lonely_island_wack_album);*/
    }

    @Override
    public View makeView() {
        return null;
    }
}
