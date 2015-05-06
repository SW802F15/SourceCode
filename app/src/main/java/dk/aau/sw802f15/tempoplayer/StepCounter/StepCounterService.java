package dk.aau.sw802f15.tempoplayer.StepCounter;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

import java.util.Arrays;
import java.util.Collections;

import dk.aau.sw802f15.tempoplayer.MusicPlayer.DynamicQueue;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.Initializers;
import dk.aau.sw802f15.tempoplayer.MusicPlayer.MusicPlayerActivity;
import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;

public class StepCounterService extends Service implements SensorEventListener {

    private static final int MIN_MS_BETWEEN_STEPS = 200;
    private static final double SENSITIVITY = 2.;
    private static final int ACCEL_DATA_SIZE = 50;
    private static final int SPM_AVG_SIZE = 10;
    private static final double MS_PER_MIN = 60 * 1000;

    private Double[] accelerometerData = new Double[ACCEL_DATA_SIZE];
    private int accelerometerIndex = ACCEL_DATA_SIZE; //essentially 0 but avoids negative indexes


    private int[] spm = new int[SPM_AVG_SIZE];
    private int spmIndex = SPM_AVG_SIZE;


    private final IBinder mBinder = new LocalBinder();
    SensorManager senSensorManager;
    Sensor senAccelerometer;
    private long lastTime;
    private long lastStep;
    private MusicPlayerActivity musicPlayerActivity;

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public StepCounterService getService() {
            // Return this instance of LocalService so clients can call public methods
            return StepCounterService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_GAME);

        Arrays.fill(accelerometerData, 0.);

        musicPlayerActivity = MusicPlayerActivity.getInstance();
        return mBinder;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        Sensor mySensor = event.sensor;

        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER && curTime > lastTime + 20){
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            accelerometerData[accelerometerIndex % ACCEL_DATA_SIZE] =
                    Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

            if(isStepTaken(curTime)){
                double timeDiff = (double)(curTime - lastStep);
                spm[spmIndex % SPM_AVG_SIZE] = (int)(1. / ( timeDiff / MS_PER_MIN));
                GUIManager.getInstance(musicPlayerActivity).setSPMText(getSpm());

                lastStep = curTime;
            }
            else if(curTime - 2000 > lastStep){
                spm[spmIndex % SPM_AVG_SIZE] = 0;
                GUIManager.getInstance(musicPlayerActivity).setSPMText(getSpm());
            }
            spmIndex++;
            accelerometerIndex++;
            //bound on indexes
            if (spmIndex == SPM_AVG_SIZE * 2) {
                spmIndex = SPM_AVG_SIZE;
            }
            if (accelerometerIndex == ACCEL_DATA_SIZE * 2){
                accelerometerIndex = ACCEL_DATA_SIZE;
            }
        }
        lastTime = curTime;
    }

    private boolean isStepTaken(long curTime) {
        double min = Collections.min(Arrays.asList(accelerometerData));
        double diff = Collections.max(Arrays.asList(accelerometerData)) - min;
        double threshold = min + (diff / 2);
        return accelerometerData[accelerometerIndex % ACCEL_DATA_SIZE] < threshold &&
               accelerometerData[(accelerometerIndex - 1) % ACCEL_DATA_SIZE] > threshold &&
               MIN_MS_BETWEEN_STEPS < curTime - lastStep &&
               diff > SENSITIVITY;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getSpm() {
        int sum = 0;

        for(int n : spm){
            sum += n;
        }

        return sum / SPM_AVG_SIZE;
    }
}
