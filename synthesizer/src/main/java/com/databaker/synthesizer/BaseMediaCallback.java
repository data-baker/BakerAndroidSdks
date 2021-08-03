package com.databaker.synthesizer;


import com.baker.sdk.basecomponent.bean.BakerError;

/**
 * Create by hsj55
 * 2019/12/12
 */
public interface BaseMediaCallback {
    /**
     * 播放中
     */
    void playing();

    /**
     * 没播放啦
     */
    void noPlay();

    /**
     * 播放完毕
     */
    void onCompletion();

    /**
     * 发生错误时回调
     */
    void onError(BakerError error);

    /**
     * 当前缓存进度
     *
     * @param percentsAvailable
     */
    void onCacheAvailable(int percentsAvailable);
}
