package com.baker.sdk.demo.tts;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.bean.BakerError;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.baker.sdk.demo.util.AudioTrackPlayer;
import com.databaker.synthesizer.BakerCallback;
import com.databaker.synthesizer.BakerSynthesizer;

public class AudioTrackPlayerActivity extends BakerBaseActivity {
    private BakerSynthesizer bakerSynthesizer;
    private static AudioTrackPlayer audioTrackPlayer;
    private EditText editText;
    private SharedPreferences mSharedPreferences;

    private String[] contents = {
            "设计和模型建立：根据客户需求和AI教字人形象的特点，进行展示柜的设计和镇型建立工作。确定展示柜的尺寸、形状和材质，以及AI教字人的姿态和动作",
            "透明屏幕选择和安装：选择适合的透明屏幕，并进行安装到展示柜的透明面板位置。透明屏幕通常由厂商提供，根据展示柜的尺寸和需求进行定制。",
            "图像渲染和处理：使用专业的图像处理软件和算法，格AI数字人的形象进行渲染和处理。",
            "这包括生成逼真的表情、动作和纹理，以及优化图像的分辦幸和色彩效果。",
            "硬件集成和调试：将逶明屏幕、图像处理设育和控制系统等硬件进行集威和连接。",
            "进行系统调试和优化，确保展示柜和AI数字人的正常运行。"
    };
    private String mVoiceName = "Jiaojiao";
    private long mStartTime, mEndTime, mFirstTime, mTotalTime = 0;
    private int index = -1;
    private boolean isFirst;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            index++;
            if (index >= contents.length) {
                Log.e("TAG-->", contents.length + "条数据首包平均耗时::" + (mTotalTime / contents.length));
            } else {
                mStartTime = System.currentTimeMillis();
                setParams();
                isFirst = true;
                bakerSynthesizer.start();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_track_player);
        setTitle("合成-SDK无播放器");
        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        editText = findViewById(R.id.edit_content);
        // 初始化sdk
        bakerSynthesizer = new BakerSynthesizer(this, sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_ID),
                sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_SECRET));


        bakerSynthesizer.setDebug(this, true);
        audioTrackPlayer = new AudioTrackPlayer();
    }

    BakerCallback bakerCallback = new BakerCallback() {

        @Override
        public void onSynthesisStarted() {

        }


        @Override
        public void onSynthesisCompleted() {
            runOnUiThread(() -> ((TextView) findViewById(R.id.tv_test)).setText("合成完成"));
            Log.i("info", "合成完成");
        }

        @Override
        public void onPrepared() {
            audioTrackPlayer.cleanAudioData();
        }


        @Override
        public void onBinaryReceived(byte[] data, String audioType, String interval, String interval_x, boolean endFlag) {
            if (isFirst) {
                mEndTime = System.currentTimeMillis();
                mFirstTime = mEndTime - mStartTime;
                Log.e("TAG-->", "音色:" + mVoiceName + ",开始时间-" + mStartTime + ",首包时间-" + mEndTime + ",首包耗时-" + mFirstTime);
                mTotalTime += mFirstTime;
                isFirst = false;
            }

            audioTrackPlayer.setAudioData(data);
        }

        /**
         * 合成失败
         */
        @Override
        public void onTaskFailed(BakerError errorBean) {
            runOnUiThread(() -> ((TextView) findViewById(R.id.tv_test)).setText("合成失败：" + errorBean.getCode() + errorBean.getMessage()));
        }
    };


    /**
     * 设置相关参数
     */
    private void setParams() {
        if (bakerSynthesizer == null) {
            return;
        }
        //私有化部署时设置以下参数
      /*  bakerSynthesizer.setUrl("ws://10.10.50.18:19008/tts/wsapi");
        bakerSynthesizer.setTtsToken("default");*/

        bakerSynthesizer.setText(editText.getText().toString());
        bakerSynthesizer.setBakerCallback(bakerCallback);
        bakerSynthesizer.setVoice(mVoiceName);
        bakerSynthesizer.setLanguage("ZH");
        //设置播放的语速，在0～9之间（支持浮点值），默认值为5
        bakerSynthesizer.setSpeed(5.0f);
        //设置语音的音量，在0～9之间（只支持整型值），默认值为5
        bakerSynthesizer.setVolume(5);
        //设置语音的音调，在0～9之间，（支持浮点值），默认值为5
        bakerSynthesizer.setPitch(5.0f);
        //设置是否返回时间戳内容。1=支持返回，0=不需要返回。不设置默认为1支持返回。
        bakerSynthesizer.setInterval(1);
        //字级别时间戳功能，同interval=1 一起使用：0=关闭字级别时间戳功能 1=开启字级别时间戳功能
        bakerSynthesizer.setEnableSubtitles(1);
        //设置合成16K音频
        bakerSynthesizer.setAudioType(BakerBaseConstants.AUDIO_TYPE_PCM_16K);
        //设置标点符号静音时长：0=默认值,  1=句中标点停顿较短，适合直播、配音解说等场景,  2=句中标点停顿较长，适合朗诵、教学等场景
        bakerSynthesizer.setSilence(0);
    }

    public void startSynthesizer(View view) {
        //开始合成，合成结束后会自动stop
        setParams();
        audioTrackPlayer.cleanAudioData();
        audioTrackPlayer.play();
        bakerSynthesizer.start();
    }

    public void stopSynthesizer(View view) {
        audioTrackPlayer.stop();
    }

    private String sharedPreferencesGet(String mKey) {
        if (mSharedPreferences != null && !TextUtils.isEmpty(mKey)) {
            return mSharedPreferences.getString(mKey, "");
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        audioTrackPlayer.stop();
        if (bakerSynthesizer != null) {
            bakerSynthesizer.onDestroy();
        }
        super.onDestroy();
    }
}