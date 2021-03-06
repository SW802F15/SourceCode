package dk.aau.sw802f15.tempoplayer.DataAccessLayer;


import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dk.aau.sw802f15.tempoplayer.Settings.SettingsFragment;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class SongScanner{

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static SongScanner _instance;
    private static Context _context;
    private static SongDatabase _db;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public static SongScanner getInstance(Context context) {
        if ( _instance == null ){
            _context = context;
            _db = new SongDatabase(context);
            _instance = new SongScanner();
        }
        return _instance;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected SongScanner(){
        //Empty because singleton
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void updateBPM() {
        List<Song> songs = _db.getSongsWithBPM(-1, 0, -1);
        for (Song song : songs) {
            loadBPM(song);
        }
    }

    private void findSongsHelper(String path){
        if (path == null) { return; }

        File dir = new File(path);
        if (!dir.exists()) { return; }

        for(File file : dir.listFiles()){
            if (file.getPath().endsWith(".mp3") &&
                    _db.getSongByPath(Uri.parse(file.getPath())) == null){
                Song song = new Song(file);
                song = _db.insertSong(song);
                loadCover(song);
            }
            else if(file.isDirectory()){
                findSongsHelper(file.getPath());
            }
        }
    }

    private void loadCover(Song song){
        if (!new File(song.getSongUri().getPath()).exists()) return;

        FFmpegMediaMetadataRetriever ffmmr = new FFmpegMediaMetadataRetriever();
        ffmmr.setDataSource(_context, song.getSongUri());
        byte[] data = ffmmr.getEmbeddedPicture();
        if(data == null){
          /*  try {
                loadCoverOnline(song);
            } catch (IOException ignored) { }*/
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
        song.setAlbumUri(Uri.parse(file.getPath()));

        _db.updateSong(song);
    }

    private void loadBPM(Song song) {
        try {
            if(song.getArtist().equals("Unknown") || song.getTitle().equals("Unknown")){
                return;
            }

            String webservice = "http://developer.echonest.com/api/v4/song/search?api_key=";
            String apiKey = "HTPFP2KLIK4BIFZTC";
            String responseFormat = "&bucket=audio_summary&artist=%s&title=%s";

            Integer bpm;
            bpm = getOnlineBPM(String.format(webservice + apiKey + responseFormat,
                    song.getArtist().replace(' ', '+'),
                    song.getTitle().replace(' ', '+')));

            updateBpm(bpm, song.getID());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Integer getOnlineBPM(final String url) throws IOException {
        Integer bpm = null;

        try {
            bpm = new AsyncTask<Void, Void, Integer>() {
                @Override
                protected Integer doInBackground(Void... params) {
                    String json = getJSON(url);

                    if(json == null) {
                        return null;
                    }

                    Integer[] bpmResults = new Integer[1];
                    bpmResults[0] = getBPMfromJSON(json);

                    if(bpmResults[0] == -1){
                        return null;
                    }


                    return bpmResults[0];
                }
            }.execute().get();
        }
        catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        return bpm;
    }


    private int getBPMfromJSON(String json){
        if(json == null){
            return -1;
        }

        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray songs = response.getJSONArray("songs");

            if(songs.length() == 0){
                return -1;
            }

            JSONObject selectSong = songs.getJSONObject(0);
            JSONObject summary = selectSong.getJSONObject("audio_summary");

            int bpm = summary.getInt("tempo");

            if (bpm <= 0) {
                return -1;
            }

            return bpm;

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private String getJSON(String url){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            InputStream inputStream = httpEntity.getContent();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + '\n');
            }

            inputStream.close();

            String response = stringBuilder.toString();

            //Returns null if response is actual html page instead of JSON
            if (response.contains("<!DOCTYPE html")) {
                return null;
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateBpm(Integer bpm, long SongID) {
        try {
            Song song = _db.getSongById(SongID);
            song.setBpm(bpm);
            _db.updateSong(song);
        }
        catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public void scanInBackground(){
        scan();
//        new Thread() {
//            @Override
//            public void run() {
//                scan();
//            }
//        }.start();
    }

    public void scan(){
        findSongs();
        updateBPM();
    }

    public void findSongs(){
        for(String path : SettingsFragment.getSavedPaths(_context)){
            findSongsHelper(path);
        }
    }
    //endregion





}
