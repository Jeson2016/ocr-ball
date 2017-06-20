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
    private static final String[] OCR_TESSERACT_DATA = {
            "chi_sim.traineddata",
            "chi_tra.traineddata",
            "eng.traineddata"
    };

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

        for (String fileName:OCR_TESSERACT_DATA) {
            File tessFile = new File(OcrConstants.TESS_DATA_DIR + fileName);
            if (!tessFile.exists()) {
                try {
                    InputStream is = mContext.getAssets().open(fileName);
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
