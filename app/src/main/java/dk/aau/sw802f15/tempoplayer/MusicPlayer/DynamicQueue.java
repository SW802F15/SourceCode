package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.content.Context;
import android.util.Log;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DynamicQueue {

    ////////////////////////////////////////////////////////////////////////
    //                         Stubs and Drivers                          //
    ////////////////////////////////////////////////////////////////////////
    //region
    private int getCurrentSPM_STUB() { return 110; }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region
    private static DynamicQueue instance = null;
    private static SongDatabase db;
    private List<Song> nextSongs = new ArrayList<Song>();
    private List<Song> prevSongs = new ArrayList<Song>();
    private Song currentSong;
    private int _prevSize = 3;
    private int _lookAheadSize = 2;
    private int _BPMDeviation = 45;
    private int prevSongsSizeBeforeAdd = -1;
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                      Public Shared Resources                       //
    ////////////////////////////////////////////////////////////////////////
    //region

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                             Accessors                              //
    ////////////////////////////////////////////////////////////////////////
    //region
    public Song getCurrentSong() {
        return currentSong;
    }
    public List<Song> getNextSongs() {
        return nextSongs;
    }
    public List<Song> getPrevSongs() {
        return prevSongs;
    }
    public int getPrevSize() {
        return _prevSize;
    }
    public int getPrevSongsSizeBeforeAdd() {
        return prevSongsSizeBeforeAdd;
    }
    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    protected DynamicQueue(){
        //Empty because singleton
    }

    public static DynamicQueue getInstance(Context context){
        if ( instance == null ){
            db = new SongDatabase(context);
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
        if (nextSongs == null || nextSongs.size() == 0) {
            nextSongs = getMatchingSongs(_lookAheadSize, _BPMDeviation);
        }
        if (nextSongs.size() == 0 && prevSongs.size() > 0){
            prevSongs.clear();
            nextSongs = getMatchingSongs(_lookAheadSize, _BPMDeviation);
        }
        return nextSongs.size() != 0;
    }

    private void moveCurrentSongToPrevious() {
        if (currentSong != null){
            prevSongsSizeBeforeAdd = prevSongs.size();
            prevSongs.add(currentSong);
            if (prevSongs.size() > _prevSize){
                prevSongs.remove(0);
            }
        }
    }

    private void updateCurrentSongFromNextSongs() {
        currentSong = nextSongs.get(0);
        nextSongs.remove(0);
        nextSongs.add(getMatchingSongs(1, _BPMDeviation).get(0));
    }

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public boolean prevSongsIsEmpty() {
        return prevSongs.size() == 0;
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
        if (prevSongs.size() == 0) {
            Log.e("selectPrevSong", "No previously played songs.");
            return;
        }

        nextSongs.add(0, currentSong);

        if (nextSongs.size() > _lookAheadSize){
            nextSongs.remove(nextSongs.size()-1);
        }

        currentSong = prevSongs.get(prevSongs.size()-1);
        prevSongs.remove(currentSong);
        prevSongs.remove(null);
    }

    public List<Song> getMatchingSongs(int num, int thresholdBMP){
        if (num < 1 || thresholdBMP < 0){
            Log.d("getMatchingSongs", "Illegal Arguments");
            return new ArrayList<>();
        }

        final int desiredBMP = getCurrentSPM_STUB();
        final List<Song> songs = db.getSongsWithBPM(desiredBMP, thresholdBMP);

        for (Song song : prevSongs){
            if(songs.contains(song)){
                songs.remove(song);
            }
        }
        for (Song song : nextSongs){
            if(songs.contains(song)){
                songs.remove(song);
            }
        }
        if (songs.contains(currentSong)){
            songs.remove(currentSong);
        }

        Collections.shuffle(songs, new Random());
        if (num > songs.size()){
            num = songs.size();
        }
        if (songs.size() == 0 && !prevSongsIsEmpty()){
            final Song song = prevSongs.get(0);
            prevSongs.remove(0);
            return new ArrayList<Song>() {{ add(song); }};
        }
        return songs.subList(0, num);
    }
    //endregion
}

