package com.baker.sdk.longtime.asr.listener;

/**
 * @author hsj55
 * 2020/9/24
 */
public interface LongTimeAsrCallBack {
    void onReady();
    void onVolume(int volume);
    void onRecording(String asrText, boolean sentenceEnd, boolean endFlag);
    void onError(String code, String errorMessage);
}
