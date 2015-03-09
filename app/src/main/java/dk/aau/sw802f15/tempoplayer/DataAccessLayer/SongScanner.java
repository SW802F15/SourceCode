package dk.aau.sw802f15.tempoplayer.DataAccessLayer;


import android.content.Context;

public class SongScanner {
    private static SongScanner instance;
    private static Context _context;
    private static SongDatabase db;

    public void findSongs(){

    }

    public static SongScanner getInstance(Context context) {
        if ( instance == null ){
            _context = context;
            db = new SongDatabase(context);
            instance = new SongScanner();
        }
        return instance;
    }
}
