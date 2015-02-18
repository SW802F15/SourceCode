package com.example.sw802f15.tempoplayer;

import android.net.Uri;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private int _bpm;
    private Uri _uri;
    private int _durationInSec;

    public Song(long songId, String songTitle, String songArtist, Uri songUri, int songDurationInSec){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _uri = songUri;
        _durationInSec = songDurationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, Uri songUri, int songDurationInSec, int songBpm){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _uri = songUri;
        _durationInSec = songDurationInSec;
        _bpm = songBpm;
    }   


    public long getID() {return _id;}

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public int getBpm() {return _bpm;}

    public Uri getUri() {return _uri;}

    public int getDurationInSec() {return _durationInSec;}
}
