package dk.aau.sw802f15.tempoplayer.DataAccessLayer;


import android.content.Context;
import android.os.Environment;

import java.io.File;

public class SongScanner {
    private static SongScanner _instance;
    private static Context _context;
    private static SongDatabase _db;

    private String _musicPathStub = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";

    public void findSongs(){
        File dir = new File(_musicPathStub);
        for(File file : dir.listFiles()){
            if (file.getPath().endsWith(".mp3") &&
                    _db.getSongByPath() == null){
                Song song = new Song(file);
                _db.insertSong(song);
            }
        }
    }

    public static SongScanner getInstance(Context context) {
        if ( _instance == null ){
            _context = context;
            _db = new SongDatabase(context);
            _instance = new SongScanner();
        }
        return _instance;
    }
}
