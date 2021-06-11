package com.baker.speech.asr.bean;

/**
 * @author hsj55
 * 2021/2/2
 */
public class AsrParams {
    private String domain = "common";

    public AsrParams(String domain) {
        this.domain = domain;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
