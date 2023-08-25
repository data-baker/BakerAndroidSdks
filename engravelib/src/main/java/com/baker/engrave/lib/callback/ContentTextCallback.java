package com.baker.engrave.lib.callback;

import com.baker.engrave.lib.bean.RecordResult;

import java.util.List;

/**
 * 文本内容接口回调
 * Create by hsj55
 * 2020/3/4
 */
public interface ContentTextCallback {
    /**
     * 获取录音文本回调。
     *
     * @param strList
     */
    void contentTextList(List<RecordResult> mRecordList);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    void onContentTextError(int errorCode, String message);
}
