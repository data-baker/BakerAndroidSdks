package com.baker.engrave.lib.net;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class CurlLogTransaction {


    private Long _id;
    private String method;
    private String url;
    private String path;
    private String requestBody;
    private String requestHeaders;
    private String host;
    private String scheme;
    private Long requestContentLength;
    private String requestContentType;
    private boolean requestBodyIsPlainText = true;



    public Long getId() {
        return _id;
    }

    public void setId(long id) {
        _id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestBody() {
        return requestBody;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        Uri uri = Uri.parse(url);
        host = uri.getHost();
        path = uri.getPath() + ((uri.getQuery() != null) ? "?" + uri.getQuery() : "");
        scheme = uri.getScheme();
    }

    public String getPath() {
        return path;
    }
    public void setRequestHeaders(Headers headers) {
        setRequestHeaders(toHttpHeaderList(headers));
    }

    public void setRequestContentType(String requestContentType) {
        this.requestContentType = requestContentType;
    }
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestContentLength(Long requestContentLength) {
        this.requestContentLength = requestContentLength;
    }

    public void setRequestBodyIsPlainText(boolean requestBodyIsPlainText) {
        this.requestBodyIsPlainText = requestBodyIsPlainText;
    }
    public boolean requestBodyIsPlainText() {
        return requestBodyIsPlainText;
    }

    public void setRequestHeaders(List<HttpHeader> headers) {
        requestHeaders = new Gson().toJson(headers);
    }


    public List<HttpHeader> getRequestHeaders() {
        return new Gson().fromJson(requestHeaders,
                new TypeToken<List<HttpHeader>>(){}.getType());
    }

    private List<HttpHeader> toHttpHeaderList(Headers headers) {
        List<HttpHeader> httpHeaders = new ArrayList<>();
        for (int i = 0, count = headers.size(); i < count; i++) {
            httpHeaders.add(new HttpHeader(headers.name(i), headers.value(i)));
        }
        return httpHeaders;
    }


    //-----静态内部类-----

    public static class HttpHeader {

        private final String name;
        private final String value;

        HttpHeader(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }



}
