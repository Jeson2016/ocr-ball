package com.android.ocrball.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.android.ocrball.WelcomeAcivity;

/**
 * Created by admin on 2017/06/14   .
 */

public class OCRSharedPrefsUtil {
    private static final String SETTING = "Setting";

    public static void setOcrPlatForm(Context context, String platForm) {
        Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(OcrConstants.OCR_SETTING_PLATFORM, platForm);
        sp.commit();
    }

    public static String getOcrPlatForm(Context context) {
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = sp.getString(OcrConstants.OCR_SETTING_PLATFORM, OcrConstants.OCR_PLATFORM_TESSERACT);
        return value;
    }

    public static void setOcrLanguage(Context context, String language) {
        Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(OcrConstants.OCR_SETTING_LANGUAGE, language);
        sp.commit();
    }

    public static String getOcrLanguage(Context context) {
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        String value = sp.getString(OcrConstants.OCR_SETTING_LANGUAGE, OcrConstants.OCR_LANGUAGE_ENG);
        return value;
    }
}
