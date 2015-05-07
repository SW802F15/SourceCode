package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import dk.aau.sw802f15.tempoplayer.DataAccessLayer.Song;
import dk.aau.sw802f15.tempoplayer.DataAccessLayer.SongDatabase;
import dk.aau.sw802f15.tempoplayer.TestHelper;

import java.util.ArrayList;
import java.util.List;

public class DynamicQueueTest extends AndroidTestCase {

    private DynamicQueue dq;

    public void setUp() throws Exception {
        super.setUp();
        DynamicQueue.clearInstance();
        dq = DynamicQueue.getInstance(getContext());
        TestHelper.initializeTestSongs(new SongDatabase(getContext()));
    }

    public void tearDown() throws Exception {

    }

    //Tests set up of test
    @SmallTest
    public void testPrecondition(){
        assertTrue(true);
    }

    @MediumTest
    public void testGetMatchingSongs(){
        List<Integer> values = new ArrayList<Integer>() {{
            add(Integer.MAX_VALUE);
            add(Integer.MAX_VALUE - 1);
            add(Integer.MIN_VALUE);
            add(Integer.MIN_VALUE + 1);
            add(-1);
            add(0);
            add(1);
            add(5);  //stub data size
            add(5 - 1);
            add(5 + 1);
            add(42);  //stub value
            add(42 - 1);
            add(42 + 1);
        }};

        for(Integer num : values){
            testGetMatchingSongsHelper(num, 45);
        }
        for(Integer thresholdBMP : values){
            testGetMatchingSongsHelper(80, thresholdBMP);
        }
    }

    private void testGetMatchingSongsHelper(int num, int thresholdBMP) {
        List<Song> result = dq.getMatchingSongs(num, thresholdBMP);

        assertTrue(result != null);
        assertTrue(result.size() <= num || num < 0);

        for (Song song : result) {
            assertTrue(song != null);
        }
    }

    @MediumTest
    public void testSelectNextSong(){
        //DynamicQueue.getInstance(getContext()).setLastSPM(100);
        dq.selectNextSong();
        Song formerSong = dq.getCurrentSong();
        dq.selectNextSong();
        Song newSong = dq.getCurrentSong();

        assertTrue(formerSong.getID() != newSong.getID());
        assertTrue(newSong.getID() == dq.getCurrentSong().getID());

        List<Song> nextSongs = dq.getNextSongs();
        assertFalse(nextSongs.contains(formerSong));
        assertFalse(nextSongs.contains(newSong));

        List<Song> prevSongs = dq.getPrevSongs();
        assertTrue(prevSongs.contains(formerSong));
        assertFalse(prevSongs.contains(newSong));
        assertTrue(prevSongs.size() == 1);
    }
    @MediumTest
    public void testSelectPrevSong(){
        //DynamicQueue.getInstance(getContext()).setLastSPM(100);
        dq.selectNextSong();
        dq.selectNextSong();
        Song formerSong = dq.getCurrentSong();
        dq.selectPrevSong();
        Song newSong = dq.getCurrentSong();

        assertTrue(formerSong.getID() != newSong.getID());
        assertTrue(newSong.getID() == dq.getCurrentSong().getID());

        List<Song> nextSongs = dq.getNextSongs();
        assertTrue(nextSongs.contains(formerSong));
        assertFalse(nextSongs.contains(newSong));

        List<Song> prevSongs = dq.getPrevSongs();
        assertFalse(prevSongs.contains(formerSong));
        assertFalse(prevSongs.contains(newSong));
        assertTrue(prevSongs.size() == 0);
    }
}