package com.databaker.synthesizer.net.okhttp.base;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.baker.sdk.basecomponent.util.GsonConverter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * @Author yanteng on 2020/8/20.
 * @Email 1019395018@qq.com
 */

public class CommonJSONCallBack<T> implements Callback {
    private final Handler handler;
    private final CallbackListener<T> listener;

    public CommonJSONCallBack(CallbackListener<T> listener) {
        // 创建主线程的handler
        handler = new Handler(Looper.getMainLooper());

        this.listener = listener;
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull final IOException e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                listener.onFailure(e);
            }
        });
    }

    @Override
    public void onResponse(@NonNull final Call call, @NonNull final Response response) throws IOException {
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    //  注意这里的 getGenericInterfaces 是获取接口泛型的方法
                    Type[] genericInterfaces = listener.getClass().getGenericInterfaces();
                    //  这里强转是因为 ParameterizedType 继承Type接口 并可以获取对应的参数类
                    ParameterizedType genericInterface = (ParameterizedType) genericInterfaces[0];
                    listener.onSuccess(GsonConverter.fromJson(response.body().string(), genericInterface.getActualTypeArguments()[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onFailure(e);
                }
            }
        });
    }

}
