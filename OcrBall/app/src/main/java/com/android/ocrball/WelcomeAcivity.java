package com.android.ocrball;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.android.ocrball.util.OCRSharedPrefsUtil;
import com.android.ocrball.util.OcrConstants;
import com.android.ocrball.util.OcrController;
import com.android.ocrball.util.ScreenCapture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
    private boolean mShouldBeginInit = true;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : OcrConstants.OCR_PERMISSIONS) {
                int i = ContextCompat.checkSelfPermission(WelcomeAcivity.this, permission);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    mShouldBeginInit = false;
                    ActivityCompat.requestPermissions(WelcomeAcivity.this, OcrConstants.OCR_PERMISSIONS,
                            OcrConstants.OCR_REQUEST_PERMISSIONS);
                    break;
                }
            }
        }

        if (mShouldBeginInit) {
            init();

            Intent intent = getIntent();
            if (null != intent) {
                String filename = intent.getStringExtra(ScreenCapture.FILE_NAME);
                int status = intent.getIntExtra(ScreenCapture.MESSAGE, ScreenCapture.CreateFail);
                if (ScreenCapture.CreateSuccess == status && !filename.isEmpty()) {
                    Log.i(TAG, "onCreate: filename = " + filename + ", status = " + status);
                    //  filename = "/sdcard/Pictures/Screenshots/11.png";
                    //  filename = "/data/user/0/com.android.ocrball/files/11.png";
                    File file = new File(filename);
                    Bitmap bitmap = getBitmapFromFileName(filename);
                    if (null != bitmap && null != file) {
                        Log.i(TAG, "onCreate: updateUiFromBitmap  & getOcrResultFile");
                        updateUiFromBitmap(bitmap);
                        mController.getOcrResultFile(WelcomeAcivity.this, mHandler, file);
                    }
                }
            }
        }
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
        super.onActivityResult(requestCode, resultCode, intent);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == OcrConstants.OCR_REQUEST_PERMISSIONS) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        WelcomeAcivity.this.finish();
                    }
                }
                mShouldBeginInit = true;
                init();
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

    private void updateUiFromBitmap(Bitmap bitmap) {
        if(null != bitmap){
            mOcrPhoto.setImageBitmap(bitmap);
        }
        mOcrPrompt.setVisibility(View.INVISIBLE);
        mOcrResaultLayout.setVisibility(View.VISIBLE);
        mOcrResaultContent.setText("");
        mOcrProgressBar.setVisibility(View.VISIBLE);
    }

    private Bitmap getBitmapFromFileName(String filename){
        Bitmap bitmap = null;
        if (!filename.isEmpty()) {
            bitmap = BitmapFactory.decodeFile(filename);
        }
        return bitmap;
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
