package com.databaker.voiceconvert.callback;

public interface AuthCallback {
    /**
     * 授权成功
     */
    void onSuccess();

    void onError(Exception e);
}
