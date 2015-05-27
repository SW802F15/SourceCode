package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;
import dk.aau.sw802f15.tempoplayer.StepCounter.StepCounterService;

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
    private int _prevSongsSizeBeforeAdd = -1;
    private static Context _context;
    private static int THRESHOLD_BPM_INCREMENTOR = 5;
    private static int DEFAULT_BPM_WITHOUT_SPM = 110;
    private static int SINGLE_SONG = 1;
    private int _lastSPM = 0;
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
    public int getPrevSongsSizeBeforeAdd() {
        return _prevSongsSizeBeforeAdd;
    }

    public void setLastSPM(int lastSpm){
        _lastSPM = lastSpm;
    }
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
        _nextSongs.add(getMatchingSongs(SINGLE_SONG, _BPMDeviation).get(0));
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
            _nextSongs.remove(_nextSongs.size() - 1);
        }

        _currentSong = _prevSongs.get(_prevSongs.size() - 1);
        _prevSongs.remove(_currentSong);
        _prevSongs.remove(null);
    }

    public List<Song> getMatchingSongs(int num, int thresholdBPM){
        if (num < 1 || num > 10000 || thresholdBPM < 0 || thresholdBPM > 10000){
            Log.d("getMatchingSongs", "Illegal Arguments");
            return new ArrayList<>();
        }
        if (_songDatabase.isEmpty()) {
            return new ArrayList<>();
        }

        final List<Song> songs = getSongsFromDB(_lastSPM, thresholdBPM);

        removeInvalidSongFromQueue(songs);


        int i = 0;
        while (songs.size() < _prevSize + _lookAheadSize && i <= 100) {
            thresholdBPM += THRESHOLD_BPM_INCREMENTOR;
            songs.addAll(getSongsFromDB(_lastSPM, thresholdBPM));
            removeInvalidSongFromQueue(songs);
            i++;
        }


        Collections.shuffle(songs, new Random());

        if (num > songs.size()){
            num = songs.size();
        }

        if (songs.size() == 0 && !prevSongsIsEmpty()){
            final Song song = _prevSongs.get(0);
            _prevSongs.remove(0);
            return new ArrayList<Song>() {
                { add(song); }
            };
        }

        return songs.subList(0, num);
    }

    private void removeInvalidSongFromQueue(List<Song> songs) {
        removeDuplicateSongs(_prevSongs, songs);
        removeDuplicateSongs(_nextSongs, songs);

        if (songs.contains(_currentSong)){
            songs.remove(_currentSong);
        }
    }

    private List<Song> getSongsFromDB(int spm, int thresholdBPM) {
        final List<Song> songs;

        if(_lastSPM == 0) {
            songs = _songDatabase.getSongsWithBPM(DEFAULT_BPM_WITHOUT_SPM, thresholdBPM, 0);
        }else{
            songs = _songDatabase.getSongsWithBPM(spm, thresholdBPM, 0);
        }

        return songs;
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

