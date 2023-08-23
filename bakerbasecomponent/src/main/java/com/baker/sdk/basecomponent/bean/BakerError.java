package com.baker.sdk.basecomponent.bean;

/**
 * @author hsj55
 * 2020/9/15
 */
public class BakerError {
    private String code;
    private String message;
    private String trace_id;
    private String sid;

    public BakerError() {
    }

    public BakerError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BakerError(String code, String message, String trace_id) {
        this.code = code;
        this.message = message;
        this.trace_id = trace_id;
    }

    public BakerError(String code, String message, String trace_id, String sid) {
        this.code = code;
        this.message = message;
        this.trace_id = trace_id;
        this.sid = sid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
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

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
