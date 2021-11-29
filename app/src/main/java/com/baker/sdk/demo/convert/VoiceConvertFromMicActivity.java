package com.baker.sdk.demo.convert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.basecomponent.util.Util;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.util.player.BakerPlayer;
import com.baker.sdk.demo.util.player.PlayerCallBack;
import com.databaker.voiceconvert.VoiceConvertManager;
import com.databaker.voiceconvert.callback.AuthCallback;
import com.databaker.voiceconvert.callback.VoiceConvertCallBack;
import com.jaredrummler.materialspinner.MaterialSpinner;

public class VoiceConvertFromMicActivity extends BakerBaseActivity {
    private final String[] voiceDescArray = new String[]{"变声娇娇", "变声天天", "变声恐龙贝克", "变声乐迪", "变声未眠"};
    private final String[] voiceNameArray = new String[]{"Vc_jiaojiao", "Vc_tiantian", "Vc_baklong", "Vc_ledi", "Vc_weimian"};

    private String currentVoiceName = voiceNameArray[0];

    private SharedPreferences mSharedPreferences;
    private Button btnRecord, btnPlay;
    private boolean isRecording = false;
    private VoiceConvertManager convertManager;
    private static BakerPlayer audioTrackPlayer;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_convert_from_mic);

        setTitle("录音转换");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        initView();

        // 1. 初始化和授权
        convertManager = new VoiceConvertManager();
        convertManager.init(VoiceConvertFromMicActivity.this,
                sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_ID),
                sharedPreferencesGet(Constants.VOICE_CONVERT_CLIENT_SECRET), new AuthCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    private void initView() {
        audioTrackPlayer = new BakerPlayer();
        audioTrackPlayer.setCallBack(playerCallBack);

        TextView tvVoiceName = findViewById(R.id.tvvcn);
        btnRecord = findViewById(R.id.btnRecord);
        btnPlay = findViewById(R.id.btnPlay);

        MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(voiceDescArray);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                currentVoiceName = voiceNameArray[position];
                tvVoiceName.setText("音色点击下面选择：" + voiceDescArray[position]);
                convertManager.setVoiceName(currentVoiceName);
            }
        });
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordConvert();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playResultPcm();
            }
        });
        aSwitch = findViewById(R.id.playInTime);
    }

    private VoiceConvertCallBack callBack = new VoiceConvertCallBack() {
        @Override
        public void onReady() {
            Log.e("VoiceConvertActivity", "onReady()");
        }

        @Override
        public void canSpeech() {
            Log.e("VoiceConvertActivity", "canSpeech()");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isRecording = true;
                    Toast.makeText(VoiceConvertFromMicActivity.this, "请开始说话", Toast.LENGTH_SHORT).show();
                    btnRecord.setText("停止录音");
                    btnRecord.setEnabled(true);
                }
            });
        }

        @Override
        public void onOriginData(byte[] data, int volume) {
            Log.e("VoiceConvertActivity", "onOriginData()");
        }

        @Override
        public void onAudioOutput(byte[] audioArray, boolean isLast, String traceId) {
            Log.e("VoiceConvertActivity", "onAudioOutput(), isLast = " + isLast + ", traceId = " + traceId);
            audioTrackPlayer.setData(audioArray, isLast);

            if (isLast) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnRecord.setEnabled(true);
                        btnRecord.setText("开始录音");
                    }
                });
            }

            if (aSwitch.isChecked()) {
                audioTrackPlayer.play();
            }
        }

        @Override
        public void onError(String errorCode, String errorMessage, String traceId) {
            Log.e("VoiceConvertActivity", "onError()");
            Log.e("VoiceConvertActivity", errorCode + ", " + errorMessage + ", " + traceId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceConvertFromMicActivity.this, errorCode + errorMessage + traceId, Toast.LENGTH_SHORT).show();
                }
            });
            //恢复到初始状态
            resumeState();
        }
    };

    private PlayerCallBack playerCallBack = new PlayerCallBack() {
        @Override
        public void onPlaying() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnPlay.setText("停止播放");
                }
            });
        }

        @Override
        public void onPaused() {

        }

        @Override
        public void onPlayCompleted() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnPlay.setText("开始播放");
                }
            });
        }

        @Override
        public void onStopped() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnPlay.setText("开始播放");
                }
            });
        }

        @Override
        public void onError(String errorCode, String errorMsg) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    btnPlay.setText("开始播放");
                }
            });
        }
    };

    /**
     * 录音转换
     */
    private void recordConvert() {
        //做网络基本检测
        try {
            if (!Util.checkConnectNetwork(VoiceConvertFromMicActivity.this)) {
                Toast.makeText(VoiceConvertFromMicActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isRecording) {
            isRecording = false;
            convertManager.stopRecord();
            btnRecord.setEnabled(false);
            btnRecord.setText("正在转换，请稍等");
        } else {
            //开始录音转换
            btnRecord.setEnabled(false);
            btnRecord.setText("正在连接服务器...");

            //清除掉播放器之前的缓存数据
            audioTrackPlayer.clean();
            convertManager.startFromMic(callBack);
        }
    }

    /**
     * 播放转换之后的效果
     */
    private void playResultPcm() {
        if (btnPlay.getText().equals("停止播放")) {
            //停止播放
            audioTrackPlayer.stop();
            btnPlay.setText("开始播放");
        } else {
            //播放
            audioTrackPlayer.play();
            btnPlay.setText("停止播放");
        }
    }


    private void resumeState() {
        isRecording = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnRecord.setEnabled(true);
                btnRecord.setText("开始录音");
                btnPlay.setText("开始播放");
            }
        });
    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (convertManager != null) {
            convertManager.release();
        }
        audioTrackPlayer.clean();
        audioTrackPlayer.setPlaying(false);
    }
}