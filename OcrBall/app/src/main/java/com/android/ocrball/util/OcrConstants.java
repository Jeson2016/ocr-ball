package com.android.ocrball.util;

import android.Manifest;
import android.os.Environment;

/**
 * Created by admin on 2017/06/06   .
 */

public class OcrConstants {
    public static final String TESS_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OcrBall/";
    public static final String TESS_DATA_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "OcrBall" + "/" + "tessdata" + "/";
    public static final String TESS_TEMP_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/OcrBall/tempFile";

    public static final String OCR_SETTING_PLATFORM = "platform";
    public static final String OCR_SETTING_LANGUAGE = "language";

    public static final String OCR_PLATFORM_TESSERACT = "Tesseract";
    public static final String OCR_PLATFORM_BAIDU = "Baidu";
    public static final String OCR_PLATFORM_GOOGLE = "Google";

    public static final String OCR_LANGUAGE_ENG = "eng";
    public static final String OCR_LANGUAGE_CHI_S = "chi_sim";
    public static final String OCR_LANGUAGE_CHI_T = "chi_tra";

    public static final int OCR_REQUEST_PICK_PHOTO = 500;
    public static final int OCR_REQUEST_PERMISSIONS = 501;

    public static final String OCR_RESULT = "result";
    public static final String OCR_BAIDU_TOKEN = "24.6d2eb8a73ed14037958fd2c882a5d806.2592000.1500197762.282335-9774268";

    //public static final int MSG_GET_RESULT_FROM_URI = 10000;
    //public static final int MSG_GET_RESULT_FROM_BITMAP = MSG_GET_RESULT_FROM_URI+1;
    //public static final int MSG_UPDATE_RESULT = MSG_GET_RESULT_FROM_URI+2;
    public static final int MSG_UPDATE_RESULT = 10001;

    public static final String[] OCR_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
}
