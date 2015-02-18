package com.example.sw802f15.tempoplayer;

import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DynamicQueueTest extends TestCase {

    private DynamicQueue dq;

    public void setUp() throws Exception {
        super.setUp();
         dq = new DynamicQueue();
    }

    public void tearDown() throws Exception {

    }

    //Tests set up of test
    @SmallTest
    public void testPrecondition(){
        assertTrue(true);
    }

    @SmallTest
    public void testGetMatchingSongs(){
        List<Integer> values = new ArrayList<Integer>() {{
            add(Integer.MAX_VALUE);
            add(Integer.MAX_VALUE - 1);
            add(Integer.MIN_VALUE);
            add(Integer.MIN_VALUE + 1);
            add(-1);
            add(0);
            add(1);
            add(3);  //stub data size
            add(3 - 1);
            add(3 + 1);
            add(42);  //stub value
            add(42 - 1);
            add(42 + 1);
        }};

        for(Integer num : values){
            testGetMatchingSongsHelper(num, 10, 42);
        }
        for(Integer thresholdBMP : values){
            testGetMatchingSongsHelper(2, thresholdBMP, 42);
        }
        for(Integer bpm : values){
            testGetMatchingSongsHelper(2, 10, bpm);
        }
    }

    private void testGetMatchingSongsHelper(int num, int thresholdBMP, int bpm) {
        List<Song> result = dq.getMatchingSongs(num, thresholdBMP);

        assertTrue(result != null);
        assertTrue(result.size() <= num || num < 0);

        int prevBPM = bpm;
        for (Song song : result) {
            assertTrue(song != null);
            assertTrue(Math.abs(song.getBpm() - bpm) >= prevBPM - bpm);
            prevBPM = song.getBpm();
        }
    }


}