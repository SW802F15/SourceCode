package dk.aau.sw802f15.tempoplayer.DataAccessLayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.webkit.URLUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                loadBPM(song);

            } else if(file.isDirectory()){
                findSongsHelper(file.getPath());
            }
        }
    }



    private void loadCover(Song song){
        if (!new File(song.getUri().getPath()).exists()) return;

        FFmpegMediaMetadataRetriever ffmmr = new FFmpegMediaMetadataRetriever();
        ffmmr.setDataSource(_context, song.getUri());
        byte[] data = ffmmr.getEmbeddedPicture();
        if(data == null){
          /*  try {
                loadCoverOnline(song);
            } catch (IOException ignored) { }*/
        }else {
            loadCoverFromFile(song, data);
        }
    }

    /* for online cover demo.
    private void loadCoverOnline(Song song) throws IOException {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet("http://developer.android.com/reference/android/os/AsyncTask.html");
                    HttpResponse response = client.execute(request);

                    String html = "";
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                    in.close();
                    html = str.toString();
                    return html;
                } catch (Exception e) {
                    return null;
                }
            }
        }.execute();
    }*/

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



    private void loadBPM(Song song) {
        try {
            getOnlineBPM(String.format("https://songbpm.com/%s/%s",
                    song.getArtist().replace(' ', '+'),
                    song.getTitle().replace(' ', '+')),
                    song.getID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getOnlineBPM(final String url, final long songID) throws IOException {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = client.execute(request);

                    String html = "";
                    InputStream in = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder str = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                    in.close();
                    html = str.toString();
                    parseHTML(html, songID);
                    return null;
                } catch (Exception e) {
                    return null;
                }
            }
        }.execute();
    }

    private void parseHTML(String src, long SongID) {
        String line = src;
        String pattern = ">(\\d+)<";

        Pattern r = Pattern.compile(pattern);

        Matcher m = r.matcher(line);
        if (m.find( )) {
            updateBPM(m.group(1)+"", SongID);
        }
    }

    private void updateBPM(String bpm, long SongID) {
        Song song = _db.getSongById(SongID);
        //song.setBPM(bpm);
        _db.updateSong(song);
    }
}
