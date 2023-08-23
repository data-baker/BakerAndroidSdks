package com.databaker.synthesizer.net.okhttp.base;

import java.lang.reflect.Type;

/**
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public interface CallbackListener<T> {
    void onSuccess(T response);

    void onFailure(Exception e);
}
