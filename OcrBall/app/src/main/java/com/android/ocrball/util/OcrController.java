package com.android.ocrball.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;


/**
 * This class due to deal with all ocr request
 */
public class OcrController {
    private Handler mActivityHandler;
    private Context mContext;

    public OcrController(Context context, Handler handler){
        mContext = context;
        mActivityHandler = handler;

        if (OCRSharedPrefsUtil.getOcrPlatForm(mContext) == OcrConstants.PLATFORM_TRAINEDDATA_VALUE) {
            new OcrTrainedDataInitAsyncTask(mContext, mActivityHandler).execute();
        }
    }

    public void initTrainedData() {
        new OcrTrainedDataInitAsyncTask(mContext, mActivityHandler).execute();
    }

    public void getOcrResultByUri(Context context, Handler handler, Uri uri) {
        switch (OCRSharedPrefsUtil.getOcrPlatForm(context)) {
            case OcrConstants.PLATFORM_TRAINEDDATA_VALUE:
                new OcrTrainedDataExecuteAsyncTask(context, handler, uri, null).execute();
                break;
            case OcrConstants.PLATFORM_BAIDU_VALUE:
                break;
            case OcrConstants.PLATFORM_GOOGLE_VALUE:
                break;
            default:
                // no thing to do
                break;
        }
    }

    public void getOcrResultByBitmap(Bitmap bitmap, Context context, Handler handler) {

    }
}
