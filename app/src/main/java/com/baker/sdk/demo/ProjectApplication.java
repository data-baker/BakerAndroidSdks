package com.baker.sdk.demo;

import android.app.Application;

public class ProjectApplication extends Application {
    private static ProjectApplication instance;
    public static ProjectApplication getAppInstance(){
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
