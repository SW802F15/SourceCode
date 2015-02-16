package com.example.sw802f15.tempoplayer;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private float _bpm;

    public Song(long songId, String songTitle, String songArtist){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
    }

    public Song(long songId, String songTitle, String songArtist, float songBpm){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _bpm = songBpm;
    }

    public long getID() {return _id;}

    public String get_title() {return _title;}

    public String getArtist() {return _artist;}

    public float getBpm() {return _bpm;}
}
