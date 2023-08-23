package com.baker.speech.asr.bean;

public class BakerAsrWord {
    private String confidence;
    private String sos;
    private String eos;
    private String word;

    public String getConfidence() {
        return confidence;
    }

    public void setConfidence(String confidence) {
        this.confidence = confidence;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }

    public String getEos() {
        return eos;
    }

    public void setEos(String eos) {
        this.eos = eos;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
