package com.baker.engrave.lib.net;

/**
 * Create by hsj55
 * 2020/3/5
 */
public class NetConstants {
    public static final String VERSION = "v3";
    //获取token的url"http://10.10.20.107:9922"

    // 正式环境
    //public static final String URL_GET_TOKEN = "https://openapi.data-baker.com/oauth/2.0/token?grant_type=client_credentials&client_secret=%s&client_id=%s";
    // public static final String BASE_URL = "https://gramophone.data-baker.com/gramophone/";

    // 沙盒环境
    public static final String URL_GET_TOKEN = "https://openapitest.data-baker.com/oauth/2.0/token?grant_type=client_credentials&client_secret=%s&client_id=%s";
    public static final String BASE_URL = "https://gramophonetest.data-baker.com:9050/gramophone/";

    // 生成环境
    // public static final String URL_GET_TOKEN = " http://10.10.50.23:9904/oauth/2.0/token?grant_type=client_credentials&client_secret=%s&client_id=%s";
    // public static final String BASE_URL = "http://10.10.50.23:9922/gramophone/";

    public static final String URL_BASE_VERSION = BASE_URL + VERSION + "/";

    // 获取录音文本
    // public static final String URL_GET_TEXT_LIST = URL_BASE_VERSION + "record/context/list";

    // 根据token申请创建模型的MID
    public static final String URL_GET_MOULD_ID = URL_BASE_VERSION + "user/record/start/session";
    // 开启模型训练
    public static final String URL_FINISH_RECORDS = URL_BASE_VERSION + "user/record/upload/information";
    // 中断提交录音
    public static final String URL_STOP_SESSION = URL_BASE_VERSION + "user/record/stop/session";
    // 根据mouldId查询mould状态信息
    public static final String URL_GET_MOULD_INFO = URL_BASE_VERSION + "user/record/model/status";
    // 根据queryId查询mould状态信息
    public static final String URL_GET_MOULD_LIST_INFO = URL_BASE_VERSION + "user/record/model/status/batch";
    // 获取配置信息
    public static final String URL_GET_CONFIG = URL_BASE_VERSION + "user/record/config/query";


    public static final String RESULT_CODE_SUCCESS = "20000";
    public static final String RESULT_CODE_TOKEN_EXPIRE = "00011";

}
