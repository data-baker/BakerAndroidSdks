package com.baker.engrave.lib.callback.innner;

import com.baker.engrave.lib.callback.DetectCallback;
import com.baker.engrave.lib.util.HLogger;

public class DetectCallbackImpl implements DetectUtilCallBack{

    private DetectCallback detectCallback;

    public void setDetectCallback(DetectCallback detectCallback) {
        this.detectCallback = detectCallback;
    }

    @Override
    public void dbDetectionResult(boolean result, int value) {
        HLogger.i("result=" + result + ", value=" + value);
        if (detectCallback != null) {
            detectCallback.dbDetectionResult(result, value);
        }
    }

    @Override
    public void dbDetecting(int value) {
        if (detectCallback != null) {
            detectCallback.dbDetecting(value);
        }
    }

    @Override
    public void netDetectError(int errorCode, String message) {
        HLogger.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
        if (detectCallback != null) {
            detectCallback.onDetectError(errorCode, message);
        }
    }
}
