package com.baker.speech.asr.base;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BakerAsrConstants {
    /**
     * 缺少ClientId
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT = "14180001";
    /**
     * 缺少Secret
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT = "14180002";

    /**
     * token获取失败
     */
    public static final String ERROR_CODE_INIT_FAILED_TOKEN_FAULT = "14180003";
    /**
     * 没有录音权限
     */
    public static final String ERROR_TYPE_RECORD_PERMISSION = "14180004";

    /**
     * 本地网络不可用
     */
    public static final String ERROR_TYPE_NET_UNUSABLE = "14180005";
    /**
     * 网络未准备好，没有调用start方法
     */
    public static final String ERROR_CODE_NOT_START = "14180006";

    /**
     * 录音尚未初始化
     */
    public final static String ERROR_CODE_NOT_INIT_RECORD = "14180007";
    /**
     * 正在录音
     */
    public final static String ERROR_CODE_RECORD_ING = "14180008";
    /**
     * 发送的数据为空
     */
    public static final String ERROR_CODE_DATA_IS_NULL = "14180009";
    /**
     * response is null
     */
    public final static String ERROR_CODE_RESPONSE_NULL = "14180010";
    /**
     * gson to object error
     */
    public final static String ERROR_CODE_GSON_ERROR = "14180011";
    /**
     * websocket发送消息出错
     */
    public final static String ERROR_CODE_WEBSOCKET_SEND_ERROR = "14180012";
    /**
     * websocket onFailure error
     */
    public final static String ERROR_CODE_WEBSOCKET_ONFAILURE = "14180013";

    /**
     * 录音意外中断
     */
    public static final String ERROR_TYPE_RECORD_INTERRUPTION = "14180014";
}
