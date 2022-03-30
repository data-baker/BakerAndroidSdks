package com.baker.engrave.lib.bean;

import java.io.Serializable;

/**
 * Create by hsj55
 * 2020/3/10
 */
public class Mould implements Serializable {
    //模型ID
    private String modelId;
    //模型状态 1=默认状态，2=录制中，3=启动训练失败，4=训练中，5=训练失败，6=训练成功。
    private int modelStatus;
    //模型状态中文(值)
    private String statusName;

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public int getModelStatus() {
        return modelStatus;
    }

    public void setModelStatus(int modelStatus) {
        this.modelStatus = modelStatus;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
