package com.android.ocrball.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.googlecode.leptonica.android.Pixa;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by admin on 2017/06/05   .
 */

public class OcrTrainedDataExecuteAsyncTask extends AsyncTask {
    private static final String TAG = "OcrTrainedDataExecuteAsyncTask";
    private Context mContext;
    private Handler mHandler;
    private Uri mUri;
    private Bitmap mBitmap;
    private File mFileBitmap;
    private TessBaseAPI mTess;

    public OcrTrainedDataExecuteAsyncTask(Context context, Handler handler, Uri uri, Bitmap bitmap) {
        mContext = context;
        mHandler = handler;
        mUri = uri;
        mBitmap = bitmap;

        mTess = new TessBaseAPI();
        mTess.init(OcrConstants.TESS_DIR, OCRSharedPrefsUtil.getOcrLanguage(context), TessBaseAPI.OEM_TESSERACT_ONLY);
    }


    public OcrTrainedDataExecuteAsyncTask(Context context, Handler handler, File bitmap) {
        mContext = context;
        mHandler = handler;
        mFileBitmap = bitmap;

        mTess = new TessBaseAPI();
        mTess.init(OcrConstants.TESS_DIR, OCRSharedPrefsUtil.getOcrLanguage(context), TessBaseAPI.OEM_TESSERACT_ONLY);
    }
    @Override
    protected Object doInBackground(Object[] params) {
        if (null != mUri) {
            Log.i(TAG, "doInBackground mUri = "+ mUri);
            InputStream is = null;
            try {
                is = mContext.getContentResolver().openInputStream(mUri);
                mBitmap = BitmapFactory.decodeStream(is);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (null != mBitmap) {
            Log.i(TAG, "doInBackground mBitmap = "+ mBitmap);
            mTess.setImage(mBitmap);
            String ocrResult = mTess.getUTF8Text();
            Message m = new Message();
            Bundle data = new Bundle();
            data.putString(OcrConstants.OCR_RESULT, ocrResult);
            m.what = OcrConstants.MSG_UPDATE_RESULT;
            m.setData(data);
            mHandler.sendMessage(m);
        }


        if (null != mFileBitmap) {
            Log.i(TAG, "doInBackground mFileBitmap = "+ mFileBitmap);
            mTess.setImage(mFileBitmap);
            String ocrResult = mTess.getUTF8Text();
            Message m = new Message();
            Bundle data = new Bundle();
            data.putString(OcrConstants.OCR_RESULT, ocrResult);
            m.what = OcrConstants.MSG_UPDATE_RESULT;
            m.setData(data);
            mHandler.sendMessage(m);
        }

        if (null != mTess) {
            Log.i(TAG, "doInBackground mTess = "+ mTess);
            mTess.end();
        }

        return null;
    }
}
