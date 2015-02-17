package com.example.sw802f15.tempoplayer;

import android.net.Uri;

import java.net.URI;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private float _bpm;
    private Uri _path;
    private int _durationInSec;

    public Song(long songId, String songTitle, String songArtist, Uri songPath, int songDurationInSec){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _path = songPath;
        _durationInSec = songDurationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, Uri songPath, int songDurationInSec, float songBpm){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _path = songPath;
        _durationInSec = songDurationInSec;
        _bpm = songBpm;
    }   

    public long getID() {return _id;}

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public float getBpm() {return _bpm;}

    public Uri getPath() {return _path;}

    public int getDurationInSec() {return _durationInSec;}

}
