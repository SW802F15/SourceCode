package com.example.sw802f15.tempoplayer;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private float _bpm;
    private String _path;
    private int _durationInSec;

    public Song(long songId, String songTitle, String songArtist, String songPath, int durationInSec){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _path = songPath;
        _durationInSec = durationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, float songBpm, String songPath){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _bpm = songBpm;
        _path = songPath;
    }

    public long getID() {return _id;}

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public float getBpm() {return _bpm;}

    public String getPath() {return _path;}

    public int getDurationInSec() {return _durationInSec;}

}
