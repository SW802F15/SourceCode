package com.example.sw802f15.tempoplayer.MusicPlayer;

import android.content.Context;
import android.util.Log;

import com.example.sw802f15.tempoplayer.DataAccessLayer.Song;
import com.example.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class DynamicQueue {


    private class StepCounterStub {
        public int getCurrentSPM() {
            return 110;
        }
    }

    private StepCounterStub sc = new StepCounterStub();
    private static SongDatabase db;

    private List<Song> nextSongs = new ArrayList<Song>();
    private List<Song> prevSongs = new ArrayList<Song>();
    private Song currentSong;
    private int _prevSize = 1;
    private int _lookAheadSize = 2;
    private int _BPMDeviation = 45;
    private static Context _context;

    private static DynamicQueue instance = null;

    protected DynamicQueue(){
        //Empty because singleton
    }

    public static DynamicQueue getInstance(Context context){
        if ( instance == null ){
            _context = context;
            db = new SongDatabase(context);
            instance = new DynamicQueue();
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
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
            nextSongs = getMatchingSongs(_lookAheadSize, _BPMDeviation);
        }
        if (nextSongs.size() == 0 && prevSongs.size() > 0){
            prevSongs.clear();
            selectNextSong();
        }

        if (currentSong != null){
            prevSongs.add(currentSong);
            if (prevSongs.size() > _prevSize){
                prevSongs.remove(0);
            }
        }
        currentSong = nextSongs.get(0);
        nextSongs.remove(0);
        nextSongs.add(getMatchingSongs(1, _BPMDeviation).get(0));
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
        final int BMP = sc.getCurrentSPM();
        final List<Song> songs = db.getSongsWithBPM(BMP, thresholdBMP);

        for (Song song : prevSongs){
            if(songs.contains(song)){
                songs.remove(song);
            }
        }
        for (Song song : nextSongs){
            if(songs.contains(song)){
                songs.remove(song);
            }
        }
        if (songs.contains(currentSong)){
            songs.remove(currentSong);
        }

        Collections.shuffle(songs, new Random());
        if (num > songs.size()){
            num = songs.size();
        }
        if (songs.size() == 0){
            final Song song = prevSongs.get(0);
            prevSongs.remove(0);
            return new ArrayList<Song>() {{ add(song); }};
        }
        return songs.subList(0, num);
    }
}

