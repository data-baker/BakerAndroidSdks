package com.baker.sdk.demo.convert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.blankj.utilcode.util.NetworkUtils;
import com.databaker.voiceconvert.VoiceConvertManager;
import com.databaker.voiceconvert.callback.AudioOutPutCallback;
import com.databaker.voiceconvert.callback.AuthCallback;
import com.databaker.voiceconvert.callback.ErrorCallback;
import com.databaker.voiceconvert.callback.SpeechCallback;
import com.databaker.voiceconvert.callback.WebSocketOpenCallback;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;

import okio.BufferedSink;
import okio.Okio;

public class VoiceConvertActivity extends BakerBaseActivity {
    private final String[] voiceDescArray = new String[]{"变声娇娇", "变声天天", "变声恐龙贝克", "变声乐迪", "变声未眠"};
    private final String[] voiceNameArray = new String[]{"Vc_jiaojiao", "Vc_tiantian", "Vc_baklong", "Vc_ledi", "Vc_weimian"};

    private String currentVoiceName = voiceNameArray[0];

    private SharedPreferences mSharedPreferences;
    private Button btnRecord, btnFileRecord;
    // 是否使用自己的音频传入，true代表自行录音进行录音转换，false代表使用SDK内部的录音机进行录音转换
    private boolean isUseCustomAudioData = false;
    private BufferedSink bufferedSink = null;
    private String mVCFilePath = "";
    private boolean isRecording = false;
    private boolean isInitSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_convert);
        setTitle("声音转换");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        mVCFilePath = getExternalFilesDir("").getAbsolutePath() + File.separator + "vc.pcm";

        initView();

        // ①.设置授权回调
        VoiceConvertManager.getInstance().auth(VoiceConvertActivity.this,
                sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_ID),
                sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_SECRET), new AuthCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }

//                    @Override
//                    public void onResult(boolean result) {
//                        isInitSuccess = result;
//                    }
                });
        // ②.设置错误回调
        VoiceConvertManager.getInstance().setErrorCallback(new ErrorCallback() {
            @Override
            public void onError(String errorCode, String errorMessage, String traceId) {
                Log.e("VoiceConvertActivity", errorCode + errorMessage + traceId);
                Toast.makeText(VoiceConvertActivity.this, errorCode + errorMessage + traceId, Toast.LENGTH_SHORT).show();
            }
        });

        // ③.设置转换数据回调
        VoiceConvertManager.getInstance().setAudioCallBack(new AudioOutPutCallback() {
            @Override
            public void onAudioOutput(byte[] audioArray, boolean isLast, String traceId) {
                try {
                    if (bufferedSink == null) {
                        File file = new File(mVCFilePath);
                        if (file.exists()) {
                            file.delete();
                        }
                        bufferedSink = Okio.buffer(Okio.sink(file));
                    }
                    bufferedSink.write(audioArray);
                    if (isLast) {
                        bufferedSink.close();
                        bufferedSink = null;
                        startPlay();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView() {
        TextView tvVoiceName = findViewById(R.id.tvvcn);
        btnRecord = findViewById(R.id.btnRecord);
        btnFileRecord = findViewById(R.id.btnFileRecord);

        //是否使用自己的音频传入，true代表自行录音进行录音转换，false代表使用SDK内部的录音机进行录音转换
        SwitchCompat switchCompat = findViewById(R.id.recordSwitch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isUseCustomAudioData = isChecked;
            }
        });

        MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(voiceDescArray);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                currentVoiceName = voiceNameArray[position];
                tvVoiceName.setText("当前音色：" + voiceDescArray[position]);
                VoiceConvertManager.getInstance().setVoiceName(currentVoiceName);
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordConvert();
            }
        });

        btnFileRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileConvert();
            }
        });
    }

    /**
     * 录音转换
     */
    private void recordConvert() {
        if (!NetworkUtils.isAvailable()) {
            Toast.makeText(VoiceConvertActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        if (btnRecord.getText().equals("停止播放")) {
//            audioTrack.stop();
//            audioTrack.release();
//            audioTrack = null;
            btnRecord.setText("开始录音");
            btnFileRecord.setEnabled(true);
            return;
        }

        if (isRecording) {
            isRecording = false;
            VoiceConvertManager.getInstance().stopRecord();
            btnRecord.setEnabled(false);
            btnRecord.setText("正在转换，请稍等");
        } else {
            btnFileRecord.setEnabled(false);
            if (isUseCustomAudioData) {
                //自行实现录音然后往SDK中发送音频数据
                btnRecord.setText("开始录音");

                // ④.当前请求网络连接成功回调
                VoiceConvertManager.getInstance().setWebSocketOnOpen(new WebSocketOpenCallback() {
                    @Override
                    public void onResult(boolean result) {
                        if (result) {
                            btnRecord.setText("停止录音");
                            startRecord();
                        } else {
                            Toast.makeText(VoiceConvertActivity.this, "建立webSocket连接失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                VoiceConvertManager.getInstance().setSaveRecordFile();
                btnRecord.setEnabled(false);
                btnRecord.setText("正在连接服务器...");
                VoiceConvertManager.getInstance().startRecord(new SpeechCallback() {
                    @Override
                    public void canSpeech() {
                        isRecording = true;
                        Toast.makeText(VoiceConvertActivity.this, "请开始说话", Toast.LENGTH_SHORT).show();
                        btnRecord.setText("停止录音");
                        btnRecord.setEnabled(true);
                    }
                });
            }
        }
    }

    /**
     * 文件转换
     */
    private void fileConvert() {
        if (!NetworkUtils.isAvailable()) {
            Toast.makeText(VoiceConvertActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }

        btnRecord.setEnabled(false);
        btnFileRecord.setEnabled(false);
    }

    /**
     * 录音
     */
    private void startRecord() {
        //在子线程运行录音操作
    }

    /**
     * 转换完后播放
     */
    private void startPlay() {

    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }
}