package com.android.ocrball;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.ocrball.view.OcrBallView;

import java.lang.ref.SoftReference;

/**
 * This service is use to support ocr ball running background
 * Created by admin on 2016/12/07   .
 */
public class OcrBallService extends Service{

    private static final String TAG = "OcrBallService";
    private static final boolean DEBUG = false || Log.isLoggable(TAG, Log.DEBUG);

    private WindowManager windowManager;
    private OcrBallView view;
    WindowManager.LayoutParams params;

    //the point with touch move
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    @Override
    public IBinder onBind(Intent intent) {
        if(DEBUG) Log.i(TAG,"onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DEBUG) Log.i(TAG,"onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(DEBUG) Log.i(TAG,"onCreate");
        windowManager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);

        createView();
    }

    private void createView(){
        if(DEBUG) Log.i(TAG,"createView");

        view = (OcrBallView)LayoutInflater.from(this).inflate(R.layout.ocrball_view, null);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, 0, 0,
                PixelFormat.TRANSPARENT);
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.gravity = Gravity.TOP | Gravity.LEFT;

        params.x = 0;
        params.y = 0;

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY() + view.getHeight() / 2;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }

                return true;
            }
        });

        windowManager.addView(view,params);
    }

    //update the position of ocrballview in widow
    private void updateViewPosition() {
        params.x = (int) (x - mTouchStartX);
        params.y = (int) (y - mTouchStartY);
        if(DEBUG)Log.i(TAG,"updateViewPosition params.x = "+ params.x + " , params.y = "+ params.y);
        windowManager.updateViewLayout(view, params);
    }


    private final IBinder mBinder = new ServiceStub(this);

    static class ServiceStub extends IOcrBallService.Stub{
        //changing weak ref to softref to prevent media playercrash
        SoftReference<OcrBallService> mService;

        ServiceStub(OcrBallService service) {
            mService = new SoftReference<OcrBallService>(service);
        }

        public void createView(){
            mService.get().createView();
        }
    }

}
