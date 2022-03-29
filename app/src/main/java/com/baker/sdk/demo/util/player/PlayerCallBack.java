package com.baker.sdk.demo.util.player;

public interface PlayerCallBack {
    void onPlaying();

    void onPaused();

    void onPlayCompleted();

    void onStopped();

    void onError(String errorCode, String errorMsg);
}
