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
     * 是否是最后一个数据块，0：否，1：是
     */
    private int end_flag;

    private String traceId;

    private String text;

    private String confidence;

    private int speed;

    private String speed_label;

    private int volume;

    private String volume_label;

    private List<BakerAsrWord> words;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public String getSpeed_label() {
        return speed_label;
    }

    public void setSpeed_label(String speed_label) {
        this.speed_label = speed_label;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String getVolume_label() {
        return volume_label;
    }

    public void setVolume_label(String volume_label) {
        this.volume_label = volume_label;
    }

    public List<BakerAsrWord> getWords() {
        return words;
    }

    public void setWords(List<BakerAsrWord> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "BakerResponse{" +
                "res_idx=" + res_idx +
                ", nbest=" + nbest +
                ", end_flag=" + end_flag +
                ", traceId='" + traceId + '\'' +
                ", text='" + text + '\'' +
                ", confidence='" + confidence + '\'' +
                ", speed=" + speed +
                ", speed_label='" + speed_label + '\'' +
                ", volume=" + volume +
                ", volume_label='" + volume_label + '\'' +
                ", words=" + words +
                '}';
    }
}
