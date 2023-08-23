package com.baker.engrave.lib.callback.innner;

public interface DetectUtilCallBack {
    /**
     * 环境检测最终结果反馈
     * result=true，表示检测通过。value是检测通过的分贝值。
     * result=false，表示检测未通过。value是检测未通过的分贝值。
     *
     * @param result
     * @param value
     */
    void dbDetectionResult(boolean result, int value);


    /**
     * 环境检测中结果反馈
     *
     * @param value
     */
    void dbDetecting(int value);


    void netDetectError(int errorCode, String message);
}
