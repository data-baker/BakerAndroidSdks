package com.baker.sdk.demo.convert;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baker.sdk.basecomponent.util.Util;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.util.AudioTrackPlayer;
import com.databaker.voiceconvert.VoiceConvertManager;
import com.databaker.voiceconvert.callback.AuthCallback;
import com.databaker.voiceconvert.callback.VoiceConvertCallBack;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VoiceConvertFromFileActivity extends BakerBaseActivity {
    private final String[] voiceDescArray = new String[]{"变声娇娇", "变声天天", "变声恐龙贝克", "变声乐迪", "变声未眠"};
    private final String[] voiceNameArray = new String[]{"Vc_jiaojiao", "Vc_tiantian", "Vc_baklong", "Vc_ledi", "Vc_weimian"};

    private String currentVoiceName = voiceNameArray[0];

    private SharedPreferences mSharedPreferences;
    private Button btnFileRecord, btnPlay;
    private VoiceConvertManager convertManager;
    private String filePath;
    private FileOutputStream fileOutputStream;
    private static AudioTrackPlayer audioTrackPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_convert_from_file);

        setTitle("根据文件转换");

        filePath = getCacheDir().getAbsolutePath() + File.separator + "temp.pcm";
        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        initView();

        // 1. 初始化和授权
        convertManager = new VoiceConvertManager();
        convertManager.init(VoiceConvertFromFileActivity.this,
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
        audioTrackPlayer = new AudioTrackPlayer();

        TextView tvVoiceName = findViewById(R.id.tvvcn);
        btnFileRecord = findViewById(R.id.btnFileRecord);
        btnPlay = findViewById(R.id.btnPlay);

        MaterialSpinner spinner = findViewById(R.id.spinner);
        spinner.setItems(voiceDescArray);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                currentVoiceName = voiceNameArray[position];
                tvVoiceName.setText("当前音色：" + voiceDescArray[position]);
                convertManager.setVoiceName(currentVoiceName);
            }
        });
        btnFileRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fileConvert();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playResultPcm();
            }
        });
    }

    private final VoiceConvertCallBack callBack = new VoiceConvertCallBack() {
        @Override
        public void onReady() {
            Log.e("VoiceConvertActivity", "onReady()");
            //3. 当网络连接成功之后，开始传输待转换bytes
            startConvert();
        }

        @Override
        public void canSpeech() {
            Log.e("VoiceConvertActivity", "canSpeech()");
        }

        @Override
        public void onOriginData(byte[] data, int volume) {
            Log.e("VoiceConvertActivity", "onOriginData()");
        }

        @Override
        public void onAudioOutput(byte[] audioArray, boolean isLast, String traceId) {
            Log.e("VoiceConvertActivity", "onAudioOutput(), isLast = " + isLast);
            try {
                //将转换后数据存储起来，供播放
                fileOutputStream.write(audioArray);
                if (isLast) {
                    fileOutputStream.close();
                    //转换完，最后一包之后，开始播放目标音频
                    startPlay();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(String errorCode, String errorMessage, String traceId) {
            Log.e("VoiceConvertActivity", "onError()");
            Log.e("VoiceConvertActivity", errorCode + ", " + errorMessage + ", " + traceId);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(VoiceConvertFromFileActivity.this, errorCode + errorMessage + traceId, Toast.LENGTH_SHORT).show();
                }
            });
            //恢复到初始状态
            resumeState();
        }
    };

    /**
     * 录音转换
     */
    private void fileConvert() {
        //做网络基本检测
        try {
            if (!Util.checkConnectNetwork(VoiceConvertFromFileActivity.this)) {
                Toast.makeText(VoiceConvertFromFileActivity.this, "网络异常，请检查网络", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //开始文件转换
        btnFileRecord.setEnabled(false);
        btnFileRecord.setText("正在连接服务器...");
        btnPlay.setEnabled(false);

        //清除掉播放器之前的缓存数据
        audioTrackPlayer.cleanAudioData();
        try {
            //若返回结果目标文件已存在，清除并重新创建此文件
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            fileOutputStream = new FileOutputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //2. 调用SDK 发起转换
        convertManager.startFromBytes(callBack);
    }

    private void startConvert() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnFileRecord.setText("正在转换，请稍等");
            }
        });
        try {
            File file = new File(filePath);
            // 读取转换之后存储的目标文件，进行播放。
            InputStream inputStream = getAssets().open("record.pcm");
            int read = 0;
            byte[] tempByte;
            while ((read = inputStream.read((tempByte = new byte[5120]), 0, 5120)) > 0) {
                //4. 向sdk传输待转换数据，必须指定5120大小/包，最后一包除外
                convertManager.sendAudio(tempByte, false);
            }
            inputStream.close();
            //5. 最后传输一包空告知，isLast = true
            convertManager.sendAudio(new byte[]{}, true);
        } catch (Exception e) {
            e.printStackTrace();
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
            btnPlay.setEnabled(true);
            btnFileRecord.setEnabled(true);
        } else {
            //播放
            audioTrackPlayer.cleanAudioData();
            startPlay();
        }
    }

    /**
     * 转换完后播放
     */
    private void startPlay() {
        try {
            File file = new File(filePath);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!file.exists()) {
                        Toast.makeText(VoiceConvertFromFileActivity.this, "无音频文件可播放", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    btnFileRecord.setEnabled(false);
                    btnFileRecord.setText("根据文件转换声音");
                    btnPlay.setText("停止播放");
                    btnPlay.setEnabled(true);
                }
            });

            // 读取转换之后存储的目标文件，进行播放。
            FileInputStream inputStream = new FileInputStream(file);
            int read = 0;
            byte[] tempByte;
            while ((read = inputStream.read((tempByte = new byte[1024]), 0, 1024)) > 0) {
                audioTrackPlayer.setAudioData(tempByte);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VoiceConvertFromFileActivity.this, "播放完毕", Toast.LENGTH_SHORT).show();
                btnFileRecord.setEnabled(true);
                btnPlay.setEnabled(true);
                btnPlay.setText("开始播放");
            }
        });
    }

    private void resumeState() {
        runOnUiThread(() -> {
            btnFileRecord.setEnabled(true);
            btnFileRecord.setText("根据文件转换声音");
            btnPlay.setText("开始播放");
            btnPlay.setEnabled(true);
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
        if (convertManager!=null){
            convertManager.release();
        }
        audioTrackPlayer.stop();
        audioTrackPlayer.setPlaying(false);
    }
}