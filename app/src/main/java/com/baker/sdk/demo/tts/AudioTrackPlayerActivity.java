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
        /**
         * 开始合成
         */
        @Override
        public void onSynthesisStarted() {

        }

        /**
         * 合成完成。
         * 当onBinaryReceived方法中endFlag参数=1，即最后一条消息返回后，会回调此方法。
         */
        @Override
        public void onSynthesisCompleted() {
            runOnUiThread(() -> ((TextView) findViewById(R.id.tv_test)).setText("合成完成"));
            Log.i("info", "合成完成");
        }

        /**
         * 第一帧数据返回时的回调
         */
        @Override
        public void onPrepared() {
            //清除掉播放器之前的缓存数据
            audioTrackPlayer.cleanAudioData();
        }

        /**
         * 流式持续返回数据的接口回调
         *
         * @param data 合成的音频数据
         * @param audioType  音频类型，如audio/pcm
         * @param interval  音频interval信息，
         * @param endFlag  是否时最后一个数据块，false：否，true：是
         * @param interval_x
         * interval-info-x: L=1&T=1,L=1&T=2,L=1&T=1,L=1&T=2,L=1&T=5
         * L表示语言种类，目前支持1：纯中文，5：中英混
         * T表示interval类型，0：默认值，1：声母，2：韵母，3：儿化韵母，4：英文，5：#3静音
         */
        @Override
        public void onBinaryReceived(byte[] data, String audioType, String interval, String interval_x, boolean endFlag) {
//            HLogger.d("data.length==" + data.length + ", interval=" + interval);
            audioTrackPlayer.setAudioData(data);
        }

        /**
         * 合成失败
         */
        @Override
        public void onTaskFailed(BakerError errorBean) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) findViewById(R.id.tv_test)).setText("合成失败：" + errorBean.getCode() + errorBean.getMessage());
                }
            });
            HLogger.d("errorCode==" + errorBean.getCode() + ",errorMsg==" + errorBean.getMessage() + ",traceId==" + errorBean.getTrace_id());
        }
    };

    /**
     * 设置相关参数
     */
    private void setParams() {
        if (bakerSynthesizer == null) {
            return;
        }
        /**********************以下是必填参数**************************/
        //设置要转为语音的合成文本
        bakerSynthesizer.setText(editText.getText().toString().trim());
//        bakerSynthesizer.setText("西安市今天白天到夜间，阴，温度16到11摄氏度，东北风4级");
        //设置返回数据的callback
        bakerSynthesizer.setBakerCallback(bakerCallback);
        //设置发音人声音名称，默认：Lingling
        bakerSynthesizer.setVoice("Jiaojiao");
        //私有化部署时设置以下参数
//        bakerSynthesizer.setUrl("ws://192.168.1.19:19009");
//        bakerSynthesizer.setTtsToken("default");
        /**********************以下是选填参数**************************/
        /**
         * 合成请求文本的语言：
         * ZH(中文和中英混)
         * ENG(纯英文，中文部分不会合成)
         * CAT(粤语）
         * SCH(四川话)
         * TJH(天津话)
         * TAI(台湾话)
         * KR(韩语)
         * BRA(巴葡语)
         * JP(日语)
         */
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
        /**
         * 可不填，不填时默认为4, 16K采样率的pcm格式
         * audiotype=4 ：返回16K采样率的pcm格式
         * audiotype=5 ：返回8K采样率的pcm格式
         */
        bakerSynthesizer.setAudioType(BakerBaseConstants.AUDIO_TYPE_PCM_16K);
        //取值范围1~20；默认值为1，不调整频谱； 1代表不调整频谱； 1以上的值代表高频能量增加幅度，值越大声音的高频部分增强越多，听起来更亮和尖细
//        bakerSynthesizer.setSpectrum(1);
        //取值范围0~20；不传时默认为0，仅针对8K音频频谱的调整。组合形式只有1种：audiotype=5&spectrum_8k=xx
//        bakerSynthesizer.setSpectrum8k(0);
        //设置标点符号静音时长：0=默认值,  1=句中标点停顿较短，适合直播、配音解说等场景,  2=句中标点停顿较长，适合朗诵、教学等场景
        bakerSynthesizer.setSilence(0);
    }

    public void startSynthesizer(View view) {
//        //开始合成，合成结束后会自动stop
        setParams();
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