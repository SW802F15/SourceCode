package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import com.example.sw802f15.tempoplayer.R;
import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;

import java.util.ArrayList;


public class MusicPlayerActivity extends Activity{

    MusicPlayerService mService;
    boolean mBound = false;

    private Initializers _initializers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MusicPlayerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_music_player);

        initializeTestSongs();


        _initializers = new Initializers(this);
        _initializers.initializeOnClickListeners();
        _initializers.initializeCoverFlow();
        _initializers.initializeDynamicQueue();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
//        if (mBound) {
//            unbindService(mConnection);
//            mBound = false;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        _initializers.startSeekBarPoll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _initializers.stopSeekBarPoll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

//        Intent musicPlayerService = new Intent(getApplicationContext(), MusicPlayerService.class);
//        //musicPlayerService.setAction("Quit");
//        stopService(musicPlayerService);
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
            mService.loadSong(DynamicQueue.getInstance(getApplicationContext()).getCurrentSong().getUri());
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    private void initializeTestSongs() {
        String initMusicPath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC + "/";
        String initCoverPath = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + "/";

        Song test_1 = new Song("Tristram", //Title
                               "Matt Uemen", //Artist
                               "Diablo SoundTrack", //Album
                                113, //BPM
                               Uri.parse(initMusicPath + "music_sample_1.mp3"), //SongUri
                               Uri.parse(initCoverPath + "cover_sample_1.jpg"), //CoverUri
                               7*60 + 40); //Duration

        Song test_2 = new Song("Let It Go",
                               "Idina Menzel",
                               "Frozen SoundTrack",
                               135,
                               Uri.parse(initMusicPath + "music_sample_2.mp3"),
                               Uri.parse(initCoverPath + "cover_sample_2.jpg"),
                               3*60 + 40);

        Song test_3 = new Song("Runnin'",
                               "Adam Lambert",
                               "Trespassing",
                               81,
                               Uri.parse(initMusicPath + "music_sample_3.mp3"),
                               Uri.parse(initCoverPath + "cover_sample_3.jpg"),
                               3*60 + 48);

        Song test_4 = new Song("Sex on Fire",
                               "Kings of Leon",
                               "Only by the Night",
                               153,
                               Uri.parse(initMusicPath + "music_sample_4.mp3"),
                               Uri.parse(initCoverPath + "cover_sample_4.jpg"),
                               3*60 + 26);

        Song test_5 = new Song("T.N.T.",
                               "AC/DC",
                               "T.N.T.",
                               131,
                               Uri.parse(initMusicPath + "music_sample_5.mp3"),
                               Uri.parse(initCoverPath + "cover_sample_5.jpg"),
                               3*60 + 34);

        Song test_6 = new Song("Still Counting",
                               "Volbeat",
                               "Guitar Gangstars & Cadillac Blood",
                               104,
                               Uri.parse(initMusicPath + "music_sample_6.mp3"),
                               Uri.parse(initCoverPath + "cover_sample_6.jpg"),
                               4*60 + 21);


        SongDatabase songDatabase = new SongDatabase(getApplicationContext());
        songDatabase.clearDatabase();
        songDatabase.insertSong(test_1);
        songDatabase.insertSong(test_2);
        songDatabase.insertSong(test_3);
        songDatabase.insertSong(test_4);
        songDatabase.insertSong(test_5);
        songDatabase.insertSong(test_6);
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

    public void setSongProgressText(int duration) {
        TextView songDurationTextView = (TextView) findViewById(R.id.textView_currentPosition);
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

}
