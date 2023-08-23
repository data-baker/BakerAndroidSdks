package com.baker.sdk.longtime.asr.bean;

/**
 * @author hsj55
 * 2020/9/24
 */
public class LongTimeAsrBaseParams {
    private String access_token;
    private String version = "1.0";
    private Object asr_params;

    public LongTimeAsrBaseParams() {
    }

    public LongTimeAsrBaseParams(String access_token, Object asr_params) {
        this.access_token = access_token;
        this.asr_params = asr_params;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Object getAsr_params() {
        return asr_params;
    }

    public void setAsr_params(Object asr_params) {
        this.asr_params = asr_params;
    }
}
