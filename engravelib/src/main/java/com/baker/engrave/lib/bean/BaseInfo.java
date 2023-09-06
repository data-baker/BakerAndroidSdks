package com.baker.engrave.lib.bean;

public class BaseInfo {
    // 系统类型(mac/windows7/android/ios)  如：Xiaomi-MI2SC
    private String systemVersion;
    //应用版本号信息 浏览器版本/app版本
    private String appVersion;
    //项目名称
   private String appName;
    //语言环境
    private String language;
    //系统版本号和code
    private String appSystemVersion;

    public BaseInfo(String systemVersion, String appVersion, String appName, String language, String appSystemVersion) {
        this.systemVersion = systemVersion;
        this.appVersion = appVersion;
        this.appName = appName;
        this.language = language;
        this.appSystemVersion = appSystemVersion;
    }

    public String getSystemVersion() {
        return systemVersion;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public String getAppName() {
        return appName;
    }

    public String getLanguage() {
        return language;
    }

    public String getAppSystemVersion() {
        return appSystemVersion;
    }
}
