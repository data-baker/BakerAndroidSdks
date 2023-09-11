package com.baker.sdk.demo.gramophone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.baker.engrave.lib.BakerVoiceEngraver;
import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.callback.ContentTextCallback;
import com.baker.engrave.lib.callback.DetectCallback;
import com.baker.engrave.lib.configuration.EngraverType;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.gramophone.fragment.ContinueDialogFragment;
import com.baker.sdk.demo.gramophone.fragment.TipDialogFragment;
import com.baker.sdk.demo.gramophone.util.PreferenceUtil;
import com.baker.sdk.demo.gramophone.util.SharedPreferencesUtil;
import com.blankj.utilcode.util.ToastUtils;

import java.util.List;


// 心即理,心外无物 - 知行合一 - 致良知 - 大智慧


public class DbDetectionActivity extends BakerBaseActivity implements DetectCallback {
    private final String TAG = "DbDetectionActivity";
    private int BTN_TYPE = 0; //0=默认状态，检测未通过。1=检测通过，可以复刻。
    private Button btnStartEngrave;
    private TextView tvDetectValue, tvDetectTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_detection);
        setTitle(R.string.string_step_1);
        initView();
        initCallBack();
        BakerVoiceEngraver.getInstance().setDetectCallback(this);
    }

    private void initView() {
        btnStartEngrave = findViewById(R.id.engrave_start);
        btnStartEngrave.setOnClickListener(this);
        tvDetectValue = findViewById(R.id.tv_db_value);
        tvDetectTip = findViewById(R.id.tv_result);


        //QueryId的作用是xxx，非常建议设置，如果在初始化时已经填写了QueryId，此方法不必重复设置。
        BakerVoiceEngraver.getInstance().setRecordSessionId(PreferenceUtil.getString(PreferenceUtil.getEngraverKey(), ""));
        BakerVoiceEngraver.getInstance().setQueryId(SharedPreferencesUtil.getQueryId(this));//如果要上传QueryID，请务必在调用getVoiceMouldId()方法之前调用。
        BakerVoiceEngraver.getInstance().getSessionIdAndTexts(); //获取，不知道获取的啥
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.engrave_start) {
            //0=默认状态，检测未通过。1=检测通过，可以复刻。
            if (BTN_TYPE == 0) {
                //返回结果=1，开启检测成功。否则开启检测失败，最有可能的原因是没有麦克风、写SD卡权限。
                int resultCode = BakerVoiceEngraver.getInstance().startDBDetection();
                if (resultCode != 1) {
                    Log.d(TAG, "开启失败");
                } else {
                    btnStartEngrave.setEnabled(false);
                    tvDetectTip.setText("环境噪音检测中，请稍候...");
                }
            } else {
                if (currentIndex == 0) {
                    startActivity(new Intent(DbDetectionActivity.this, EngraveActivity.class));
                    finish();
                } else {
                    ContinueDialogFragment dialog = new ContinueDialogFragment();
                    dialog.setCancelListener(() -> {
                        BakerVoiceEngraver.getInstance().recordInterrupt();
                        PreferenceUtil.putString(PreferenceUtil.getEngraverKey(), "");
                        BakerVoiceEngraver.getInstance().setRecordSessionId("");
                        BakerVoiceEngraver.getInstance().getSessionIdAndTexts();
                    });
                    dialog.show(getSupportFragmentManager(), "");
                }

            }
        }
    }

    int currentIndex;
    boolean isFirst = false;

    public void initCallBack() {
        //获取文本内容
        BakerVoiceEngraver.getInstance().setContentTextCallback(new ContentTextCallback() {
            @Override
            public void contentTextList(List<RecordResult> mRecordList, String sessionId, int index) {
                currentIndex = index;
                runOnUiThread(() -> {
                    isFirst = !isFirst;
                    if (!TextUtils.isEmpty(sessionId)) {
                        if (!isFirst) {
                            startActivity(new Intent(DbDetectionActivity.this, EngraveActivity.class));
                            finish();
                        } else {
                            String originSessionId = PreferenceUtil.getString(PreferenceUtil.getEngraverKey(), "");
                            if (!TextUtils.isEmpty(originSessionId) && !sessionId.equals(originSessionId)) {
                                TipDialogFragment dialog = new TipDialogFragment();
                                dialog.show(getSupportFragmentManager(), "");
                            }
                        }
                        PreferenceUtil.putString(PreferenceUtil.getEngraverKey(), sessionId);
                    }

                });
            }

            @Override
            public void onContentTextError(int errorCode, String message) {
                runOnUiThread(() -> {
                    Toast.makeText(DbDetectionActivity.this, message + "", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onContentTextError errorCode = " + errorCode + " message = " + message);
                });
            }
        });
    }

    @Override
    public void dbDetecting(int value) {
        tvDetectValue.setText(value + " db");
    }

    @Override
    public void dbDetectionResult(boolean result, int value) {
        btnStartEngrave.setEnabled(true);
        tvDetectValue.setText(value + " db");
        if (value > 70) {
            tvDetectTip.setText("环境噪音检测不通过，请换安静环境再试哦。");
        } else if (value > 50) {
            tvDetectTip.setText("环境很一般，在更安静的环境下效果更佳哦");
        } else {
            tvDetectTip.setText("环境很安静，开始去复刻吧");
        }
        if (result) {
            BTN_TYPE = 1;
            btnStartEngrave.setText("开始复刻");
        } else {
            BTN_TYPE = 0;
            btnStartEngrave.setText("重新检测");
        }
    }

    @Override
    public void onDetectError(final int errorCode, final String message) {
        runOnUiThread(() -> {
            if (errorCode == 90013) {
                //因音频焦点丢失或电话等异常中断检测
                btnStartEngrave.setEnabled(true);
                tvDetectValue.setText("0 db");
                BTN_TYPE = 0;
                tvDetectTip.setText("检测中断啦，请再试一次吧");
                btnStartEngrave.setText("重新检测");
            } else {
                Toast.makeText(DbDetectionActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBack() {
        finish();
    }
}