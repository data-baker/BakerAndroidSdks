package com.databaker.synthesizer.bean;

/**
 * Create by hsj55
 * 2019/11/28
 */
public class BaseResponse {
    private int code;
    private String message;
    private String trace_id;
    private BakerResponse data;

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

    public BakerResponse getData() {
        return data;
    }

    public void setData(BakerResponse data) {
        this.data = data;
    }
}
