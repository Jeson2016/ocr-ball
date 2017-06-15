package com.android.ocrball.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

/**
 * Created by ckt on 17-2-22.
 */

public class ScreenShotView extends View {
    private static final String TAG = "ScreenShotView";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    //count the touch position
    private int x_down;
    private int y_down;
    private int x_up;
    private int y_up;

    private boolean canscreenshot;
    private Paint paint; // the select area paint

    public ScreenShotView(Context context){
        super(context);
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG,"onDraw canscreenshot = "+ canscreenshot);
        if(canscreenshot){
            paint.setColor(Color.TRANSPARENT);
        } else {
            paint.setColor(Color.RED);
            paint.setAlpha(80);
            canvas.drawRect(new Rect(x_down,y_down, x_up, y_up), paint);
        }
        super.onDraw(canvas);
    }

    public void setArea(int x_down,int y_down, int x_up, int y_up){
        Log.i(TAG, "setArea x_down = "+ x_down + ", y_down = "+ y_down + ", x_up = "+ x_up + ", y_up = "+ y_up);
        this.x_down = x_down;
        this.y_down = y_down;
        this.x_up = x_up;
        this.y_up = y_up;
    }

    public boolean isCanscreenshot() {
        return canscreenshot;
    }

    public void setCanscreenshot(boolean canscreenshot) {
        this.canscreenshot = canscreenshot;
    }
}
