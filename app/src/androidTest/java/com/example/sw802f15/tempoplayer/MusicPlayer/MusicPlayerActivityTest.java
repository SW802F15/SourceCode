package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import com.example.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import android.view.KeyEvent;

public class MusicPlayerActivityTest extends ActivityInstrumentationTestCase2<MusicPlayerActivity>
{
    private AudioManager _am = null;
    private Activity _ac = null;
    private MusicPlayerActivity _mpa = null;

    public MusicPlayerActivityTest()
    {
        super(MusicPlayerActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        setActivityInitialTouchMode(false);

        _ac = getActivity();
        Context ctx = _ac.getApplicationContext();
        //_mpa = new MusicPlayerActivity();
        //Context ctx = _mpa.getApplicationContext();
        _am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
        _am.setStreamVolume(AudioManager.STREAM_MUSIC, 6,
                AudioManager.FLAG_PLAY_SOUND+AudioManager.FLAG_SHOW_UI);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    @SmallTest
    public void testVolumeUp()
    {
        int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        v.volumeUp();
        int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume + 1;
        assertEquals(expectedVolume, endVolume);
    }

    @SmallTest
    public void testVolumeDown()
    {
        int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        v.volumeDown();
        final int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int expectedVolume = initialVolume - 1;
        assertEquals(expectedVolume, endVolume);
    }

    @SmallTest
    public void testPhysicalVolumeUp()
    {
        int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        sendKeys(KeyEvent.KEYCODE_VOLUME_UP);
        int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume + 1;
        assertEquals(expectedVolume, endVolume);
    }

    @SmallTest
    public void testPhysicalVolumeDown()
    {
        int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        sendKeys(KeyEvent.KEYCODE_VOLUME_DOWN);
        int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume - 1;
        assertEquals(expectedVolume, endVolume);
    }
}