package com.databaker.voiceconvert.callback;

public interface VoiceConvertCallBack {

    /**
     * 授权验证通过，且与服务器连接成功，准备就绪回调
     */
    void onReady();

    /**
     * mic初始化成功后，只有使用sdk唤起的系统麦克风录音，才会有此回调
     */
    void canSpeech();

    /**
     * 原始音频 和 分贝值回调
     * @param data
     * @param volume
     */
    void onOriginData(byte[] data, int volume);

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
