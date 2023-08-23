package com.baker.sdk.longtime.asr.bean;

import java.util.List;

/**
 * @author hsj55
 * 2020/9/24
 */
public class LongTimeAsrResponse {
    private int code;
    private String message;
    private String trace_id;
    private String sentence_id;
    private String asr_text;
    private int end_flag;
    private String sentence_end;
    private String sos;
    private String eos;
    private String confidence;
    private int speed;
    private String speed_label;
    private int volume;
    private String volume_label;
    private List<BakerLongAsrWord> words;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrace_id() {
        return trace_id;
    }

    public void setTrace_id(String trace_id) {
        this.trace_id = trace_id;
    }

    public String getSentence_id() {
        return sentence_id;
    }

    public void setSentence_id(String sentence_id) {
        this.sentence_id = sentence_id;
    }

    public String getAsr_text() {
        return asr_text;
    }

    public void setAsr_text(String asr_text) {
        this.asr_text = asr_text;
    }

    public int getEnd_flag() {
        return end_flag;
    }

    public void setEnd_flag(int end_flag) {
        this.end_flag = end_flag;
    }

    public String getSentence_end() {
        return sentence_end;
    }

    public void setSentence_end(String sentence_end) {
        this.sentence_end = sentence_end;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public String getEos() {
        return eos;
    }

    public void setEos(String eos) {
        this.eos = eos;
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

    public List<BakerLongAsrWord> getWords() {
        return words;
    }

    public void setWords(List<BakerLongAsrWord> words) {
        this.words = words;
    }

    @Override
    public String toString() {
        return "LongTimeAsrResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", trace_id='" + trace_id + '\'' +
                ", sentence_id='" + sentence_id + '\'' +
                ", asr_text='" + asr_text + '\'' +
                ", end_flag=" + end_flag +
                ", sentence_end='" + sentence_end + '\'' +
                '}';
    }
}
