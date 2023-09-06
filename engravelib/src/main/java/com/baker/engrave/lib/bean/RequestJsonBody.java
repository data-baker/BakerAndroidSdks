package com.baker.engrave.lib.bean;

import java.util.ArrayList;
import java.util.List;

public class RequestJsonBody {
    private BaseInfo baseInfo;
    private String level;
    private String userid;
    //业务类型(bbyc_android/bbyc_ios/bbyc_web)
    private String businessType;
    private String time;
    private List<String> contentList;

    public RequestJsonBody(BaseInfo baseInfo, String level,  String businessType) {
        this.baseInfo = baseInfo;
        this.level = level;
        this.businessType = businessType;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setContentList(List<String> contentList) {
        this.contentList = contentList;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }

    public String getUserid() {
        return userid;
    }

    public String getBusinessType() {
        return businessType;
    }

    public String getTime() {
        return time;
    }

    public List<String> getContentList() {
        return contentList;
    }
}
