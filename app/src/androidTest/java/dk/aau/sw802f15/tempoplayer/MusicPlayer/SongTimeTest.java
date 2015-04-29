package dk.aau.sw802f15.tempoplayer.MusicPlayer;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

/**
 * Created by Avalon on 28-04-2015.
 */
public class SongTimeTest extends AndroidTestCase {
    public void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testNegativeDuration(){
        try {
            SongTime songTime = new SongTime(-1);
            Assert.fail("songTime object should not be able to be created with value less than 0");
        }catch (IndexOutOfBoundsException e){
            assertTrue(true);
        }

    }

    @SmallTest
    public void testOutOfBoundDuration(){
        try {
            SongTime songTimeMax = new SongTime(Integer.MAX_VALUE);
            SongTime songTimeMin = new SongTime(Integer.MIN_VALUE);
            Assert.fail("songTime object should not be able to be created with over/underflowed value");
        }catch (IndexOutOfBoundsException e){
            assertTrue(true);
        }

    }

    @SmallTest
    public void testValidSongTime(){
        try {
            SongTime songTimeLow = new SongTime(136);
            String formattedSongTimeLow = songTimeLow.getFormattedSongTime();

            SongTime songTimeHigh = new SongTime(3600);
            String formattedSongTimeHigh = songTimeHigh.getFormattedSongTime();

            if(!formattedSongTimeLow.equals("02:16")){
                Assert.fail("Proper time returned the wrong formatted time");
            }

            if(!formattedSongTimeHigh.equals("01:00:00")){
                Assert.fail("Proper time returned the wrong formatted time");
            }

            assertTrue(true);
        }catch (IndexOutOfBoundsException e){
            Assert.fail();
        }

    }
}
