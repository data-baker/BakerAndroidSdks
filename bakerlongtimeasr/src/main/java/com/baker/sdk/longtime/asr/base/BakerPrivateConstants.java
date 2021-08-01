package com.baker.sdk.longtime.asr.base;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author hsj55
 * 2020/9/24
 */
public class BakerPrivateConstants {

    //"ws://192.168.1.19:39002"
    public final static String baseUrl = "wss://openapi.data-baker.com/asr/realtime";
//    public final static String baseUrl = "ws://192.168.1.21:39002";

    //上传至服务器每片的长度
    public static final int bufferSizeForUpload = 5120;

    public static LinkedBlockingQueue<byte[]> dataQueue = new LinkedBlockingQueue<>();

    public static String clientId = "";
    public static String clientSecret = "";

    public static String token = "";
}
