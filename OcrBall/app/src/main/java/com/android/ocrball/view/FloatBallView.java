package com.android.ocrball.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.ocrball.R;
import com.android.ocrball.manager.MyWindowManager;

/**
 * Created by ckt on 17-2-14.
 */

public class FloatBallView extends FrameLayout {
    private static final String TAG = "FloatBallView";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    WindowManager windowManager;
    WindowManager.LayoutParams mParams;

    View floatball;

    //FloatBall View width and height
    //public static int viewWidth;
    //public static int viewHeight;

    //StatusBar height
    private static int statusBarHeight = 0;

    //tracking finger touch position in screen
    private float xInScreen;
    private float yInScreen;

    //save finger touch down position in screen
    private float xDownInScreen;
    private float yDownInScreen;

    private float xInView;
    private float yInView;

    public FloatBallView(Context context){
        super(context);
        //Resources resources = context.getResources();

        int resourceId= Resources.getSystem().getIdentifier("status_bar_height","dimen","android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        Log.i(TAG,"statusBarHeight = "+ statusBarHeight);
        init(context);
    }

    private void init(Context context){
        if(DEBUG)
            Log.i(TAG,"init");
        windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.floatball_view, this);

        floatball = findViewById(R.id.floatball);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(DEBUG)
            Log.i(TAG,"onTouchEvent event.getAction() = "+ event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                //Record finger press at the time of the data
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - statusBarHeight;
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - statusBarHeight;
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - statusBarHeight;
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                if(xInScreen == xDownInScreen && yInScreen == yDownInScreen){
                    openOcrBallView();
                }
                break;
        }
        return true;
    }

    //update the position of floatballview in widow
    private void updateViewPosition() {
        if(mParams != null) {
            mParams.x = (int) (xInScreen - xInView);
            mParams.y = (int) (yInScreen - yInView);
            if(DEBUG) Log.i(TAG,"updateViewPosition mParams.x = "+ mParams.x + " , mParams.y = "+ mParams.y);
            windowManager.updateViewLayout(this, mParams);
        }
    }

    private void openOcrBallView(){
        if(DEBUG) Log.i(TAG,"openOcrBallView mParams.x = "+ mParams.x + ", mParams.y = "+ mParams.y);
        MyWindowManager.createOcrBallView(getContext());
        MyWindowManager.removeFloatBallView(getContext());
    }

    public WindowManager.LayoutParams getmParams(){
        return mParams;
    }

    public void setParams(WindowManager.LayoutParams params){
        mParams = params;
    }
}
