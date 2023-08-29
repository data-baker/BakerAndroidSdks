package com.baker.engrave.lib.callback;

import android.content.Context;

import com.baker.engrave.lib.bean.Mould;
import com.baker.engrave.lib.bean.RecordResult;
import com.baker.engrave.lib.configuration.EngraverType;
import com.baker.engrave.lib.net.NetUtil;
import com.baker.engrave.lib.util.RecordUtil;

import java.io.IOException;
import java.util.List;

/**
 * Create by hsj55
 * 2020/3/5
 */
public interface BaseNetCallback {


    /**
     * 获取当前录制句子的下表
     *
     * @return 当前下标
     */
    int getCurrentIndex();

    /**
     * 获取ClientId
     *
     * @return
     */
    String getClientId();

    /**
     * 获取Secret
     *
     * @return
     */
    String getClientSecret();


    /**
     * 初始化id
     *
     * @param context      上下文
     * @param clientId     clientid
     * @param clientSecret secret
     * @param queryID      querid
     * @param listener     回调方法
     */
    void initSDK(Context context, String clientId, String clientSecret, String queryID, final InitListener listener);

    /**
     * 设置QueryId
     *
     * @param queryID
     */
    void setQueryId(String queryID);

    /* *//**
     * 提供文本内容接口。
     *//*
    void getTextList();*/

    /**
     * 开启环境检测
     */
    int startDBDetection();

    void getSessionIdAndTexts();


    /**
     * 开启录音
     *
     * @param contentIndex 录音文本下标
     */
    int startRecord(int contentIndex);


    /**
     * 结束录制
     */
    void endRecord();

    /**
     * 非正常结束录制
     */
    void recordInterrupt();

    /**
     * 声音合成时所需token。
     */
    String getToken();

    /**
     * 录制完成，提交确认信息，模型开始训练
     *
     * @return false 不满足条件|未录制完成
     */
    boolean finishRecords(String phone, String notifyUrl);

    /**
     * 根据mouldId查询mould信息回调
     */
    void getMouldInfo(String mouldId);

    /**
     * 根据queryId分页查询mould信息回调
     *
     * @param page
     * @param limit
     * @param queryId
     */
    void getMouldList(int page, int limit, String queryId);


    /**
     * 查询未完成的session回话
     */
    void setRecordSessionId(String sessionId);


    /**
     * 设置回调录音文本
     *
     * @param callback
     */
    void setContentTextCallback(ContentTextCallback callback);


    /**
     * 噪音检测相关回调
     *
     * @param callback
     */
    void setDetectCallback(DetectCallback callback);


    /**
     * 当前条目是否录制
     *
     * @param index
     * @return
     */
    boolean isRecord(int index);

    /**
     * 停止播放
     */
    void stopPlay();

    /**
     * 试听播放
     *
     * @param currentIndex 需要播放的条目
     * @param listener     播放状态监听
     */
    void startPlay(final int currentIndex, final PlayListener listener);


    /**
     * 录音上传及识别回调
     *
     * @param callback
     */
    void setRecordCallback(RecordCallback callback);

    /**
     * 开启训练回调
     *
     * @param callback
     */
    void setUploadRecordsCallback(UploadRecordsCallback callback);


    void setMouldCallback(MouldCallback callback);


    void requestConfig();


    void setType(EngraverType type);


}
