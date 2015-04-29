package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import dk.aau.sw802f15.tempoplayer.TestHelper;

public class SongScannerTest extends AndroidTestCase {


    /*
    Errors with the test suite and NOT the SongScanner methods:

    1)
    java.lang.NoClassDefFoundError: java.lang.ReflectiveOperationException
    at dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScannerTest.testFindSongsHelper(SongScannerTest.java:104)
    at java.lang.reflect.Method.invokeNative(Native Method)
    at android.test.AndroidTestRunner.runTest(AndroidTestRunner.java:192)
    at android.test.AndroidTestRunner.runTest(AndroidTestRunner.java:177)
    at android.test.InstrumentationTestRunner.onStart(InstrumentationTestRunner.java:555)
    at android.app.Instrumentation$InstrumentationThread.run(Instrumentation.java:1619)

    2)
    Test failed to run to completion. Reason: 'Instrumentation run failed due to 'java.lang.IllegalStateException''.
    Check device logcat for details

    3)
    Test failed to run to completion. Reason: 'Instrumentation run failed due to 'java.lang.NullPointerException''.
    Check device logcat for details

    Errors are latent and crashes test run.
    Errors appear at seemingly random places.
    */


    private SongDatabase _songDatabase;
    private SongScanner _songScanner;
    private final String _songpath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/music_sample_3.mp3";
    private final String _correctDirPath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";
    private final String _wrongDirPath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_PICTURES + "/";
    private final String _invalidDirPath = "egækm893/3r33a";
    Song _song;
    Song _nonExistingSong;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _songScanner = SongScanner.getInstance(getContext());
        _songDatabase = new SongDatabase(getContext());
        _songDatabase.clearDatabase();
        _song = new Song(new File(Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/music_sample_3.mp3"));
        _nonExistingSong = new Song("Tristram", "Matt Uemen", //Title, Artist
                "Diablo SoundTrack", 130,  //Album , BPM
                Uri.parse("nonExistingFilePath/" + "music_sample_1.mp3"),
                null, //SongUri, CoverUri
                7 * 60 + 40);

        connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    public void testFindSongs() {
        _songDatabase.insertSong(_song);
        _songScanner.findSongs();
        List<Song> songs = _songDatabase.getSongsWithBPM(100, 1000);
        assertEquals(14, songs.size());
    }

    public void testRemoveSongs() {
        _songDatabase.insertSong(_nonExistingSong);
        List<Song> songs = _songDatabase.getSongsWithBPM(100, 1000);
        assertEquals(1, songs.size());
        _songScanner.removeSongs();
        songs = _songDatabase.getSongsWithBPM(100, 1000);
        assertEquals(0, songs.size());
    }

    @MediumTest
    public void testFindSongsHelper() {
        //todo java.lang.NoClassDefFoundError: java.lang.ReflectiveOperationException
        //at dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongScannerTest.testFindSongsHelper(SongScannerTest.java:135)
        Method findSongsHelper = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "findSongsHelper",
                getContext());

        if (findSongsHelper == null) {
            Assert.fail();
        }

        try {
            findSongsHelper.invoke(_songScanner, _correctDirPath);
            List<Song> songs = _songDatabase.getSongsWithBPM(100, 1000);
            assertEquals(14, songs.size());

            _songDatabase.clearDatabase();
            findSongsHelper.invoke(_songScanner, _wrongDirPath);
            songs = _songDatabase.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

            _songDatabase.clearDatabase();
            findSongsHelper.invoke(_songScanner, _invalidDirPath);
            songs = _songDatabase.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

            _songDatabase.clearDatabase();
            findSongsHelper.invoke(_songScanner, "");
            songs = _songDatabase.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

//            TODO can't invoke methods with null as an argument
//            _songDatabase.clearDatabase();
//            findSongsHelper.invoke(_songScanner, null);
//            songs = _songDatabase.getSongsWithBPM(100, 1000);
//            assertEquals(0, songs.size());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @MediumTest
    public void testLoadCover() {
        Method loadCover = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "loadCover",
                getContext());

        if (loadCover == null) Assert.fail();

        Uri defaultCover = _song.getAlbumUri();

        _song = _songDatabase.insertSong(_song);

        try {

            loadCover.invoke(_songScanner, _song);
            assertNotSame(defaultCover, _songDatabase.getSongById(_song.getID()).getAlbumUri());

            //As long as it does not crash
            loadCover.invoke(_songScanner, _nonExistingSong);

        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @LargeTest
    public void testGetJSONInvalidInput() {
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String expectedValue = null;
        String actualValue = "";
        String parameter = "aegrw";
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        while(!networkInfo.isConnected()){
            // busy wait and update network info
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(expectedValue, actualValue);
    }

    //Remember to be connected to the internet before testing.
    @LargeTest
    public void testGetJSONSuccessfulConnection() {
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String expectedValue = "{\"response\": {\"status\": {\"version\": \"4.2\", \"code\": 0, \"message\": \"Success\"}, \"songs\": [{\"artist_id\": \"ARIGTAO11FED0C4411\", \"artist_name\": \"Adam Lambert\", \"id\": \"SOCQUYA1393C5C5D05\", \"audio_summary\": {\"key\": 2, \"analysis_url\": \"http://echonest-analysis.s3.amazonaws.com/TR/wcCWhgRei5t1y90nKh9ooAthH8GlbMlQFynIMxaJk2M7RwpuQ3Tog_twM7UhinUD4nKejxlIcIT93zn_U%3D/3/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1428567241&Signature=arXNUs5r/FIgX3b5hfe0azAIu/I%3D\", \"energy\": 0.872807, \"liveness\": 0.153161, \"tempo\": 81.04, \"speechiness\": 0.091843, \"acousticness\": 0.008626, \"instrumentalness\": 0.0, \"mode\": 0, \"time_signature\": 4, \"duration\": 228.53288, \"loudness\": -4.473, \"audio_md5\": \"223770b0ecf8c0fbd5c5b5fef049bbcb\", \"valence\": 0.34062, \"danceability\": 0.300303}, \"title\": \"Runnin'\"}]}}";
        String actualValue = "";
        String parameter = "http://developer.echonest.com/api/v4/song/search?api_key=HTPFP2KLIK4BIFZTC&bucket=audio_summary&artist=adam+lambert&title=runnin'";
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }


        while(!networkInfo.isConnected()){
            // busy wait and update network info
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //This is done because each request generates a random signature, this removes that signature.
        expectedValue = stripSignature(expectedValue);//expectedValue.substring(0, 413).concat(expectedValue.substring(423, 434).concat(expectedValue.substring(461, expectedValue.length())));
        actualValue = stripSignature(actualValue);//actualValue.substring(0, 413).concat(actualValue.substring(423, 434).concat(actualValue.substring(461, actualValue.length())));
        actualValue = actualValue.replace("\n", "");

        assertEquals(expectedValue, actualValue);
    }

    @LargeTest
    public void testGetJSONWrongInput() {
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String expectedValue = null;
        String actualValue = "";
        String parameter = "http://developer.com";
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        while(!networkInfo.isConnected()){
            // busy wait and update network info
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(expectedValue, actualValue);
    }

    @LargeTest
    public void testGetJSONNoSongReturned() {
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String expectedValue = "{\"response\": {\"status\": {\"version\": \"4.2\", \"code\": 0, \"message\": \"Success\"}, \"songs\": []}}";
        String actualValue = "";
        String parameter = "http://developer.echonest.com/api/v4/song/search?api_key=HTPFP2KLIK4BIFZTC&bucket=audio_summary&artist=lars+lilholt&title=programming+masters%27";
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        while(!networkInfo.isConnected()){
            // busy wait and update network info
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        if(actualValue != null) {
            actualValue = actualValue.replace("\n", "");
        }

        assertEquals(expectedValue, actualValue);
    }

    @LargeTest
    public void testGetJSONFailedConnection() {

        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);

        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        String expectedValue = null;
        String failValue = "{\"response\": {\"status\": {\"version\": \"4.2\", \"code\": 0, \"message\": \"Success\"}, \"songs\": [{\"artist_id\": \"ARIGTAO11FED0C4411\", \"artist_name\": \"Adam Lambert\", \"id\": \"SOCQUYA1393C5C5D05\", \"audio_summary\": {\"key\": 2, \"analysis_url\": \"http://echonest-analysis.s3.amazonaws.com/TR/wcCWhgRei5t1y90nKh9ooAthH8GlbMlQFynIMxaJk2M7RwpuQ3Tog_twM7UhinUD4nKejxlIcIT93zn_U%3D/3/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1428567241&Signature=arXNUs5r/FIgX3b5hfe0azAIu/I%3D\", \"energy\": 0.872807, \"liveness\": 0.153161, \"tempo\": 81.04, \"speechiness\": 0.091843, \"acousticness\": 0.008626, \"instrumentalness\": 0.0, \"mode\": 0, \"time_signature\": 4, \"duration\": 228.53288, \"loudness\": -4.473, \"audio_md5\": \"223770b0ecf8c0fbd5c5b5fef049bbcb\", \"valence\": 0.34062, \"danceability\": 0.300303}, \"title\": \"Runnin'\"}]}}";
        String actualValue = "";
        String parameter = "http://developer.echonest.com/api/v4/song/search?api_key=HTPFP2KLIK4BIFZTC&bucket=audio_summary&artist=adam+lambert&title=runnin'";
        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        while(networkInfo.isConnected()){
            // busy wait and update network info
            networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        }

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //This is done because each request generates a random signature, this removes that signature.
        failValue = stripSignature(failValue);//.substring(0, 413).concat(failValue.substring(423, 434).concat(failValue.substring(461, failValue.length())));

        if(actualValue != null) {
            actualValue = stripSignature(actualValue);//actualValue.substring(0, 413).concat(actualValue.substring(423, 434).concat(actualValue.substring(461, actualValue.length())));
            actualValue = actualValue.replace("\n", "");
        }

        wifiManager.setWifiEnabled(true);
        assertTrue(!failValue.equals(actualValue) && expectedValue == actualValue);
    }

    private String stripSignature(String json){
        json = json.replaceAll("Signature=([a-zA-Z0-9\\/%]*)", "");
        json = json.replaceAll("Expires=([a-zA-Z0-9\\/%]*)", "");

        return json;
    }

    @SmallTest
    public void testGetBPMfromJSONEmpty(){
        testGetBPMfromJSONHelper(-1, "");
    }

    @SmallTest
    public void testGetBPMfromJSONValid(){
        testGetBPMfromJSONHelper(81, "{\"response\": {\"status\": {\"version\": \"4.2\", \"code\": 0, \"message\": \"Success\"}, \"songs\": [{\"artist_id\": \"ARIGTAO11FED0C4411\", \"artist_name\": \"Adam Lambert\", \"id\": \"SOCQUYA1393C5C5D05\", \"audio_summary\": {\"key\": 2, \"analysis_url\": \"http://echonest-analysis.s3.amazonaws.com/TR/wcCWhgRei5t1y90nKh9ooAthH8GlbMlQFynIMxaJk2M7RwpuQ3Tog_twM7UhinUD4nKejxlIcIT93zn_U%3D/3/full.json?AWSAccessKeyId=AKIAJRDFEY23UEVW42BQ&Expires=1428930721&Signature=RUa6feC0UPIPxvJEE30riJCEz5M%3D\", \"energy\": 0.872807, \"liveness\": 0.153161, \"tempo\": 81.04, \"speechiness\": 0.091843, \"acousticness\": 0.008626, \"instrumentalness\": 0.0, \"mode\": 0, \"time_signature\": 4, \"duration\": 228.53288, \"loudness\": -4.473, \"audio_md5\": \"223770b0ecf8c0fbd5c5b5fef049bbcb\", \"valence\": 0.34062, \"danceability\": 0.300303}, \"title\": \"Runnin'\"}]}}");
    }

    @SmallTest
    public void testGetBPMfromJSONNotValid(){
        testGetBPMfromJSONHelper(-1, "ø8å6æ68æ");
    }

    private void testGetBPMfromJSONHelper(int expectedValue, String parameter){
        SongScanner songScannerClass = SongScanner.getInstance(getContext());
        int actualValue;

        Method privateMethod = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "getBPMfromJSON",
                getContext());

        if (privateMethod == null) {
            assertTrue(false);
        }

        try {
            actualValue = (int) privateMethod.invoke(songScannerClass, parameter);
            assertEquals(expectedValue, actualValue);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}