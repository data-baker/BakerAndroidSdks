package com.databaker.synthesizer.bean;

/**
 * @Author yanteng on 2020/8/27.
 * @Email 1019395018@qq.com
 */
//此包下面没有混淆，bean混淆会无法解析数据。
//尽量所有的bean类放在此包下面。
//直接写内部类如果忘记-keep的错误很难找见
public class Token {
    //{"access_token":"9e73fa32-da7c-4102-8337-0a93b264cd1b","token_type":"bearer","expires_in":1582,"scope":"tts"}
    private String access_token;
    private String token_type;
    private int expires_in;
    private String scope;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "Token{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in=" + expires_in +
                ", scope='" + scope + '\'' +
                '}';
    }
}
