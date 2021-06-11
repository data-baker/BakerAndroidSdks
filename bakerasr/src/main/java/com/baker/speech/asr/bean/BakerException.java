package com.baker.speech.asr.bean;

import androidx.annotation.Nullable;

import java.io.IOException;

/**
 * @author hsj55
 * 2021/2/2
 */
public class BakerException extends IOException {
    private String code;
    private String message;

    public BakerException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public BakerException(String code) {
        this.code = code;
    }

    public BakerException(Exception e) {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Nullable
    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "BakerException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
