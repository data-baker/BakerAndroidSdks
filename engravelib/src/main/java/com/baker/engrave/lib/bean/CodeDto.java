package com.baker.engrave.lib.bean;

import java.io.Serializable;

/**
 * @Author yanteng on 2019/3/19.
 * @Email 1019395018@qq.com
 */

public class CodeDto implements Serializable {
    public static final String ERR_FORMAT = "%s（%d）";
    public static final String ERR_FORMAT_MESSAGE = "%s";
    public static final int RES_SUCCESS = 20000;
    public static final String RES_MSG_SUCCESS = "OK";

    public static final CodeDto INVALID_TOKEN = new CodeDto("40014", "登录失效");
    private String code;
    private String message;
    private String success;

    public CodeDto() {
    }

    public CodeDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CodeDto{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", success='" + success + '\'' +
                '}';
    }
}
