package com.example.sw802f15.tempoplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MusicPlayerActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
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

    private void DRIVER_MusicPlayerService(){
        play();
    }

    public void play(){
        Intent musicPlayerService = new Intent(getApplicationContext(), MusicPlayerService.class);
        musicPlayerService.setAction("Play");
        startService(musicPlayerService);
    }
}