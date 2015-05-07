package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;

import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static final int MS_PER_SEC = 1000;
    private static final int INT_DOES_NOT_EXIST = -1;
    private static final Uri URI_DOES_NOT_EXIST = null;
    private long _id;
    private String _title;
    private String _artist;
    private String _album;
    private Integer _bpm;
    private int _durationInSec;
    private Uri _songUri;
    private Uri _albumUri;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public long getID() {
        return _id;
    }
    public void setID(long id) {
        _id = id;
    }

    public String getTitle() {
        return _title;
    }

    public String getArtist() {
        return _artist;
    }

    public String getAlbum() {
        return _album;
    }

    public Integer getBpm() {
        return _bpm;
    }
    public void setBpm(Integer bpm) {
        this._bpm = bpm;
    }

    public int getDurationInSec() {
        return _durationInSec;
    }

    public Uri getSongUri() {
        return _songUri;
    }

    public Uri getAlbumUri() {
        return _albumUri;
    }
    public void setAlbumUri(Uri _albumUri) {
        this._albumUri = _albumUri;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public Song(long songId, String songTitle, String songArtist, String songAlbum, Integer songBpm,
                Uri uri, Uri albumUri, int durationInSec) {
        setValues(songId, songTitle, songArtist, songAlbum, songBpm, uri, albumUri, durationInSec);
    }

    public Song(String songTitle, String songArtist, String songAlbum, Integer songBpm, Uri uri,
                Uri albumUri, int durationInSec){
        setValues(INT_DOES_NOT_EXIST, songTitle, songArtist, songAlbum, songBpm, uri, albumUri, durationInSec);
    }

    public Song(File file) {
        FFmpegMediaMetadataRetriever metadataRetriever = new FFmpegMediaMetadataRetriever();
        metadataRetriever.setDataSource(file.getPath());

        setValues(INT_DOES_NOT_EXIST,
                metadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_TITLE),
                metadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST),
                metadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM),
                INT_DOES_NOT_EXIST,
                Uri.parse(file.getPath()),
                URI_DOES_NOT_EXIST,
                Integer.parseInt(metadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_DURATION))/MS_PER_SEC
        );
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private void setValues(long id, String songTitle, String songArtist, String songAlbum, Integer songBpm,
                           Uri uri, Uri albumUri, int durationInSec){
        _id = id;
        _title = songTitle != null && !songTitle.equals("") ? songTitle : getTitleFromUri(uri) ;
        _artist = songArtist != null ? songArtist : "Unknown" ;
        _album = songAlbum != null ? songAlbum : "Unknown" ;
        _bpm = songBpm;
        _songUri = uri;
        _durationInSec = durationInSec;
        _albumUri = albumUri != null ? albumUri : Uri.EMPTY;
    }

    private String getTitleFromUri(Uri uri) {
        if (uri == null || uri.getLastPathSegment() == null) {
            return "Unknown";
        }

        return uri.getLastPathSegment();
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof Song)) {
            return false;
        }

        Song song = (Song) object;

        return _id == song._id;
    }

    @Override
    public int hashCode() {
        return (int) (_id ^ (_id >>> 32));
    }

    public String getDurationInMinAndSec() {
        int minutes = _durationInSec / 60;
        int seconds = _durationInSec % 60;
        String displayedMinutes;
        String displayedSeconds;

        displayedMinutes = (minutes < 10 ? "0" : "") + Integer.toString(minutes);
        displayedSeconds = (seconds < 10 ? "0" : "") + Integer.toString(seconds);

        return displayedMinutes + ":" + displayedSeconds;
    }
    //endregion
}
