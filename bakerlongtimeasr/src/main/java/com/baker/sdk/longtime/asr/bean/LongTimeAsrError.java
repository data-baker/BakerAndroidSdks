package com.baker.sdk.longtime.asr.bean;

/**
 * @author hsj55
 * 2020/9/24
 */
public class LongTimeAsrError {
    private String code;
    private String errorMessage;

    public LongTimeAsrError(String code, String errorMessage) {
        this.code = code;
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "LongTimeAsrError{" +
                "code=" + code +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
