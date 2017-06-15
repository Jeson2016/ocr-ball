package com.android.ocrball.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import java.io.File;


/**
 * This class due to deal with all ocr request
 */
public class OcrController {
    private Handler mActivityHandler;
    private Context mContext;

    public OcrController(Context context, Handler handler){
        mContext = context;
        mActivityHandler = handler;

        if (OCRSharedPrefsUtil.getOcrPlatForm(mContext).equals(OcrConstants.OCR_PLATFORM_TESSERACT)) {
            new OcrTrainedDataInitAsyncTask(mContext, mActivityHandler).execute();
        }
    }

    public void initTrainedData() {
        new OcrTrainedDataInitAsyncTask(mContext, mActivityHandler).execute();
    }

    public void getOcrResult(Context context, Handler handler, Uri uri, Bitmap bitmap) {
        switch (OCRSharedPrefsUtil.getOcrPlatForm(context)) {
            case OcrConstants.OCR_PLATFORM_TESSERACT:
                new OcrTrainedDataExecuteAsyncTask(context, handler, uri, bitmap).execute();
                break;
            case OcrConstants.OCR_PLATFORM_BAIDU:
                new OcrBaiduExecuteAsyncTask(context, handler, uri, bitmap).execute();
                break;
            case OcrConstants.OCR_PLATFORM_GOOGLE:
                break;
            default:
                // no thing to do
                break;
        }
    }

    public void getOcrResultFile(Context context, Handler handler, File bitmap) {
        switch (OCRSharedPrefsUtil.getOcrPlatForm(context)) {
            case OcrConstants.OCR_PLATFORM_TESSERACT:
                new OcrTrainedDataExecuteAsyncTask(context, handler, bitmap).execute();
                break;
            case OcrConstants.OCR_PLATFORM_BAIDU:
                new OcrBaiduExecuteAsyncTask(context, handler, bitmap).execute();
                break;
            case OcrConstants.OCR_PLATFORM_GOOGLE:
                break;
            default:
                // no thing to do
                break;
        }
    }
}
