package com.example.sw802f15.tempoplayer;

import android.net.Uri;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private String _album;
    private int _bpm;
    private Uri _uri;
    private int _durationInSec;

    public Song(String songTitle, String songArtist, String songAlbum, Uri uri, int durationInSec){
        _id = -2;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _uri = uri;
        _durationInSec = durationInSec;
    }

    public Song(String songTitle, String songArtist, String songAlbum, int songBpm, Uri uri, int durationInSec){
        _id = -2;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _uri = uri;
        _durationInSec = durationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, String songAlbum, Uri uri, int durationInSec){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _uri = uri;
        _durationInSec = durationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, String songAlbum, int songBpm, Uri uri, int durationInSec){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _uri = uri;
        _durationInSec = durationInSec;
    }   


    public long getID() {return _id;}

    public void setID(long id)
    {
        _id = id;
    }

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public String getAlbum() {return _album;}

    public int getBpm() {return _bpm;}

    public Uri getUri() {return _uri;}

    public int getDurationInSec() {return _durationInSec;}
}
