package com.baker.sdk.longtime.asr;

import android.content.Context;

import com.baker.sdk.longtime.asr.base.LongTimeAsrImpl;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrCallBack;
import com.baker.sdk.longtime.asr.listener.LongTimeAsrInterface;

/**
 * @author hsj55
 * 2020/9/29
 */
public class LongTimeAsr implements LongTimeAsrInterface {
    private LongTimeAsrImpl impl;

    public LongTimeAsr() {
        impl = new LongTimeAsrImpl();
    }

    @Override
    public void isDebug(boolean d) {
        impl.isDebug(d);
    }

    @Override
    public void initSdk(Context context, LongTimeAsrCallBack callBack) {
        impl.initSdk(context, callBack);
    }

    @Override
    public void initSdk(Context context, String clientId, String secret, LongTimeAsrCallBack callBack) {
        impl.initSdk(context, clientId, secret, callBack);
    }

    @Override
    public void setAudioFormat(String format) {
        impl.setAudioFormat(format);
    }

    @Override
    public void setSampleRate(int rate) {
        impl.setSampleRate(rate);
    }

    @Override
    public void setAddPct(boolean addPct) {
        impl.setAddPct(addPct);
    }

    @Override
    public void setDomain(String domain) {
        impl.setDomain(domain);
    }

    @Override
    public void setUrl(String url) {
        impl.setUrl(url);
    }

    @Override
    public void startAsr() {
        impl.startAsr();
    }

    @Override
    public void stopAsr() {
        impl.stopAsr();
    }

    @Override
    public void start() {
        impl.start();
    }

    @Override
    public void send(byte[] data) {
        impl.send(data);
    }

    @Override
    public void end() {
        impl.end();
    }

    @Override
    public void release() {
        impl.release();
    }
}
