package com.example.sw802f15.tempoplayer;

import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
                res.add(new Song(1, "title1", "1", Uri.fromFile(new File(filepath)), 1, 42+11));
                res.add(new Song(2, "title2", "2", Uri.fromFile(new File(filepath)), 2, 42+2));
                res.add(new Song(3, "title3", "3", Uri.fromFile(new File(filepath)), 3, 42+3));
                return res;
            }
        }
    }
    StepCounterStub sc = new StepCounterStub();
    DatabaseStub db = new DatabaseStub();


    public List<Song> getMatchingSongs(int num, int thresholdBMP){
        if (num < 1 || thresholdBMP < 0){
            Log.d("getMatchingSongs", "Bad parameters");
            return new ArrayList<>();
        }
        final int BMP = sc.getCurrnetSPM();
        List<Song> songs = db.getSongsWithBPM(BMP, thresholdBMP);

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

