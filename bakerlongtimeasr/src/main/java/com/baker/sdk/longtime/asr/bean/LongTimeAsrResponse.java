package com.baker.sdk.longtime.asr.bean;

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
