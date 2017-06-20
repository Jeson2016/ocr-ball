package com.android.ocrball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.ocrball.util.OCRSharedPrefsUtil;
import com.android.ocrball.util.OcrConstants;
import com.android.ocrball.util.OcrController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class WelcomeAcivity extends AppCompatActivity {
    private static final String TAG = "OcrWelcome";
    private static final boolean DEBUG = true || Log.isLoggable(TAG, Log.DEBUG);

    private OcrController mController;

    private ImageView mOcrPhoto;
    private LinearLayout mOcrResaultLayout;
    private TextView mOcrResaultContent;
    private TextView mOcrPrompt;
    private ProgressBar mOcrProgressBar;
    private Button mCameraScanButton;
    private Button mSelectButton;
    private Button mActiveButton;
    private AlertDialog mPlatFormSelectDialog;
    private AlertDialog mLanguageSelectDialog;
    private MenuItem mPlatFormItem;
    private MenuItem mLanguageItem;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mOcrProgressBar.setVisibility(View.INVISIBLE);
            mOcrResaultContent.setText(msg.getData().getString(OcrConstants.OCR_RESULT));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_acivity);

        init();
    }

    private void init() {
        mController = new OcrController(WelcomeAcivity.this, mHandler);

        mOcrPhoto = (ImageView) this.findViewById(R.id.ocr_photo_view);
        mOcrResaultLayout = (LinearLayout) this.findViewById(R.id.ocr_resault_layout);
        mOcrResaultContent = (TextView) this.findViewById(R.id.ocr_resault_textview);
        mOcrPrompt = (TextView) this.findViewById(R.id.default_prompt_textview);
        mOcrProgressBar = (ProgressBar) this.findViewById(R.id.ocr_progressBar);
        mSelectButton = (Button) this.findViewById(R.id.select_button);
        mActiveButton = (Button) this.findViewById(R.id.active_button);

        mSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, OcrConstants.OCR_REQUEST_PICK_PHOTO);
            }
        });

        mActiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOcrBallService();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mPlatFormItem = menu.findItem(R.id.action_menu_platform);
        mLanguageItem = menu.findItem(R.id.action_menu_language);

        mPlatFormItem.setTitle(WelcomeAcivity.this.getResources().getString(
                R.string.action_menu_platform_title,
                OCRSharedPrefsUtil.getOcrPlatForm(WelcomeAcivity.this)
        ));
        mLanguageItem.setTitle(WelcomeAcivity.this.getResources().getString(
                R.string.action_menu_language_title,
                OCRSharedPrefsUtil.getOcrLanguage(WelcomeAcivity.this)
        ));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_platform:
                showPlatFormSelectDialog();
                break;
            case R.id.action_menu_language:
                showLanguageSelectDialog();
                break;
            default:
                //no thing to do
                break;
        }
        return true;
    }

    private void showPlatFormSelectDialog() {
        final RadioGroup radiosGroup = (RadioGroup) LayoutInflater.from(WelcomeAcivity.this)
                .inflate(R.layout.platform_select_option, null);
        final RadioButton tButton = (RadioButton) radiosGroup.findViewById(R.id.radio_platform_td);
        final RadioButton bButton = (RadioButton) radiosGroup.findViewById(R.id.radio_platform_bt);
        final RadioButton gButton = (RadioButton) radiosGroup.findViewById(R.id.radio_platform_google);
        gButton.setClickable(false);

         switch (OCRSharedPrefsUtil.getOcrPlatForm(WelcomeAcivity.this)) {
            case OcrConstants.OCR_PLATFORM_TESSERACT:
                tButton.setChecked(true);
                mLanguageItem.setVisible(false);
                break;
            case OcrConstants.OCR_PLATFORM_BAIDU:
                bButton.setChecked(true);
                mLanguageItem.setVisible(true);
                break;
            case OcrConstants.OCR_PLATFORM_GOOGLE:
                gButton.setChecked(true);
                break;
            default:
                // no thing to do
                break;
        }

        radiosGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (tButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrPlatForm(WelcomeAcivity.this,
                            OcrConstants.OCR_PLATFORM_TESSERACT);
                    mController.initTrainedData();
                }
                if (bButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrPlatForm(WelcomeAcivity.this,
                            OcrConstants.OCR_PLATFORM_BAIDU);
                }
                if (gButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrPlatForm(WelcomeAcivity.this,
                            OcrConstants.OCR_PLATFORM_GOOGLE);
                }
                if (null!=mPlatFormSelectDialog && mPlatFormSelectDialog.isShowing()) {
                    mPlatFormSelectDialog.dismiss();
                }
                mPlatFormItem.setTitle(WelcomeAcivity.this.getResources().getString(
                        R.string.action_menu_platform_title,
                        OCRSharedPrefsUtil.getOcrPlatForm(WelcomeAcivity.this)
                ));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeAcivity.this);
        builder.setTitle(R.string.platform_dialog_title);
        builder.setView(radiosGroup);
        mPlatFormSelectDialog = builder.create();
        mPlatFormSelectDialog.show();
    }

    private void showLanguageSelectDialog() {
        final RadioGroup radiosGroup = (RadioGroup) LayoutInflater.from(WelcomeAcivity.this)
                .inflate(R.layout.language_select_option, null);
        final RadioButton eButton = (RadioButton) radiosGroup.findViewById(R.id.radio_language_eng);
        final RadioButton sButton = (RadioButton) radiosGroup.findViewById(R.id.radio_language_chi_sim);
        final RadioButton tButton = (RadioButton) radiosGroup.findViewById(R.id.radio_language_chi_tra);
        sButton.setClickable(false);
        tButton.setClickable(false);

        switch (OCRSharedPrefsUtil.getOcrLanguage(WelcomeAcivity.this)) {
            case OcrConstants.OCR_LANGUAGE_ENG:
                eButton.setChecked(true);
                break;
            case OcrConstants.OCR_LANGUAGE_CHI_S:
                sButton.setChecked(true);
                break;
            case OcrConstants.OCR_LANGUAGE_CHI_T:
                tButton.setChecked(true);
                break;
            default:
                // no thing to do
                break;
        }

        radiosGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (eButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrLanguage(WelcomeAcivity.this,
                            OcrConstants.OCR_LANGUAGE_ENG);
                }
                if (sButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrLanguage(WelcomeAcivity.this,
                            OcrConstants.OCR_LANGUAGE_CHI_S);
                }
                if (tButton.getId() == checkedId) {
                    OCRSharedPrefsUtil.setOcrLanguage(WelcomeAcivity.this,
                            OcrConstants.OCR_LANGUAGE_CHI_T);
                }
                if (null!=mLanguageSelectDialog && mLanguageSelectDialog.isShowing()) {
                    mLanguageSelectDialog.dismiss();
                }
                mLanguageItem.setTitle(WelcomeAcivity.this.getResources().getString(
                        R.string.action_menu_language_title,
                        OCRSharedPrefsUtil.getOcrLanguage(WelcomeAcivity.this)
                ));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(WelcomeAcivity.this);
        builder.setTitle(R.string.language_dialog_title);
        builder.setView(radiosGroup);
        mLanguageSelectDialog = builder.create();
        mLanguageSelectDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if ((requestCode == OcrConstants.OCR_REQUEST_PICK_PHOTO)
                && (resultCode == Activity.RESULT_OK)) {
            if (null != intent) {
                Uri photoUri = (Uri) intent.getData();
                if (null != photoUri) {
                    updateUiFromUri(photoUri);
                    mController.getOcrResult(WelcomeAcivity.this, mHandler, photoUri, null);
                }
            }
        }
    }

    private void updateUiFromUri(Uri photoUri) {
        if (null != photoUri) {
            InputStream is = null;
            try {
                is = WelcomeAcivity.this.getContentResolver().openInputStream(photoUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                mOcrPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        mOcrPrompt.setVisibility(View.INVISIBLE);
        mOcrResaultLayout.setVisibility(View.VISIBLE);
        mOcrResaultContent.setText("");
        mOcrProgressBar.setVisibility(View.VISIBLE);
    }

    private void startOcrBallService(){
        this.startService(new Intent(this, OcrBallService.class));
        this.finish();
    }

    private final ServiceConnection mOcrBallService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) Log.v(TAG, "*** OcrBal connected (yay!)");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) Log.v(TAG, "*** Keyguard disconnected (boo!)");
        }
    };
}
