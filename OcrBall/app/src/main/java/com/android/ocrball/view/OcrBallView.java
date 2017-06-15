package com.android.ocrball.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.android.ocrball.OcrBallService;
import com.android.ocrball.R;
import com.android.ocrball.ScreenShotActivity;
import com.android.ocrball.manager.MyWindowManager;

/**
 * Created by ckt on 17-1-11.
 */

public class OcrBallView extends LinearLayout {
    private static final String TAG = "OcrBallView";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    private ImageButton mScreenShotButton;
    private ImageButton mBackButton;

    public OcrBallView(Context context){
        super(context);
        LayoutInflater.from(context).inflate(R.layout.ocrball_view, this);
        mScreenShotButton = (ImageButton)this.findViewById(R.id.screenshot_btn);
        mBackButton = (ImageButton)this.findViewById(R.id.back_btn);

        mScreenShotButton.setOnClickListener(mScreenShotClickListener);
        mBackButton.setOnClickListener(mBackClickListener);
    }

    View.OnClickListener mScreenShotClickListener = new View.OnClickListener (){

        @Override
        public void onClick(View v) {
            if(DEBUG)
              Log.i(TAG, "ScreenShotButton Click");
            Intent intent = new Intent(v.getContext(), ScreenShotActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
            MyWindowManager.removeOcrBallView(getContext());

        }
    };


    View.OnClickListener mBackClickListener = new View.OnClickListener (){

        @Override
        public void onClick(View v) {
            if(DEBUG)
                Log.i(TAG, "BackButton Click");
            MyWindowManager.removeOcrBallView(getContext());
            MyWindowManager.createFloatBallView(getContext());
        }
    };
}
