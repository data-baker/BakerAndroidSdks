package com.baker.sdk.longtime.asr.listener;

import com.baker.sdk.longtime.asr.bean.LongTimeAsrResponse;

/**
 * @author hsj55
 * 2020/9/24
 */
public interface LongTimeAsrCallBack {
    void onReady();
    void onVolume(int volume);
    void onRecording(LongTimeAsrResponse response);
    void onError(String code, String errorMessage);
}
