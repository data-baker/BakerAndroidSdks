package com.baker.sdk.http;

import com.baker.sdk.http.bean.AuthorInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hsj55
 * 2020/9/21
 */
public class BakerHttpConstants {
    //获取tts合成需要的token的url
    public static final String URL_GET_TOKEN = "https://openapi.data-baker.com/oauth/2.0/token?grant_type=client_credentials&client_secret=%s&client_id=%s";

    private static final List<AuthorInfo> authorInfos = new ArrayList<>();

    public static AuthorInfo getAuthorInfoByClientId(String clientId) {
        for (AuthorInfo info : authorInfos) {
            if (clientId.equals(info.getClientId())) {
                return info;
            }
        }
        return null;
    }

    static void addToken(String clientId, String secret, String token) {
        boolean flag = false;
        for (AuthorInfo info : authorInfos) {
            if (clientId.equals(info.getClientId())) {
                info.setAccessToken(token);
                flag = true;
                break;
            }
        }
        if (!flag) {
            authorInfos.add(new AuthorInfo(clientId, secret, token));
        }
    }

}
