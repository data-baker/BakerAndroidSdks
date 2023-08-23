package com.baker.engrave.lib.callback.innner;

import android.text.TextUtils;

import com.baker.engrave.lib.bean.Mould;
import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.callback.ContentTextCallback;
import com.baker.engrave.lib.callback.MouldCallback;
import com.baker.engrave.lib.callback.RecordCallback;
import com.baker.engrave.lib.callback.UploadRecordsCallback;
import com.baker.engrave.lib.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class NetCallbackImpl implements NetCallback {

    private ContentTextCallback contentTextCallback;

    private RecordCallback recordCallback;
    private UploadRecordsCallback uploadRecordsCallback;
    private MouldCallback mouldCallback;

    public void setContentTextCallback(ContentTextCallback contentTextCallback) {
        this.contentTextCallback = contentTextCallback;
    }


    public void setRecordCallback(RecordCallback recordCallback) {
        this.recordCallback = recordCallback;
    }

    public void setUploadRecordsCallback(UploadRecordsCallback uploadRecordsCallback) {
        this.uploadRecordsCallback = uploadRecordsCallback;
    }

    public void setMouldCallback(MouldCallback mouldCallback) {
        this.mouldCallback = mouldCallback;
    }


    private String mSessionId;

    public String getSessionId() {
        return mSessionId;
    }

    //录音结果总条数
    private final List<RecordResult> mRecordList = new ArrayList<>();


    public List<RecordResult> getRecordList() {
        return mRecordList;
    }

    @Override
    public void recordTextList(String[] recordTextList) {
        if (recordTextList != null && contentTextCallback != null) {
            LogUtil.d("取到了text，text.length=" + recordTextList.length);
            mRecordList.clear();
            for (String text : recordTextList) {
                RecordResult recordResult = new RecordResult(text, 0, false);
                mRecordList.add(recordResult);
            }
            contentTextCallback.contentTextList(recordTextList);
        }
    }

    @Override
    public void voiceSessionId(String sessionId) {
        if (!TextUtils.isEmpty(sessionId)) {
            mSessionId = sessionId;
        }
    }

    @Override
    public void uploadRecordsResult(boolean result) {
        if (uploadRecordsCallback != null) {
            String mouldId = null;
            if (!TextUtils.isEmpty(mSessionId)) {
                mouldId = mSessionId.substring(0, mSessionId.length() - 13);
                LogUtil.e("截取后mouldId：" + mouldId);
            }
            uploadRecordsCallback.uploadRecordsResult(result, mouldId);
        }
    }

    @Override
    public void mouldInfo(Mould mould) {
        if (mouldCallback != null) {
            mouldCallback.mouldInfo(mould);
        }
    }

    @Override
    public void mouldList(List<Mould> list) {
        if (mouldCallback != null) {
            mouldCallback.mouldList(list);
        }
    }

    @Override
    public void netTokenError(int errorCode, String message) {
        LogUtil.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
    }

    @Override
    public void netRecordError(int errorCode, String message) {
        LogUtil.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
        if (recordCallback != null) {
            recordCallback.onRecordError(errorCode, message);
        }
    }

    @Override
    public void netContentTextError(int errorCode, String message) {
        LogUtil.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
        if (contentTextCallback != null) {
            contentTextCallback.onContentTextError(errorCode, message);
        }
    }

    @Override
    public void onMouldError(int errorCode, String message) {
        LogUtil.e("发生错误：errorCode=" + errorCode + ",errorMsg=" + message);
        if (mouldCallback != null) {
            mouldCallback.onMouldError(errorCode, message);
        }
    }
}
