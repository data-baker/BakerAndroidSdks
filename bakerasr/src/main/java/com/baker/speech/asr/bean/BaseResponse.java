package com.baker.speech.asr.bean;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BaseResponse {
    private int code;
    private String message;
    private String trace_id;
    private String sid;
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

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public BakerResponse getData() {
        return data;
    }

    public void setData(BakerResponse data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", trace_id='" + trace_id + '\'' +
                ", sid='" + sid + '\'' +
                ", data=" + data +
                '}';
    }
}
