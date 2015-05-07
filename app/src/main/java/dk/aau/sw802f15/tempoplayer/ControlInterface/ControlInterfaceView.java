package dk.aau.sw802f15.tempoplayer.ControlInterface;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;

import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Map;



public class ControlInterfaceView extends View implements GestureDetector.OnGestureListener {


    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region

    public GestureDetectorCompat detector;
    private TapCounter tapCounter;
    private static final String TAG = "ControlInterface";

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                            Constructors                            //
    ////////////////////////////////////////////////////////////////////////
    //region
    public ControlInterfaceView(Context context) {
        super(context);
        tapCounter = new TapCounter();
        detector = new GestureDetectorCompat(context, this);
        setBackground(new ColorDrawable());
    }

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown");
        tapCounter.increment();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG, "onLongPress");
        tapCounter.clear();
    }
    //endregion

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
