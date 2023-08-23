package com.databaker.synthesizer.util;

import com.baker.sdk.basecomponent.bean.BakerError;
import com.databaker.synthesizer.BakerSynthesizerErrorConstants;

/**
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public class SynthesizerErrorUtils {

    public static String formatError(String errorCode) {
        switch (errorCode) {
            case BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_CLIENT_ID_FAULT:
                return "缺少ClientId";
            case BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_CLIENT_SECRET_FAULT:
                return "缺少Secret";
            case BakerSynthesizerErrorConstants.ERROR_CODE_INIT_FAILED_TOKEN_FAULT:
                return "token获取失败";
            case BakerSynthesizerErrorConstants.ERROR_CODE_STRING_NULL:
                return "合成文本内容为空";
            case BakerSynthesizerErrorConstants.ERROR_CODE_PARAMS_NOT_AVAILABLE_SET_VOICE_FAULT:
                return "发音人参数错误";
            case BakerSynthesizerErrorConstants.ERROR_CODE_ERROR_INFO:
                return "合成失败，失败信息相关错误";
            case BakerSynthesizerErrorConstants.ERROR_CODE_PARAMS_NOT_AVAILABLE_TXT_TRANSCODING_FAULT:
                return "合成文本内容转码错误";
            case BakerSynthesizerErrorConstants.ERROR_CODE_RESPONSE_NOT_AVAILABLE_ISNULL_FAULT:
                return "服务器返回结果为空";
            case BakerSynthesizerErrorConstants.ERROR_CODE_MEDIA_ERROR:
                return "播放器相关错误";
            default:
                return "未知错误";
        }
    }

    public static BakerError formatErrorBean(String errorCode) {
        return new BakerError(errorCode, SynthesizerErrorUtils.formatError(errorCode));
    }

    public static BakerError formatErrorBean(String errorCode, String additionalInfo) {
        return new BakerError(errorCode, SynthesizerErrorUtils.formatError(errorCode) + "-->" + additionalInfo);
    }

    public static BakerError formatErrorBean(int serviceErrorCode, String additionalInfo, String trace_id) {
        //包装一下服务器的错误码
        return new BakerError(String.valueOf(serviceErrorCode), additionalInfo, trace_id);
    }
}
