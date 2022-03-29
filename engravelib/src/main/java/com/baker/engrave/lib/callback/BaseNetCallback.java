package com.baker.engrave.lib.callback;

import com.baker.engrave.lib.bean.Mould;

import java.util.List;

/**
 * Create by hsj55
 * 2020/3/5
 */
public interface BaseNetCallback {
    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
     void netTokenError(int errorCode, String message);

     void token(String mToken);

     void recordTextList(String[] recordTextList);

     void netContentTextError(int errorCode, String message);

    /**
     * 环境检测中结果反馈
     *
     * @param value
     */
     void dbDetecting(int value);

    /**
     * 环境检测最终结果反馈
     * result=true，表示检测通过。value是检测通过的分贝值。
     * result=false，表示检测未通过。value是检测未通过的分贝值。
     *
     * @param result
     * @param value
     */
     void dbDetectionResult(boolean result, int value);

     void netDetectError(int errorCode, String message);

    /**
     * MouldID的回调
     *
     * @param sessionId
     */
     void voiceSessionId(String sessionId);

    /**
     * 录音中、识别中、识别结果回调。
     * typeCode=1，录音中
     * typeCode=2，识别中
     * typeCode=3，最终结果
     *
     * @param typeCode
     * @param recognizeResult
     */
     void recordsResult(int typeCode, int recognizeResult);

    /**
     * 录音过程中，会将声音分贝值实时返回
     * @param volume
     */
     void recordVolume(int volume);

     void netRecordError(int errorCode, String message);

    /**
     * 提交成功回调。
     * result=true,表示成功。
     * result=false,表示失败。
     *
     * @param result
     */
     void uploadRecordsResult(boolean result);
    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
     void onUploadError(int errorCode, String message);

    /**
     * 根据mouldId查询mould信息回调
     * @param mould
     */
     void mouldInfo(Mould mould);

    /**
     * 根据queryId分页查询mould信息回调
     * @param list
     */
     void mouldList(List<Mould> list);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
     void onMouldError(int errorCode, String message);
}
