package com.example.sw802f15.tempoplayer.DataAccessLayer;

import android.net.Uri;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private long _id;
    private String _title;
    private  String _artist;
    private String _album;
    private Integer _bpm;
    private Uri _uri;
    private int _durationInSec;
    private Uri _albumUri;

    public Song(String songTitle, String songArtist, String songAlbum, Integer songBpm, Uri uri,
                Uri albumUri, int durationInSec){
        _id = -2;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _uri = uri;
        _albumUri = albumUri;
        _durationInSec = durationInSec;
    }

    public Song(long songId, String songTitle, String songArtist, String songAlbum, Integer songBpm,
                Uri uri, Uri albumUri, int durationInSec) {
        _id = songId;
        _title = songTitle;
        _artist = songArtist;
        _album = songAlbum;
        _bpm = songBpm;
        _uri = uri;
        _durationInSec = durationInSec;
        _albumUri = albumUri;
        }


        public long getID() {return _id;}

    public void setID(long id)
    {
        _id = id;
    }

    public String getTitle() {return _title;}

    public String getArtist() {return _artist;}

    public String getAlbum() {return _album;}

    public Integer getBpm() {return _bpm;}

    public Uri getUri() {return _uri;}

    public int getDurationInSec() {return _durationInSec;}

    public Uri getAlbumUri() {
        return _albumUri;
    }

    public void setAlbumUri(Uri _albumUri) {
        this._albumUri = _albumUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;

        Song song = (Song) o;

        return _id == song._id;
    }

    @Override
    public int hashCode() {
        return (int) (_id ^ (_id >>> 32));
    }
}
