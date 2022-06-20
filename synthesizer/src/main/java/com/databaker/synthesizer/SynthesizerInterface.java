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
     * 设置发音人声音名称，默认：标准合成_模仿儿童_果子
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
     * 合成请求文本的语言，目前支持ZH(中文和中英混)和ENG(纯英文，中文部分不会合成),默认：ZH
     *
     * @param l
     */
    void setLanguage(String l);

    /**
     * 设置播放的语速，在0～9之间（只支持整型值），不传时默认为5
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
     * 设置语音的音调，取值0-9，不传时默认为5中语调
     *
     * @param p
     */
    void setPitch(float p);

    /**
     * 可不填，不填时默认为3，表示mp3格式
     * audiotype=4 ：返回16K采样率的pcm格式
     * audiotype=5 ：返回8K采样率的pcm格式
     * audiotype=6 ：返回16K采样率的wav格式
     * audiotype=6&rate=1 ：返回8K的wav格式
     *
     * @param type
     */
    void setAudioType(int type);

    /**
     * 可不填，不填时默认为2，取值范围1-8，2以上的值仅针对返回MP3格式，对应的码率为：
     * 1 —— 8kbps
     * 2 —— 16kbps
     * 3 —— 24kbps
     * 4 —— 32kbps
     * 5 —— 40kbps
     * 6 —— 48kbps
     * 7 —— 56kbps
     * 8 —— 64kbps
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

    /**
     *
     * @param token
     */
    void setTtsToken(String token);

    /**
     * 设置是否返回时间戳内容。true=支持返回，false=不需要返回。不设置默认为false不返回。
     *
     * @param enable
     */
    void setEnableTimestamp(boolean enable);
}
