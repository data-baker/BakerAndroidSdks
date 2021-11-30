package com.baker.engrave.lib.bean;


public class RecordingCheckDto extends CodeDto {

    private RecordingSocketBean.AudioBean data;

    public RecordingSocketBean.AudioBean getData() {
        return data;
    }

    public void setData(RecordingSocketBean.AudioBean data) {
        this.data = data;
    }


    @Override
    public String toString() {
        return "RecordingCheckDto{" +
                "data=" + data +
                "} " + super.toString();
    }
}
