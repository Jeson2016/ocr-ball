package com.android.ocrball.util;

import android.os.Environment;

/**
 * Created by admin on 2017/06/06   .
 */

public class OcrConstants {
    public static final String TESS_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OcrBall/";
    public static final String TESS_DATA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "OcrBall" + "/" + "tessdata" + "/";
    public static final String TESS_DATA_FILE_ENG = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "OcrBall" + "/" + "tessdata" + "/" + "eng.traineddata";
    public static final String TESS_DATA_FILE_CH_S = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "OcrBall" + "/" + "tessdata" + "/" + "chi_sim.traineddata";
    public static final String TESS_DATA_FILE_CH_T = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "OcrBall" + "/" + "tessdata" + "/" + "chi_tra.traineddata";
    public static final String TESS_ASSETS_FILE_ENG = "eng.traineddata";
    public static final String TESS_ASSETS_FILE_CH_S = "chi_sim.traineddata";
    public static final String TESS_ASSETS_FILE_CH_T = "chi_tra.traineddata";

    public static final String OCR_SETTING_PLATFORM = "platform";
    public static final String OCR_SETTING_LANGUAGE = "language";

    public static final String OCR_LANGUAGE_ENG = "eng";

    public static final int OCR_INT_ZERO = 0;
    public static final int OCR_INT_ONE = 1;
    public static final int OCR_INT_TWO = 2;
    public static final int OCR_REQUEST_PICK_PHOTO = 500;

    public static final String PLATFORM_TRAINEDDATA = "traineddata";
    public static final int PLATFORM_TRAINEDDATA_VALUE = 0;
    public static final String PLATFORM_BAIDU = "baidu";
    public static final int PLATFORM_BAIDU_VALUE = 1;
    public static final String PLATFORM_GOOGLE = "google";
    public static final int PLATFORM_GOOGLE_VALUE = 2;

    public static final String OCR_RESULT = "result";
    public static final String OCR_BAIDU_TOKEN = "24.6d2eb8a73ed14037958fd2c882a5d806.2592000.1500197762.282335-9774268";

    public static final int MSG_GET_RESULT_FROM_URI = 10000;
    public static final int MSG_GET_RESULT_FROM_BITMAP = MSG_GET_RESULT_FROM_URI+1;
    public static final int MSG_UPDATE_RESULT = MSG_GET_RESULT_FROM_URI+2;
    public static final int MSG_COPY_SUCCESS = MSG_GET_RESULT_FROM_URI+3;
    public static final int MSG_COPY_FAIL = MSG_GET_RESULT_FROM_URI+4;
}
