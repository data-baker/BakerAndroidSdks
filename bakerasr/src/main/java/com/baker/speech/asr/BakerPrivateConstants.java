package com.baker.speech.asr;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BakerPrivateConstants {
    //"ws://192.168.1.19:39002"
    public final static String baseUrl = "wss://openapi.data-baker.com/asr/wsapi";

    //上传至服务器每片的长度
    public static final int bufferSizeForUpload = 5120;

    public static LinkedBlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<>();

    public static String clientId = "";
    public static String clientSecret = "";

    public static String token = "";
}
