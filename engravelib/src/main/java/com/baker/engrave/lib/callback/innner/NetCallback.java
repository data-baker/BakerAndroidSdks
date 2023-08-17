package com.baker.engrave.lib.callback.innner;

import com.baker.engrave.lib.bean.Mould;

import java.util.List;

public interface NetCallback {

    void recordTextList(String[] recordTextList);


    void voiceSessionId(String sessionId);

    /**
     * 提交成功回调。
     * result=true,表示成功。
     * result=false,表示失败。
     *
     * @param result
     */
    void uploadRecordsResult(boolean result);

    /**
     * 根据mouldId查询mould信息回调
     *
     * @param mould
     */
    void mouldInfo(Mould mould);

    /**
     * 根据queryId分页查询mould信息回调
     *
     * @param list
     */
    void mouldList(List<Mould> list);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    void netTokenError(int errorCode, String message);

    void netRecordError(int errorCode, String message);

    void netContentTextError(int errorCode, String message);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    void onMouldError(int errorCode, String message);
}
