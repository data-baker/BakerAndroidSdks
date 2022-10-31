package com.databaker.synthesizer;

import android.content.Context;

/**
 * @Author yanteng on 2020/8/19.
 * @Email 1019395018@qq.com
 */

public interface SynthesizerInterface {


    void start();

    void onDestroy();


    /**
     * 设置返回数据的callback
     *
     * @param c
     */
    void setBakerCallback(SynthesizerCallback c);

    /**
     * 私有化部署的服务器url地址。
     *
     * @param u
     */
    void setUrl(String u);

    /**
     * 设置发音人声音名称，例如：Jiaojiao
     *
     * @param name
     */
    void setVoice(String name);

    /**
     * 设置要转为语音的合成文本,不能为空，做utf-8转码传输。
     *
     * @param text
     */
    void setText(String text);

    /**
     * 合成请求文本的语言
     * ZH(中文和中英混)
     * ENG(纯英文，中文部分不会合成)
     * CAT(粤语）
     * SCH(四川话)
     * TJH(天津话)
     * TAI(台湾话)
     * KR(韩语)
     * BRA(巴葡语)
     * JP(日语)
     *
     * @param l
     */
    void setLanguage(String l);

    /**
     * 设置播放的语速，在0～9之间（支持浮点值），默认值为5
     *
     * @param s
     */
    void setSpeed(float s);

    /**
     * 设置语音的音量，在0～9之间（只支持整型值），不传时默认值为5
     *
     * @param v
     */
    void setVolume(int v);

    /**
     * 设置语音的音调，在0～9之间，（支持浮点值），默认值为5
     *
     * @param p
     */
    void setPitch(float p);

    /**
     * audiotype = 4，返回16K采样率的pcm格式，默认值
     * audiotype = 5，返回8K采样率的pcm格式
     * audiotype=5&rate=3，返回24K采样率的pcm格式
     * （示例及sdk播放器未支持24k声音播放）
     * @param type
     */
    void setAudioType(int type);

    /**
     * rate=3，请求24k采样率时使用
     *
     * @param r
     */
    void setRate(int r);

    void setClientId(String clientId);

    void setClientSecret(String clientSecret);

    /**
     * 设置每个字的播放时长
     *
     * @param duration
     */
    void setPerDuration(int duration);

    void setDebug(Context context, boolean debug);

    void bakerPlay();

    void bakerPause();

    void bakerStop();

    boolean isPlaying();

    int getCurrentPosition();

    int getDuration();

    void setTtsToken(String token);

    void setSpectrum(int spectrum);
    void setSpectrum8k(int spectrum);
    /**
     * 音子级别时间戳功能：
     * 0:关闭音子级别时间戳功能
     * 1:开启音子级别时间戳功能
     *
     * @param enable
     */
    void setInterval(int enable);
    void setEnableSubtitles(int enable);
    void setSilence(int enable);
}
