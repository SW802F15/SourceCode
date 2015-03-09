package dk.aau.sw802f15.tempoplayer.Settings;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import dk.aau.sw802f15.tempoplayer.Settings.SettingsActivity;


public class SettingsActivityTest extends ActivityInstrumentationTestCase2<SettingsActivity> {

    public SettingsActivityTest(){
        super(SettingsActivity.class);
    }

    @Override
    protected void setUp() throws Exception
    {

    }

    @SmallTest
    public void testPrecondition(){
        assertTrue(true);
    }




}