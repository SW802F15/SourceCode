package dk.aau.sw802f15.tempoplayer.DataAccessLayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.webkit.URLUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Executors;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class SongScanner{
    private static SongScanner _instance;
    private static Context _context;
    private static SongDatabase _db;

    private String _musicPathStub = Environment.getExternalStorageDirectory() + "/"
            + Environment.DIRECTORY_MUSIC + "/tempo/";

    protected SongScanner(){
        //Empty because singleton
    }

    public static SongScanner getInstance(Context context) {
        if ( _instance == null ){
            _context = context;
            _db = new SongDatabase(context);
            _instance = new SongScanner();
        }
        return _instance;
    }
    public void scanInBackground(){
        new Runnable() {
            @Override
            public void run(){
                removeSongs();
                findSongs();
            }
        }.run();
    }


    public void findSongs(){
        findSongsHelper(_musicPathStub);
    }

    public void removeSongs(){
        Map<Integer, String> paths = _db.getAllSongPaths();

        for (Integer key : paths.keySet()) {
            if(!new File(paths.get(key)).exists()){
                _db.deleteSongByID(key);
            }
        }

    }

    private void findSongsHelper(String path){
        if (path == null) { return; }

        File dir = new File(path);
        if (!dir.exists()) { return; }

        for(File file : dir.listFiles()){
            if (file.getPath().endsWith(".mp3") &&
                    _db.getSongByPath(Uri.fromFile(file)) == null){
                Song song = new Song(file);
                song = _db.insertSong(song);
                loadCover(song);

            } else if(file.isDirectory()){
                findSongsHelper(file.getPath());
            }
        }
    }

    private void loadCover(Song song){
        if (!new File(song.getAlbumUri().getPath()).exists()) return;
        
        FFmpegMediaMetadataRetriever ffmmr = new FFmpegMediaMetadataRetriever();
        ffmmr.setDataSource(_context, song.getUri());
        byte[] data = ffmmr.getEmbeddedPicture();
        if(data == null){
            //loadCoverOnline(song);
        }else {
            loadCoverFromFile(song, data);
        }
    }

    private void loadCoverFromFile(Song song, byte[] data) {
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);

        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/" + Environment.DIRECTORY_PICTURES + "/tempo";
        File dir = new File(file_path);

        if(!dir.exists()) dir.mkdirs();

        File file = new File(dir, song.getID() + ".png");

        FileOutputStream fOut = null;
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        song.setAlbumUri(Uri.fromFile(file));

        _db.updateSong(song);
    }
}
