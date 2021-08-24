package com.databaker.voiceconvert;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baker.sdk.basecomponent.util.Util;
import com.baker.sdk.http.BakerTokenManager;
import com.baker.sdk.http.CallbackListener;
import com.databaker.voiceconvert.callback.AuthCallback;

import org.jetbrains.annotations.NotNull;

public class VoiceConvertManager1 {
    private Context mContext = null;
    private BakerTokenManager tokenManager;
    private String token;
    private String mVoiceName = "Vc_jiaojiao";
    private boolean enableVad = false;
    private boolean enableAlign = false;

    public void init(@NotNull Context context, @NotNull String clientId, @NotNull String clientSecret, AuthCallback authCallback) {
        mContext = context;

        if (TextUtils.isEmpty(clientId)) {
            if (authCallback != null) {
                authCallback.onError(new IllegalArgumentException("缺少ClientId"));
            }
            return;
        }
        if (TextUtils.isEmpty(clientSecret)) {
            if (authCallback != null) {
                authCallback.onError(new IllegalArgumentException("缺少Secret"));
            }
            return;
        }

        try {
            if (!Util.checkConnectNetwork(mContext)) {
                if (authCallback != null) {
                    authCallback.onError(new RuntimeException("网络连接不可用"));
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (authCallback != null) {
                authCallback.onError(e);
                return;
            }
        }

        //调用授权
        tokenManager = new BakerTokenManager();
        tokenManager.authentication(clientId, clientSecret, new CallbackListener<String>() {
            @Override
            public void onSuccess(String response) {
                token = response;
                if (authCallback != null) {
                    authCallback.onSuccess();
                    return;
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("LongTimeAsrImpl", "baker long time asr sdk init token error, " + e.getMessage());
                e.printStackTrace();
                if (authCallback != null) {
                    authCallback.onError(new RuntimeException("baker long time asr sdk init token error, " + e.getMessage()));
                    return;
                }
            }
        });
    }

    public void setVoiceName(String voiceName) {
        mVoiceName = voiceName;
    }

    public void setVadEnable(boolean bool) {
        enableVad = bool;
    }

    public void setAudioAlign(boolean bool) {
        enableAlign = bool;
    }


}
