package com.baker.sdk.http;

import android.text.TextUtils;

import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.baker.sdk.http.bean.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hsj55
 * 2020/9/21
 */
public class BakerTokenManager {
    private int retryCount = 0;
    private CallbackListener listener;
    private String mClientId, mClientSecret;

    private BakerTokenManager() {
    }

    private static class InnerClass {
        private final static BakerTokenManager INSTANCE = new BakerTokenManager();
    }

    public static BakerTokenManager getInstance() {
        return InnerClass.INSTANCE;
    }

    public synchronized void authentication(String clientId, String secret, CallbackListener callbackListener) {
        listener = callbackListener;
        mClientId = clientId;
        mClientSecret = secret;
        authentication(true);
    }

    /**
     * 鉴权获取token
     *
     * @param isRetry 是否重试；true-->失败后会自动重试两次。false-->失败后不重试
     */
    private void authentication(boolean isRetry) {
        if (isRetry) {
            retryCount = 2;
        }
        String url = String.format(BakerHttpConstants.URL_GET_TOKEN, mClientSecret, mClientId);

        Map<String, String> map = new HashMap<>();
        CommonOkHttpClient.sendRequest(CommonOkHttpRequest.createGetRequest(url, map), new CallbackListener<Token>() {
            @Override
            public void onSuccess(Token response) {
                if (response != null && !TextUtils.isEmpty(response.getAccess_token())) {
                    WriteLog.writeLogs("authentication==ttsToken==" + response.getAccess_token());
                    retryCount = 2;
                    BakerHttpConstants.addToken(mClientId, mClientSecret, response.getAccess_token());
                    if (listener != null) {
                        listener.onSuccess(response.getAccess_token());
                    }
                } else {
                    WriteLog.writeLogs("authentication==ttsToken==失败重试");
                    if (retryCount > 0) {
                        retryCount = retryCount - 1;
                        authentication(false);
                    } else {
                        if (listener != null) {
                            listener.onFailure(new Exception("服务器返回未知数据"));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Exception e) {
                WriteLog.writeLogs("authentication==ttsToken==" + e.getMessage());
                if (retryCount > 0) {
                    retryCount = retryCount - 1;
                    authentication(false);
                } else {
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                }
            }
        });
    }

    public void release(){
        listener = null;
    }
}
