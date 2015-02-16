package com.example.sw802f15.tempoplayer;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long id;
    private String title;
    private  String artist;
    private float bpm;

    public Song(long songId, String songTitle, String songArtist){
        id = songId;
        title = songTitle;
        artist = songArtist;
    }

    public Song(long songId, String songTitle, String songArtist, float songBpm){
        id = songId;
        title = songTitle;
        artist = songArtist;
        bpm = songBpm;
    }

    public long getId() {return id;}

    public String getTitle() {return title;}

    public String getArtist() {return artist;}

    public float getBpm() {return bpm;}
}
