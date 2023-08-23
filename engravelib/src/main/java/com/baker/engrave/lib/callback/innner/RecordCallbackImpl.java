package com.baker.engrave.lib.callback.innner;

import com.baker.engrave.lib.callback.RecordCallback;
import com.baker.engrave.lib.util.LogUtil;

public class RecordCallbackImpl implements RecordUtilCallback {
    private RecordCallback recordCallback;

    public void setRecordCallback(RecordCallback recordCallback) {
        this.recordCallback = recordCallback;
    }

    @Override
    public void recordVolume(int volume) {
        if (recordCallback != null) {
            recordCallback.recordVolume(volume);
        }
    }

    @Override
    public void recordsResult(int typeCode, String recognizeResult) {
        if (recordCallback != null) {
            LogUtil.e("---5");
            recordCallback.recordsResult(typeCode, recognizeResult);
        }
    }

    @Override
    public void netRecordError(int errorCode, String message) {
        LogUtil.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
        if (recordCallback != null) {
            recordCallback.onRecordError(errorCode, message);
        }
    }
}
