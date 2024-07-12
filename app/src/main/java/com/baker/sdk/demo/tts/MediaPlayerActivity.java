package com.baker.sdk.demo.tts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.baker.sdk.basecomponent.BakerBaseConstants;
import com.baker.sdk.basecomponent.bean.BakerError;
import com.baker.sdk.basecomponent.util.HLogger;
import com.baker.sdk.demo.R;
import com.baker.sdk.demo.base.BakerBaseActivity;
import com.baker.sdk.demo.base.Constants;
import com.databaker.synthesizer.BakerMediaCallback;
import com.databaker.synthesizer.BakerSynthesizer;

public class MediaPlayerActivity extends BakerBaseActivity {
    private BakerSynthesizer bakerSynthesizer;
    private EditText editText;
    private TextView resultTv;
    private SharedPreferences mSharedPreferences;

    //1715841752114
    //1715841750141
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);
        setTitle("合成-SDK含播放器");

        mSharedPreferences = getSharedPreferences(Constants.SP_TABLE_NAME, Context.MODE_PRIVATE);
        editText = findViewById(R.id.edit_content);
        resultTv = findViewById(R.id.tv);
        resultTv.setMovementMethod(ScrollingMovementMethod.getInstance());

        //初始化sdk
        bakerSynthesizer = new BakerSynthesizer(this, sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_ID),
                sharedPreferencesGet(Constants.TTS_ONLINE_CLIENT_SECRET));
        bakerSynthesizer.setDebug(MediaPlayerActivity.this, true);
    }

    BakerMediaCallback bakerMediaCallback = new BakerMediaCallback() {

        @Override
        public void onPrepared() {
            appendResult("\n合成准备就绪");
            if (bakerSynthesizer != null) {
                bakerSynthesizer.bakerPlay();
            }
        }

        @Override
        public void onCacheAvailable(int percentsAvailable) {
            appendResult("\n缓存进度：" + percentsAvailable + "%");
        }

        @Override
        public void onCompletion() {
            appendResult("\n播放结束");
        }

        @Override
        public void onError(BakerError errorBean) {
            HLogger.d("--onError-- errorCode=" + errorBean.getCode() + ", errorMsg=" + errorBean.getMessage() + ",traceId==" + errorBean.getTrace_id());
        }

        @Override
        public void playing() {
            appendResult("\n播放啦");
        }

        @Override
        public void noPlay() {
            appendResult("\n没有播放啦");
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
        //设置返回数据的callback
        bakerSynthesizer.setBakerCallback(bakerMediaCallback);
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

    public void start(View view) {
//        //开始合成，合成结束后会自动stop
        setParams();
        if (bakerSynthesizer != null) {
            bakerSynthesizer.start();
        }
    }

    public void stop(View view) {
        if (bakerSynthesizer != null) {
            bakerSynthesizer.bakerStop();
            appendResult("\n停止播放");
        }
    }

    public void pauseOrPlay(View view) {
        if (bakerSynthesizer != null) {
            boolean isPlaying = bakerSynthesizer.isPlaying();
            if (isPlaying) {
                bakerSynthesizer.bakerPause();
                appendResult("\n暂停");
            } else {
                bakerSynthesizer.bakerPlay();
                appendResult("\n播放");
            }
        }
    }

    public void isPlay(View view) {
        if (bakerSynthesizer != null) {
            boolean isPlaying = bakerSynthesizer.isPlaying();
            appendResult("\n当前播放状态：" + isPlaying);
        }
    }

    public void playDuration(View view) {
        if (bakerSynthesizer != null) {
            int currentPosition = bakerSynthesizer.getCurrentPosition();
            appendResult("\n当前播放至：" + currentPosition + "秒");
        }
    }

    public void duration(View view) {
        if (bakerSynthesizer != null) {
            int duration = bakerSynthesizer.getDuration();
            appendResult("\n音频总长度：" + duration + "秒");
        }
    }

    private void appendResult(final String str) {
        resultTv.post(new Runnable() {
            @Override
            public void run() {
                resultTv.append(str);
                int scrollAmount = resultTv.getLayout().getLineTop(resultTv.getLineCount())
                        - resultTv.getHeight();
                if (scrollAmount > 0)
                    resultTv.scrollTo(0, scrollAmount);
                else
                    resultTv.scrollTo(0, 0);
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
        if (bakerSynthesizer != null) {
            bakerSynthesizer.onDestroy();
        }
        super.onDestroy();
    }
}