package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;

import junit.framework.Assert;


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
        Intent musicPlayerService = new Intent(getContext(), MusicPlayerService.class);
        startService(musicPlayerService);

        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC
                + "/tempo/music_sample_3.mp3";
        testSongValid = new Song(1, "Tristram", "Matt", "Diablo", null, Uri.parse(path), null, 460);
        testSongInvalid = new Song(1, "Tristram", "Matt", "Diablo", null, Uri.parse(path.substring(6)), null, 460);
    }

    //Tests set up of test
    @SmallTest
    public void testPrecondition(){}

    @MediumTest
    public void testPlaySong(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);
        new CountDownTimer(1000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertTrue(getService().isPlaying());
            }
        }.start();


        Intent stopIntent = new Intent();
        stopIntent.setAction("Stop");
        startService(stopIntent);

        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongInvalid.getSongUri(), "mp3");
        startService(loadIntent);

        playIntent.setAction("Play");
        startService(playIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().isPlaying());
            }
        }.start();
    }

    @MediumTest
    public void testLoadSong(){
        getService().loadSong(testSongValid.getSongUri());
        assertTrue(getService().isLoaded);

        getService().loadSong(testSongInvalid.getSongUri());
        assertFalse(getService().isLoaded);

        getService().loadSong(null);
        assertFalse(getService().isLoaded);
    }

    @MediumTest
    public void testPause(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        Intent pauseIntent = new Intent();
        pauseIntent.setAction("Pause");
        startService(pauseIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().isPlaying());
                try {
                    startService(playIntent);
                } catch (IllegalStateException e){
                    e.printStackTrace();
                    Assert.fail();
                }
            }
        }.start();
    }

    @MediumTest
    public void testPausePosition(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                final Intent pauseIntent = new Intent();
                pauseIntent.setAction("Pause");
                startService(pauseIntent);
                final int currentPosition = getService().getCurrentPosition();

                new CountDownTimer(2000,1) {
                    @Override
                    public void onTick(long millisUntilFinished) { }
                    @Override
                    public void onFinish() {
                        assertTrue(currentPosition == getService().getCurrentPosition());
                    }
                }.start();
            }
        }.start();
    }

    @MediumTest
    public void testUnPause(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                final Intent pauseIntent = new Intent();
                pauseIntent.setAction("Pause");
                startService(pauseIntent);
                final float currentPosition = getService().getCurrentPosition();
                startService(playIntent);

                new CountDownTimer(1000,1) {
                    @Override
                    public void onTick(long millisUntilFinished) { }
                    @Override
                    public void onFinish() {
                        assertTrue(currentPosition < getService().getCurrentPosition());
                    }
                }.start();
            }
        }.start();
    }

    @MediumTest
    public void testStop(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        Intent stopIntent = new Intent();
        stopIntent.setAction("Stop");
        startService(stopIntent);
        new CountDownTimer(2000,1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertFalse(getService().isPlaying());
                try {
                    startService(playIntent);
                    Assert.fail();
                } catch (IllegalStateException ignored){ }
            }
        }.start();
    }

    @MediumTest
    public void testPrepareSong(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
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
        loadIntent.setDataAndType(testSongInvalid.getSongUri(), "mp3");
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

    @MediumTest
    public void testRepeat(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final int startPos = (7*60+38)*1000;
        getService().seekTo(startPos);

        final Intent repeatIntent = new Intent();
        repeatIntent.setAction("Repeat");
        startService(repeatIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        new CountDownTimer(5000, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertTrue(getService().isPlaying() &&
                           getService().isLooping() &&
                           getService().getCurrentPosition() < startPos);
            }
        }.start();
    }

    @MediumTest
    public void testUnRepeat(){
        Intent loadIntent = new Intent();
        loadIntent.setAction("Load");
        loadIntent.setDataAndType(testSongValid.getSongUri(), "mp3");
        startService(loadIntent);

        final int startPos = (7*60+38)*1000;
        getService().seekTo(startPos);

        final Intent repeatIntent = new Intent();
        repeatIntent.setAction("Repeat");
        startService(repeatIntent);

        final Intent playIntent = new Intent();
        playIntent.setAction("Play");
        startService(playIntent);

        new CountDownTimer(5000, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                final Intent repeatIntent = new Intent();
                repeatIntent.setAction("Repeat");
                startService(repeatIntent);
            }
        }.start();

        new CountDownTimer(7000, 1) {
            @Override
            public void onTick(long millisUntilFinished) { }
            @Override
            public void onFinish() {
                assertTrue(getService().isPlaying() &&
                           !getService().isLooping() &&
                           getService().getCurrentPosition() < startPos);
            }
        }.start();
    }

//    @MediumTest
//    public void testSeekBarLabelsUpdated()
//    {
//        TextView minLabel = (TextView) Initializers._activity.findViewById(R.id.textView_currentPosition);
//        TextView maxLabel = (TextView) Initializers._activity.findViewById(R.id.textView_songDuration);
//
//        getService().loadSong(testSongValid.getSongUri());
//
//        assertEquals("07:40", minLabel.getText());
//        assertEquals("00:00", maxLabel.getText());
//    }
}
