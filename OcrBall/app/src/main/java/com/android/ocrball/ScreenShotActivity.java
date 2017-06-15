package com.android.ocrball;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.ocrball.util.ScreenCapture;
import com.android.ocrball.view.MarkSizeView;
import com.android.ocrball.view.ScreenShotView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static android.R.attr.scaleHeight;
import static android.R.attr.width;
import static android.R.attr.y;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ScreenShotActivity extends Activity{
    private static final String TAG = "ScreenShotActivity";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    //screenshot position
    private int screenshot_x_start;
    private int screenshot_y_start;
    private int screenshot_x_end;
    private int screenshot_y_end;

    private int screenshot_width;
    private int screenshot_height;

    private Bitmap bitmap;
    private ScreenShotView screenShotView;

    private Context context;


    MarkSizeView markSizeView;
    private Rect markedArea;

    private MediaProjectionManager mMediaProjectionManager;
    private ScreenCapture screenCaptureService;

    private static final int REQUEST_MEDIA_PROJECTION = 100;
    private int result;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        } else {
            Toast.makeText(this, R.string.can_not_capture_under_5_0, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_screen_shot);

        mMediaProjectionManager = (MediaProjectionManager)this.getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        markSizeView = (MarkSizeView)findViewById(R.id.mark_size);
        markSizeView.setmOnClickListener(new MarkSizeView.onClickListener() {
            @Override
            public void onConfirm(Rect rect) {
                ScreenShotActivity.this.markedArea = new Rect(rect);
                markSizeView.reset();
                startIntent();
            }

            @Override
            public void onConfirm(MarkSizeView.GraphicPath path) {

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onTouch() {

            }
        });

     /*   screenShotView = new ScreenShotView(this);
        screenShotView.postInvalidate();
        screenShotView.setOnTouchListener(this);
        this.addContentView(screenShotView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));*/
    }

/*    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "onTouch event: "+ event.getAction());
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                screenshot_x_start = (int)event.getX();
                screenshot_y_start = (int)event.getY();
                screenshot_width = 0;
                screenshot_height = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                screenshot_x_end = (int)event.getX();
                screenshot_y_end = (int)event.getY();
                screenShotView.setArea(screenshot_x_start,screenshot_y_start, screenshot_x_end, screenshot_y_end);
                screenShotView.postInvalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(event.getX()>screenshot_x_start){
                    screenshot_width = (int)event.getX() - screenshot_x_start;
                }else {
                    screenshot_width = (int)(screenshot_x_start - event.getX());
                    screenshot_x_start = (int)event.getX();
                }

                if(event.getY()>screenshot_y_start){
                    screenshot_height = (int)event.getY() - screenshot_y_start;
                }else {
                    screenshot_height = (int)(screenshot_y_start - event.getY());
                    screenshot_y_start = (int)event.getY();
                }
                getBitmap(this);
                break;
        }
        return true;
    }*/

    private Bitmap getBitmap(Activity activity){
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        bitmap = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int toHeight = frame.top;
        bitmap = Bitmap.createBitmap(bitmap, screenshot_x_start, screenshot_y_start+2*toHeight, screenshot_width, screenshot_height);
        try {
            FileOutputStream fout = new FileOutputStream("mnt/sdcard/test.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private void startIntent() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivityForResult(mMediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "进入了");
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            } else if (data != null && resultCode != 0) {
                Log.i(TAG, "user agree the application to capture screen");
                result = resultCode;
                intent = data;
                startScreenCapture(data, resultCode);
                Log.i(TAG, "start service ScreenCaptureService");
            }
        }
    }

    private void startScreenCapture(Intent intent, int resultCode) {
        screenCaptureService=new ScreenCapture(this ,intent, resultCode, this.markedArea);
        try {
            screenCaptureService.toCapture();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (screenCaptureService!=null)
            screenCaptureService.onDestroy();
        super.onDestroy();
    }
}
