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
    //配置的热词组的id
    private String hotwordid = "";
    //asr个性化模型的id
    private String diylmid = "";
    //默认关闭静音检测
    private Boolean enable_vad = false;
    //最大开始静音时长
    private int max_begin_silence = 0;
    //最大结束静音时长
    private int max_end_silence = 0;
    /**
     * 1=sdk麦克风录音 2=接收字节流
     */
    private int type;

    public AsrParams(int sample_rate) {
        this.sample_rate = sample_rate;
    }

    public AsrParams(String audio_format, int sample_rate, boolean add_pct, String domain, String hotwordid, String diylmid, Boolean enable_vad, int max_begin_silence, int max_end_silence, int type) {
        this.audio_format = audio_format;
        this.sample_rate = sample_rate;
        this.add_pct = add_pct;
        this.domain = domain;
        this.hotwordid = hotwordid;
        this.diylmid = diylmid;
        this.enable_vad = enable_vad;
        this.max_begin_silence = max_begin_silence;
        this.max_end_silence = max_end_silence;
        this.type = type;
    }

    public AsrParams(int sample_rate, boolean add_pct, String domain, String hotwordid, String diylmid, Boolean enable_vad, int max_begin_silence, int max_end_silence, int type) {
        this.sample_rate = sample_rate;
        this.add_pct = add_pct;
        this.domain = domain;
        this.hotwordid = hotwordid;
        this.diylmid = diylmid;
        this.enable_vad = enable_vad;
        this.max_begin_silence = max_begin_silence;
        this.max_end_silence = max_end_silence;
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

    public Boolean getEnable_vad() {
        return enable_vad;
    }

    public void setEnable_vad(Boolean enable_vad) {
        this.enable_vad = enable_vad;
    }

    public int getMax_begin_silence() {
        return max_begin_silence;
    }

    public void setMax_begin_silence(int max_begin_silence) {
        this.max_begin_silence = max_begin_silence;
    }

    public int getMax_end_silence() {
        return max_end_silence;
    }

    public void setMax_end_silence(int max_end_silence) {
        this.max_end_silence = max_end_silence;
    }
}
