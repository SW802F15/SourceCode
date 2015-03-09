package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

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
}