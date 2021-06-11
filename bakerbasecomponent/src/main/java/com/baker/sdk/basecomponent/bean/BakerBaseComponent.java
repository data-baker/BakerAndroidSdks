package com.baker.sdk.basecomponent.bean;

/**
 * @author hsj55
 * 2020/9/21
 */
public class BakerBaseComponent {
    private String tag;
    private String clientId;
    private String packageName;
    private String versionName;
    private String uuid;

    public BakerBaseComponent(String tag, String clientId, String packageName, String versionName, String uuid) {
        this.tag = tag;
        this.clientId = clientId;
        this.packageName = packageName;
        this.versionName = versionName;
        this.uuid = uuid;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
