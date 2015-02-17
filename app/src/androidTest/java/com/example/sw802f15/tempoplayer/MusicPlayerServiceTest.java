package com.example.sw802f15.tempoplayer;

import android.content.Intent;
import android.net.Uri;
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
        Intent startIntent = new Intent();
        startService(startIntent);


    }

    @SmallTest
    public void testLoadSong(){
        Intent startIntent = new Intent();
        startIntent.setAction("Load");
        startIntent.setDataAndType(testSongValid.getUri(), "mp3");
        startService(startIntent);
        assertTrue(getService().isLoaded);

        startIntent.setAction("Load");
        startIntent.setDataAndType(testSongInvalid.getUri(), "mp3");
        startService(startIntent);
        assertFalse(getService().isLoaded);

        startIntent.setAction("Load");
        startIntent.setDataAndType(null, "mp3");
        startService(startIntent);
        assertFalse(getService().isLoaded);
    }
}
