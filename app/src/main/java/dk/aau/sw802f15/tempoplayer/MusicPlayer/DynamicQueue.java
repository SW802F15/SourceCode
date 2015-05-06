package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DynamicQueue {
    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static DynamicQueue instance = null;
    private static SongDatabase _songDatabase;
    private List<Song> _nextSongs = new ArrayList<Song>();
    private List<Song> _prevSongs = new ArrayList<Song>();
    private Song _currentSong;
    private int _prevSize = 3;
    private int _lookAheadSize = 2;
    private int _BPMDeviation = 45;
    private int prevSongsSizeBeforeAdd = -1;
    private static Context _context;
    private int _lastSPM;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public Song getCurrentSong() {
        return _currentSong;
    }
    public List<Song> getNextSongs() {
        return _nextSongs;
    }
    public List<Song> getPrevSongs() {
        return _prevSongs;
    }
    public int getPrevSize() {
        return _prevSize;
    }
    public int getPrevSongsSizeBeforeAdd() { return _prevSongsSizeBeforeAdd; }
    public void setLastSPM(int lastSpm){_lastSPM = lastSpm;};
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected DynamicQueue(){ /*Empty because singleton*/ }

    public static DynamicQueue getInstance(Context context){
        if ( instance == null ){
            _context = context;
            _songDatabase = new SongDatabase(context);
            instance = new DynamicQueue();
        }
        return instance;
    }

    public static void clearInstance(){
        instance = null;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                        Private Functionality                       //
    ////////////////////////////////////////////////////////////////////////
    //region
    private boolean refillQueueWhenEmpty() {
        if (_nextSongs == null || _nextSongs.size() == 0) {
            _nextSongs = getMatchingSongs(_lookAheadSize, _BPMDeviation);
        }
        
        if (_nextSongs.size() == 0 && _prevSongs.size() > 0){
            _prevSongs.clear();
            _nextSongs = getMatchingSongs(_lookAheadSize, _BPMDeviation);
        }

        return _nextSongs.size() != 0;
    }

    private void moveCurrentSongToPrevious() {
        if (_currentSong != null){
            _prevSongsSizeBeforeAdd = _prevSongs.size();
            _prevSongs.add(_currentSong);

            if (_prevSongs.size() > _prevSize){
                _prevSongs.remove(0);
            }
        }
    }

    private void updateCurrentSongFromNextSongs() {
        _currentSong = _nextSongs.get(0);
        _nextSongs.remove(0);
        _nextSongs.add(getMatchingSongs(1, _BPMDeviation).get(0));
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public boolean prevSongsIsEmpty() {
        return _prevSongs.size() == 0;
    }

    final public boolean selectNextSong() {
        if (!refillQueueWhenEmpty()){
            return false;
        }

        moveCurrentSongToPrevious();
        updateCurrentSongFromNextSongs();

        return true;
    }

    public void selectPrevSong() {
        if (_prevSongs.size() == 0) {
            Log.e("selectPrevSong", "No previously played songs.");
            return;
        }

        _nextSongs.add(0, _currentSong);

        if (_nextSongs.size() > _lookAheadSize){
            _nextSongs.remove(_nextSongs.size()-1);
        }

        _currentSong = _prevSongs.get(_prevSongs.size() - 1);
        _prevSongs.remove(_currentSong);
        _prevSongs.remove(null);
    }

    public List<Song> getMatchingSongs(int num, int thresholdBPM){
        if (num < 1 || thresholdBPM < 0){
            Log.d("getMatchingSongs", "Illegal Arguments");
            return new ArrayList<>();
        }

        final int desiredBPM = _lastSPM;
        final List<Song> songs = _songDatabase.getSongsWithBPM(desiredBPM, thresholdBPM);

        removeDuplicateSongs(_prevSongs, songs);
        removeDuplicateSongs(_nextSongs, songs);

        if (songs.contains(_currentSong)){
            songs.remove(_currentSong);
        }

        Collections.shuffle(songs, new Random());

        if (num > songs.size()){
            num = songs.size();
        }

        if (songs.size() == 0 && !prevSongsIsEmpty()){
            final Song song = _prevSongs.get(0);
            _prevSongs.remove(0);
            return new ArrayList<Song>() {{ add(song); }};
        }

        return songs.subList(0, num);
    }

    private void removeDuplicateSongs(List<Song> songList, List<Song> songs){
        for (Song song : songList){
            if(songs.contains(song)){
                songs.remove(song);
            }
        }
    }

    public List<Bitmap> getDynamicQueueAsList() {
        List<Bitmap> allAlbumCovers = new ArrayList<>();

        for (Song song : getPrevSongs())
        {
            allAlbumCovers.add(GUIManager.getInstance(_context).getBitmapFromUri(song.getAlbumUri()));
        }

        allAlbumCovers.add(GUIManager.getInstance(_context).getBitmapFromUri(getCurrentSong().getAlbumUri()));

        for (Song song : getNextSongs())
        {
            allAlbumCovers.add(GUIManager.getInstance(_context).getBitmapFromUri(song.getAlbumUri()));
        }

        return allAlbumCovers;
    }

    //endregion
}

