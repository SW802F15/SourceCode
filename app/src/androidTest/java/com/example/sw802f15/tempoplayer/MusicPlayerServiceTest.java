package com.example.sw802f15.tempoplayer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import java.io.IOException;

public class MusicPlayerServiceTest extends ServiceTestCase<MusicPlayerService>
{
    String pathToMp3File = "";
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
        pathToMp3File = "";
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
        getService().loadSong(testSongValid);


    }

    @SmallTest
    public void testLoadSong(){
        Intent startIntent = new Intent();
        startService(startIntent);

        getService().loadSong(testSongValid);
        try {
            getService().musicPlayer.prepare();
        } catch (IllegalStateException ignored) {
            Assert.fail();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getService().loadSong(testSongInvalid);
        try {
            getService().musicPlayer.prepare();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }

        getService().loadSong(null);
        try {
            getService().musicPlayer.prepare();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
