package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DynamicQueue {
    private class StepCounterStub {
        public int getCurrnetSPM() {
            return 42;
        }
    }
    private class DatabaseStub {
        private String filepath = Environment.getExternalStorageDirectory() + "/Music/music_sample.mp3";
        public List<Song> getSongsWithBPM(int BMP, int tresholdBMP){
            if (BMP != 42) return null;
            else {
                List<Song> res = new ArrayList<Song>();
               // long songId, String songTitle, String songArtist, String songAlbum, int songBpm, Uri uri, int durationInSec)
                res.add(new Song(1, "title1", "1", "a", 42+11, Uri.fromFile(new File(filepath)), 1));
                res.add(new Song(2, "title2", "2", "b", 42+2, Uri.fromFile(new File(filepath)), 1));
                res.add(new Song(3, "title3", "3", "c", 42+111, Uri.fromFile(new File(filepath)), 1));

                return res;
            }
        }
    }
    private StepCounterStub sc = new StepCounterStub();
    private DatabaseStub db = new DatabaseStub();

    private List<Song> nextSongs = new ArrayList<Song>();
    private List<Song> prevSongs = new ArrayList<Song>();
    private Song currentSong;
    private int _prevSize;
    private int _lookAheadSize;
    private int _thresholdBMP;

    public DynamicQueue(int prevSize, int lookAheadSize, int thresholdBMP){
        if(prevSize < 1 || lookAheadSize < 1 || thresholdBMP < 0){
            Log.e("DynamicQueue", "Illegal Arguments");
            throw new IllegalArgumentException();
        }
        _prevSize = prevSize;
        _lookAheadSize = lookAheadSize;
        _thresholdBMP = thresholdBMP;
    }

    public Song getCurrentSong() {
        return currentSong;
    }

    public List<Song> getNextSongs() {
        return nextSongs;
    }

    public List<Song> getPrevSongs() {
        return prevSongs;
    }

    public void selectNextSong() {
        if (nextSongs == null || nextSongs.size() == 0) {
            nextSongs = getMatchingSongs(_lookAheadSize, _thresholdBMP);
        }
        if (nextSongs.size() == 0){
            throw new IllegalStateException("No songs available");
        }

        if (currentSong != null){
            prevSongs.add(currentSong);
            if (prevSongs.size() > _prevSize){
                prevSongs.remove(0);
            }
        }
        currentSong = nextSongs.get(0);
        nextSongs.remove(0);
        nextSongs.add(getMatchingSongs(1, _thresholdBMP).get(0));
    }

    public void selectPrevSong() {
        if (prevSongs.size() == 0) {
            Log.e("selectPrevSong", "No previously played songs.");
            return;
        }

        nextSongs.add(0, currentSong);
        if (nextSongs.size() > _lookAheadSize){
            nextSongs.remove(nextSongs.size()-1);
        }

        currentSong = prevSongs.get(prevSongs.size()-1);
        prevSongs.remove(currentSong);
        prevSongs.remove(null);
    }

    public List<Song> getMatchingSongs(int num, int thresholdBMP){
        if (num < 1 || thresholdBMP < 0){
            Log.d("getMatchingSongs", "Illegal Arguments");
            return new ArrayList<>();
        }
        final int BMP = sc.getCurrnetSPM();
        List<Song> songs = db.getSongsWithBPM(BMP, thresholdBMP);

        for (Song song : songs){
            if(prevSongs.contains(song)){
                prevSongs.remove(song);
            }
        }

        Collections.sort(songs, new Comparator<Song>() {
            @Override
            public int compare(Song lhs, Song rhs) {
                return Math.abs(BMP - lhs.getBpm()) - Math.abs(BMP - rhs.getBpm());
            }
        });
        if (num > songs.size()){
            num = songs.size();
        }
        return songs.subList(0, num);
    }
}

