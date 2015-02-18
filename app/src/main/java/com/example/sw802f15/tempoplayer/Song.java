package com.example.sw802f15.tempoplayer;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private String _album;
    private int _bpm;
    private String _filePath;

    public Song(String songTitle, String songArtist, String songAlbum, String filePath){
        _id = -2;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _filePath = filePath;
    }

    public Song(String songTitle, String songArtist, String songAlbum, int songBpm, String filePath){
        _id = -2;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _filePath = filePath;
    }

    public Song(long songId, String songTitle, String songArtist, String songAlbum, String filePath){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _filePath = filePath;
    }

    public Song(long songId, String songTitle, String songArtist, String songAlbum, int songBpm, String filePath){
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _filePath = filePath;
    }

    public long getID() {return _id;}

    public void setID(long id)
    {
        _id = id;
    }

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public String getAlbum() {return _album;}

    public float getBpm() {return _bpm;}

    public String getPath() {return _filePath;}
}
