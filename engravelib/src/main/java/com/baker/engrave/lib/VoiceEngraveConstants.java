package com.baker.engrave.lib;

/**
 * Create by hsj55
 * 2020/3/5
 */
public class VoiceEngraveConstants {
    //请求token失败
    public static final int NORMAL_ERROR = 90001;
    //Token过期
    public static final int ERROR_CODE_TOKEN_OVERDUE = 90002;
    //参数值为空或不正确
    public static final int ERROR_CODE_PARAM_NULL = 90003;
    //网络请求错误
    public static final int ERROR_CODE_NET_WRONG = 90004;
    //网络请求data值为空
    public static final int ERROR_CODE_DATA_NULL = 90005;
    //服务器返回错误的代码
    public static final int ERROR_CODE_FROM_SERVER = 90006;
    //解析response出错
    public static final int ERROR_CODE_RESPONSE = 90007;
    //当前上传的录音的mouldId与录制的录音的mouldId不一致
    public static final int ERROR_CODE_MOULD_DIFFERENT = 90008;
    //当前上传的录音的contentText与录制的录音的contentText不一致
    public static final int ERROR_CODE_TEXT_DIFFERENT = 90009;
    //停止录音出错
    public static final int ERROR_CODE_STOP_RECORD = 90010;
    //停止检测噪音出错
    public static final int ERROR_CODE_STOP_DETECT = 90011;
    //创建录音文件时异常
    public static final int ERROR_CODE_FILE = 90012;
    //因音频焦点丢失或电话等异常中断录音
    public static final int ERROR_CODE_INTERRUPT = 90013;


}
