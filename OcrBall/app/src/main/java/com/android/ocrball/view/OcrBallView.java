package com.android.ocrball.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.android.ocrball.R;

/**
 * Created by ckt on 17-1-11.
 */

public class OcrBallView extends FrameLayout {
    private static final String TAG = "OcrBallView";
    private static final boolean DEBUG = false || Log.isLoggable(TAG, Log.DEBUG);

    private View mOcrball;

    public OcrBallView(Context context){
        super(context);
    }

    public OcrBallView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init(){
        if(DEBUG) Log.i(TAG,"init");
        mOcrball = this.findViewById(R.id.ocrball);
        //mOcrball.setOnClickListener(mOcrballClickListener);
    }

/*    View.OnClickListener mOcrballClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(DEBUG) Log.i(TAG,"mOcrballClickListener onClick");
        }
    };*/
}
