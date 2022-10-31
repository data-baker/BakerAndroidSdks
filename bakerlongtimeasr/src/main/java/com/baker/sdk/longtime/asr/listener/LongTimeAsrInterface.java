package com.baker.sdk.longtime.asr.listener;

import android.content.Context;

/**
 * @author hsj55
 * 2020/9/29
 */
public interface LongTimeAsrInterface {
    void isDebug(boolean d);
    void initSdk(Context context, LongTimeAsrCallBack callBack);
    void initSdk(Context context, String clientId, String secret, LongTimeAsrCallBack callBack);
    void initSdk(Context context, String clientId, String secret);
    void setCallBack(LongTimeAsrCallBack callBack);
    void setAudioFormat(String format);
    void setSampleRate(int rate);
    void setAddPct(boolean addPct);
    void setDomain(String domain);
    void setHotWordId(String id);
    void setDiylmid(String id);
    void setUrl(String url);
    void startAsr();
    void stopAsr();
    void start();
    void send(byte[] data);
    void end();
    void release();
}
