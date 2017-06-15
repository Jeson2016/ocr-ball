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
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.ocrball.manager.MyWindowManager;
import com.android.ocrball.view.OcrBallView;

import java.lang.ref.SoftReference;

/**
 * This service is use to support ocr ball running background
 * Created by admin on 2016/12/07   .
 */
public class OcrBallService extends Service{

    private static final String TAG = "OcrBallService";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    @Override
    public IBinder onBind(Intent intent) {
        if(DEBUG) Log.i(TAG,"onBind");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(DEBUG) Log.i(TAG,"onStartCommand");
        createView();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(DEBUG) Log.i(TAG,"onCreate");
    }

    private void createView(){
        if(DEBUG) Log.i(TAG,"createView");
        MyWindowManager.createFloatBallView(getApplicationContext());
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
