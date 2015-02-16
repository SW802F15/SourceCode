package com.example.sw802f15.tempoplayer;

import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

//REMEMBER TO MOVE 'Test' TO END OF CLASS NAME.
public class TestMusicPlayerService extends ServiceTestCase
{
    /**
     * Constructor
     *
     */
    public TestMusicPlayerService() {
        super(MusicPlayerService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        String pathToMp3File = "";
    }

    //Tests set up of test
    @SmallTest
    public void testPrecondition(){}




}
