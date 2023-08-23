package com.baker.sdk.http;

/**
 * @author hsj55
 * 2020/9/17
 */
public interface CallbackListener<T> {
    void onSuccess(T response);

    void onFailure(Exception e);
}
