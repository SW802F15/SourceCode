package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.test.AndroidTestCase;

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
        _ss.findSongs();
        List<Song> songs = _db.getSongsWithBPM(100, 1000);
        assertEquals(10, songs.size());
    }
}