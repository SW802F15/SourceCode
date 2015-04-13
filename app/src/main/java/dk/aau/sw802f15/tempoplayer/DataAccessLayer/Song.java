package dk.aau.sw802f15.tempoplayer.DataAccessLayer;

import android.media.MediaMetadataRetriever;
import android.net.Uri;

import java.io.File;

/**
 * Created by Draegert on 16-02-2015.
 */
public class Song {
    private static final Uri DEFAULTALBUMPATH = Uri.parse("android.resource" + "://" +
                                                          "com." + "example." + "sw802f15." + "tempoplayer" +
                                                          "/" + "drawable" + "/" + "defaultalbumcover.png");
    private final Integer _bpmStub = 100;
    private final Uri _albumStub = null;

    private static final int MS_PER_SEC = 1000;

    private long _id;
    private String _title;
    private  String _artist;
    private String _album;
    private Integer _bpm;
    private Uri _uri;
    private int _durationInSec;
    private Uri _albumUri;

    public Song(long songId, String songTitle, String songArtist, String songAlbum, Integer songBpm,
                Uri uri, Uri albumUri, int durationInSec) {
        setValues(songId, songTitle, songArtist, songAlbum, songBpm,
                uri, albumUri, durationInSec);
    }

    public Song(String songTitle, String songArtist, String songAlbum, Integer songBpm, Uri uri,
                Uri albumUri, int durationInSec){
        setValues(-1, songTitle, songArtist, songAlbum, songBpm,
                uri, albumUri, durationInSec);
    }

    public Song(File file) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getPath());

        setValues(-1,
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST),
                mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM),
                _bpmStub,
                Uri.fromFile(file),
                _albumStub,
                Integer.parseInt(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))/MS_PER_SEC
        );
    }

    private void setValues(long id, String songTitle, String songArtist, String songAlbum, Integer songBpm,
                           Uri uri, Uri albumUri, int durationInSec){
        _id = id;
        _title = songTitle != null ? songTitle : getUriAsTitle(uri) ;
        _artist = songArtist != null ? songArtist : "Unknown" ;
        _album = songAlbum != null ? songAlbum : "Unknown" ;
        _bpm = songBpm;
        _uri = uri;
        _durationInSec = durationInSec;
        _albumUri = albumUri != null ? albumUri : DEFAULTALBUMPATH;
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

    public void setBpm(int bpm){
        this._bpm = bpm;
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

    public String getDurationInMinAndSec() {
        int durationOriginal = getDurationInSec();

        String leftSide, rightSide;

        int right = (durationOriginal % 60);
        int left = (int)Math.floor((double)durationOriginal / 60);

        if(right < 10){
            rightSide = "0" + right;
        }else{
            rightSide = "" + right;
        }

        if(left < 10){
            leftSide = "0" + left;
        }else{
            leftSide = "" + left;
        }

        return leftSide + ":" + rightSide;
    }

    private String getUriAsTitle(Uri uri) {
        String title = uri.getLastPathSegment();
        if (title == null) {
            return "Unknown";
        }
        return title;
    }
}
