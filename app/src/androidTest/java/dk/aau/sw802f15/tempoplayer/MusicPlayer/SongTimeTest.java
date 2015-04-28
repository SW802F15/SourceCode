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
            SongTime songTimeMax = new SongTime(Integer.MAX_VALUE + 1);
            SongTime songTimeMin = new SongTime(Integer.MIN_VALUE - 1);
            Assert.fail("songTime object should not be able to be created with over/underflowed value");
        }catch (IndexOutOfBoundsException e){
            assertTrue(true);
        }

    }
}
