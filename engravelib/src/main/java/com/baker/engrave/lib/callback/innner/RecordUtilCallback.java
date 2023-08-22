package com.baker.engrave.lib.callback.innner;

public interface RecordUtilCallback {


    void recordVolume(int volume);

    /**
     * 录音中、识别中、识别结果回调。
     * typeCode=1，录音中
     * typeCode=2，识别中
     * typeCode=3，最终结果
     *
     * @param typeCode
     * @param recognizeResult
     */
    void recordsResult(int typeCode, String recognizeResult);

    void netRecordError(int errorCode, String message);
}
