package com.android.ocrball;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This service is use to support ocr ball running background
 * Created by admin on 2016/12/07   .
 */
public class OcrBallService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
