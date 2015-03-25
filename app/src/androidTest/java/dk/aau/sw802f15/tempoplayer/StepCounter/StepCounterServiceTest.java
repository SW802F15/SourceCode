package dk.aau.sw802f15.tempoplayer.StepCounter;

import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class StepCounterServiceTest extends ServiceTestCase<StepCounterService> {

    /**
     * Constructor
     */
    public StepCounterServiceTest() {
        super(StepCounterService.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testGetSPM() {

        assertEquals(getService().getSpm(), 100);

    }

}