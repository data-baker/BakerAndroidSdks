package com.databaker.voiceconvert.callback;

public interface VoiceConvertCallBack {
    void onReady();
    /**
     * 变声后的音频回调
     * 子线程中回调
     *
     * @param audioArray 音频序列
     * @param isLast     是否是最后一包
     * @param traceId    本次声音转换唯一标识id
     */
    void onAudioOutput(byte[] audioArray, boolean isLast, String traceId);
    void onError(String errorCode, String errorMessage, String traceId);
}
