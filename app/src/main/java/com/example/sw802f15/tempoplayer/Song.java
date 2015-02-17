package com.example.sw802f15.tempoplayer;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private float _bpm;
    private String _filePath;

    public Song(long songId, String songTitle, String songArtist, String filePath){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _filePath = filePath;
    }

    public Song(long songId, String songTitle, String songArtist, float songBpm, String filePath){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _bpm = songBpm;
        _filePath = filePath;
    }

    public long getID() {return _id;}

    public String get_title() {return _title;}

    public String getArtist() {return _artist;}

    public float getBpm() {return _bpm;}

    public String getPath() {return _filePath;}
}
