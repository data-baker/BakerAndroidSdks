package com.databaker.voiceconvert;

public class Constants {

    private static final String ERROR_CODE_PREFIX = "191";

    /**
     * 没有token，先进行鉴权
     */
    public static final String ERROR_NO_TOKEN = ERROR_CODE_PREFIX + "80001";


    /**
     * 网络请求出错
     */
    public static final String ERROR_WEB_SOCKET = ERROR_CODE_PREFIX + "80002";

    /**
     * 无录音权限
     */
    public static final String ERROR_NO_PERMISSION = ERROR_CODE_PREFIX + "80003";

    public static final int bufferSizeForUpload = 5120;
}
