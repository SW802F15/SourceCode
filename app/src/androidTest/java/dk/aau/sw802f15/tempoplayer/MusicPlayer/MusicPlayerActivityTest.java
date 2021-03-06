package dk.aau.sw802f15.tempoplayer.MusicPlayer;



import android.content.Context;
import android.media.AudioManager;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;
import dk.aau.sw802f15.tempoplayer.StepCounter.StepCounterService;
import dk.aau.sw802f15.tempoplayer.TestHelper;

public class MusicPlayerActivityTest extends ActivityInstrumentationTestCase2<MusicPlayerActivity> {
    private AudioManager _audioManager = null;
    private File emptyFolder = null;
    private MusicPlayerActivity _activity;

    public MusicPlayerActivityTest() {
        super(MusicPlayerActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if(_activity != null){
            _activity.finish();
        }

        _activity = getActivity();
        _audioManager = (AudioManager) _activity.getSystemService(Context.AUDIO_SERVICE);
        _audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 6,
                AudioManager.FLAG_PLAY_SOUND + AudioManager.FLAG_SHOW_UI);

        emptyFolder = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC + "/EmptyDir/");
        emptyFolder.mkdir();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();

        emptyFolder = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC + "/EmptyDir/");
        emptyFolder.delete();

        if(_activity != null) _activity.finish();


    }

    @SmallTest
    public void testVolumeUp() {
        int initialVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        _activity.volumeUp();
        int endVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume + 1;
        assertEquals(expectedVolume, endVolume);
    }

    @SmallTest
    public void testVolumeDown() {
        int initialVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        _activity.volumeDown();
        final int endVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        final int expectedVolume = initialVolume - 1;
        assertEquals(expectedVolume, endVolume);
    }

/*    @SmallTest
    public void testPhysicalVolumeUp() {
        int initialVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sendKeys(KeyEvent.KEYCODE_VOLUME_UP);
        int endVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume + 1;
        assertEquals(expectedVolume, endVolume);
    }
*/
/*    @SmallTest
    public void testPhysicalVolumeDown() {
        int initialVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sendKeys(KeyEvent.KEYCODE_VOLUME_DOWN);
        int endVolume = _audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int expectedVolume = initialVolume - 1;
        assertEquals(expectedVolume, endVolume);
    }*/

    @MediumTest
    public void testSongsInEmptyDir() {
        //todo throws 'Instrumentation run failed due to 'java.lang.NullPointerException''
        String parameter = emptyFolder.getAbsolutePath();
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.MusicPlayerActivity,
                "dirContainsSongs",
                _activity.getApplicationContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        boolean privateResult = true;

        try {
            privateResult = (boolean) privateMethod.invoke(_activity, parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertFalse(privateResult);
    }

    @MediumTest
    public void testSongsInNonEmptyDir(){
        //Assuming there are 5 or more dongs in the test dir.
        String parameter = MusicPlayerActivity._musicPathStub;
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.MusicPlayerActivity,
                "dirContainsSongs",
                _activity.getApplicationContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        boolean privateResult = true;
        try {
            privateResult = (boolean)privateMethod.invoke(_activity, parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertTrue(privateResult);
    }

    @MediumTest
    public void testSongsInNestedDir(){
        String path = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_MUSIC;
        Method dirContainsSongs = TestHelper.testPrivateMethod(TestHelper.Classes.MusicPlayerActivity,
                "dirContainsSongs",
                _activity.getApplicationContext());

        if (dirContainsSongs == null) {
            assertTrue(false);
        }

        boolean privateResult = true;
        try {
            privateResult = (boolean)dirContainsSongs.invoke(_activity, path);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertTrue(privateResult);
    }
}