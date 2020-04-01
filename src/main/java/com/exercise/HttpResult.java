package com.exercise;

import java.io.Serializable;

public class HttpResult implements Serializable {
    private int code;

    private String body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
