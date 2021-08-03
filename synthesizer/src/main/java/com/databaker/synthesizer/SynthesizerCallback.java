package com.databaker.synthesizer;

import com.baker.sdk.basecomponent.bean.BakerError;

/**
 * 数据回调方法
 * Create by hsj55
 * 2019/11/8
 */
public interface SynthesizerCallback {

    void onSynthesisStarted();

    /**
     * 播放准备完毕
     * 当第一帧数据回来后，代表有数据可以播放
     */
    void onPrepared();

    /**
     * @param data      合成的音频数据，已使用base64加密，客户端需进行base64解密。
     * @param audioType 音频类型，如audio/pcm，audio/mp3
     * @param interval  音频interval信息，
     * @param endFlag   是否时最后一个数据块，false：否，true：是
     */
    void onBinaryReceived(byte[] data, String audioType, String interval, boolean endFlag);

    void onSynthesisCompleted();

    /**
     * {"code":40000,"message":"…","trace_id":" 1572234229176271"}
     *
     * @param error
     */
    void onTaskFailed(BakerError error);
}
