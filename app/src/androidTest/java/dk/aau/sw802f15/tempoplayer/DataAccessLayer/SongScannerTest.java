package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.net.Uri;
import android.os.Environment;
import android.test.AndroidTestCase;

import java.io.File;
import java.util.List;

public class SongScannerTest extends AndroidTestCase {

    private SongDatabase _db;
    private SongScanner _ss;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _ss = SongScanner.getInstance(getContext());
        _db = new SongDatabase(getContext());
        _db.clearDatabase();

    }
    public void testFindSongs(){
        _db.insertSong(new Song(new File(Environment.getExternalStorageDirectory() + "/"
                + Environment.DIRECTORY_MUSIC + "/tempo/music_sample_3.mp3")));
        _ss.findSongs();
        List<Song> songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(10, songs.size());
    }

    public void testRemoveSongs(){
        Song nonExistingSong = new Song("Tristram", "Matt Uemen", //Title, Artist
                "Diablo SoundTrack", 130,  //Album , BPM
                Uri.parse("nonExistingFilePath/" + "music_sample_1.mp3"),
                null, //SongUri, CoverUri
                7*60 + 40);
        _db.insertSong(nonExistingSong);
        List<Song> songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(1, songs.size());
        _ss.removeSongs();
        songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(0, songs.size());
    }
}