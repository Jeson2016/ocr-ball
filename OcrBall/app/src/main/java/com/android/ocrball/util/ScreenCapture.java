package com.android.ocrball.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.ocrball.R;
import com.android.ocrball.ScreenShotActivity;
import com.android.ocrball.WelcomeAcivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;

/**
 * Created by ckt on 17-6-21.
 */

public class ScreenCapture {
    private static final String TAG = "ScreenCapture";
    public static final String MESSAGE = "message";
    public static final String FILE_NAME = "temp_file";

    public static final int CreateSuccess = 100;
    public static final int CreateFail = 10;

    private SimpleDateFormat dateFormat = null;
    private String strDate = null;
    private String pathImage = null;
    private String nameImage = null;

    private MediaProjection mMediaProjection = null;
    private VirtualDisplay mVirtualDisplay = null;

    public static int mResultCode = 0;
    public static Intent mResultData = null;
    public static MediaProjectionManager mMediaProjectionManager = null;

    private WindowManager mWindowManager = null;
    private int windowWidth = 0;
    private int windowHeight = 0;
    private ImageReader mImageReader = null;
    private DisplayMetrics metrics = null;
    private int mScreenDensity = 0;
    private int mScreenWidth = 0;

    Handler handler = new Handler(Looper.getMainLooper());
    private Rect mRect;
    private ScreenShotActivity activity;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public ScreenCapture(ScreenShotActivity activity, Intent intent, int resultCode
            , Rect rect){
        this.activity=activity;
        mResultData=intent;
        mResultCode=resultCode;
        this.mRect = rect;
        Log.i(TAG,"before mRect.top = "+ mRect.top + ", mRect.left = "+ mRect.left + ", mRect.bottom = "+ mRect.bottom + ", mRect.right = "+ mRect.right);
        this.mScreenWidth = ViewUtil.getScreenWidth(activity);
        try {
            createVirtualEnvironment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void toCapture() {
        try {

            handler.postDelayed(new Runnable() {
                public void run() {
                    Log.d(TAG, "before startVirtual");
                    startVirtual();
                    Log.d(TAG, "after startVirtual");
                }
            }, 10);

            handler.postDelayed(new Runnable() {
                public void run() {
                    //capture the screen
                    try {
                        Log.d(TAG, "before startCapture");
                        startCapture();
                        Log.d(TAG, "after startCapture");
                    } catch (Exception e) {
                        e.printStackTrace();
                        sendBroadcastCaptureFail();
                    }
                }
            }, 100);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Error e) {
            e.printStackTrace();
        }
    }

    private void sendBroadcastCaptureFail() {
        Toast.makeText(activity, R.string.screen_capture_fail, Toast.LENGTH_SHORT);
        activity.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createVirtualEnvironment() {
        dateFormat = new SimpleDateFormat("yyyy_MM_dd_hh_mm_ss");
        strDate = dateFormat.format(new java.util.Date());
        pathImage = Environment.getExternalStorageDirectory().getPath() + "/Pictures/";
        nameImage = pathImage + strDate + ".png";
        mMediaProjectionManager = (MediaProjectionManager) activity.getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        mWindowManager = (WindowManager) activity.getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowWidth = ViewUtil.getScreenWidth(activity);
        windowHeight = ViewUtil.getSceenHeight(activity);
        metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mImageReader = ImageReader.newInstance(windowWidth, windowHeight, 0x1, 2); //ImageFormat.RGB_565

        Log.d(TAG, "prepared the virtual environment");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startVirtual() {
        if (mMediaProjection != null) {
            Log.d(TAG, "want to display virtual");
            virtualDisplay();
        } else {
            Log.d(TAG, "want to build mediaprojection and display virtual");
            setUpMediaProjection();

            virtualDisplay();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setUpMediaProjection() {
        try {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(mResultCode, mResultData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "mMediaProjection defined");
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void virtualDisplay() {
        try {
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                    windowWidth, windowHeight, mScreenDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mImageReader.getSurface(), null, null);
            Log.d(TAG, "virtual displayed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startCapture() throws Exception {
        strDate = dateFormat.format(new java.util.Date());
        nameImage = pathImage + strDate + ".png";

        Image image = mImageReader.acquireLatestImage();

        if (image==null){
            Log.d(TAG, "image==null,restart");
            handler.post(new Runnable() {
                @Override
                public void run() {
                    toCapture();
                }
            });
            return;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        final Image.Plane[] planes = image.getPlanes();
        final ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * width;
        Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        image.close();
        Log.d(TAG, "image data captured");

        if (width!=mScreenWidth ||rowPadding !=0){
            int[] pixel=new int[width + rowPadding / pixelStride];
            bitmap.getPixels(pixel,0,width + rowPadding / pixelStride,0,0,width + rowPadding / pixelStride,1);
            int leftPadding=0;
            int rightPadding=width + rowPadding / pixelStride;
            for (int i=0;i<pixel.length;i++){
                if (pixel[i]!=0){
                    leftPadding=i;
                    break;
                }
            }
            for (int i=pixel.length-1;i>=0;i--){
                if (pixel[i]!=0){
                    rightPadding=i;
                    break;
                }
            }
            width=Math.min(width,mScreenWidth);
            if (rightPadding-leftPadding>width){
                rightPadding= width;
            }
            bitmap=Bitmap.createBitmap(bitmap,leftPadding, 0, rightPadding-leftPadding, height);
        }

        Log.d(TAG, "bitmap cuted first");
        if (mRect != null) {

            if (mRect.left < 0)
                mRect.left = 0;
            if (mRect.right < 0)
                mRect.right = 0;
            if (mRect.top < 0)
                mRect.top = 0;
            if (mRect.bottom < 0)
                mRect.bottom = 0;
            int cut_width = Math.abs(mRect.left - mRect.right);
            int cut_height = Math.abs(mRect.top - mRect.bottom);
            Log.d(TAG, "mRect.left = "+ mRect.left + ", mRect.right = "+ mRect.right + ", mRect.top = "+mRect.top + ", mRect.bottom = "+ mRect.bottom);
            Log.i(TAG, "cut_width = "+ cut_width + ", cut_height = "+ cut_height);
            if (cut_width > 0 && cut_height > 0) {
                Bitmap cutBitmap = Bitmap.createBitmap(bitmap, mRect.left, mRect.top, cut_width, cut_height);
                Log.d(TAG, "bitmap cuted second");

                saveCutBitmap(cutBitmap);
            }
        } else {
            saveCutBitmap(bitmap);
        }
        bitmap.recycle();//自由选择是否进行回收
    }

    private void saveCutBitmap(Bitmap cutBitmap) {
        Log.d(TAG,"saveCutBitmap");
        File localFile = new File(activity.getFilesDir(), "temp.png");
        String fileName = localFile.getAbsolutePath();
        Log.d(TAG,"image file: "+ fileName);
        try {
            if (!localFile.exists()) {
                localFile.createNewFile();
                Log.d(TAG,"image file created: "+ fileName);
            }
            FileOutputStream fileOutputStream = new FileOutputStream(localFile);
            if (fileOutputStream != null) {
                cutBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        } catch (IOException e) {
            sendBroadcastCaptureFail();
            return;
        }
        Intent newIntent = new Intent(activity, WelcomeAcivity.class);
        newIntent.putExtra(ScreenCapture.MESSAGE, ScreenCapture.CreateSuccess);
        newIntent.putExtra(ScreenCapture.FILE_NAME,fileName );
        activity.startActivity(newIntent);
        activity.finish();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        Log.d(TAG, "mMediaProjection undefined");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
        Log.d(TAG, "virtual display stopped");
    }

    public void onDestroy() {
        stopVirtual();
        tearDownMediaProjection();
        Log.d(TAG, "application destroy");
    }


    public static int getScreenWidth(Activity activity){
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }
}