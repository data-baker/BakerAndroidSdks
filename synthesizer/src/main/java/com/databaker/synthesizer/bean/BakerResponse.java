package com.databaker.synthesizer.bean;

/**
 * Create by hsj55
 * 2019/11/20
 */
public class BakerResponse {
    private int idx;
    private String audio_data;
    private String audio_type;
    private String interval;
    private int end_flag;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getAudio_data() {
        return audio_data;
    }

    public void setAudio_data(String audio_data) {
        this.audio_data = audio_data;
    }

    public String getAudio_type() {
        return audio_type;
    }

    public void setAudio_type(String audio_type) {
        this.audio_type = audio_type;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getEnd_flag() {
        return end_flag;
    }

    public void setEnd_flag(int end_flag) {
        this.end_flag = end_flag;
    }

    @Override
    public String toString() {
        return "BakerResponse{" +
                "idx=" + idx +
                ", audio_data='" + audio_data + '\'' +
                ", audio_type='" + audio_type + '\'' +
                ", interval='" + interval + '\'' +
                ", end_flag=" + end_flag +
                '}';
    }
}
