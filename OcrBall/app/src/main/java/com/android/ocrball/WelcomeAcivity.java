package com.android.ocrball;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeAcivity extends Activity {
    private static final String TAG = "OcrWelcome";

    private LinearLayout mOcrResaultLayout;
    private TextView mOcrResaultContent;
    private TextView mOcrPrompt;
    private Button mSelectButton;
    private Button mActiveButton;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_acivity);

        init();
    }

    private void init() {
        mOcrResaultLayout = (LinearLayout) this.findViewById(R.id.ocr_resault_layout);
        mOcrResaultContent = (TextView) this.findViewById(R.id.ocr_resault_textview);
        mOcrPrompt = (TextView) this.findViewById(R.id.default_prompt_textview);
        mSelectButton = (Button) this.findViewById(R.id.select_button);
        mActiveButton = (Button) this.findViewById(R.id.active_button);

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
