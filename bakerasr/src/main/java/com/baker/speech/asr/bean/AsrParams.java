package com.baker.speech.asr.bean;

/**
 * @author hsj55
 * 2021/2/2
 */
public class AsrParams {
    private String audio_format = "pcm";
    private int sample_rate = 16000;
    private boolean add_pct = true;
    private String domain = "common";
    /**
     * 1=sdk麦克风录音 2=接收字节流
     */
    private int type;

    public AsrParams(boolean add_pct, String domain, int type) {
        this.add_pct = add_pct;
        this.domain = domain;
        this.type = type;
    }

    public AsrParams(String audio_format, int sample_rate, boolean add_pct, String domain, int type) {
        this.audio_format = audio_format;
        this.sample_rate = sample_rate;
        this.add_pct = add_pct;
        this.domain = domain;
        this.type = type;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAudio_format() {
        return audio_format;
    }

    public void setAudio_format(String audio_format) {
        this.audio_format = audio_format;
    }

    public int getSample_rate() {
        return sample_rate;
    }

    public void setSample_rate(int sample_rate) {
        this.sample_rate = sample_rate;
    }

    public boolean isAdd_pct() {
        return add_pct;
    }

    public void setAdd_pct(boolean add_pct) {
        this.add_pct = add_pct;
    }
}
