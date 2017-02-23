package com.android.ocrball.manager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TabHost;

import com.android.ocrball.view.FloatBallView;
import com.android.ocrball.view.OcrBallView;

/**
 * Created by ckt on 17-2-14.
 */

public class MyWindowManager {
    private static final String TAG = "MyWindowManager";

    private static WindowManager mWindowManager;

    private static OcrBallView mOcrBallView;
    private static WindowManager.LayoutParams mOcrBallParams;
    private static FloatBallView mFloatBallView;
    private static WindowManager.LayoutParams mFloatBallParams;


    public static WindowManager getWindowManager(Context context){
        if(mWindowManager == null)
            mWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        return mWindowManager;
    }

    public static void createFloatBallView(Context context){
        if(mFloatBallView == null){
            WindowManager windowManager = getWindowManager(context);
            mFloatBallView = new FloatBallView(context);
            if(mFloatBallParams == null){

                int screenWidth = windowManager.getDefaultDisplay().getWidth();
                int screenHeight = windowManager.getDefaultDisplay().getHeight();

                mFloatBallParams = new WindowManager.LayoutParams();
                mFloatBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                mFloatBallParams.format = PixelFormat.RGBA_8888;
                mFloatBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mFloatBallParams.gravity = Gravity.LEFT | Gravity.TOP;
                mFloatBallParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mFloatBallParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                mFloatBallParams.x = screenWidth;
                mFloatBallParams.y = screenHeight/2;
            }
            mFloatBallView.setParams(mFloatBallParams);
            mWindowManager.addView(mFloatBallView, mFloatBallParams);
        }
    }

    public static void removeFloatBallView(Context context){
        if(mFloatBallView != null){
            WindowManager windowManger = getWindowManager(context);
            windowManger.removeView(mFloatBallView);
            mFloatBallView = null;
        }
    }

    public static void createOcrBallView(Context context){
        if(mOcrBallView == null){
            WindowManager windowManager = getWindowManager(context);
            mOcrBallView = new OcrBallView(context);
            if(mOcrBallParams == null){
                mOcrBallParams = new WindowManager.LayoutParams();
                mOcrBallParams = new WindowManager.LayoutParams();
                mOcrBallParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                mOcrBallParams.format = PixelFormat.RGBA_8888;
                mOcrBallParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mOcrBallParams.gravity = Gravity.LEFT | Gravity.TOP;
                mOcrBallParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                mOcrBallParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
                if(mFloatBallView != null) {
                    mOcrBallParams.x =mFloatBallView.getmParams().x;
                    mOcrBallParams.y = mFloatBallView.getmParams().y;
                } else {
                    int screenWidth = windowManager.getDefaultDisplay().getWidth();
                    int screenHeight = windowManager.getDefaultDisplay().getHeight();
                    mOcrBallParams.x = screenWidth;
                    mOcrBallParams.y = screenHeight/2;
                }
            }else {
                mOcrBallParams.x =mFloatBallView.getmParams().x;
                mOcrBallParams.y = mFloatBallView.getmParams().y;
            }
            windowManager.addView(mOcrBallView, mOcrBallParams);

        }
    }

    public static void removeOcrBallView(Context context){
        if(mOcrBallView != null){
            WindowManager windowManger = getWindowManager(context);
            windowManger.removeView(mOcrBallView);
            mOcrBallView = null;
        }
    }

    public static boolean isBallViewShowing(){
        return mFloatBallView != null || mOcrBallView != null;
    }
}
