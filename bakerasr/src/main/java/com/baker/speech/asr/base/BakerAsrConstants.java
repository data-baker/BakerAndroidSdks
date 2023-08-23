package com.baker.speech.asr.base;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BakerAsrConstants {
    /**
     * 缺少ClientId
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT = "13180001";
    /**
     * 缺少Secret
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT = "13180002";

    /**
     * token获取失败
     */
    public static final String ERROR_CODE_INIT_FAILED_TOKEN_FAULT = "13180003";
    /**
     * 没有录音权限
     */
    public static final String ERROR_TYPE_RECORD_PERMISSION = "13180004";

    /**
     * 本地网络不可用
     */
    public static final String ERROR_TYPE_NET_UNUSABLE = "13180005";
    /**
     * 网络未准备好，没有调用start方法
     */
    public static final String ERROR_CODE_NOT_START = "13180006";

    /**
     * 录音尚未初始化
     */
    public final static String ERROR_CODE_NOT_INIT_RECORD = "13180007";
    /**
     * 正在录音
     */
    public final static String ERROR_CODE_RECORD_ING = "13180008";
    /**
     * 发送的数据为空
     */
    public static final String ERROR_CODE_DATA_IS_NULL = "13180009";
    /**
     * response is null
     */
    public final static String ERROR_CODE_RESPONSE_NULL = "13180010";
    /**
     * gson to object error
     */
    public final static String ERROR_CODE_GSON_ERROR = "13180011";
    /**
     * websocket发送消息出错
     */
    public final static String ERROR_CODE_WEBSOCKET_SEND_ERROR = "13180012";
    /**
     * websocket onFailure error
     */
    public final static String ERROR_CODE_WEBSOCKET_ONFAILURE = "13180013";

    /**
     * 录音意外中断
     */
    public static final String ERROR_TYPE_RECORD_INTERRUPTION = "13180014";
}
