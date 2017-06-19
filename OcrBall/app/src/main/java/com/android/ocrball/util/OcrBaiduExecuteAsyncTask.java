package com.android.ocrball.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.WordSimple;

import java.io.File;

/**
 * Created by admin on 2017/06/19   .
 */

public class OcrBaiduExecuteAsyncTask extends AsyncTask {
    private Context mContext;
    private Handler mHandler;
    private Uri mUri;
    private GeneralBasicParams mParams;
    private StringBuilder mResult;

    public OcrBaiduExecuteAsyncTask(Context context, Handler handler, Uri uri) {
        mContext = context;
        mHandler = handler;
        mUri = uri;
        mResult = new StringBuilder();
        mParams = new GeneralBasicParams();
        mParams.setDetectDirection(true);
        OCR.getInstance().initWithToken(mContext, OcrConstants.OCR_BAIDU_TOKEN);
    }

    @Override
    protected Object doInBackground(Object[] params) {
        mParams.setImageFile(getFileByUri());
        OCR.getInstance().recognizeGeneralBasic(mParams, new OnResultListener<GeneralResult>() {
            @Override
            public void onResult(GeneralResult result) {
                for (WordSimple wordSimple : result.getWordList()) {
                    WordSimple word = wordSimple;
                    mResult.append(word.getWords());
                    mResult.append("\n");
                }

                Message m = new Message();
                Bundle data = new Bundle();
                data.putString(OcrConstants.OCR_RESULT, mResult.toString());
                m.what = OcrConstants.MSG_UPDATE_RESULT;
                m.setData(data);
                mHandler.sendMessage(m);
            }
            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError对象
            }
        });
        return null;
    }

    private File getFileByUri() {
        String FilePath = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = mContext.getContentResolver().query(mUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            FilePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return new File(FilePath);
    }
}