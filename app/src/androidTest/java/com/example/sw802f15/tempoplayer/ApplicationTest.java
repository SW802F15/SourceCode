package com.example.sw802f15.tempoplayer;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.test.InstrumentationTestCase;

/**
 TEST METHODS MUST BE PREFIXED WITH test....
 */
public class ApplicationTest extends InstrumentationTestCase
{
    public void test() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }
}