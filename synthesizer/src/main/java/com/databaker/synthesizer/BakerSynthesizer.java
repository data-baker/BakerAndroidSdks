package com.databaker.synthesizer;

import android.content.Context;

/**
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public class BakerSynthesizer implements SynthesizerInterface {

    private SynthesizerInterface synthesizer;

    public BakerSynthesizer(Context context) {
        synthesizer = new BakerSynthesizerImpl(context);
    }

    public BakerSynthesizer(Context context, String clientId, String clientSecret) {
        synthesizer = new BakerSynthesizerImpl(context, clientId, clientSecret);
    }

    public BakerSynthesizer(Context context, String clientId, String clientSecret, int connectTimeOut) {
        synthesizer = new BakerSynthesizerImpl(context, clientId, clientSecret, connectTimeOut);
    }

    public BakerSynthesizer(Context context, String clientId, String clientSecret, String url) {
        synthesizer = new BakerSynthesizerImpl(context, clientId, clientSecret, url);
    }

    @Override
    public void start() {
        synthesizer.start();
    }

    @Override
    public void onDestroy() {
        synthesizer.onDestroy();
    }

    @Override
    public void setBakerCallback(SynthesizerCallback c) {
        synthesizer.setBakerCallback(c);
    }

    @Override
    public void setUrl(String u) {
        synthesizer.setUrl(u);
    }

    @Override
    public void setVoice(String name) {
        synthesizer.setVoice(name);
    }

    @Override
    public void setText(String text) {
        synthesizer.setText(text);
    }

    @Override
    public void setLanguage(String l) {
        synthesizer.setLanguage(l);
    }

    @Override
    public void setSpeed(float s) {
        synthesizer.setSpeed(s);
    }

    @Override
    public void setVolume(int v) {
        synthesizer.setVolume(v);
    }

    @Override
    public void setPitch(float p) {
        synthesizer.setPitch(p);
    }

    @Override
    public void setAudioType(int type) {
        synthesizer.setAudioType(type);
    }

    @Override
    public void setRate(int r) {
        synthesizer.setRate(r);
    }

    @Override
    public void setClientId(String clientId) {
        synthesizer.setClientId(clientId);
    }

    @Override
    public void setClientSecret(String clientSecret) {
        synthesizer.setClientSecret(clientSecret);
    }

    @Override
    public void setPerDuration(int duration) {
        synthesizer.setPerDuration(duration);
    }

    @Override
    public void setDebug(Context context, boolean debug) {
        synthesizer.setDebug(context, debug);
    }

    @Override
    public void bakerPlay() {
        synthesizer.bakerPlay();
    }

    @Override
    public void bakerPause() {
        synthesizer.bakerPause();
    }

    @Override
    public void bakerStop() {
        synthesizer.bakerStop();
    }

    @Override
    public boolean isPlaying() {
        return synthesizer.isPlaying();
    }

    @Override
    public int getCurrentPosition() {
        return synthesizer.getCurrentPosition();
    }

    @Override
    public int getDuration() {
        return synthesizer.getDuration();
    }

    @Override
    public void setTtsToken(String token) {
        synthesizer.setTtsToken(token);
    }

    @Override
    public void setEnableTimestamp(boolean enable) {
        synthesizer.setEnableTimestamp(enable);
    }
}
