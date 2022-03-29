package com.baker.engrave.lib.callback;

/**
 * Create by hsj55
 * 2020/3/4
 */
public interface UploadRecordsCallback {
    /**
     * 提交成功回调。
     * result=true,表示成功。
     * result=false,表示失败。
     *
     * @param result
     */
    public void uploadRecordsResult(boolean result, String mouldId);
    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    public void onUploadError(int errorCode, String message);
}
