package com.baker.sdk.longtime.asr.bean;

/**
 * @author hsj55
 * 2020/9/24
 */
public class LongTimeAsrParams {
    private String audio_data;
    private String audio_format = "pcm";
    private int sample_rate = 16000;
    private int req_idx;
    private int speech_type = 1;
    private boolean add_pct = true;
    private String domain = "common";
    //配置的热词组的id
    private String hotwordid = "";
    //asr个性化模型的id
    private String diylmid = "";

    /**
     * 1=sdk麦克风录音 2=接收字节流
     */
    private int type;

    public LongTimeAsrParams() {
    }

    public LongTimeAsrParams(String audio_data, int req_idx) {
        this.audio_data = audio_data;
        this.req_idx = req_idx;
    }

    public LongTimeAsrParams(int sample_rate, boolean add_pct, String domain, String hotwordid, String diylmid, int type) {
        this.sample_rate = sample_rate;
        this.add_pct = add_pct;
        this.domain = domain;
        this.hotwordid = hotwordid;
        this.diylmid = diylmid;
        this.type = type;
    }

    public LongTimeAsrParams(String audio_format, int sample_rate, boolean add_pct, String domain, String hotwordid, String diylmid, int type) {
        this.audio_format = audio_format;
        this.sample_rate = sample_rate;
        this.add_pct = add_pct;
        this.domain = domain;
        this.hotwordid = hotwordid;
        this.diylmid = diylmid;
        this.type = type;
    }

    public String getAudio_data() {
        return audio_data;
    }

    public void setAudio_data(String audio_data) {
        this.audio_data = audio_data;
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

    public int getReq_idx() {
        return req_idx;
    }

    public void setReq_idx(int req_idx) {
        this.req_idx = req_idx;
    }

    public int getSpeech_type() {
        return speech_type;
    }

    public void setSpeech_type(int speech_type) {
        this.speech_type = speech_type;
    }

    public boolean isAdd_pct() {
        return add_pct;
    }

    public void setAdd_pct(boolean add_pct) {
        this.add_pct = add_pct;
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

    public String getHotwordid() {
        return hotwordid;
    }

    public void setHotwordid(String hotwordid) {
        this.hotwordid = hotwordid;
    }

    public String getDiylmid() {
        return diylmid;
    }

    public void setDiylmid(String diylmid) {
        this.diylmid = diylmid;
    }
}
