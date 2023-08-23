package com.databaker.synthesizer;

/**
 * 常量类（不建议开发者使用）
 * Create by hsj55
 * 2019/11/18
 */
public class SynthesizerConstants {
    //获取tts合成需要的token的url
    public static final String URL_GET_TOKEN = "https://openapi.data-baker.com/oauth/2.0/token?grant_type=client_credentials&client_secret=%s&client_id=%s";

    public static String ttsToken;
    public static String mClientId;
    public static String mClientSecret;

}
