package com.databaker.synthesizer;


/**
 * @Author yanteng on 2020/9/23.
 * @Email 1019395018@qq.com
 */

public class BakerSynthesizerErrorConstants {
    /**
     * 缺少ClientId
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT = "11180001";
    /**
     * 缺少Secret
     */
    public static final String ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT = "11180002";
    /**
     * token获取失败
     */
    public static final String ERROR_CODE_INIT_FAILED_TOKEN_FAULT = "11180003";

    /**
     * 合成文本内容为空
     */
    public static final String ERROR_CODE_STRING_NULL = "11180004";

    /**
     * 发音人参数错误
     */
    public static final String ERROR_CODE_PARAMS_NOT_AVAILABLE_SET_VOICE_FAULT = "11180005";

    /**
     * 返回结果解析错误
     */
    public static final String ERROR_CODE_RESPONSE_NOT_AVAILABLE_ANALYSIS_FAULT = "11180006";

    /**
     * 合成失败，失败信息相关错误
     */
    public static final String ERROR_CODE_ERROR_INFO = "11180007";

    /**
     * 播放器相关错误
     */
    public static final String ERROR_CODE_MEDIA_ERROR = "11180008";
    /**
     * 合成文本内容转码错误
     */
    public static final String ERROR_CODE_PARAMS_NOT_AVAILABLE_TXT_TRANSCODING_FAULT = "11180009";
    /**
     * 返回结果解析错误;返回null
     */
    public static final String ERROR_CODE_RESPONSE_NOT_AVAILABLE_ISNULL_FAULT = "11180010";
}
