package com.example.sw802f15.tempoplayer;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;
import com.example.sw802f15.tempoplayer.MusicPlayerActivity;

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
        _ac = getActivity();
        Context ctx = _ac.getApplicationContext();
        //_mpa = new MusicPlayerActivity();
        //Context ctx = _mpa.getApplicationContext();
        _am = (AudioManager)ctx.getSystemService(Context.AUDIO_SERVICE);
    }

    @SmallTest
    public void testVolumeUp()
    {
        final int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        v.volumeUp();
        final int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int expectedVolume = initialVolume + 1;
        assertEquals(expectedVolume, endVolume);
    }

    @SmallTest
    public void testVolumeDown()
    {
        final int initialVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        MusicPlayerActivity v = (MusicPlayerActivity)_ac;
        v.volumeDown();
        final int endVolume = _am.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int expectedVolume = initialVolume - 1;
        assertEquals(expectedVolume, endVolume);
    }
}