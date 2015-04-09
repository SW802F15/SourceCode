package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;

import junit.framework.Assert;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import dk.aau.sw802f15.tempoplayer.TestHelper;

public class SongScannerTest extends AndroidTestCase {

    private SongDatabase _db;
    private SongScanner _ss;
    private final String _songpath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/music_sample_3.mp3";
    private final String _correctDirPath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";
    private final String _wrongDirPath = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_PICTURES + "/";
    private final String _invalidDirPath = "egækm893/3r33a";
    Song _song;
    Song _nonExistingSong;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _ss = SongScanner.getInstance(getContext());
        _db = new SongDatabase(getContext());
        _db.clearDatabase();
        _song = new Song(new File(Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/music_sample_3.mp3"));
        _nonExistingSong = new Song("Tristram", "Matt Uemen", //Title, Artist
                "Diablo SoundTrack", 130,  //Album , BPM
                Uri.parse("nonExistingFilePath/" + "music_sample_1.mp3"),
                null, //SongUri, CoverUri
                7 * 60 + 40);


    }

    public void testFindSongs() {
        _db.insertSong(_song);
        _ss.findSongs();
        List<Song> songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(10, songs.size());
    }

    public void testRemoveSongs() {
        _db.insertSong(_nonExistingSong);
        List<Song> songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(1, songs.size());
        _ss.removeSongs();
        songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(0, songs.size());
    }
    @MediumTest
    public void testFindSongsHelper() {
        Method findSongsHelper = TestHelper.testPrivateMethod(TestHelper.Classes.SongScanner,
                "findSongsHelper",
                getContext());

        if (findSongsHelper == null) Assert.fail();

        try {
            findSongsHelper.invoke(_ss, _correctDirPath);
            List<Song> songs = _db.getSongsWithBPM(100, 1000);
            assertEquals(10, songs.size());

            _db.clearDatabase();
            findSongsHelper.invoke(_ss, _wrongDirPath);
            songs = _db.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

            _db.clearDatabase();
            findSongsHelper.invoke(_ss, _invalidDirPath);
            songs = _db.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

            _db.clearDatabase();
            findSongsHelper.invoke(_ss, "");
            songs = _db.getSongsWithBPM(100, 1000);
            assertEquals(0, songs.size());

//            TODO can't invoke methods with null as an argument
//            _db.clearDatabase();
//            findSongsHelper.invoke(_ss, null);
//            songs = _db.getSongsWithBPM(100, 1000);
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

        _song = _db.insertSong(_song);

        try {

            loadCover.invoke(_ss, _song);
            assertNotSame(defaultCover, _db.getSongById(_song.getID()).getAlbumUri());

            //As long as it does not crash
            loadCover.invoke(_ss, _nonExistingSong);

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

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //This is done because each request generates a random signature, this removes that signature.
        expectedValue = expectedValue.substring(0, 413).concat(expectedValue.substring(423, 434).concat(expectedValue.substring(461, expectedValue.length())));
        actualValue = actualValue.substring(0, 413).concat(actualValue.substring(423, 434).concat(actualValue.substring(461, actualValue.length())));
        actualValue = actualValue.replace("\n", "");

        assertEquals(expectedValue, actualValue);
    }

    //Remember to be disconnected to the internet before testing.
    @LargeTest
    public void testGetJSONFailedConnection() {
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

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //This is done because each request generates a random signature, this removes that signature.
        failValue = failValue.substring(0, 413).concat(failValue.substring(423, 434).concat(failValue.substring(461, failValue.length())));
        actualValue = actualValue.substring(0, 413).concat(actualValue.substring(423, 434).concat(actualValue.substring(461, actualValue.length())));
        actualValue = actualValue.replace("\n", "");

        assertTrue(!failValue.equals(actualValue) && expectedValue.equals(actualValue));
    }

    //Remember to be connected to the internet before testing.
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

        try {
            actualValue = (String) privateMethod.invoke(songScannerClass, parameter);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        assertEquals(expectedValue, actualValue);
    }
}