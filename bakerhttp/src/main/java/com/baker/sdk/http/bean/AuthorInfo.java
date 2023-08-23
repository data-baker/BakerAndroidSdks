package com.baker.sdk.http.bean;

/**
 * @author hsj55
 * 2020/9/21
 */
public class AuthorInfo {
    private String clientId;
    private String clientSecret;
    private String accessToken;

    public AuthorInfo(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthorInfo(String clientId, String clientSecret, String accessToken) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "AuthorInfo{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
}
