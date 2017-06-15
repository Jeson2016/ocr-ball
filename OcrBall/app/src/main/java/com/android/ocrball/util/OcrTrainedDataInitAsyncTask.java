package com.android.ocrball.util;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class OcrTrainedDataInitAsyncTask extends AsyncTask {

    private Context mContext;
    private Handler mHandler;

    public OcrTrainedDataInitAsyncTask(Context context, Handler handler){
        mContext = context;
        mHandler = handler;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        File tessDataDir = new File(OcrConstants.TESS_DATA_DIR);
        if (!tessDataDir.exists()) {
            tessDataDir.mkdirs();
        }
        File tessFile = new File(OcrConstants.TESS_DATA_FILE_ENG);
        String tessFileName = OcrConstants.TESS_ASSETS_FILE_ENG;
        for(int i=0;i<3;i++) {
            switch (i) {
                case OcrConstants.OCR_INT_ONE:
                    tessFile = new File(OcrConstants.TESS_DATA_FILE_CH_S);
                    tessFileName = OcrConstants.TESS_ASSETS_FILE_CH_S;
                    break;
                case OcrConstants.OCR_INT_TWO:
                    tessFile = new File(OcrConstants.TESS_DATA_FILE_CH_T);
                    tessFileName = OcrConstants.TESS_ASSETS_FILE_CH_T;
                    break;
                default:
                    // nothing to do
                    break;
            }

            if (tessDataDir.exists() && !tessFile.exists()) {
                try {
                    InputStream is = mContext.getAssets().open(tessFileName);
                    FileOutputStream fos = new FileOutputStream(tessFile);
                    byte[] buffer = new byte[1024];
                    int byteCount = 0;
                    while ((byteCount = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, byteCount);
                    }
                    fos.flush();
                    is.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
