package com.example.sw802f15.tempoplayer;

import android.test.InstrumentationTestCase;

public class TestMusicPlayerService extends InstrumentationTestCase
{
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}
