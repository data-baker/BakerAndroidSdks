package com.baker.engrave.lib.callback;

/**
 * Create by hsj55
 * 2020/3/4
 */
public interface RecordCallback {
    /**
     * 录音中、识别中、识别结果回调。
     *
     * @param typeCode        1:录音中  2：识别中 3：成功 4：失败
     * @param recognizeResult 识别率，百分比，例如70 就是70%的意思
     */
    void recordsResult(int typeCode, int recognizeResult);

    /**
     * 录音过程中，会将声音分贝值实时返回
     *
     * @param volume
     */
    void recordVolume(int volume);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    void onRecordError(int errorCode, String message);
}
