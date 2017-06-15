package com.android.ocrball.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

/**
 * Created by ckt on 17-6-21.
 */

public class ViewUtil {

    public static boolean isNavigationBarShow(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y!=size.y;
        }else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if(menu || back) {
                return false;
            }else {
                return true;
            }
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)){
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }

    public static int getScreenWidth(Activity activity){
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }



    public static int getSceenHeight(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getHeight()+getNavigationBarHeight(activity);
    }
}
