package com.baker.engrave.lib.bean;

import java.util.Objects;

/**
 * 识别结果
 */
public class RecordResult {

    private String audioText = ""; //语音文本
    private int recognitionRate = 0; //识别率
    private boolean isPass = false; //是否通过
    private String filePath = ""; //文件路径
    private String audioUrl= "";

    @Override
    public String toString() {
        return "RecordResult{}";
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public RecordResult() {
    }

    public RecordResult(String audioText, int recognitionRate, boolean isPass) {
        this.audioText = audioText;
        this.recognitionRate = recognitionRate;
        this.isPass = isPass;
    }

    public RecordResult(String audioText, int recognitionRate, boolean isPass, String audioUrl) {
        this.audioText = audioText;
        this.recognitionRate = recognitionRate;
        this.isPass = isPass;
        this.audioUrl = audioUrl;
    }

    public String getAudioText() {
        return audioText;
    }

    public void setAudioText(String audioText) {
        this.audioText = audioText;
    }

    public int getRecognitionRate() {
        return recognitionRate;
    }

    public void setRecognitionRate(int recognitionRate) {
        this.recognitionRate = recognitionRate;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }
}
