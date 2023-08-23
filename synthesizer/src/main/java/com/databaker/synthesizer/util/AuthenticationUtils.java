package com.databaker.synthesizer.util;

import android.text.TextUtils;

import com.baker.sdk.basecomponent.writelog.WriteLog;
import com.databaker.synthesizer.SynthesizerConstants;
import com.databaker.synthesizer.bean.Token;
import com.databaker.synthesizer.net.okhttp.base.CallbackListener;
import com.databaker.synthesizer.net.okhttp.base.CommonOkHttpClient;
import com.databaker.synthesizer.net.okhttp.base.CommonOkHttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取token工具类
 *
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public class AuthenticationUtils {

    private int retryCount = 0;
    private final CallbackListener listener;

    public AuthenticationUtils(CallbackListener listener) {
        this.listener = listener;
    }
    /**
     * 鉴权获取token
     *
     * @param isRetry 是否重试；true-->失败后会自动重试两次。false-->失败后不重试
     */
    public void authentication(boolean isRetry) {
        if (isRetry) {
            retryCount = 2;
        }
        String url = String.format(SynthesizerConstants.URL_GET_TOKEN, SynthesizerConstants.mClientSecret, SynthesizerConstants.mClientId);

        Map<String, String> map = new HashMap<>();
        CommonOkHttpClient.sendRequest(CommonOkHttpRequest.createGetRequest(url, map), new CallbackListener<Token>() {
            @Override
            public void onSuccess(Token response) {
                if (response != null && !TextUtils.isEmpty(response.getAccess_token())) {
//                    SynthesizerConstants.ttsToken = response.getAccess_token();
                    WriteLog.writeLogs("authentication==ttsToken==" + response.getAccess_token());
                    retryCount = 2;
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
                e.printStackTrace();
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

}
