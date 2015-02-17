package com.example.sw802f15.tempoplayer;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;


public class MusicPlayerServiceTest extends ServiceTestCase<MusicPlayerService>
{
    Song testSongValid = null;
    Song testSongInvalid = null;

    /**
     * Constructor
     */
    public MusicPlayerServiceTest() {
        super(MusicPlayerService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC
                + "/music_sample.mp3";
        testSongValid = new Song(1, "Tristram", "Matt", Uri.parse(path), 460);
        testSongInvalid = new Song(1, "Tristram", "Matt", Uri.parse(path.substring(6)), 460);
    }

    //Tests set up of test
    @SmallTest
    public void testPrecondition(){}


    @SmallTest
    public void testPlaySong(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getUri(), "mp3");
        startService(loadIntent);

        Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertTrue(getService().musicPlayer.isPlaying());
            }
        }.start();


        Intent stopIntent = new Intent();
        stopIntent.setAction("Stop");
        startService(stopIntent);

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongInvalid.getUri(), "mp3");
        startService(loadIntent);

        playIntent.setAction("Play");
        startService(playIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().musicPlayer.isPlaying());
            }
        }.start();
    }

    @SmallTest
    public void testLoadSong(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getUri(), "mp3");
        startService(loadIntent);
        assertTrue(getService().isLoaded);

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongInvalid.getUri(), "mp3");
        startService(loadIntent);
        assertFalse(getService().isLoaded);

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(null, "mp3");
        startService(loadIntent);
        assertFalse(getService().isLoaded);
    }

    @SmallTest
    public void testPrepareSong(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getUri(), "mp3");
        startService(loadIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertTrue(getService().isPrepared);
            }
        }.start();

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongInvalid.getUri(), "mp3");
        startService(loadIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().isPrepared);
            }
        }.start();

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(null, "mp3");
        startService(loadIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().isPrepared);
            }
        }.start();
    }
}
