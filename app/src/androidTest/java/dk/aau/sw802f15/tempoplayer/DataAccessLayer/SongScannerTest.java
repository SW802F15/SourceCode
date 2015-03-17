package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;
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
}