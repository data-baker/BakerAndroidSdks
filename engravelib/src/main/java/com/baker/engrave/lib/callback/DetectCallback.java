package com.baker.engrave.lib.callback;

/**
 * Create by hsj55
 * 2020/3/4
 * <p>
 * 环境检测callback
 */
public interface DetectCallback {

    /**
     * 环境检测中结果反馈
     *
     * @param value
     */
    public void dbDetecting(int value);

    /**
     * 环境检测最终结果反馈
     * result=true，表示检测通过。value是检测通过的分贝值。
     * result=false，表示检测未通过。value是检测未通过的分贝值。
     *
     * @param result
     * @param value
     */
    public void dbDetectionResult(boolean result, int value);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    public void onDetectError(int errorCode, String message);
}
