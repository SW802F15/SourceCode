package dk.aau.sw802f15.tempoplayer.ControlInterface;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;

import android.util.Log;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import dk.aau.sw802f15.tempoplayer.MusicPlayerGUI.GUIManager;


public class ControlInterfaceView extends View implements GestureDetector.OnGestureListener {


    ////////////////////////////////////////////////////////////////////////
    //                      Private Shared Resources                      //
    ////////////////////////////////////////////////////////////////////////
    //region

    private GestureDetectorCompat detector;
    private TapCounter tapCounter;
    private static final String TAG = "ControlInterface";
    private boolean active;
    private WindowManager windowManager;

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
        windowManager = (WindowManager) context.getApplicationContext()
                                .getSystemService(Activity.WINDOW_SERVICE);
    }

    //endregion

    ////////////////////////////////////////////////////////////////////////
    //                  Public Functionality - Interface                  //
    ////////////////////////////////////////////////////////////////////////
    //region
    public void addWindow() {
        if (active){
            return;
        }
        active = true;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.OPAQUE);

        windowManager.addView(this, params);
    }

    public boolean removeWindow() {
        if (!active){
            return false;
        }
        active = false;
        windowManager.removeView(this);
        return true;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        detector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i(TAG, "onDown");
        tapCounter.increment();
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i(TAG, "onLongPress");
        GUIManager.getInstance(getContext()).findStopButton().callOnClick();
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
