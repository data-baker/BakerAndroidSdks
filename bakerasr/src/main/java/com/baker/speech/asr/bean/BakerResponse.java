package com.baker.speech.asr.bean;

import java.util.List;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BakerResponse {
    /**
     * 数据块序列号，请求内容会以流式的数据块方式返回给客户端。
     * 服务器端生成，从1递增
     */
    private int res_idx;
    /**
     * 识别结果
     */
    private List<String> nbest;
    /**
     * 识别结果预测
     */
    private List<String> uncertain;
    /**
     * 是否是最后一个数据块，0：否，1：是
     */
    private int end_flag;

    private String traceId;

    public int getRes_idx() {
        return res_idx;
    }

    public void setRes_idx(int res_idx) {
        this.res_idx = res_idx;
    }

    public List<String> getNbest() {
        return nbest;
    }

    public void setNbest(List<String> nbest) {
        this.nbest = nbest;
    }

    public List<String> getUncertain() {
        return uncertain;
    }

    public void setUncertain(List<String> uncertain) {
        this.uncertain = uncertain;
    }

    public int getEnd_flag() {
        return end_flag;
    }

    public void setEnd_flag(int end_flag) {
        this.end_flag = end_flag;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "BakerResponse{" +
                "res_idx=" + res_idx +
                ", nbest=" + nbest +
                ", uncertain=" + uncertain +
                ", end_flag=" + end_flag +
                '}';
    }
}
