package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.test.AndroidTestCase;

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

    }
}